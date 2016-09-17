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

package com.davidbracewell.parsing;

import com.davidbracewell.parsing.expressions.Expression;
import com.davidbracewell.parsing.handlers.InfixHandler;
import com.davidbracewell.parsing.handlers.PrefixHandler;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A grammar representing the rules for parsing within a <code>Parser</code>. Rules in the form of
 * <code>ParserHandler</code>s are defined for each <code>TokenType</code>. There are two main types of
 * <code>ParserHandler</code>. The first is <code>PrefixHandler</code> which takes care of prefix operators and the
 * second is <code>InfixHandler</code> which handles infix operators.</p>
 * <p>The grammar provides methods for determining if a given token should be parsed using an infix or prefix handler
 * as well as the precedence of its associated type.</p>
 *
 * @author David B. Bracewell
 */
public class Grammar implements Serializable {
   private static final long serialVersionUID = 1L;

   private final Map<ParserTokenType, PrefixHandler> prefixHandlers = new HashMap<>();
   private final Map<ParserTokenType, InfixHandler> infixHandlers = new HashMap<>();
   private final PrefixHandler prefixSkipHandler;

   protected Grammar() {
      this(null);
   }

   protected Grammar(PrefixHandler prefixSkipHandler) {
      this.prefixSkipHandler = prefixSkipHandler;
   }


   /**
    * Registers a prefix handler
    *
    * @param type    The token type that causes the prefix handler to parse
    * @param handler The prefix handler
    * @return This grammar (for builder pattern)
    */
   public Grammar register(@NonNull ParserTokenType type, @NonNull PrefixHandler handler) {
      prefixHandlers.put(type, handler);
      return this;
   }

   /**
    * Registers an infix handler
    *
    * @param type    The token type that causes the infix handler to parse
    * @param handler The infix handler
    * @return This grammar (for builder pattern)
    */
   public Grammar register(@NonNull ParserTokenType type, @NonNull InfixHandler handler) {
      infixHandlers.put(type, handler);
      return this;
   }

   /**
    * Determines if the token can be parsed with an infix handler
    *
    * @param token The token to check
    * @return True if it has an associated infix handler, false otherwise
    */
   public boolean isInfix(ParserToken token) {
      return token != null && infixHandlers.containsKey(token.type);
   }

   /**
    * Determines if the token can be parsed with a prefix handler
    *
    * @param token The token to check
    * @return True if it has an associated prefix handler, false otherwise
    */
   public boolean isPrefix(ParserToken token) {
      return token != null && prefixHandlers.containsKey(token.type);
   }

   /**
    * Parses a prefix expression
    *
    * @param expressionIterator The parser to use
    * @param token              The token causing the prefix parse
    * @return A parsed expression
    * @throws ParseException Something went wrong parsing.
    */
   public Expression parse(ExpressionIterator expressionIterator, @NonNull ParserToken token) throws ParseException {
      PrefixHandler handler = prefixHandlers.containsKey(token.type)
                              ? prefixHandlers.get(token.type)
                              : prefixSkipHandler;
      if (handler == null) {
         throw new ParseException("No PrefixHandler registered for token type " + token.type);
      }
      return handler.parse(expressionIterator, token);
   }

   /**
    * Parses an infix or postfix expression
    *
    * @param expressionIterator The parser to use
    * @param left               The expression to the left of the operator
    * @param token              The token causing the prefix parse
    * @return A parsed expression
    * @throws ParseException Something went wrong parsing.
    */
   public Expression parse(ExpressionIterator expressionIterator, Expression left, @NonNull ParserToken token) throws ParseException {
      if (isInfix(token)) {
         return infixHandlers.get(token.type).parse(expressionIterator, left, token);
      }
      return left;
   }

   /**
    * Gets the precedence of the associated infix handler.
    *
    * @param token The token whose handler we want precedence for
    * @return the precedence of the associated infix handler or 0 if there is none.
    */
   public int precedence(ParserToken token) {
      if (isInfix(token)) {
         return infixHandlers.get(token.type).precedence();
      }
      return 0;
   }


}//END OF PrattParserGrammar
