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

package com.gengoai.parsing.handlers;


import com.gengoai.parsing.ExpressionIterator;
import com.gengoai.parsing.ParseException;
import com.gengoai.parsing.ParserToken;
import com.gengoai.parsing.expressions.Expression;

import java.io.Serializable;

/**
 * <p>Abstract based handler for infix operations.</p>
 *
 * @author David B. Bracewell
 */
public abstract class InfixHandler implements ParserHandler {
   private static final long serialVersionUID = 1L;
   private final int precedence;

   /**
    * Default constructor
    *
    * @param precedence The precedence of the handler
    */
   protected InfixHandler(int precedence) {
      this.precedence = precedence;
   }

   @Override
   public final int precedence() {
      return precedence;
   }

   /**
    * Constructs an expression from the current token and the parser.
    *
    * @param expressionIterator The parser
    * @param left               The expression that takes place before the infix operator
    * @param token              The token that caused the handler to be invoked
    * @return An expression representing the parse
    * @throws ParseException An error occurred parsing
    */
   public abstract Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException;


   @FunctionalInterface
   public interface InfixHAndlerFunction extends Serializable {

      Expression apply(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException;
   }

   public static InfixHandler infix(final int precedence, final InfixHAndlerFunction function) {
      return new InfixHandler(precedence) {
         private static final long serialVersionUID = 1L;

         @Override
         public Expression parse(ExpressionIterator expressionIterator, Expression left, ParserToken token) throws ParseException {
            return function.apply(expressionIterator, left, token);
         }
      };
   }

} //END OF InfixHandler
