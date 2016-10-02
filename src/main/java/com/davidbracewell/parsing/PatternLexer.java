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

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.string.CharPredicate;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * The type Pattern lexer.
 *
 * @author David B. Bracewell
 */
public class PatternLexer implements Lexer {
   private final List<LexicalEntry> patterns = new ArrayList<>();
   private final CharPredicate reserved;

   /**
    * Instantiates a new Pattern lexer.
    *
    * @param patterns the patterns
    * @param reserved the reserved
    */
   protected PatternLexer(List<LexicalEntry> patterns, CharPredicate reserved) {
      this.patterns.addAll(patterns);
      this.reserved = reserved;
   }

   @Override
   public ParserTokenStream lex(@NonNull Resource input) throws IOException {
      return new ParserTokenStream(new TokenIterator(input.readToString()));
   }


   private class TokenIterator implements Iterator<ParserToken> {
      private final String content;
      private int position = 0;
      private ParserToken next = null;

      private TokenIterator(String content) {
         this.content = content;
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      private boolean advance() {
         while (next == null && position < content.length()) {
            ParserTokenType longestType = null;
            int longest = 0;
            for (LexicalEntry entry : patterns) {
               int match = entry.getPattern().match(content, position);

               if (!entry.isQuoted() && match > 1) {
                  int r = reserved.indexIn(content, position);
                  if (r >= 0 && r <= position + match) {
                     match = r - position;
                  }
               }
               if (match > longest) {
                  longest = match;
                  longestType = entry.getTokenType();
               }
            }

            if (longest > 0) {
               next = new ParserToken(content.substring(position, position + longest), longestType);
               position = position + longest;
            } else {
               if (!Character.isWhitespace(content.charAt(position))) {
                  throw new IllegalArgumentException("Error in parsing unparsed region at character position="
                                                        + position + " [" + content.charAt(position) + "]");
               }
               position++;
            }
         }
         return next != null;
      }

      @Override
      public ParserToken next() {
         if (!advance()) {
            throw new NoSuchElementException();
         }
         ParserToken toReturn = next;
         next = null;
         return toReturn;
      }
   }

   @Value
   private static class LexicalEntry {
      LexicalPattern pattern;
      ParserTokenType tokenType;
      boolean isQuoted;
   }


   /**
    * Builder builder.
    *
    * @return the builder
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * The type Builder.
    */
   public static class Builder {
      private final List<LexicalEntry> patterns = new ArrayList<>();
      private CharPredicate reserved = CharPredicate.NONE;
      private String reservedString = "";

      /**
       * Add builder.
       *
       * @param tokenType the token type
       * @param pattern   the pattern
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, @NonNull LexicalPattern pattern) {
         patterns.add(new LexicalEntry(pattern, tokenType, false));
         return this;
      }

      /**
       * Add builder.
       *
       * @param tokenType the token type
       * @param character the character
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, char character) {
         patterns.add(new LexicalEntry(LexicalPattern.charLiteral(character), tokenType, false));
         return this;
      }

      /**
       * Add reserved builder.
       *
       * @param tokenType the token type
       * @param character the character
       * @return the builder
       */
      public Builder addReserved(@NonNull ParserTokenType tokenType, char character) {
         patterns.add(new LexicalEntry(LexicalPattern.charLiteral(character), tokenType, false));
         this.reservedString += character;
         return this;
      }

      /**
       * Sets quote character.
       *
       * @param character the character
       * @return the quote character
       */
      public Builder setQuoteCharacter(char character) {
         this.reservedString += character;
         return this;
      }

      /**
       * Add builder.
       *
       * @param tokenType the token type
       * @param predicate the predicate
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, @NonNull CharPredicate predicate) {
         patterns.add(new LexicalEntry(LexicalPattern.charPredicate(predicate), tokenType, false));
         return this;
      }

      /**
       * Reserved chars builder.
       *
       * @param predicate the predicate
       * @return the builder
       */
      public Builder reservedChars(@NonNull CharPredicate predicate) {
         this.reserved = predicate;
         return this;
      }

      /**
       * Add quoted builder.
       *
       * @param tokenType the token type
       * @param pattern   the pattern
       * @return the builder
       */
      public Builder addQuoted(@NonNull ParserTokenType tokenType, @NonNull LexicalPattern pattern) {
         patterns.add(new LexicalEntry(pattern, tokenType, true));
         return this;
      }

      /**
       * Build pattern lexer.
       *
       * @return the pattern lexer
       */
      public PatternLexer build() {
         return new PatternLexer(patterns, CharPredicate.anyOf(reservedString));
      }

   }

}//END OF PatternLexer
