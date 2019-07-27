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

import com.gengoai.Validation;

import java.io.Serializable;
import java.util.Objects;

/**
 * Defines a type associated with a token.
 *
 * @author David B. Bracewell
 */
final class ParserTokenTypeImpl implements ParserTokenType, Serializable {
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

   @Override
   public String name() {
      return name;
   }

   @Override
   public String toString() {
      return name;
   }

   @Override
   public int hashCode() {
      return Objects.hash(name);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final ParserTokenTypeImpl other = (ParserTokenTypeImpl) obj;
      return Objects.equals(this.name, other.name);
   }
}//END OF TokenType
