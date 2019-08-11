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

import com.gengoai.string.Strings;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The type Regex lexer.
 *
 * @author David B. Bracewell
 */
class RegexLexer implements Lexer {
   private static final long serialVersionUID = 1L;
   private static final Pattern VARIABLE_PLACEHOLDER = Pattern.compile("\\(\\?<>");

   private final TokenDef[] definitions;
   private final String[] groups;
   private final String[][] vars;
   private final Pattern regex;

   /**
    * Instantiates a new Regex lexer.
    *
    * @param definitions the definitions
    */
   protected RegexLexer(TokenDef... definitions) {
      this.definitions = definitions;
      this.groups = new String[definitions.length];
      this.vars = new String[definitions.length][];

      StringBuilder pattern = new StringBuilder();
      for (int i = 0; i < definitions.length; i++) {
         groups[i] = "V" + i;
         int v = 0;
         String p = definitions[i].getPattern();
         Matcher m = VARIABLE_PLACEHOLDER.matcher(p);
         while (m.find()) {
            p = p.replaceFirst(VARIABLE_PLACEHOLDER.pattern(), "(?<" + groups[i] + "V" + v + ">");
            v++;
         }
         vars[i] = v == 0 ? null : new String[v];
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
      this.regex = Pattern.compile("(?:" + pattern.toString().substring(1) + ")", Pattern.MULTILINE | Pattern.DOTALL);
   }

   @Override
   public TokenStream lex(String input) {
      return new AbstractTokenStream() {
         private static final long serialVersionUID = 1L;
         private final Matcher matcher = regex.matcher(input);
         private int lastEnd;


         @Override
         protected ParserToken next() {
            ParserToken token = null;

            if (lastEnd >= input.length()) {
               return EOF_TOKEN;
            }

            int endOffset = lastEnd;
            int startOffset = 0;

            if (!matcher.find(lastEnd)) {
               if (Strings.isNullOrBlank(input.substring(endOffset))) {
                  validate(input, endOffset, input.length());
               }
            }

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
                     token = new ParserToken(definitions[i],
                                             matcher.group(group),
                                             startOffset,
                                             endOffset,
                                             varValues.toArray(new String[0]));
                  } else {
                     token = new ParserToken(definitions[i],
                                             matcher.group(group),
                                             startOffset,
                                             endOffset);
                  }
                  break;
               }
            }

            if (token == null) {
               throw new IllegalStateException(
                  "Parsing Error: Unmatched token starting at {" + input.substring(lastEnd) + "}");
            }

            if (startOffset > 0) {
               validate(input, lastEnd, startOffset);
            }

            lastEnd = endOffset;
            return token;
         }

      };


   }

   private static void validate(String input, int startOffset, int endOffSet) {
      String s = input.substring(startOffset, endOffSet);
      if (Strings.isNotNullOrBlank(s)) {
         throw new IllegalStateException("Parsing Error: Unmatched region '" + s + "'");
      }
   }

}//END OF RegexLexer
