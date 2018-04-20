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

import com.gengoai.Regex;
import com.gengoai.conversion.Cast;
import com.gengoai.io.resource.Resource;
import com.gengoai.string.StringUtils;
import com.gengoai.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * An implementation of a lexer that uses regular expressions to tokenize a String into a
 * <code>ParserTokenStream</code>.
 *
 * @author David B. Bracewell
 */
public class RegularExpressionLexer implements Lexer, Serializable {
   private static final long serialVersionUID = 1L;
   private final Set<ParserTokenType> types;
   private final Pattern lexer;

   /**
    * Default Constructor
    *
    * @param lexicalPatterns The lexical patterns to capture
    */
   public RegularExpressionLexer(@NonNull Collection<Tuple2<ParserTokenType, String>> lexicalPatterns) {
      Preconditions.checkArgument(!lexicalPatterns.isEmpty());
      types = new HashSet<>();
      StringBuilder pattern = new StringBuilder();
      for (Tuple2<ParserTokenType, String> Tuple2 : lexicalPatterns) {
         types.add(Tuple2.getKey());
         pattern.append(String.format("|(?<%s>%s)", Tuple2.getKey().toString(), Tuple2.getValue()));
      }
      this.lexer = Pattern.compile("(" + pattern.toString().substring(1) + ")", Pattern.MULTILINE | Pattern.DOTALL);
   }

   @Override
   public ParserTokenStream lex(final Resource input) throws IOException {
      return new ParserTokenStream(new MatchIterator(input.readToString()));
   }


   private class MatchIterator implements Iterator<ParserToken> {
      final Matcher matcher;
      final String input;
      int lastEnd = 0;
      Boolean hasNext;

      private MatchIterator(String input) {
         this.matcher = lexer.matcher(input);
         this.hasNext = matcher.find();
         this.input = input;
      }

      private boolean advance() {
         if (hasNext == null) {
            hasNext = matcher.find();
         }
         return hasNext;
      }


      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public ParserToken next() {
         if (!advance()) {
            throw new NoSuchElementException();
         }
         hasNext = null;
         ParserToken token = null;
         int endOffset = lastEnd;
         int startOffset = 0;

         for (ParserTokenType type : types) {
            if (matcher.group(type.toString()) != null) {
               endOffset = matcher.end(type.toString());
               startOffset = matcher.start(type.toString());
               List<String> groups = new ArrayList<>();
               for (int i = 2; i < matcher.groupCount(); i++) {
                  if (matcher.group(i) != null && !matcher.group(i).equals(matcher.group())) {
                     groups.add(matcher.group(i));
                  }
               }
               token = new ParserToken(matcher.group(), type);
               break;
            }
         }

         if (token == null) {
            throw new IllegalStateException("Error in parsing {" + input + "}");
         }

         if (startOffset > 0) {
            String s = input.substring(lastEnd, startOffset);
            if (!StringUtils.isNullOrBlank(s)) {
               throw new IllegalStateException("Error in parsing {" + input + "} unparsed region: " + s);
            }
         }


         lastEnd = endOffset;
         return token;
      }

   }

   /**
    * @return A Builder to create a lexer
    */
   public static Builder builder() {
      return new Builder();
   }

   /**
    * A builder for constructing Lexers
    *
    * @author David B. Bracewell
    */
   public static class Builder {

      private final Set<Tuple2<ParserTokenType, String>> lexicalItems = new LinkedHashSet<>();

      /**
       * Adds a pattern to capture a given type
       *
       * @param type    The token type
       * @param pattern The pattern that captures the given token type (Overrides the pattern on the TokenType)
       * @return The LexerBuilder
       */
      public Builder add(ParserTokenType type, String pattern) {
         lexicalItems.add(Tuple2.of(type, pattern));
         return this;
      }

      /**
       * Adds a pattern to capture a given type
       *
       * @param type    The token type
       * @param pattern The pattern that captures the given token type (Overrides the pattern on the TokenType)
       * @return The LexerBuilder
       */
      public Builder add(ParserTokenType type, Regex pattern) {
         lexicalItems.add(Tuple2.of(type, pattern.toString()));
         return this;
      }

      /**
       * Adds a pattern to capture a given type
       *
       * @param type The token type
       * @return The LexerBuilder
       */
      public Builder add(ParserTokenType type) {
         Preconditions.checkArgument(type instanceof HasLexicalPattern, "Type must define its own lexical pattern");
         lexicalItems.add(Tuple2.of(type, Cast.<HasLexicalPattern>as(type).lexicalPattern()));
         return this;
      }


      /**
       * @return A lexer
       */
      public RegularExpressionLexer build() {
         return new RegularExpressionLexer(lexicalItems);
      }


   }//END OF Lexer$Builder


}//END OF Lexer
