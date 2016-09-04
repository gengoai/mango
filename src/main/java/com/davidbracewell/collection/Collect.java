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

package com.davidbracewell.collection;

import com.davidbracewell.collection.list.PrimitiveArrayList;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public interface Collect {

   static <T> boolean addAll(@NonNull Collection<? super T> collection, @NonNull Iterator<? extends T> iterator) {
      return addAll(collection, asIterable(iterator));
   }

   static <T> boolean addAll(@NonNull Collection<? super T> collection, @NonNull Iterable<? extends T> iterable) {
      boolean allAdded = true;
      for (T t : iterable) {
         if (!collection.add(t)) {
            allAdded = false;
         }
      }
      return allAdded;
   }

   static <T> boolean addAll(@NonNull Collection<? super T> collection, @NonNull Stream<? extends T> stream) {
      return addAll(collection, asIterable(stream.iterator()));
   }


   /**
    * Wraps an <code>array</code> as an <code>Iterable</code>
    *
    * @param <T>       the type parameter
    * @param array     The array to wrap
    * @param itemClass the type of item in the array
    * @return An Iterable wrapping the iterator.
    */
   static <T> Iterable<T> asIterable(@NonNull final Object array, @NonNull final Class<T> itemClass) {
      checkArgument(array.getClass().isArray());
      if (array.getClass().getComponentType().isPrimitive()) {
         return new PrimitiveArrayList<>(array, itemClass);
      }
      return () -> new Iterator<T>() {
         int pos = 0;

         @Override
         public boolean hasNext() {
            return pos < Array.getLength(array);
         }

         @Override
         public T next() {
            return itemClass.cast(Array.get(array, pos++));
         }
      };
   }

   /**
    * Wraps an <code>Iterator</code> as an <code>Iterable</code>
    *
    * @param <T>      the type parameter
    * @param iterator The iterator to wrap
    * @return An Iterable wrapping the iterator.
    */
   static <T> Iterable<T> asIterable(final Iterator<T> iterator) {
      if (iterator == null) {
         return () -> Cast.as(Collections.emptyIterator());
      }
      return () -> iterator;
   }


   /**
    * First optional.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @return the optional
    */
   static <T> Optional<T> getFirst(@NonNull Iterable<T> iterable) {
      return Iterators.getFirst(iterable.iterator());
   }

   static <T> Optional<T> getLast(@NonNull Iterable<T> iterable) {
      return Iterators.getLast(iterable.iterator());
   }


   /**
    * <p>Creates a default instance of the collection type. If the passed in class is an implementation then that
    * implementation is created using the no-arg constructor.</p> <table> <tr><td>Set</td><td>HashSet</td></tr>
    * <tr><td>List</td><td>ArrayList</td></tr> <tr><td>Queue</td><td>LinkedList</td></tr>
    * <tr><td>Deque</td><td>LinkedList</td></tr> <tr><td>Stack</td><td>Stack</td></tr> </table>
    *
    * @param collectionClass the collection class
    * @return t
    */
   @SneakyThrows
   static <T extends Collection> T create(Class<T> collectionClass) {
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
      }

      return collectionClass.newInstance();
   }

   /**
    * Difference collection.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the collection
    */
   static <T, C extends Collection<T>> C difference(@NonNull Supplier<C> supplier, Collection<? extends T> c1, Collection<? extends T> c2) {
      if (c1 == null && c2 == null) {
         return supplier.get();
      } else if (c1 == null) {
         return c2.stream().collect(Collectors.toCollection(supplier));
      } else if (c2 == null) {
         return c1.stream().collect(Collectors.toCollection(supplier));
      }
      return c1.stream().filter(v -> !c2.contains(v)).collect(Collectors.toCollection(supplier));
   }

   /**
    * Intersection collection.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the collection
    */
   static <T, C extends Collection<T>> C intersection(@NonNull Supplier<C> supplier, Collection<? extends T> c1, Collection<? extends T> c2) {
      if (c1 == null || c2 == null || c1.isEmpty() || c2.isEmpty()) {
         return supplier.get();
      }
      return c1.stream().filter(c2::contains).collect(Collectors.toCollection(supplier));

   }

   /**
    * Union collection.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the collection
    */
   static <T, C extends Collection<T>> C union(@NonNull Supplier<C> supplier, Collection<? extends T> c1, Collection<? extends T> c2) {
      if (c1 == null && c2 == null) {
         return supplier.get();
      } else if (c1 == null) {
         return c2.stream().collect(Collectors.toCollection(supplier));
      } else if (c2 == null) {
         return c1.stream().collect(Collectors.toCollection(supplier));
      }
      return Stream.concat(c1.stream(), c2.stream()).collect(Collectors.toCollection(supplier));
   }

   static <T> Collection<T> union(Collection<? extends T> c1, Collection<? extends T> c2) {
      return union(ArrayList::new, c1, c2);
   }

   static <T> Collection<T> difference(Collection<? extends T> c1, Collection<? extends T> c2) {
      return difference(ArrayList::new, c1, c2);
   }

   static <T> Collection<T> intersection(Collection<? extends T> c1, Collection<? extends T> c2) {
      return intersection(ArrayList::new, c1, c2);
   }


}// END OF CollectionUtils
