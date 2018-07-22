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
 */

package com.gengoai.config;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.ClasspathResource;
import com.gengoai.io.resource.Resource;
import com.gengoai.parsing.*;
import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.expressions.PrefixOperatorExpression;
import com.gengoai.parsing.expressions.ValueExpression;
import com.gengoai.parsing.handlers.PrefixOperatorHandler;
import com.gengoai.parsing.handlers.ValueHandler;
import com.gengoai.scripting.ScriptEnvironment;
import com.gengoai.scripting.ScriptEnvironmentManager;
import com.gengoai.string.StringUtils;

import javax.script.ScriptException;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import static com.gengoai.Validation.notNull;

/**
 * @author David B. Bracewell
 */
class ConfigParser extends Parser {
   private static final long serialVersionUID = 1L;

   private static Grammar CONFIG_GRAMMAR = new Grammar() {{
      register(ConfigTokenizer.ConfigTokenType.IMPORT, new PrefixOperatorHandler(ValueExpression.class));
      register(ConfigTokenizer.ConfigTokenType.SCRIPT, new PrefixOperatorHandler(ValueExpression.class));
      register(ConfigTokenizer.ConfigTokenType.PROPERTY, new PrefixOperatorHandler(ValueExpression.class));
      register(ConfigTokenizer.ConfigTokenType.APPEND_PROPERTY, new PrefixOperatorHandler(ValueExpression.class));
      register(ConfigTokenizer.ConfigTokenType.VALUE, new ValueHandler());
      register(ConfigTokenizer.ConfigTokenType.SECTION_HEADER, new SectionHandler());
   }};

   private static Lexer CONFIG_LEXER = input -> new ParserTokenStream(
      new Iterator<ParserToken>() {
         final ConfigTokenizer backing = new ConfigTokenizer(input.reader());
         ParserToken next = null;

         @Override
         public boolean hasNext() {
            if (next == null) {
               try {
                  next = backing.next();
               } catch (IOException | ParseException e) {
                  throw new RuntimeException(e);
               }
            }
            return next != null;
         }

         @Override
         public ParserToken next() {
            if (!hasNext()) {
               throw new NoSuchElementException();
            }
            ParserToken returnToken = next;
            next = null;
            return returnToken;
         }
      }
   );

   private final Evaluator<Expression> evaluator = new Evaluator<Expression>() {{
      $void(PrefixOperatorExpression.class,
            ConfigTokenizer.ConfigTokenType.IMPORT,
            exp -> importConfig(exp.right.toString().trim()));

      $void(PrefixOperatorExpression.class,
            ConfigTokenizer.ConfigTokenType.SCRIPT,
            exp -> importScript(exp.right.toString().trim()));

      $void(PrefixOperatorExpression.class,
            ConfigTokenizer.ConfigTokenType.APPEND_PROPERTY,
            exp -> setProperty(exp, ""));

      $void(PrefixOperatorExpression.class,
            ConfigTokenizer.ConfigTokenType.PROPERTY,
            exp -> setProperty(exp, ""));

      $void(SectionExpression.class,
            ConfigTokenizer.ConfigTokenType.SECTION_HEADER,
            exp -> handleSection(StringUtils.EMPTY, exp));
   }};

   private final Resource resource;
   private final String resourceName;


   /**
    * Instantiates a new Config parser.
    *
    * @param config the config
    */
   public ConfigParser(Resource config) {
      super(CONFIG_GRAMMAR, CONFIG_LEXER);
      this.resource = config;
      this.resourceName = config.descriptor();
   }

   private void importScript(String script) {
      Resource scriptResource = new ClasspathResource(script.trim(), Config.getDefaultClassLoader());
      String extension = script.substring(script.lastIndexOf('.') + 1);
      ScriptEnvironment env = ScriptEnvironmentManager.getInstance().getEnvironmentForExtension(extension);
      try {
         env.eval(scriptResource);
      } catch (ScriptException | IOException e) {
         throw new RuntimeException(e);
      }
   }

   private void importConfig(String importStatement) throws ParseException {
      if (!Config.loadDefaultConf(importStatement)) {
         String path;

         if (importStatement.contains("/")) {
            path = importStatement;
            if (!path.endsWith(".conf")) {
               path += ".conf";
            }
         } else {
            if (importStatement.endsWith(".conf")) {
               int index = importStatement.lastIndexOf('.');
               path = importStatement.substring(0, index).replaceAll("\\.", "/") + ".conf";
            } else {
               path = importStatement.replace(".", "/") + ".conf";
            }
         }

         path = Config.resolveVariables(path).trim();
         if (path.startsWith("file:")) {
            Config.loadConfig(Resources.from(path));
         } else {
            Config.loadConfig(new ClasspathResource(path));
         }
      }
   }

   private void setProperty(PrefixOperatorExpression assignment, String section) {
      String key = section;

      if (assignment.operator.text.equals("_")) {
         if (StringUtils.isNullOrBlank(section)) {
            throw new IllegalStateException("Trying to set a non-section value using the \"_\" property.");
         }
         key = section.substring(0, section.length() - 1);
      } else {
         key = section + assignment.operator.text;
      }

      String value = assignment.right.as(ValueExpression.class).value;

      //unescape things
      value = StringUtils.trim(value);
      value = value.replaceAll("(?<!\\\\)\\\\\n", "\n");
      value = value.replaceAll("\\\\(.)", "$1");

      if (assignment.operator.type == ConfigTokenizer.ConfigTokenType.APPEND_PROPERTY) {
         if (Config.hasProperty(key)) {
            value = Config.getRaw(key) + "," + value;
         }
      }

      Config.getInstance().setterFunction.setProperty(key, value, resourceName);
   }


   public void parse() throws ParseException {
      super.evaluateAll(resource, evaluator);
   }

   private void handleSection(String parent, SectionExpression exp) {
      final SectionExpression section = notNull(exp.as(SectionExpression.class));
      final String prefix = StringUtils.isNullOrBlank(parent) ? exp.sectionPrefix : parent + "." + exp.sectionPrefix;
      for (Expression x : section.assignments) {
         if (x.isInstance(SectionExpression.class)) {
            handleSection(prefix, x.as(SectionExpression.class));
         } else {
            setProperty(x.as(PrefixOperatorExpression.class), prefix + ".");
         }
      }
   }


}//END OF ConfigParser
