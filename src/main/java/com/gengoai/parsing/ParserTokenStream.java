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

import java.util.*;

/**
 * <p>Represents a stream of tokens from a Lexer.</p>
 *
 * @author David B. Bracewell
 */
public class ParserTokenStream {

   private final Iterator<ParserToken> tokenIterator;
   private final LinkedList<ParserToken> buffer;
   private ParserToken current;

   /**
    * Default Constructor
    *
    * @param iterator The token iterator
    */
   public ParserTokenStream(Iterator<ParserToken> iterator) {
      this.tokenIterator = iterator == null ? Collections.emptyIterator() : iterator;
      this.buffer = new LinkedList<>();
   }


   /**
    * @return The next token in the stream or null if there is not one
    */
   public ParserToken consume() {
      lookAhead(0);
      current = buffer.isEmpty() ? null : buffer.removeFirst();
      return current;
   }

   /**
    * Consumes the next token and ensures it is of a given type
    *
    * @param expectedType The expected token type
    * @return The next token
    * @throws ParseException The next token is not of the expected type
    */
   public ParserToken consume(ParserTokenType expectedType) throws ParseException {
      ParserToken next = consume();
      if (next == null || !expectedType.isInstance(next.type)) {
         throw new ParseException("Expected the next token to be of type " + expectedType + ", but found " + (next == null
                                                                                                              ? null
                                                                                                              : next.type));
      }
      return next;
   }

   /**
    * @return The current token in the stream
    */
   public ParserToken current() {
      return current;
   }

   /**
    * Looks ahead in the token stream
    *
    * @param distance The number of tokens to look ahead
    * @return The token a given distance from the current position in the stream or null if end of stream
    */
   public ParserToken lookAhead(int distance) {
      while (distance >= buffer.size() && tokenIterator.hasNext()) {
         buffer.addLast(tokenIterator.next());
      }
      return buffer.size() > distance ? buffer.getLast() : null;
   }


   /**
    * Looks ahead to determine the type of the token a given distance from the front of the stream.
    *
    * @param distance The number of tokens from the front of the stream
    * @return The token type
    */
   public ParserTokenType lookAheadType(int distance) {
      ParserToken token = lookAhead(distance);
      return token == null ? null : token.type;
   }

   /**
    * Determines if the token at the front of the stream is a match for a given type without consuming the token.
    *
    * @param rhs The token type
    * @return True if there is a match, False otherwise
    */
   public boolean nonConsumingMatch(ParserTokenType rhs) {
      ParserToken token = lookAhead(0);
      return token != null && token.type.isInstance(rhs);
   }

   /**
    * Consumes all tokens until the token at the front of the stream is of one of the given types.
    *
    * @param types The types to cause the stream to stop consuming
    * @return The list of tokens that were consumed.
    */
   public List<ParserToken> consumeUntil(ParserTokenType... types) {
      List<ParserToken> tokens = new ArrayList<>();
      while (lookAheadType(0) != null && !isOfType(lookAheadType(0), types)) {
         tokens.add(consume());
      }
      return tokens;
   }

   /**
    * Consumes all tokens until the token at the front of the stream is of one of the given types.
    *
    * @param types The types to cause the stream to stop consuming
    * @return The list of tokens that were consumed.
    */
   public List<ParserToken> consumeWhile(ParserTokenType... types) {
      List<ParserToken> tokens = new ArrayList<>();
      while (isOfType(lookAheadType(0), types)) {
         tokens.add(consume());
      }
      return tokens;
   }

   private boolean isOfType(ParserTokenType current, ParserTokenType[] types) {
      if (current == null || types == null) {
         return false;
      }
      for (ParserTokenType type : types) {
         if (type.isInstance(current)) {
            return true;
         }
      }
      return false;
   }


   /**
    * Determines if the next token in the stream is of an expected type and consumes it if it is
    *
    * @param expected The expected type
    * @return True if the next token is of the expected type, false otherwise
    */
   public boolean match(ParserTokenType expected) {
      ParserToken next = lookAhead(0);
      if (next == null || !next.type.isInstance(expected)) {
         return false;
      }
      consume();
      return true;
   }


}//END OF TokenStream
