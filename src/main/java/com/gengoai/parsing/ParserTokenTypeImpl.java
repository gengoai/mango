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

import com.gengoai.Tag;
import com.gengoai.Validation;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Defines a type associated with a token.
 *
 * @author David B. Bracewell
 */
public final class ParserTokenTypeImpl implements ParserTokenType, Serializable {
   private static final long serialVersionUID = 1L;
   private final String name;

   /**
    * Instantiates a new Parser token type.
    *
    * @param name the name
    */
   public ParserTokenTypeImpl(String name) {
      Validation.notNullOrBlank(name);
      this.name = name.toUpperCase().trim();
   }

   /**
    * Token type parser token type.
    *
    * @param name the name
    * @return the parser token type
    */
   public static ParserTokenTypeImpl tokenType(String name) {
      return new ParserTokenTypeImpl(name);
   }

   @Override
   public boolean isInstance(Tag tokenType) {
      return tokenType instanceof ParserTokenTypeImpl && name().equals(tokenType.name());
   }

   public boolean isInstance(TokenDef tokenDef) {
      return tokenDef != null && isInstance(tokenDef.getTag());
   }

   public boolean isInstance(TokenDef... tokenDefs) {
      return Arrays.stream(tokenDefs).anyMatch(this::isInstance);
   }

   @Override
   public String name() {
      return name;
   }

}//END OF TokenType
