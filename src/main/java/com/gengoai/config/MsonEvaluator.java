/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 *
 */

package com.gengoai.config;

import com.gengoai.collection.Iterables;
import com.gengoai.io.Resources;
import com.gengoai.io.resource.ClasspathResource;
import com.gengoai.json.JsonEntry;
import com.gengoai.logging.Loggable;
import com.gengoai.parsing.v2.Evaluator;
import com.gengoai.parsing.v2.Expression;
import com.gengoai.parsing.v2.ParseException;
import com.gengoai.parsing.v2.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.v2.expressions.ListExpression;
import com.gengoai.parsing.v2.expressions.PrefixOperatorExpression;
import com.gengoai.parsing.v2.expressions.ValueExpression;
import com.gengoai.string.Strings;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static com.gengoai.function.CheckedConsumer.asFunction;

/**
 * @author David B. Bracewell
 */
class MsonEvaluator extends Evaluator<Expression> implements Loggable {
   final String resourceName;
   final LinkedList<String> scope = new LinkedList<>();

   MsonEvaluator(String resourceName) {
      this.resourceName = resourceName;
      $(PrefixOperatorExpression.class,
        ConfigTokenType.IMPORT,
        asFunction(exp -> handleImport(exp.getValue().as(ValueExpression.class).getValue().asString())));
      $(BinaryOperatorExpression.class,
        ConfigTokenType.EQUAL_PROPERTY,
        asFunction(this::handleProperty));
      $(BinaryOperatorExpression.class,
        ConfigTokenType.APPEND_PROPERTY,
        asFunction(this::handleAppendProperty));
      $(BinaryOperatorExpression.class,
        ConfigTokenType.BEGIN_OBJECT,
        asFunction(this::handleSection));
   }

   private JsonEntry convertExpression(Expression exp) throws ParseException {
      if (exp.isInstance(ValueExpression.class)) {
         return JsonEntry.from(exp.as(ValueExpression.class).getValue().get());
      }

      if (exp.isInstance(ListExpression.class, ConfigTokenType.BEGIN_ARRAY)) {
         ListExpression arrayExpression = exp.as(ListExpression.class);
         JsonEntry array = JsonEntry.array();
         for (Expression e : arrayExpression) {
            array.addValue(convertExpression(e));
         }
         return array;
      }

      if (exp.isInstance(ListExpression.class, ConfigTokenType.BEGIN_OBJECT)) {
         ListExpression mve = exp.as(ListExpression.class);
         JsonEntry map = JsonEntry.object();
         for (Expression e : mve) {
            BinaryOperatorExpression boe = e.as(BinaryOperatorExpression.class);
            String key = boe.getKey().as(ValueExpression.class).getValue().asString();
            map.addProperty(key, convertExpression(boe.getValue()));
         }
         return map;
      }

      throw new ParseException("Unexpected Expression: " + exp);
   }

   private String effectiveKey(String key) {
      String effectiveKey;
      if (scope.isEmpty()) {
         effectiveKey = key;
      } else {
         effectiveKey = Strings.join(scope, ".") + "." + key;
      }
      if (effectiveKey.endsWith("._") && effectiveKey.length() > 2) {
         effectiveKey = effectiveKey.substring(0, effectiveKey.length() - 2);
      }
      return effectiveKey;
   }

   private void handleAppendProperty(BinaryOperatorExpression exp) throws ParseException {
      String key = effectiveKey(exp.getKey().as(ValueExpression.class).getValue().asString());
      List<Object> list = Config.get(key).asList(Object.class);
      if (list == null) {
         list = new ArrayList<>();
      }
      processJson(list, convertExpression(exp.getValue()));
      Config.getInstance()
         .setterFunction
         .setProperty(key, JsonEntry.array(list).toString(), resourceName);
   }

   private void handleImport(String importString) throws ParseException {
      logFine("Handing Import of {0}", importString);
      if (!importString.endsWith(Config.CONF_EXTENSION) && importString.contains("/")) {
         //We don't have a MSON extension at the end and the import string is a path
         throw new ParseException(String.format("Invalid Import Statement (%s)", importString));
      }
      String path = Config.resolveVariables(importString).trim();
      if (path.contains("/")) {
         if (path.startsWith("file:")) {
            logFine("Loading config from: {0}", path);
            Config.loadConfig(Resources.from(path));
         } else {
            logFine("Loading config from resource: {0}", path);
            Config.loadConfig(new ClasspathResource(path));
         }
      } else {
         logFine("Loading package config: {0}", path);
         Config.loadPackageConfig(path);
      }
   }

   private void handleProperty(BinaryOperatorExpression exp) throws ParseException {
      logFine("Handling property: {0}" + exp);
      String key = effectiveKey(convertExpression(exp.getKey()).getAsString());
      logFine("Effective key: {0}", key);
      JsonEntry value = convertExpression(exp.getValue());
      String stringValue = value.isPrimitive() ? value.get().toString() : value.toString();
      Config.getInstance().setterFunction.setProperty(key, stringValue, resourceName);
      System.out.println(key + " : " + value);
   }

   private void handleSection(BinaryOperatorExpression exp) throws ParseException {
      String section = effectiveKey(exp.getKey().as(ValueExpression.class).getValue().asString());
      scope.addLast(section);
      ListExpression mve = exp.getValue().as(ListExpression.class);
      for (Expression expression : mve) {
         BinaryOperatorExpression boe = expression.as(BinaryOperatorExpression.class);
         if (boe.getType().isInstance(ConfigTokenType.BEGIN_OBJECT)) {
            handleSection(boe);
         } else if (boe.getType().equals(ConfigTokenType.APPEND_PROPERTY)) {
            handleAppendProperty(boe);
         } else {
            handleProperty(boe);
         }
      }
      scope.removeLast();
   }

   private void processJson(List<Object> list, JsonEntry entry) {
      if (entry.isNull()) {
         list.add(null);
      } else if (entry.isPrimitive()) {
         list.add(entry.get());
      } else if (entry.isObject()) {
         list.add(entry.getAsMap());
      } else if (entry.isArray()) {
         for (JsonEntry e : Iterables.asIterable(entry.elementIterator())) {
            processJson(list, e);
         }
      }
   }
}//END OF MsonEvaluator
