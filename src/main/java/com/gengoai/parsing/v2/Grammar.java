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
 * <p>A grammar representing the rules for parsing. Rules are defined using <code>ParserHandler</code>s, which are
 * associated with individual <code>Tag</code>s. There are two main types of handlers, prefix and postfix. The
 * <code>PrefixHandler</code> takes care of prefix operators and the <code>PostfixHandler</code> handles infix and
 * postfix operators.</p>
 *
 * <p>By default a grammar will throw a <code>ParseException</code> when it encounters a token type that it does not
 * know how to handle. Grammars can be set to instead ignore these tokens.</p>
 *
 * @author David B. Bracewell
 */
public abstract class Grammar implements Serializable {
   private final Map<Tag, PostfixHandler> postfixHandlerMap = new HashMap<>();
   private final Map<Tag, Integer> precedenceMap = new HashMap<>();
   private final Map<Tag, PrefixHandler> prefixHandlerMap = new HashMap<>();
   private final Set<Tag> skipTags = new HashSet<>();

   /**
    * Gets postfix handler.
    *
    * @param token the token
    * @return the postfix handler
    */
   public Optional<PostfixHandler> getPostfixHandler(ParserToken token) {
      return getPostfixHandler(token.getType());
   }

   /**
    * Gets postfix handler.
    *
    * @param tokenType the token type
    * @return the postfix handler
    */
   public Optional<PostfixHandler> getPostfixHandler(Tag tokenType) {
      return Optional.ofNullable(postfixHandlerMap.get(tokenType));
   }

   /**
    * Gets prefix handler.
    *
    * @param token the token
    * @return the prefix handler
    */
   public Optional<PrefixHandler> getPrefixHandler(ParserToken token) {
      return getPrefixHandler(token.getType());
   }

   /**
    * Gets prefix handler.
    *
    * @param tokenType the token type
    * @return the prefix handler
    */
   public Optional<PrefixHandler> getPrefixHandler(Tag tokenType) {
      return Optional.ofNullable(prefixHandlerMap.get(tokenType));
   }

   /**
    * Handler.
    *
    * @param type    the type
    * @param handler the handler
    */
   protected void handler(Tag type, ParserHandler handler) {
      handler(type, handler, 0);
   }

   /**
    * Handler.
    *
    * @param type       the type
    * @param handler    the handler
    * @param precedence the precedence
    */
   protected void handler(Tag type, ParserHandler handler, int precedence) {
      if (handler instanceof PostfixHandler) {
         postfixHandlerMap.put(type, Cast.as(handler));
         precedenceMap.put(type, Math.max(precedence, 1));
      } else {
         prefixHandlerMap.put(type, Cast.as(handler));
      }
   }

   /**
    * Is ignored boolean.
    *
    * @param token the token
    * @return the boolean
    */
   public boolean isIgnored(ParserToken token) {
      return isIgnored(token.getType());
   }

   /**
    * Is ignored boolean.
    *
    * @param tokenType the token type
    * @return the boolean
    */
   public boolean isIgnored(Tag tokenType) {
      return skipTags.contains(tokenType);
   }

   /**
    * Postfix.
    *
    * @param type    the type
    * @param handler the handler
    */
   protected void postfix(Tag type, PostfixHandler handler) {
      handler(type, handler, 1);
   }

   /**
    * Postfix.
    *
    * @param type       the type
    * @param handler    the handler
    * @param precedence the precedence
    */
   protected void postfix(Tag type, PostfixHandler handler, int precedence) {
      handler(type, handler, precedence);
   }

   /**
    * Precedence of int.
    *
    * @param tokenType the token type
    * @return the int
    */
   public int precedenceOf(Tag tokenType) {
      if (tokenType.isInstance(TokenStream.EOF)) {
         return Integer.MIN_VALUE;
      }
      return precedenceMap.getOrDefault(tokenType, 0);
   }

   /**
    * Precedence of int.
    *
    * @param token the token
    * @return the int
    */
   public int precedenceOf(ParserToken token) {
      if (token.equals(TokenStream.EOF_TOKEN)) {
         return Integer.MIN_VALUE;
      }
      return precedenceOf(token.getType());
   }

   /**
    * Prefix.
    *
    * @param type    the type
    * @param handler the handler
    */
   protected void prefix(Tag type, PrefixHandler handler) {
      handler(type, handler, 0);
   }

   /**
    * Prefix.
    *
    * @param type       the type
    * @param handler    the handler
    * @param precedence the precedence
    */
   protected void prefix(Tag type, PrefixHandler handler, int precedence) {
      handler(type, handler, precedence);
   }

   /**
    * Skip.
    *
    * @param type the type
    */
   protected void skip(Tag type) {
      skipTags.add(type);
   }

}//END OF Grammar
