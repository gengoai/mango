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

package com.davidbracewell.collection.list;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.collection.Streams;
import lombok.NonNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public final class Lists {

  private Lists() {
    throw new IllegalAccessError();
  }

  /**
   * Difference set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> List<E> difference(List<? extends E> s1, List<? extends E> s2) {
    return Collect.difference(bestSupplier(s1, s2), s1, s2);
  }

  public static <T> List<T> ensureSize(@NonNull List<T> list, int desiredSize, T defaultValue) {
    while (list.size() < desiredSize) {
      list.add(defaultValue);
    }
    return list;
  }

  /**
   * Flatten list.
   *
   * @param <T>  the type parameter
   * @param list the list
   * @return the list
   */
  public static <T> List<T> flatten(Collection<? extends Iterable<T>> list) {
    if (list == null) {
      return Collections.emptyList();
    }
    return list.stream()
               .filter(Objects::nonNull)
               .flatMap(Streams::asStream)
               .collect(Collectors.toList());
  }

  /**
   * Union set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> List<E> union(List<? extends E> s1, List<? extends E> s2) {
    return Collect.union(bestSupplier(s1, s2), s1, s2);
  }

  /**
   * Intersection set.
   *
   * @param <E> the type parameter
   * @param s1  the s 1
   * @param s2  the s 2
   * @return the set
   */
  public static <E> List<E> intersection(List<? extends E> s1, List<? extends E> s2) {
    return Collect.intersection(bestSupplier(s1, s2), s1, s2);
  }

  private static <E> Supplier<List<E>> bestSupplier(List<?> list1, List<?> list2) {
    if (list1 == null && list2 == null) {
      return ArrayList::new;
    } else if (list1 == null) {
      return () -> Collect.create(list2.getClass());
    }
    return () -> Collect.create(list1.getClass());
  }

  /**
   * As list list.
   *
   * @param <T>   the type parameter
   * @param <Y>   the type parameter
   * @param items the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> List<T> list(Y... items) {
    return createList(ArrayList::new, items);
  }

  /**
   * As linked list list.
   *
   * @param <T>   the type parameter
   * @param <Y>   the type parameter
   * @param items the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> List<T> linkedList(Y... items) {
    return createList(LinkedList::new, items);
  }

  /**
   * As sorted list list.
   *
   * @param <T>   the type parameter
   * @param <Y>   the type parameter
   * @param items the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> List<T> sortedList(Y... items) {
    return createList(SortedArrayList::new, items);
  }

  /**
   * As list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param items    the others
   * @return the list
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <T, Y extends T> List<T> createList(@NonNull Supplier<List<T>> supplier, Y... items) {
    if (items == null || items.length == 0) {
      return Collections.emptyList();
    }
    return createList(supplier, Streams.asStream(items));
  }

  /**
   * As array list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  public static <T, Y extends T> List<T> asArrayList(Stream<Y> stream) {
    return createList(ArrayList::new, stream);
  }

  /**
   * As linked list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  public static <T, Y extends T> List<T> asLinkedList(Stream<Y> stream) {
    return createList(LinkedList::new, stream);
  }

  /**
   * As sorted list list.
   *
   * @param <T>    the type parameter
   * @param <Y>    the type parameter
   * @param stream the stream
   * @return the list
   */
  public static <T, Y extends T> List<T> asSortedList(Stream<Y> stream) {
    return createList(SortedArrayList::new, stream);
  }

  /**
   * As array list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  public static <T, Y extends T> List<T> asArrayList(Iterator<Y> iterator) {
    return createList(ArrayList::new, Streams.asStream(iterator));
  }

  /**
   * As linked list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  public static <T, Y extends T> List<T> asLinkedList(Iterator<Y> iterator) {
    return createList(LinkedList::new, Streams.asStream(iterator));
  }

  /**
   * As sorted list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterator the iterator
   * @return the list
   */
  public static <T, Y extends T> List<T> asSortedList(Iterator<Y> iterator) {
    return createList(SortedArrayList::new, Streams.asStream(iterator));
  }

  /**
   * As array list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  public static <T, Y extends T> List<T> asArrayList(Iterable<Y> iterable) {
    return createList(ArrayList::new, Streams.asStream(iterable));
  }

  /**
   * As linked list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  public static <T, Y extends T> List<T> asLinkedList(Iterable<Y> iterable) {
    return createList(LinkedList::new, Streams.asStream(iterable));
  }

  /**
   * As sorted list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param iterable the iterable
   * @return the list
   */
  public static <T, Y extends T> List<T> asSortedList(Iterable<Y> iterable) {
    return createList(SortedArrayList::new, Streams.asStream(iterable));
  }

  /**
   * Create list list.
   *
   * @param <T>      the type parameter
   * @param <Y>      the type parameter
   * @param supplier the supplier
   * @param stream   the stream
   * @return the list
   */
  public static <T, Y extends T> List<T> createList(@NonNull Supplier<List<T>> supplier, Stream<Y> stream) {
    if (stream == null) {
      return Collections.emptyList();
    }
    return stream.collect(Collectors.toCollection(supplier));
  }


}//END OF Lists
