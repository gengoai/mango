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

package com.gengoai.collection.index;


import java.util.Arrays;

import static com.gengoai.Validation.notNull;

/**
 * Common methods for reading counters from structured files, creating synchronized and unmodifiable wrappers.
 *
 * @author David B. Bracewell
 */
public final class Indexes {

   private Indexes() {
      throw new IllegalAccessError();
   }

   /**
    * Creates a new index using the given set of elements
    *
    * @param <TYPE>   the component type of the index
    * @param elements the elements to initialize the index with
    * @return A new index containing the given elements
    */
   @SafeVarargs
   public static <TYPE> Index<TYPE> indexOf(TYPE... elements) {
      return elements == null ? new HashMapIndex<>() : indexOf(Arrays.asList(elements));
   }

   /**
    * Creates a new index using the given set of elements
    *
    * @param <TYPE>   the component type of the index
    * @param elements the elements to initialize the index with
    * @return A new index containing the given elements
    */
   public static <TYPE> Index<TYPE> indexOf(Iterable<TYPE> elements) {
      Index<TYPE> index = new HashMapIndex<>();
      index.addAll(elements);
      return index;
   }

   /**
    * <p>Wraps an index making each method call synchronized.</p>
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @param index  The index to wrap
    * @return the synchronized index
    */
   public static <TYPE> Index<TYPE> synchronizedIndex(Index<TYPE> index) {
      return new SynchronizedIndex<>(notNull(index));
   }

   /**
    * <p>Creates a new index with each method call synchronized.</p>
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @return the synchronized index
    */
   public static <TYPE> Index<TYPE> synchronizedIndex() {
      return new SynchronizedIndex<>(new HashMapIndex<>());
   }


   /**
    * Wraps an index making its entries unmodifiable.
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @param index  The index to wrap
    * @return the unmodifiable index
    */
   public static <TYPE> Index<TYPE> unmodifiableIndex(final Index<TYPE> index) {
      return new UnmodifiableIndex<>(notNull(index));
   }


}//END OF Indexes
