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
import com.gengoai.io.Resources;
import com.gengoai.parsing.v2.expressions.BinaryOperatorExpression;
import com.gengoai.parsing.v2.expressions.ValueExpression;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

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
         prefix(ConfigScanner.ConfigTokenType.STRING, ValueExpression.STRING_HANDLER);
         prefix(ConfigScanner.ConfigTokenType.KEY, ValueExpression.STRING_HANDLER);
         prefix(ConfigScanner.ConfigTokenType.BOOLEAN, ValueExpression.NUMERIC_HANDLER);

//         prefix(ConfigScanner.ConfigTokenType.BEGIN_OBJECT,
//                (parser, token) -> {
//                   List<Expression> objExpressions = new ArrayList<>();
//                   while (!parser.peek().isInstance(TokenStream.EOF,
//                                                    ConfigScanner.ConfigTokenType.END_OBJECT)) {
//                      objExpressions.add(parser.parseExpression(token));
//                   }
//                   if (parser.peek().isInstance(TokenStream.EOF)) {
//                      throw new ParseException("Parsing Error: Premature EOF");
//                   }
//                   parser.consume(ConfigScanner.ConfigTokenType.END_OBJECT);
//                   System.out.println("\tObject: " + objExpressions);
//                   return new StringValueExpression(token);
//                });

         postfix(ConfigScanner.ConfigTokenType.BEGIN_OBJECT,
                 (parser, token, left) -> {
                    List<Expression> objExpressions = new ArrayList<>();
                    while (!parser.peek().isInstance(TokenStream.EOF,
                                                     ConfigScanner.ConfigTokenType.END_OBJECT)) {
                       objExpressions.add(parser.parseExpression());
                    }
                    if (parser.peek().isInstance(TokenStream.EOF)) {
                       throw new ParseException("Parsing Error: Premature EOF");
                    }
                    parser.consume(ConfigScanner.ConfigTokenType.END_OBJECT);
                    return left;
                 }, 1);

         postfix(ConfigScanner.ConfigTokenType.EQUAL_PROPERTY,
                 (parser, token, left) -> {
                    Expression right = parser.parseExpression(token);
                    return new BinaryOperatorExpression(token, left, right);
                 }, 5);
      }
   };


   public static void main(String[] args) throws Exception {
      ParserGenerator pg = parserGenerator(MSON_GRAMMAR, MSON_LEXER);
      Parser p = pg.parse(Resources.from("/home/ik/prj/mango/src/test/resources/com/gengoai/other.conf"));
      while (p.hasNext()) {
         System.out.println(p.parseExpression());
      }
   }


}//END OF MSON_TEST
