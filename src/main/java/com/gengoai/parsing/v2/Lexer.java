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

import com.gengoai.StringTag;
import com.gengoai.Tag;
import com.gengoai.io.resource.Resource;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public interface Lexer extends Serializable {
   Tag EOF = new StringTag("~~~EOF~~~");

   TokenStream lex(String input);

   default TokenStream lex(Resource resource) throws IOException {
      return lex(resource.readToString());
   }

   static Lexer create(TokenDef... tokens) {
      return new Lexer() {
         private static final long serialVersionUID = 1L;
         final Pattern regex = Pattern.compile(Arrays.stream(tokens)
                                                     .map(e -> String.format("(?<%s>%s)",
                                                                             e.getTag().name(),
                                                                             e.getPattern()))
                                                     .collect(Collectors.joining("|")),
                                               Pattern.MULTILINE | Pattern.DOTALL);

         @Override
         public TokenStream lex(Resource resource) throws IOException {
            return new TokenStream() {
               private ParserToken current = null;
               private ParserToken next = null;
               int lastEnd = 0;


               @Override
               public ParserToken token() {
                  return current;
               }

               @Override
               public ParserToken consume() {
                  return null;
               }

               @Override
               public ParserToken peek() {
                  return null;
               }

               private ParserToken next() {
                  ParserToken next = null;
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
                           token = new ParserToken(matcher.group(group), tags[i], varValues);
                        } else {
                           token = new ParserToken(matcher.group(group), tags[i]);
                        }
                        break;
                     }
                  }
               }

            };
         }


      };
   }


}//END OF Lexer
