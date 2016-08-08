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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import lombok.NonNull;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Deque;
import java.util.HashMap;
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
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.Stack;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public interface Collect {

  @SafeVarargs
  static <T, Y extends T> List<T> list(Y first, Y... others) {
    return list(ArrayList::new, first, others);
  }

  @SafeVarargs
  static <T, Y extends T> List<T> linkedList(Y first, Y... others) {
    return list(LinkedList::new, first, others);
  }

  @SafeVarargs
  static <T, Y extends T> List<T> sortedList(Y first, Y... others) {
    return list(SortedArrayList::new, first, others);
  }

  @SafeVarargs
  static <T, Y extends T> List<T> list(@NonNull Supplier<List<T>> supplier, Y first, Y... others) {
    if (others == null) {
      return Collections.singletonList(first);
    }
    List<T> list = supplier.get();
    list.add(first);
    Collections.addAll(list, others);
    return list;
  }


  @SafeVarargs
  static <T, Y extends T> Set<T> set(Y first, Y... others) {
    return set(HashSet::new, first, others);
  }

  @SafeVarargs
  static <T, Y extends T> Set<T> sortedSet(Y first, Y... others) {
    return set(TreeSet::new, first, others);
  }

  @SafeVarargs
  static <T, Y extends T> Set<T> set(@NonNull Supplier<Set<T>> supplier, Y first, Y... others) {
    if (others == null) {
      return Collections.singleton(first);
    }
    Set<T> set = supplier.get();
    set.add(first);
    Collections.addAll(set, others);
    return set;
  }

  /**
   * From stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> stream(Iterator<T> iterator) {
    return stream(iterator, false);
  }

  /**
   * From stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> stream(Iterable<T> iterable) {
    return stream(iterable, false);
  }

  /**
   * Paralle from.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> stream(Iterator<T> iterator, boolean parallel) {
    if (iterator == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), parallel);
  }

  /**
   * Paralle from.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> stream(Iterable<T> iterable, boolean parallel) {
    if (iterable == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(iterable.spliterator(), parallel);
  }

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
   * <p>Fills a map with an iterable converting the even elements of the iterable to the keys and the odd elements to
   * the values using the given key and value converters. A null or empty iterable results in an empty map. </p>
   *
   * @param <K>            The key type
   * @param <V>            The value type
   * @param map            The map to fill
   * @param iterable       The iterable to convert into a map
   * @param keyConverter   The converter to use for the keys (even elements)
   * @param valueConverter The converter to use for the values (odd elements)
   * @return The map.
   */
  static <K, V> Map<K, V> fillMap(@NonNull Map<K, V> map, Iterable<?> iterable, @NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter) {
    if (iterable == null) {
      return map;
    }
    for (Iterator<?> iterator = iterable.iterator(); iterator.hasNext(); ) {
      Object key = iterator.next();
      if (!iterator.hasNext()) {
        throw new IllegalArgumentException("Size of iterable must be divisible by 2");
      }
      Object value = iterator.next();
      map.put(keyConverter.apply(key), valueConverter.apply(value));
    }
    return map;
  }

  /**
   * First optional.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the optional
   */
  static <T> Optional<T> first(Iterable<T> iterable) {
    return stream(iterable).findFirst();
  }

  /**
   * First optional.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the optional
   */
  static <T> Optional<T> first(Iterator<T> iterator) {
    return stream(iterator).findFirst();
  }

  /**
   * <p>Creates a HashMap from a string converting the keys and values using {@link Convert#getConverter(Class)}. Empty
   * or null  strings result in an empty Map. The string format should be in csv where the commas separate the
   * key-value
   * pairs. Keys and values are the separated using either <code>:</code> or <code>=</code> depending on which one is
   * present and appears first. </p>
   *
   * @param <K>        The key type
   * @param <V>        The value type
   * @param input      The input string
   * @param keyClass   The key class
   * @param valueClass The value class
   * @return The resulting map
   */
  static <K, V> Map<K, V> fromString(String input, @NonNull Class<K> keyClass, @NonNull Class<V> valueClass) {
    return fromString(input,
      Convert.getConverter(keyClass),
      Convert.getConverter(valueClass)
    );
  }

  /**
   * <p>Creates a HashMap from a string converting the keys and values using the supplied functions. Empty or null
   * strings result in an empty Map. The string format should be in csv where the commas separate the key-value pairs.
   * Keys and values are the separated using either <code>:</code> or <code>=</code> depending on which one is present
   * and appears first. </p>
   *
   * @param <K>            The key type
   * @param <V>            The value type
   * @param input          The input string
   * @param keyConverter   The function to convert an object to the key type
   * @param valueConverter The function to convert an object to the value type
   * @return The resulting map
   */
  static <K, V> Map<K, V> fromString(String input, @NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter) {
    if (Strings.isNullOrEmpty(input)) {
      return Collections.emptyMap();
    }
    String str = input.replaceFirst("^\\s*\\{", "").replaceFirst("}$\\s*", "");
    Map<K, V> map = Maps.newHashMap();

    try (CSVReader reader = CSV.builder().reader(new StringReader(str))) {
      reader.forEach(row -> {
        row.forEach(cell -> {
          int ci = cell.indexOf(':');
          int ei = cell.indexOf('=');
          char delimiter = ei == -1 || (ci != -1 && ci < ei) ? ':' : '=';
          List<String> keyValuePair = StringUtils.split(cell, delimiter);
          String key = keyValuePair.size() > 0 ? keyValuePair.get(0) : null;
          String value = keyValuePair.size() > 1 ? keyValuePair.get(1) : null;
          map.put(keyConverter.apply(key), valueConverter.apply(value));
        });
      });
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    return map;
  }

  /**
   * Map hash map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param k1  the k 1
   * @param v1  the v 1
   * @return the hash map
   */
  static <K, V> Map<K, V> map(K k1, V v1) {
    HashMap<K, V> map = new HashMap<>(1);
    map.put(k1, v1);
    return map;
  }

  /**
   * Map hash map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param k1  the k 1
   * @param v1  the v 1
   * @param k2  the k 2
   * @param v2  the v 2
   * @return the hash map
   */
  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2) {
    HashMap<K, V> map = new HashMap<>(2);
    map.put(k1, v1);
    map.put(k2, v2);
    return map;
  }

  /**
   * Map hash map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param k1  the k 1
   * @param v1  the v 1
   * @param k2  the k 2
   * @param v2  the v 2
   * @param k3  the k 3
   * @param v3  the v 3
   * @return the hash map
   */
  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3) {
    HashMap<K, V> map = new HashMap<>(3);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    return map;
  }

  /**
   * Map hash map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param k1  the k 1
   * @param v1  the v 1
   * @param k2  the k 2
   * @param v2  the v 2
   * @param k3  the k 3
   * @param v3  the v 3
   * @param k4  the k 4
   * @param v4  the v 4
   * @return the hash map
   */
  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
    HashMap<K, V> map = new HashMap<>(4);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    return map;
  }

  /**
   * Map hash map.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param k1  the k 1
   * @param v1  the v 1
   * @param k2  the k 2
   * @param v2  the v 2
   * @param k3  the k 3
   * @param v3  the v 3
   * @param k4  the k 4
   * @param v4  the v 4
   * @param k5  the k 5
   * @param v5  the v 5
   * @return the hash map
   */
  static <K, V> Map<K, V> map(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4, K k5, V v5) {
    HashMap<K, V> map = new HashMap<>(5);
    map.put(k1, v1);
    map.put(k2, v2);
    map.put(k3, v3);
    map.put(k4, v4);
    map.put(k5, v5);
    return map;
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
    return stream(iterable)
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
      return Cast.as(Sets.newHashSet());
    } else if (List.class.equals(collectionClass)) {
      return Cast.as(Lists.newArrayList());
    } else if (Queue.class.equals(collectionClass)) {
      return Cast.as(Lists.newLinkedList());
    } else if (Deque.class.equals(collectionClass)) {
      return Cast.as(Lists.newLinkedList());
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
  static <T> Collection<T> difference(Collection<T> c1, Collection<T> c2) {
    if (c1 == null) {
      return Collections.emptyList();
    } else if (c2 == null || c2.isEmpty()) {
      return new ArrayList<>(c1);
    }
    List<T> diff = new ArrayList<>(c1);
    diff.removeAll(c2);
    return diff;
  }

  /**
   * Intersection collection.
   *
   * @param <T> the type parameter
   * @param c1  the c 1
   * @param c2  the c 2
   * @return the collection
   */
  static <T> Collection<T> intersection(Collection<T> c1, Collection<T> c2) {
    if (c1 == null || c2 == null || c1.isEmpty() || c2.isEmpty()) {
      return Collections.emptyList();
    }
    return c1.stream().filter(c2::contains).collect(Collectors.toList());

  }

  /**
   * Union collection.
   *
   * @param <T> the type parameter
   * @param c1  the c 1
   * @param c2  the c 2
   * @return the collection
   */
  static <T> Collection<T> union(Collection<T> c1, Collection<T> c2) {
    if (c1 == null && c2 == null) {
      return Collections.emptyList();
    } else if (c1 == null) {
      return new ArrayList<>(c2);
    } else if (c2 == null) {
      return new ArrayList<>(c1);
    }
    return Stream.concat(c1.stream(), c2.stream()).collect(Collectors.toList());
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
      .flatMap(Collect::stream)
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
    return stream(new Iterator<Map.Entry<T, U>>() {
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
