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

package com.gengoai.parsing;

import com.gengoai.io.resource.Resource;
import com.gengoai.string.Strings;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Regex lexer.
 *
 * @author David B. Bracewell
 */
public class RegexLexer implements Lexer, Serializable {
   private static final Pattern VARIABLE_PLACEHOLDER = Pattern.compile("\\(\\?<>");
   private static final long serialVersionUID = 1L;
   private final ParserTokenType[] tags;
   private final String[][] vars;
   private final String[] groups;
   private final Pattern regex;


   /**
    * Instantiates a new Regex lexer.
    *
    * @param definitions the definitions
    */
   public RegexLexer(TokenDef... definitions) {
      this.tags = new ParserTokenType[definitions.length];
      this.vars = new String[definitions.length][];
      this.groups = new String[definitions.length];
      StringBuilder pattern = new StringBuilder();
      for (int i = 0; i < definitions.length; i++) {
         if (definitions[i] == null) {
            throw new NullPointerException();
         }
         tags[i] = definitions[i].getTag();
         groups[i] = "G" + i;


         String p = definitions[i].getPattern();
         int v = 0;
         Matcher m = VARIABLE_PLACEHOLDER.matcher(p);
         while (m.find()) {
            p = p.replaceFirst(VARIABLE_PLACEHOLDER.pattern(), "(?<" + groups[i] + "V" + v + ">");
            v++;
         }
         vars[i] = v == 0 ? null : new String[v];
         if (v > 0) {
            for (int j = 0; j < v; j++) {
               vars[i][j] = groups[i] + "V" + j;
            }
         }

         pattern.append("|(?<")
                .append(groups[i])
                .append(">")
                .append(p)
                .append(")");
      }
      this.regex = Pattern.compile("(" + pattern.toString().substring(1) + ")", Pattern.MULTILINE | Pattern.DOTALL);
   }

   @Override
   public ParserTokenStream lex(Resource input) throws IOException {
      return new ParserTokenStream(new MatchIterator(input.readToString()));
   }


   private class MatchIterator implements Iterator<ParserToken> {
      private final Matcher matcher;
      private final String input;
      private Boolean hasNext = null;
      int lastEnd = 0;


      /**
       * Instantiates a new Match iterator.
       *
       * @param input the input
       */
      public MatchIterator(String input) {
         this.input = input;
         this.matcher = regex.matcher(input);
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
         for (int i = 0; i < groups.length; i++) {
            String group = groups[i];
            if (matcher.group(group) != null) {
               endOffset = matcher.end(group);
               startOffset = matcher.start(group);
               if (vars[i] != null) {
                  List<String> varValues = new ArrayList<>();
                  for (int j = 0; j < vars[i].length; j++) {
                     varValues.add(matcher.group(vars[i][j]));
                  }
                  token = new ParserToken(matcher.group(group), tags[i], startOffset, varValues);
               } else {
                  token = new ParserToken(matcher.group(group), tags[i], startOffset);
               }
               break;
            }
         }
         if (token == null) {
            throw new IllegalStateException(String.format("Parsing Error: start=%s, content='%s'", startOffset,
                                                          Strings.abbreviate(input.substring(startOffset), 10)));
         }
         if (startOffset > 0) {
            String s = input.substring(lastEnd, startOffset);
            if (Strings.isNotNullOrBlank(s)) {
               throw new IllegalStateException(String.format("Parsing Error: start=%s, content='%s'", startOffset,
                                                             Strings.abbreviate(input.substring(startOffset), 10)));
            }
         }
         lastEnd = endOffset;
         return token;
      }
   }


}//END OF RegexLexer
