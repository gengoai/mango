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

import com.gengoai.string.CharMatcher;

import java.util.function.Function;

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
   public HierarchicalRegistry(Class<T> owner, String rootName, Function<String, T> creator) {
      super(owner);
      this.ROOT = make(rootName, creator);
   }

   public HierarchicalRegistry(Class<T> owner) {
      super(owner);
      this.ROOT = null;
   }

   protected void checkName(String name) {
      if (!CharMatcher.LetterOrDigit.or(CharMatcher.anyOf("_$")).matchesAllOf(name)) {
         throw new IllegalArgumentException(name + " is invalid");
      }
   }
}//END OF HierarchicalRegistry
