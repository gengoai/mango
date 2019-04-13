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
 * @author David B. Bracewell
 */
public class HierarchicalRegistry<T extends HierarchicalEnumValue> extends Registry<T> {
   public final T ROOT;

   /**
    * Instantiates a new Registry.
    *
    * @param owner the owner
    */
   public HierarchicalRegistry(SerializableFunction<String, T> newInstance, Class<T> owner, String rootName) {
      super(newInstance, owner);
      this.ROOT = make(rootName);
   }


   protected String ensureParent(T parent, String name) {
      if (parent == null || parent == ROOT) {
         return name;
      }
      if (name.startsWith(canonicalName)) {
         name = name.substring(canonicalName.length() + 1);
      }
      return name.startsWith(parent.name()) ? name : parent + Character.toString(SEPARATOR) + name;
   }

   public HierarchicalRegistry(SerializableFunction<String, T> newInstance, Class<T> owner) {
      super(newInstance, owner);
      this.ROOT = null;
   }

   @Override
   public T make(String name) {
      return super.make(name);
   }

   public T make(T parent, String name) {
      return make(ensureParent(parent, name));
   }

   protected void checkName(String name) {
      if (!CharMatcher.LetterOrDigit.or(CharMatcher.anyOf("_$")).matchesAllOf(name)) {
         throw new IllegalArgumentException(name + " is invalid");
      }
   }


}//END OF HierarchicalRegistry
