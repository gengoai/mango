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
 * @author David B. Bracewell
 */
public class RegexLexer implements Lexer {
   private static final long serialVersionUID = 1L;
   private static final Pattern VARIABLE_PLACEHOLDER = Pattern.compile("\\(\\?<>");

   private final TokenDef[] definitions;
   private final String[] groups;
   private final String[][] vars;
   private final Pattern regex;

   public RegexLexer(TokenDef... definitions) {
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
      this.regex = Pattern.compile("(" + pattern.toString() + ")", Pattern.MULTILINE | Pattern.DOTALL);
   }

   @Override
   public TokenStream lex(String input) {
      return new TokenStream() {
         private final Matcher matcher = regex.matcher(input);
         private ParserToken current;
         private ParserToken next;
         private int lastEnd;

         @Override
         public ParserToken token() {
            return current;
         }

         @Override
         public ParserToken consume() {
            if (next != null) {
               current = next;
               next = null;
            } else {
               current = next();
            }
            return current;
         }

         @Override
         public ParserToken peek() {
            return null;
         }

         private ParserToken next() {
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
                     token = new ParserToken(definitions[0].getTag(),
                                             matcher.group(group),
                                             startOffset,
                                             endOffset,
                                             varValues.toArray(new String[0]));
                  } else {
                     token = new ParserToken(definitions[0].getTag(),
                                             matcher.group(group),
                                             startOffset,
                                             endOffset);
                  }
                  break;
               }
            }
            if (token == null) {
               throw new IllegalStateException("Error in parsing {" + input + "}");
            }
            if (startOffset > 0) {
               String s = input.substring(lastEnd, startOffset);
               if (Strings.isNotNullOrBlank(s)) {
                  throw new IllegalStateException("Error in parsing {" + input + "} unparsed region: " + s);
               }
            }
            lastEnd = endOffset;
            return token;
         }

      };
   }
}//END OF RegexLexer
