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

package com.gengoai.parsing;

import com.gengoai.parsing.expressions.Expression;
import com.gengoai.parsing.handlers.InfixHandler;
import com.gengoai.parsing.handlers.PrefixHandler;
import com.gengoai.parsing.handlers.PrefixSkipHandler;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>A grammar representing the rules for parsing. Rules are defined using <code>ParserHandler</code>s, which are
 * associated with individual <code>ParserTokenType</code>s. There are two main types of handlers, prefix and infix. The
 * <code>PrefixHandler</code> takes care of prefix operators and the <code>InfixHandler</code> handles infix and postfix
 * operators.</p>
 *
 * <p>By default a grammar will throw a <code>ParseException</code> when it encounters a token type that it does not
 * know how to handle. Grammars can be set to instead ignore these tokens.</p>
 *
 * @author David B. Bracewell
 */
public class Grammar implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<ParserTokenType, PrefixHandler> prefixHandlers = new HashMap<>();
   private final Map<ParserTokenType, InfixHandler> infixHandlers = new HashMap<>();
   private final PrefixHandler prefixSkipHandler;

   /**
    * Instantiates a new Grammar which will throw a <code>ParseException</code> when encountering token types it cannot
    * handle.
    */
   public Grammar() {
      this(false);
   }

   /**
    * Instantiates a new Grammar.
    *
    * @param skipNonRegisteredTokenTypes When true the grammar will ignore token types is cannot handle and when false
    *                                    it will throw a <code>ParseException</code>.
    */
   public Grammar(boolean skipNonRegisteredTokenTypes) {
      this.prefixSkipHandler = skipNonRegisteredTokenTypes ? new PrefixSkipHandler() : null;
   }


   /**
    * Registers a prefix handler
    *
    * @param type    The token type that causes the prefix handler to parse
    * @param handler The prefix handler
    * @return This grammar (for fluent pattern)
    */
   public Grammar register(@NonNull ParserTokenType type, @NonNull PrefixHandler handler) {
      prefixHandlers.put(type, handler);
      return this;
   }

   /**
    * Registers a token type as being ignored, i.e. the grammar will skip it when seen.
    *
    * @param type the token type to ignore
    * @return This grammar (for fluent pattern)
    */
   public Grammar registerSkip(@NonNull ParserTokenType type) {
      return register(type, new PrefixSkipHandler());
   }

   /**
    * Registers an infix handler
    *
    * @param type    The token type that causes the infix handler to parse
    * @param handler The infix handler
    * @return This grammar (for fluent pattern)
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

   /**
    * Determines if the given the token should be skipped or not
    *
    * @param token the token to check
    * @return True if the token should be skipped, false if it should be parsed.
    */
   public boolean skip(ParserToken token) {
      if (token == null) {
         return false;
      }
      if (isPrefix(token)) {
         if (prefixHandlers.containsKey(token.getType())) {
            return prefixHandlers.get(token.getType()) instanceof PrefixSkipHandler;
         } else {
            return prefixSkipHandler != null;
         }
      }
      return false;
   }


}//END OF PrattParserGrammar
