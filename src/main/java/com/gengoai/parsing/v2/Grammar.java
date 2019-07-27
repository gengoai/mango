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

import com.gengoai.Tag;
import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public abstract class Grammar implements Serializable {
   private final Map<Tag, PostfixHandler> postfixHandlerMap = new HashMap<>();
   private final Map<Tag, Integer> precedenceMap = new HashMap<>();
   private final Map<Tag, PrefixHandler> prefixHandlerMap = new HashMap<>();
   private final Set<Tag> skipTags = new HashSet<>();


   protected void prefix(Tag type, PrefixHandler handler) {
      handler(type, handler, 0);
   }

   protected void prefix(Tag type, PrefixHandler handler, int precedence) {
      handler(type, handler, precedence);
   }

   protected void postfix(Tag type, PostfixHandler handler) {
      handler(type, handler, 1);
   }

   protected void postfix(Tag type, PostfixHandler handler, int precedence) {
      handler(type, handler, precedence);
   }

   protected void handler(Tag type, ParserHandler handler) {
      handler(type, handler, 0);
   }

   protected void handler(Tag type, ParserHandler handler, int precedence) {
      if (handler instanceof PostfixHandler) {
         postfixHandlerMap.put(type, Cast.as(handler));
         precedenceMap.put(type, Math.max(precedence, 1));
      } else {
         prefixHandlerMap.put(type, Cast.as(handler));
      }
   }

   protected void skip(Tag type) {
      skipTags.add(type);
   }


   public Optional<PostfixHandler> getPostfixHandler(ParserToken token) {
      return getPostfixHandler(token.getType());
   }

   public Optional<PostfixHandler> getPostfixHandler(Tag tokenType) {
      return Optional.ofNullable(postfixHandlerMap.get(tokenType));
   }

   public Optional<PrefixHandler> getPrefixHandler(ParserToken token) {
      return getPrefixHandler(token.getType());
   }

   public Optional<PrefixHandler> getPrefixHandler(Tag tokenType) {
      return Optional.ofNullable(prefixHandlerMap.get(tokenType));
   }

   public boolean isIgnored(ParserToken token) {
      return isIgnored(token.getType());
   }

   public boolean isIgnored(Tag tokenType) {
      return skipTags.contains(tokenType);
   }

   public int precedenceOf(Tag tokenType) {
      if (tokenType.isInstance(TokenStream.EOF)) {
         return Integer.MIN_VALUE;
      }
      return precedenceMap.getOrDefault(tokenType, 0);
   }

   public int precedenceOf(ParserToken token) {
      if (token.equals(TokenStream.EOF_TOKEN)) {
         return Integer.MIN_VALUE;
      }
      return precedenceOf(token.getType());
   }

}//END OF Grammar
