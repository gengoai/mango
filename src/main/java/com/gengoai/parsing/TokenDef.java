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

import java.io.Serializable;

/**
 * The type Token def.
 *
 * @author David B. Bracewell
 */
public class TokenDef implements Serializable {
   private static final long serialVersionUID = 1L;
   private final String pattern;
   private final ParserTokenType tag;

   /**
    * Instantiates a new Token def.
    *
    * @param tag     the tag
    * @param pattern the pattern
    */
   public TokenDef(ParserTokenType tag, String pattern) {
      this.tag = tag;
      this.pattern = pattern;
   }

   /**
    * Instantiates a new Token def.
    *
    * @param tag     the tag
    * @param pattern the pattern
    */
   public TokenDef(TokenDef tag, String pattern) {
      this.tag = tag.getTag();
      this.pattern = pattern;
   }

   /**
    * Define token def.
    *
    * @param tag     the tag
    * @param pattern the pattern
    * @return the token def
    */
   public static TokenDef define(ParserTokenType tag, String pattern) {
      return new TokenDef(tag, pattern);
   }

   /**
    * Define token def.
    *
    * @param tag     the tag
    * @param pattern the pattern
    * @return the token def
    */
   public static TokenDef define(TokenDef tag, String pattern) {
      return new TokenDef(tag, pattern);
   }

   /**
    * Gets pattern.
    *
    * @param namedGroup the named group
    * @return the pattern
    */
   public String getPattern(String namedGroup) {
      if (pattern.contains("(?<>")) {
         return pattern.replace("(?<>", "(?<" + namedGroup + ">");
      }
      return pattern;
   }

   /**
    * Gets pattern.
    *
    * @return the pattern
    */
   public String getPattern() {
      return pattern;
   }

   /**
    * Gets tag.
    *
    * @return the tag
    */
   public ParserTokenType getTag() {
      return tag;
   }
}//END OF TokenDef
