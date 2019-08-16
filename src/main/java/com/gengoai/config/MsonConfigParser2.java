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

import com.gengoai.io.resource.Resource;
import com.gengoai.parsing.v2.*;
import com.gengoai.parsing.v2.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.v2.expressions.ListExpression;
import com.gengoai.parsing.v2.expressions.PrefixOperatorExpression;
import com.gengoai.parsing.v2.expressions.ValueExpression;

import java.io.IOException;
import java.io.StringReader;

import static com.gengoai.config.ConfigTokenType.*;

/**
 * @author David B. Bracewell
 */
public class MsonConfigParser2 {
   private static final Lexer MSON_LEXER = input -> {
      ConfigScanner scanner = new ConfigScanner(new StringReader(input));
      return new AbstractTokenStream() {
         @Override
         protected ParserToken next() {
            try {
               ParserToken pt = scanner.next();
               if (pt == null) {
                  return TokenStream.EOF_TOKEN;
               }
               return pt;
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
      };
   };
   private static final Grammar MSON_GRAMMAR = new Grammar() {
      {
         skip(COMMENT);
         prefix(STRING, ValueExpression.STRING_HANDLER);
         prefix(KEY, ValueExpression.STRING_HANDLER);
         prefix(BEAN, ValueExpression.STRING_HANDLER);
         prefix(BOOLEAN, ValueExpression.BOOLEAN_HANDLER);
         prefix(NULL, ValueExpression.NULL_HANDLER);
         prefix(VALUE_SEPARATOR, ValueExpression.STRING_HANDLER);
         postfix(BEGIN_OBJECT, BinaryOperatorExpression.listValueHandler(END_OBJECT, null));
         prefix(BEGIN_OBJECT, ListExpression.handler(BEGIN_OBJECT, END_OBJECT, VALUE_SEPARATOR));
         postfix(EQUAL_PROPERTY, BinaryOperatorExpression.HANDLER, 5);
         postfix(APPEND_PROPERTY, BinaryOperatorExpression.HANDLER, 5);
         postfix(KEY_VALUE_SEPARATOR, BinaryOperatorExpression.HANDLER, 5);
         prefix(IMPORT, PrefixOperatorExpression.HANDLER);
         prefix(BEGIN_ARRAY, ListExpression.handler(BEGIN_ARRAY, END_ARRAY, VALUE_SEPARATOR));
      }
   };

   public static void parseResource(Resource resource) throws IOException, ParseException {
      new Parser(MSON_GRAMMAR, MSON_LEXER.lex(resource)).evaluateAll(new MsonEvaluator(resource.descriptor()));
   }

}//END OF MsonConfigParser2
