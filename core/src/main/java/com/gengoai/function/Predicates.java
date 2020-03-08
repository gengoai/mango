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

package com.gengoai.function;

import java.util.Collection;
import java.util.Set;

import static com.gengoai.Validation.notNull;
import static com.gengoai.collection.Sets.hashSetOf;

/**
 * @author David B. Bracewell
 */
public final class Predicates {

   private Predicates() {
      throw new IllegalAccessError();
   }


   public static <T> SerializablePredicate<T> isIn(Set<T> set) {
      notNull(set);
      return set::contains;
   }

   public static <T> SerializablePredicate<T> isIn(Collection<T> collection) {
      notNull(collection);
      return collection::contains;
   }

   @SafeVarargs
   public static <T> SerializablePredicate<T> isIn(T... array) {
      if (array == null || array.length == 0) {
         return t -> false;
      }
      return isIn(hashSetOf(array));
   }

}//END OF Predicates
