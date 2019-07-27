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


import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class Parser implements TokenStream, Serializable {
   private final Grammar grammar;
   private final TokenStream tokenStream;

   public Parser(Grammar grammar,
                 TokenStream tokenStream
                ) {
      this.grammar = grammar;
      this.tokenStream = tokenStream;
   }


   public Expression parseExpression() {
      return parseExpression(0);
   }

   public Expression parseExpression(ParserToken precedence) {
      return parseExpression(grammar.precedenceOf(precedence));
   }

   public Expression parseExpression(int precedence) {
      ParserToken token = consume();
      Expression left = grammar.getPrefixHandler(token)
                               .orElseThrow(() -> new IllegalStateException("Parsing Error: Unable to parse '" +
                                                                               token().getType() +
                                                                               "', no prefix handler registered"))
                               .handle(this, token);

      skip();
      while (precedence < grammar.precedenceOf(peek())) {
         token = consume();
         left = grammar.getPostfixHandler(token)
                       .orElseThrow(() -> new IllegalStateException("Parsing Error: Unable to parse '" +
                                                                       token().getType() +
                                                                       "', no postfix handler registered"))
                       .handle(this, token, left);
         skip();
      }
      return left;
   }

   private void skip() {
      while (grammar.isIgnored(tokenStream.peek())) {
         tokenStream.consume();
      }
   }


   @Override
   public ParserToken token() {
      return tokenStream.token();
   }

   @Override
   public ParserToken consume() {
      return tokenStream.consume();
   }

   @Override
   public ParserToken peek() {
      return tokenStream.peek();
   }


   public static enum MathTypes implements TokenDef {
      NUMBER("\\d+"),
      ADD("\\+"),
      SUBTRACT("\\-"),
      MULTIPLY("\\*"),
      DIVIDE("\\/");

      private final String pattern;

      MathTypes(String pattern) {
         this.pattern = pattern;
      }

      @Override
      public String getPattern() {
         return pattern;
      }
   }

   private static class NumericExpression extends Expression {
      public final double number;

      public NumericExpression(double number) {
         super(MathTypes.NUMBER);
         this.number = number;
      }

      public NumericExpression(ParserToken token) {
         super(token.getType());
         this.number = Double.parseDouble(token.getText());
      }
   }

   public static class MathGrammar extends Grammar {
      {
         prefix(MathTypes.NUMBER, (p, t) -> new NumericExpression(t));
         postfix(MathTypes.ADD, (p, t, l) -> {
            Expression right = p.parseExpression(t);
            return new NumericExpression(
               l.as(NumericExpression.class).number + right.as(NumericExpression.class).number
            );
         });
         postfix(MathTypes.SUBTRACT, (p, t, l) -> {
            Expression right = p.parseExpression(t);
            return new NumericExpression(
               l.as(NumericExpression.class).number - right.as(NumericExpression.class).number
            );
         });
         postfix(MathTypes.MULTIPLY, (p, t, l) -> {
            Expression right = p.parseExpression(t);
            return new NumericExpression(
               l.as(NumericExpression.class).number * right.as(NumericExpression.class).number
            );
         }, 2);
         postfix(MathTypes.DIVIDE, (p, t, l) -> {
            Expression right = p.parseExpression(t);
            return new NumericExpression(
               l.as(NumericExpression.class).number / right.as(NumericExpression.class).number
            );
         }, 2);
      }
   }

   public static void main(String[] args) throws Exception {
      final Lexer lexer = Lexer.create(MathTypes.values());

      Parser p = new Parser(new MathGrammar(), lexer.lex("23 + 4 * 2 - 1 + 5 * 2 / 2"));
      NumericExpression ne = p.parseExpression().as(NumericExpression.class);
      System.out.println(ne.number);
   }


}//END OF Parser
