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

package com.gengoai.enums;

import com.gengoai.Tag;
import com.gengoai.conversion.Cast;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionUtils;
import com.gengoai.reflection.Types;

import java.io.Serializable;
import java.lang.reflect.Type;

/**
 * @author David B. Bracewell
 */
public class EnumValue implements Tag, Serializable, Cloneable, JsonSerializable {
   private static final long serialVersionUID = 1L;
   private final String fullName;
   private final String name;

   public static EnumValue fromJson(JsonEntry entry, Type... types) {
      String name = null;
      if (entry.isArray()) {
         name = entry.elementIterator().next().getAsString();
      }
      if (entry.isString()) {
         name = entry.getAsString();
      }
      Class<?> clazz = null;
      if (types != null && types.length > 1) {
         clazz = Types.asClass(types[1]);
      } else {
         clazz = ReflectionUtils.getClassForNameQuietly(name.substring(0, name.lastIndexOf('.')));
      }
      try {
         return Cast.as(Reflect.onClass(clazz)
                               .getMethod("make")
                               .invoke(null, name));
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Instantiates a new enum value.
    *
    * @param name the name of the enum value
    */
   protected EnumValue(String name) {
      this.name = name;
      this.fullName = getClass().getCanonicalName() + "." + this.name;
   }

   /**
    * <p>Retrieves the canonical name of the enum value, which is the canonical name of the enum class and the
    * specified name of the enum value.</p>
    *
    * @return the canonical name of the enum value
    */
   public final String canonicalName() {
      return fullName;
   }

   @Override
   protected final Object clone() throws CloneNotSupportedException {
      super.clone();
      return this;
   }

   @Override
   public final boolean equals(Object obj) {
      return obj instanceof EnumValue && canonicalName().equals(
         Cast.<EnumValue>as(obj).canonicalName());
   }

   @Override
   public final int hashCode() {
      return canonicalName().hashCode();
   }

   @Override
   public boolean isInstance(Tag value) {
      return this.equals(value);
   }

   @Override
   public String name() {
      return name;
   }

   @Override
   public JsonEntry toJson() {
      return JsonEntry.from(canonicalName());
   }

   @Override
   public final String toString() {
      return name;
   }

}//END OF EnumValue
