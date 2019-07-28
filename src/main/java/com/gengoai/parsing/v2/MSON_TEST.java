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

package com.gengoai.parsing.v2;

import com.gengoai.config.ConfigScanner;
import com.gengoai.config.ConfigScanner.ConfigTokenType;
import com.gengoai.io.Resources;
import com.gengoai.parsing.v2.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.v2.expressions.ListExpression;
import com.gengoai.parsing.v2.expressions.PrefixOperatorExpression;
import com.gengoai.parsing.v2.expressions.ValueExpression;

import java.io.StringReader;

import static com.gengoai.parsing.v2.ParserGenerator.parserGenerator;

/**
 * @author David B. Bracewell
 */
public class MSON_TEST {

   private static final Lexer MSON_LEXER = input -> {
      ConfigScanner scanner = new ConfigScanner(new StringReader(input));
      return new AbstractTokenStream() {
         @Override
         protected ParserToken next() {
            try {
               com.gengoai.parsing.ParserToken pt = scanner.next();
               if (pt == null) {
                  return TokenStream.EOF_TOKEN;
               }
               return new ParserToken(pt.type, pt.text, pt.start, pt.end, pt.variables);
            } catch (Exception e) {
               throw new RuntimeException(e);
            }
         }
      };
   };


   private static final Grammar MSON_GRAMMAR = new Grammar() {
      {
         prefix(ConfigTokenType.STRING, ValueExpression.STRING_HANDLER);
         prefix(ConfigTokenType.KEY, ValueExpression.STRING_HANDLER);
         prefix(ConfigTokenType.BEAN, ValueExpression.STRING_HANDLER);
         prefix(ConfigTokenType.BOOLEAN, ValueExpression.BOOLEAN_HANDLER);
         prefix(ConfigTokenType.NULL, ValueExpression.NULL_HANDLER);
         prefix(ConfigTokenType.VALUE_SEPARATOR, ValueExpression.STRING_HANDLER);
         postfix(ConfigTokenType.BEGIN_OBJECT,
                 (parser, token, left) -> new BinaryOperatorExpression(token, left, new ListExpression(token.getType(),
                                                                                                       parser.parseExpressionList(
                                                                                                          ConfigTokenType.END_OBJECT,
                                                                                                          null))), 1);
         prefix(ConfigTokenType.BEGIN_OBJECT, ListExpression.handler(ConfigTokenType.BEGIN_OBJECT,
                                                                     ConfigTokenType.END_OBJECT,
                                                                     ConfigTokenType.VALUE_SEPARATOR));
         postfix(ConfigTokenType.EQUAL_PROPERTY, BinaryOperatorExpression.HANDLER, 5);
         postfix(ConfigTokenType.APPEND_PROPERTY, BinaryOperatorExpression.HANDLER, 5);
         postfix(ConfigTokenType.KEY_VALUE_SEPARATOR, BinaryOperatorExpression.HANDLER, 5);
         prefix(ConfigTokenType.IMPORT, PrefixOperatorExpression.HANDLER);
         prefix(ConfigTokenType.BEGIN_ARRAY, ListExpression.handler(ConfigTokenType.BEGIN_ARRAY,
                                                                    ConfigTokenType.END_ARRAY,
                                                                    ConfigTokenType.VALUE_SEPARATOR));
      }

   };


   private static final Evaluator<Boolean> MSON_EVAL = new Evaluator<Boolean>() {
      {
         $(BinaryOperatorExpression.class,
           ConfigTokenType.EQUAL_PROPERTY,
           boe -> {
              eval(boe.getKey());
              System.out.print(" = ");
              eval(boe.getValue());
              System.out.println();
              return true;
           });

         $(ValueExpression.class, v -> {
            System.out.print(v.value);
            return true;
         });

         $(ListExpression.class,
           ConfigTokenType.BEGIN_OBJECT,
           l -> {
              for (int i = 0; i < l.numberOfExpressions(); i++) {
                 System.out.print("\t");
                 eval(l.get(i));
                 System.out.print(", ");
              }
              return true;
           });
      }
   };

   public static void main(String[] args) throws Exception {
      ParserGenerator pg = parserGenerator(MSON_GRAMMAR, MSON_LEXER);
      Parser p = pg.parse(Resources.from("/home/ik/prj/mango/src/test/resources/com/gengoai/other.conf"));
      p.evaluateAll(MSON_EVAL);
   }


}//END OF MSON_TEST
