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

import java.util.Objects;

/**
 * A parse token from a Lexer to be used in a Parser
 *
 * @author David B. Bracewell
 */
public final class ParserToken {

   /**
    * The Text.
    */
   public final String text;
   /**
    * The Type.
    */
   public final ParserTokenType type;

   /**
    * Static method for constructing tokens
    *
    * @param text The text of the token
    * @param type The type of the token
    */
   public ParserToken(String text, ParserTokenType type) {
      this.text = text;
      this.type = type;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final ParserToken other = (ParserToken) obj;
      return Objects.equals(this.text, other.text)
                && Objects.equals(this.type, other.type);
   }

   /**
    * Gets text.
    *
    * @return the text
    */
   public String getText() {
      return this.text;
   }

   /**
    * Gets type.
    *
    * @return the type
    */
   public ParserTokenType getType() {
      return this.type;
   }

   @Override
   public int hashCode() {
      return Objects.hash(text, type);
   }

   /**
    * Is instance boolean.
    *
    * @param types the types
    * @return the boolean
    */
   public boolean isInstance(ParserTokenType... types) {
      return type.isInstance(types);
   }

   public String toString() {
      return "ParserToken(text=" + this.getText() + ", type=" + this.getType() + ")";
   }
}//END OF ParseToken
