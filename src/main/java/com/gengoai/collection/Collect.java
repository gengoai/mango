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

package com.gengoai.collection;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.*;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public final class Collect {


   private Collect() {
      throw new IllegalAccessError();
   }

   /**
    * <p>Creates a default instance of a collection type. If the passed in class is an implementation then that
    * implementation is created using the no-arg constructor. Interfaces (e.g. Set and List) have default
    * implementations assigned and returned. </p>
    *
    * @param <T>             the type parameter
    * @param collectionClass the collection class
    * @return t t
    */
   public static <T extends Collection> T create(Class<T> collectionClass) {
      if (collectionClass == null) {
         return null;
      }
      if (Set.class.equals(collectionClass)) {
         return Cast.as(new HashSet<>());
      } else if (List.class.equals(collectionClass)) {
         return Cast.as(new ArrayList<>());
      } else if (Queue.class.equals(collectionClass)) {
         return Cast.as(new LinkedList<>());
      } else if (Deque.class.equals(collectionClass)) {
         return Cast.as(new LinkedList<>());
      } else if (Stack.class.equals(collectionClass)) {
         return Cast.as(new Stack<>());
      } else if (NavigableSet.class.equals(collectionClass)) {
         return Cast.as(new TreeSet<>());
      }
      try {
         return collectionClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   public static <T> Collection<T> asCollection(Iterable<? extends T> iterable) {
      return new IterableCollection<>(iterable);
   }

   public static <T> Collection<T> asCollection(Iterator<? extends T> iterator) {
      return asCollection(Iterables.asIterable(iterator));
   }

   private static class IterableCollection<E> extends AbstractCollection<E> implements Serializable {
      private static final long serialVersionUID = 1L;
      private final Iterable<? extends E> iterable;

      private IterableCollection(Iterable<? extends E> iterable) {
         this.iterable = iterable;
      }

      @Override
      public Iterator<E> iterator() {
         return Cast.cast(iterable.iterator());
      }

      @Override
      public int size() {
         return Iterables.size(iterable);
      }
   }

}// END OF CollectionUtils
