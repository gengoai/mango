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

package com.davidbracewell;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collection;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import static com.davidbracewell.collection.Streams.asStream;

/**
 * <p>Mimics {@link String#intern()} with any object using heap memory. Uses weak references so that objects no longer
 * in memory can be reclaimed.</p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public final class Interner<E> implements Serializable {
   private static final long serialVersionUID = 1L;
   private volatile WeakHashMap<E, E> map = new WeakHashMap<>();

   /**
    * <p>Adds or gets the canonical version of the incoming object.</p>
    *
    * @param object The object to intern
    * @return The interned value
    * @throws NullPointerException if the object is null
    */
   public synchronized E intern(@NonNull final E object) {
      return map.computeIfAbsent(object, o -> object);
   }

   /**
    * <p>Interns all elements in the given iterable.</p>
    *
    * @param iterable the items of elements to intern.
    * @return the interned elements.
    * @throws NullPointerException if the collection is null
    */
   public Collection<E> internAll(@NonNull Iterable<? extends E> iterable) {
      return asStream(iterable).map(this::intern).collect(Collectors.toList());
   }

   @Override
   public String toString() {
      return "Interner{size=" + size() + "}";
   }

   /**
    * <p>The number of items that have been interned.</p>
    *
    * @return the number of items that have been interned.
    */
   public int size() {
      return map.size();
   }

}//END OF Interner
