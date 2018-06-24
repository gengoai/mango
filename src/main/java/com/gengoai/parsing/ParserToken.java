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

/**
 * A parse token from a Lexer to be used in a Parser
 *
 * @author David B. Bracewell
 */
public final class ParserToken {

   public final String text;
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

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof ParserToken)) return false;
      final ParserToken other = (ParserToken) o;
      final Object this$text = this.getText();
      final Object other$text = other.getText();
      if (this$text == null ? other$text != null : !this$text.equals(other$text)) return false;
      final Object this$type = this.getType();
      final Object other$type = other.getType();
      if (this$type == null ? other$type != null : !this$type.equals(other$type)) return false;
      return true;
   }

   public String getText() {
      return this.text;
   }

   public ParserTokenType getType() {
      return this.type;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $text = this.getText();
      result = result * PRIME + ($text == null ? 43 : $text.hashCode());
      final Object $type = this.getType();
      result = result * PRIME + ($type == null ? 43 : $type.hashCode());
      return result;
   }

   public String toString() {
      return "ParserToken(text=" + this.getText() + ", type=" + this.getType() + ")";
   }
}//END OF ParseToken
