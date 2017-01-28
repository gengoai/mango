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

package com.davidbracewell.config;

import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.ClasspathResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.parsing.*;
import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.expressions.PrefixOperatorExpression;
import com.davidbracewell.parsing.expressions.ValueExpression;
import com.davidbracewell.parsing.handlers.PrefixOperatorHandler;
import com.davidbracewell.parsing.handlers.ValueHandler;
import com.davidbracewell.scripting.ScriptEnvironment;
import com.davidbracewell.scripting.ScriptEnvironmentManager;
import com.davidbracewell.string.StringUtils;
import lombok.SneakyThrows;

import java.io.IOException;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

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

   private static Lexer CONFIG_LEXER = new Lexer() {
      @Override
      public ParserTokenStream lex(final Resource input) throws IOException {
         return new ParserTokenStream(
                                        new Iterator<ParserToken>() {
                                           final ConfigTokenizer backing = new ConfigTokenizer(input.reader());
                                           ParserToken next = null;

                                           @Override
                                           @SneakyThrows
                                           public boolean hasNext() {
                                              if (next == null) {
                                                 next = backing.next();
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

                                           @Override
                                           public void remove() {
                                              throw new UnsupportedOperationException();
                                           }
                                        }
         );
      }
   };


   private final Resource resource;
   private final String resourceName;


   /**
    * Instantiates a new Config parser.
    *
    * @param config the config
    * @throws java.io.IOException the iO exception
    */
   public ConfigParser(Resource config) throws IOException {
      super(CONFIG_GRAMMAR, CONFIG_LEXER);
      this.resource = config;
      this.resourceName = config.descriptor();
   }

   @SneakyThrows
   private void importScript(String script) {
      Resource scriptResource = new ClasspathResource(script.trim(), Config.getDefaultClassLoader());
      String extension = script.substring(script.lastIndexOf('.') + 1);
      ScriptEnvironment env = ScriptEnvironmentManager.getInstance().getEnvironmentForExtension(extension);
      env.eval(scriptResource);
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

   public List<Expression> parse() throws ParseException {
      ExpressionIterator iterator = parse(resource);
      Expression exp;
      while ((exp = iterator.next()) != null) {

         if (exp.match(ConfigTokenizer.ConfigTokenType.IMPORT)) {

            importConfig(exp.as(PrefixOperatorExpression.class).right.toString().trim());

         } else if (exp.match(ConfigTokenizer.ConfigTokenType.SCRIPT)) {

            importScript(exp.as(PrefixOperatorExpression.class).right.toString().trim());

         } else if (exp.match(ConfigTokenizer.ConfigTokenType.APPEND_PROPERTY)) {

            setProperty(exp.as(PrefixOperatorExpression.class), "");

         } else if (exp.match(ConfigTokenizer.ConfigTokenType.PROPERTY)) {

            setProperty(exp.as(PrefixOperatorExpression.class), "");

         } else if (exp.match(ConfigTokenizer.ConfigTokenType.SECTION_HEADER)) {

            handleSection(StringUtils.EMPTY, exp.as(SectionExpression.class));

         }
      }

      return Collections.emptyList();
   }

   private void handleSection(String parent, SectionExpression exp) {
      final SectionExpression section = exp.as(SectionExpression.class);
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
