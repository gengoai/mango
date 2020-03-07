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

package com.gengoai;

import com.gengoai.annotation.JsonHandler;
import com.gengoai.json.JsonEntry;
import com.gengoai.string.Strings;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * <p>A tag which is represented as a string. Care must be taken in that different string variations will represent
 * different tags.</p>
 *
 * @author David B. Bracewell
 */
@JsonHandler(StringTag.Marshaller.class)
@EqualsAndHashCode(callSuper = false)
public class StringTag implements Tag, Serializable {
   private static final long serialVersionUID = 1L;
   private final String tag;

   /**
    * Default Constructor
    *
    * @param tag The tag name
    */
   public StringTag(String tag) {
      Validation.checkArgument(!Strings.isNullOrBlank(tag), "Tag must not be null or blank.");
      this.tag = tag;
   }

   @Override
   public boolean isInstance(Tag tag) {
      return equals(tag);
   }

   @Override
   public String name() {
      return toString();
   }

   @Override
   public String toString() {
      return tag;
   }

   /**
    * StringTag Json Marshaller
    */
   public static class Marshaller extends com.gengoai.json.JsonMarshaller<StringTag> {

      @Override
      protected StringTag deserialize(JsonEntry entry, Type type) {
         return new StringTag(entry.getAsString());
      }

      @Override
      protected JsonEntry serialize(StringTag stringTag, Type type) {
         return JsonEntry.from(stringTag.tag);
      }
   }

}//END OF StringTag
