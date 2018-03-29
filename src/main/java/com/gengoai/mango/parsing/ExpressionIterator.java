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

package com.gengoai.mango.parsing;

import com.gengoai.mango.parsing.expressions.Expression;
import lombok.NonNull;

/**
 * Provides a way to iterate over expressions resulting from parsing a token stream.
 *
 * @author David B. Bracewell
 */
public final class ExpressionIterator {
   private final Grammar grammar;
   private final ParserTokenStream tokenStream;

   /**
    * Instantiates a new Expression iterator.
    *
    * @param grammar     the grammar
    * @param tokenStream the token stream
    */
   public ExpressionIterator(@NonNull Grammar grammar, @NonNull ParserTokenStream tokenStream) {
      this.grammar = grammar;
      this.tokenStream = tokenStream;
   }

   /**
    * Parses the token stream to get the next expression
    *
    * @return the next expression in the parse
    * @throws ParseException Something went wrong parsing
    */
   public Expression next() throws ParseException {
      return next(0);
   }

   /**
    * Parses the token stream to get the next expression
    *
    * @param precedence The precedence of the next prefix expression
    * @return the next expression in the parse
    * @throws ParseException Something went wrong parsing
    */
   public Expression next(int precedence) throws ParseException {
      ParserToken token;
      Expression result;

      do {
         token = tokenStream.consume();
         if (token == null) {
            return null;
         }
         result = grammar.parse(this, token);
      } while (result == null);


      //Consume things that will be skipped
      while (grammar.skip(tokenStream.lookAhead(0))) {
         tokenStream.consume();
      }

      while (precedence < grammar.precedence(tokenStream.lookAhead(0))) {
         token = tokenStream.consume();
         result = grammar.parse(this, result, token);
      }
      return result;
   }

   /**
    * Gets the token stream wrapped by this iterator
    *
    * @return the token stream
    */
   public ParserTokenStream tokenStream() {
      return tokenStream;
   }

   /**
    * Determines if there are more expressions in the parse
    *
    * @return True if more expressions can be parsed, False if not
    */
   public boolean hasNext() {
      return tokenStream.lookAhead(0) != null;
   }


}//END OF ExpressionIterator
