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

import com.gengoai.function.SerializableFunction;
import com.gengoai.string.CharMatcher;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.gengoai.Validation.notNullOrBlank;

/**
 * The type Registry.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class Registry<T extends EnumValue> implements Serializable {
   private static final long serialVersionUID = 1L;
   protected final Map<String, T> registry = new ConcurrentHashMap<>();
   protected final SerializableFunction<String, T> newInstance;
   protected final String canonicalName;

   /**
    * Instantiates a new Registry.
    *
    * @param owner the owner
    */
   public Registry(SerializableFunction<String, T> newInstance, Class<T> owner) {
      this.newInstance = newInstance;
      this.canonicalName = owner.getCanonicalName();
   }

   protected void checkName(String name) {
      if (!CharMatcher.LetterOrDigit.or(CharMatcher.anyOf("_")).matchesAllOf(name)) {
         throw new IllegalArgumentException(name + " is invalid");
      }
   }

   /**
    * Make t.
    *
    * @param name the name
    * @return the t
    */
   public T make(String name) {
      if (name.startsWith(canonicalName)) {
         name = name.substring(canonicalName.length() + 1);
      }
      String norm = normalize(name);
      checkName(norm);
      return registry.computeIfAbsent(norm, newInstance);
   }

   /**
    * Value of t.
    *
    * @param name the name
    * @return the t
    */
   public T valueOf(String name) {
      return registry.get(normalize(name));
   }

   /**
    * Normalize string.
    *
    * @param name the name
    * @return the string
    */
   String normalize(String name) {
      notNullOrBlank(name, "Name cannot be null or blank");
      StringBuilder toReturn = new StringBuilder();
      boolean previousSpace = false;
      for (char c : name.toCharArray()) {
         if (Character.isWhitespace(c)) {
            if (!previousSpace) {
               toReturn.append('_');
            }
            previousSpace = true;
         } else {
            previousSpace = false;
            toReturn.append(Character.toUpperCase(c));
         }
      }
      return toReturn.toString();
   }

   /**
    * Values collection.
    *
    * @return the collection
    */
   public Collection<T> values() {
      return Collections.unmodifiableCollection(registry.values());
   }

}//END OF Registry
