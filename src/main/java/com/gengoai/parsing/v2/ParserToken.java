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

import java.util.Arrays;
import java.util.Objects;

/**
 * The type Parser token.
 *
 * @author David B. Bracewell
 */
public class ParserToken {
   private final int end;
   private final int start;
   private final String text;
   private final Tag type;
   private final String[] variables;

   /**
    * Instantiates a new Parser token.
    *
    * @param type  the type
    * @param text  the text
    * @param start the start
    * @param end   the end
    */
   public ParserToken(Tag type, String text, int start, int end) {
      this(type, text, start, end, new String[0]);
   }

   /**
    * Instantiates a new Parser token.
    *
    * @param type  the type
    * @param text  the text
    * @param start the start
    */
   public ParserToken(Tag type, String text, int start) {
      this(type, text, start, start+text.length(), new String[0]);
   }

   /**
    * Instantiates a new Parser token.
    *
    * @param type      the type
    * @param text      the text
    * @param start     the start
    * @param end       the end
    * @param variables the variables
    */
   public ParserToken(Tag type, String text, int start, int end, String[] variables) {
      this.type = type;
      this.text = text;
      this.start = start;
      this.end = end;
      this.variables = variables;
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ParserToken)) return false;
      ParserToken that = (ParserToken) o;
      return Objects.equals(type, that.type) &&
                Objects.equals(text, that.text) &&
                Arrays.equals(variables, that.variables);
   }

   /**
    * Gets end.
    *
    * @return the end
    */
   public int getEnd() {
      return end;
   }

   /**
    * Gets start.
    *
    * @return the start
    */
   public int getStart() {
      return start;
   }

   /**
    * Gets text.
    *
    * @return the text
    */
   public String getText() {
      return text;
   }

   /**
    * Gets type.
    *
    * @return the type
    */
   public Tag getType() {
      return type;
   }

   /**
    * Gets type.
    *
    * @param <T>    the type parameter
    * @param tClass the t class
    * @return the type
    */
   public <T extends Tag> T getType(Class<T> tClass) {
      return Cast.as(type, tClass);
   }

   /**
    * Gets variable.
    *
    * @param index the index
    * @return the variable
    */
   public String getVariable(int index) {
      return variables[index];
   }

   /**
    * Gets variable count.
    *
    * @return the variable count
    */
   public int getVariableCount() {
      return variables.length;
   }

   @Override
   public int hashCode() {
      int result = Objects.hash(type, text);
      result = 31 * result + Arrays.hashCode(variables);
      return result;
   }

   /**
    * Is instance boolean.
    *
    * @param tags the tags
    * @return the boolean
    */
   public boolean isInstance(Tag... tags) {
      return type.isInstance(tags);
   }

   @Override
   public String toString() {
      return "ParserToken{" +
                "type=" + type +
                ", text='" + text + '\'' +
                ", variables=" + Arrays.toString(variables) +
                '}';
   }
}//END OF ParserToken
