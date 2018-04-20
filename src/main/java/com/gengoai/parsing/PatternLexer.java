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

import com.gengoai.io.resource.Resource;
import com.google.common.base.CharMatcher;
import lombok.NonNull;
import lombok.Value;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * <p>A lexer that uses {@link LexicalPattern}s in a longest match wins strategy.</p>
 *
 * @author David B. Bracewell
 */
public class PatternLexer implements Lexer, Serializable {
   private static final long serialVersionUID = 1L;
   private final List<LexicalEntry> patterns;

   private PatternLexer(List<LexicalEntry> patterns) {
      this.patterns = new ArrayList<>(patterns);
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
   }


   /**
    * <p>Creates a new builder to make constructing <code>PatternLexer</code> instances easier</p>
    *
    * @return the builder
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * <p>Builder class for <code>PatternLexer</code>s.</p>
    */
   public static class Builder {
      private final List<LexicalEntry> patterns = new ArrayList<>();

      /**
       * Adds a new lexical pattern
       *
       * @param tokenType the token type of the pattern
       * @param pattern   the pattern to add
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, @NonNull LexicalPattern pattern) {
         patterns.add(new LexicalEntry(pattern, tokenType));
         return this;
      }

      /**
       * Adds a character literal pattern
       *
       * @param tokenType the token type of the pattern
       * @param character the character associated with the token type
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, char character) {
         patterns.add(new LexicalEntry(LexicalPattern.charLiteral(character), tokenType));
         return this;
      }


      /**
       * Adds a <code>CharPredicate</code> pattern.
       *
       * @param tokenType the token type  of the pattern
       * @param predicate the predicate associated with the pattern
       * @return the builder
       */
      public Builder add(@NonNull ParserTokenType tokenType, @NonNull CharMatcher predicate) {
         patterns.add(new LexicalEntry(LexicalPattern.charPredicate(predicate), tokenType));
         return this;
      }


      /**
       * Creates a new <code>PatternLexer</code>
       *
       * @return the new spattern lexer
       */
      public PatternLexer build() {
         return new PatternLexer(patterns);
      }

   }

}//END OF PatternLexer
