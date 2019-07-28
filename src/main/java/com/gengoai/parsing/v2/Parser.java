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


import com.gengoai.Tag;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

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

   @Override
   public ParserToken consume() {
      return tokenStream.consume();
   }

   /**
    * <p>Parses the given resource and evaluates it with the given evaluator. Requires that the parse result in a
    * single expression.</p>
    *
    * @param <O>       the return type of the evaluator
    * @param evaluator the evaluator to use for transforming expressions
    * @return the single return values from the evaluator
    */
   public <O> O evaluate(Evaluator<? extends O> evaluator) throws ParseException {
      try {
         return evaluator.eval(parseExpression());
      } catch (ParseException e) {
         throw e;
      } catch (Exception e2) {
         throw new ParseException(e2);
      }
   }

   public <O> List<O> evaluateAll(Evaluator<? extends O> evaluator) throws ParseException {
      List<O> evaluationResults = new ArrayList<>();
      while (tokenStream.hasNext()) {
         try {
            evaluationResults.add(evaluator.eval(parseExpression()));
         } catch (ParseException e) {
            throw e;
         } catch (Exception e2) {
            throw new ParseException(e2);
         }
      }
      return evaluationResults;
   }

   public List<Expression> parseAllExpressions() throws ParseException {
      List<Expression> expressions = new ArrayList<>();
      while (tokenStream.hasNext()) {
         expressions.add(parseExpression());
      }
      return expressions;
   }

   public List<Expression> parseExpressionList(Tag endOfList, Tag separator) throws ParseException {
      List<Expression> objExpressions = new ArrayList<>();
      boolean isFirst = true;
      while (!peek().isInstance(TokenStream.EOF, endOfList)) {
         if (!isFirst && separator != null) {
            consume(separator);
         }
         isFirst = false;
         objExpressions.add(parseExpression());
      }
      if (peek().isInstance(TokenStream.EOF)) {
         throw new ParseException("Parsing Error: Premature EOF");
      }
      consume(endOfList);
      return objExpressions;
   }

   public Expression parseExpression() throws ParseException {
      return parseExpression(0);
   }

   public Expression parseExpression(ParserToken precedence) throws ParseException {
      return parseExpression(grammar.precedenceOf(precedence));
   }

   public Expression parseExpression(int precedence) throws ParseException {
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

   @Override
   public ParserToken peek() {
      return tokenStream.peek();
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


}//END OF Parser
