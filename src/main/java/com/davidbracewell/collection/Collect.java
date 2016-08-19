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
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.davidbracewell.collection.CollectionHelpers.asStream;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public interface Collect {

  static <K, V> void put(@NonNull Map<K, V> map, Map.Entry<K, V> entry) {
    if (entry != null) {
      map.put(entry.getKey(), entry.getValue());
    }
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
    Preconditions.checkArgument(array.getClass().isArray());
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
  static <T> Optional<T> first(Iterable<T> iterable) {
    return asStream(iterable).findFirst();
  }

  /**
   * First optional.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the optional
   */
  static <T> Optional<T> first(Iterator<T> iterator) {
    return asStream(iterator).findFirst();
  }


  /**
   * Sum double.
   *
   * @param iterable the iterable
   * @return the double
   */
  static double sum(Iterable<? extends Number> iterable) {
    return analyze(iterable).getSum();
  }


  static EnhancedDoubleStatistics analyze(Iterable<? extends Number> iterable) {
    if (iterable == null) {
      return new EnhancedDoubleStatistics();
    }
    return asStream(iterable)
      .mapToDouble(Number::doubleValue)
      .collect(EnhancedDoubleStatistics::new, EnhancedDoubleStatistics::accept, EnhancedDoubleStatistics::combine);
  }

  /**
   * Arg max.
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param <E>        the type parameter
   * @param collection the collection
   * @return the optional
   */
  static <K, V extends Comparable, E extends Map.Entry<K, V>> Optional<E> argMax(Collection<? extends E> collection) {
    if (collection == null) {
      return Optional.empty();
    }
    Comparator<Map.Entry<K, V>> comparator = Sorting.mapEntryComparator(false, true);
    return collection.stream().reduce(BinaryOperator.maxBy(comparator)).map(Cast::as);
  }

  /**
   * Arg min.
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param <E>        the type parameter
   * @param collection the collection
   * @return the optional
   */
  static <K, V extends Comparable, E extends Map.Entry<K, V>> Optional<E> argMin(Collection<? extends E> collection) {
    if (collection == null) {
      return Optional.empty();
    }
    Comparator<Map.Entry<K, V>> comparator = Sorting.mapEntryComparator(false, true);
    return collection.stream().reduce(BinaryOperator.minBy(comparator)).map(Cast::as);
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

    try {
      return collectionClass.newInstance();
    } catch (InstantiationException | IllegalAccessException e) {
      throw Throwables.propagate(e);
    }
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

  /**
   * Flatten list.
   *
   * @param <T>  the type parameter
   * @param list the list
   * @return the list
   */
  static <T> List<T> flatten(Collection<? extends Iterable<T>> list) {
    if (list == null) {
      return Collections.emptyList();
    }
    return list.stream()
               .filter(Objects::nonNull)
               .flatMap(CollectionHelpers::asStream)
               .collect(Collectors.toList());
  }

  /**
   * Zip stream.
   *
   * @param <T>     the type parameter
   * @param <U>     the type parameter
   * @param stream1 the stream 1
   * @param stream2 the stream 2
   * @return the stream
   */
  static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Stream<T> stream1, @NonNull final Stream<U> stream2) {
    if (stream1 == null || stream2 == null) {
      return Stream.empty();
    }
    return zip(stream1.iterator(), stream2.iterator());
  }

  /**
   * Zip stream.
   *
   * @param <T>       the type parameter
   * @param <U>       the type parameter
   * @param iterator1 the iterator 1
   * @param iterator2 the iterator 2
   * @return the stream
   */
  static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterator<T> iterator1, @NonNull final Iterator<U> iterator2) {
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


  /**
   * Zip with index.
   *
   * @param <T>    the type parameter
   * @param stream the stream
   * @return the stream
   */
  static <T> Stream<Map.Entry<T, Integer>> zipWithIndex(Stream<T> stream) {
    if (stream == null) {
      return Stream.empty();
    }
    final AtomicInteger integer = new AtomicInteger();
    return stream.map(t -> new Tuple2<>(t, integer.getAndIncrement()));
  }


  static <T> List<T> ensureSize(@NonNull List<T> list, int desiredSize, T defaultValue) {
    while (list.size() <= desiredSize) {
      list.add(defaultValue);
    }
    return list;
  }


}// END OF CollectionUtils
