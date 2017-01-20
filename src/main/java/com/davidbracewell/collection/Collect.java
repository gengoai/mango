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
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Ordering;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.davidbracewell.collection.Streams.asStream;
import static com.davidbracewell.tuple.Tuples.$;
import static com.google.common.base.Preconditions.checkArgument;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public interface Collect {

   /**
    * Wraps an <code>array</code> as an <code>Iterable</code>
    *
    * @param <T>       the component type of the array
    * @param array     The array to wrap
    * @param itemClass the component type of the array
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
            Preconditions.checkElementIndex(pos, Array.getLength(array));
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
   static <T> Iterable<T> asIterable(final Iterator<? extends T> iterator) {
      if (iterator == null) {
         return () -> Cast.as(Collections.emptyIterator());
      }
      return () -> Cast.as(iterator);
   }

   /**
    * <p>Creates a default instance of a collection type. If the passed in class is an implementation then that
    * implementation is created using the no-arg constructor. Interfaces (e.g. Set and List) have default
    * implementations assigned and returned. </p>
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
      } else if (NavigableSet.class.equals(collectionClass)) {
         return Cast.as(new TreeSet<>());
      }
      return collectionClass.newInstance();
   }

   /**
    * <p>Returns the first item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the first element in the iterable or null if none
    */
   static <T> Optional<T> getFirst(@NonNull Iterable<? extends T> iterable) {
      return Optional.ofNullable(Iterables.getFirst(iterable, null));
   }

   /**
    * <p>Returns the last item in an iterable. </p>
    *
    * @param <T>      the type of element in the iterable
    * @param iterable the iterable
    * @return An optional containing the last element in the iterable or null if none
    */
   static <T> Optional<T> getLast(@NonNull Iterable<T> iterable) {
      return Optional.ofNullable(Iterables.getLast(iterable, null));
   }

   static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(@NonNull Iterable<? extends E> iterable) {
      return maxIndexAndValue(iterable, Ordering.natural());
   }

   static <E extends Comparable<? super E>> Tuple2<Integer, E> maxIndexAndValue(@NonNull Iterable<? extends E> iterable, @NonNull Comparator<? super E> comparator) {
      int index = -1;
      E max = null;
      int i = 0;
      for (E e : iterable) {
         if (max == null || comparator.compare(e,max) > 0) {
            max = e;
            index = i;
         }
         i++;
      }

      return $(index, max);
   }

   static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable) {
      return minIndexAndValue(iterable, Ordering.natural());
   }

   static <E extends Comparable<? super E>> Tuple2<Integer, E> minIndexAndValue(@NonNull Iterable<? extends E> iterable, @NonNull Comparator<? super E> comparator) {
      int index = -1;
      E min = null;
      int i = 0;
      for (E e : iterable) {
         if (min == null || comparator.compare(e,min) < 0) {
            min = e;
            index = i;
         }
         i++;
      }

      return $(index, min);
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param iterable The iterable instance to sort
    * @param <E>      The component type of the iterable which implements the <code>Comparable</code> interface.
    * @return A list of the items in the given iterable sorted using the items natural comparator.
    */
   static <E extends Comparable<? super E>> List<E> sort(@NonNull Iterable<E> iterable) {
      return asStream(iterable).sorted().collect(Collectors.toList());
   }

   /**
    * <p>Sorts the items of an iterable returning an array of the sorted items.</p>
    *
    * @param iterable   The iterable instance to sort
    * @param comparator The comparator to use for sorting
    * @param <E>        The component type of the iterable.
    * @return A list of the items in the given iterable sorted using the given comparator.
    */
   static <E> List<E> sort(@NonNull Iterable<E> iterable, @NonNull Comparator<? super E> comparator) {
      return asStream(iterable).sorted(comparator).collect(Collectors.toList());
   }

   /**
    * <p>Zips (combines) two iterators together. For example, if iterable 1 contained [1,2,3] and iterable 2 contained
    * [4,5,6] the result would be [(1,4), (2,5), (3,6)]. Note that the length of the resulting stream will be the
    * minimum of the two iterables.</p>
    *
    * @param <T>       the component type of the first iterator
    * @param <U>       the component type of the second iterator
    * @param iterable1 the iterator making up the key in the resulting entries
    * @param iterable2 the iterator making up the value in the resulting entries
    * @return A stream of entries whose keys are taken from iterable1 and values are taken from iterable2
    */
   static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterable<? extends T> iterable1, @NonNull final Iterable<? extends U> iterable2) {
      return zip(iterable1.iterator(), iterable2.iterator());
   }

   /**
    * <p>Zips (combines) two iterators together. For example, if iterator 1 contained [1,2,3] and iterator 2 contained
    * [4,5,6] the result would be [(1,4), (2,5), (3,6)]. Note that the length of the resulting stream will be the
    * minimum of the two iterators.</p>
    *
    * @param <T>       the component type of the first iterator
    * @param <U>       the component type of the second iterator
    * @param iterator1 the iterator making up the key in the resulting entries
    * @param iterator2 the iterator making up the value in the resulting entries
    * @return A stream of entries whose keys are taken from iterator1 and values are taken from iterator2
    */
   static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterator<? extends T> iterator1, @NonNull final Iterator<? extends U> iterator2) {
      return asStream(new Iterator<Map.Entry<T, U>>() {
         @Override
         public boolean hasNext() {
            return iterator1.hasNext() && iterator2.hasNext();
         }

         @Override
         public Map.Entry<T, U> next() {
            if (!iterator1.hasNext() || !iterator2.hasNext()) {
               throw new NoSuchElementException();
            }
            return new Tuple2<>(iterator1.next(), iterator2.next());
         }
      });
   }


}// END OF CollectionUtils
