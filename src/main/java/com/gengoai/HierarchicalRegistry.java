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

import static com.gengoai.HierarchicalEnumValue.SEPARATOR;

/**
 * <p>Registry for hierarchical enum values</p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public final class HierarchicalRegistry<T extends HierarchicalEnumValue> extends Registry<T> {
   /**
    * The Root.
    */
   public final T ROOT;

   /**
    * Instantiates a new Registry.
    *
    * @param newInstance the new instance
    * @param owner       the owner
    * @param rootName    the root name
    */
   public HierarchicalRegistry(SerializableFunction<String, T> newInstance, Class<T> owner, String rootName) {
      super(newInstance, owner);
      this.ROOT = make(rootName);
   }


   /**
    * Instantiates a new Hierarchical registry.
    *
    * @param newInstance the new instance
    * @param owner       the owner
    */
   public HierarchicalRegistry(SerializableFunction<String, T> newInstance, Class<T> owner) {
      super(newInstance, owner);
      this.ROOT = null;
   }

   protected void checkName(String name) {
      if (!CharMatcher.LetterOrDigit.or(CharMatcher.anyOf("_$")).matchesAllOf(name)) {
         throw new IllegalArgumentException(name + " is invalid");
      }
   }

   /**
    * Ensures that the non-root parent is prepended onto the enum value name
    *
    * @param parent the parent
    * @param name   the name
    * @return the string
    */
   protected String ensureParent(T parent, String name) {
      if (parent == null || parent == ROOT) {
         return name;
      }
      if (name.startsWith(canonicalName)) {
         name = name.substring(canonicalName.length() + 1);
      }
      String prefix = parent.name() + SEPARATOR;
      return name.startsWith(prefix) ? name : parent + Character.toString(SEPARATOR) + name;
   }

   /**
    * Generates an enum value with the given parent.
    *
    * @param parent the parent
    * @param name   the name of the enum value
    * @return the enum value
    */
   public T make(T parent, String name) {
      return make(ensureParent(parent, name));
   }

   @Override
   public T make(String name) {
      return super.make(name);
   }


}//END OF HierarchicalRegistry
