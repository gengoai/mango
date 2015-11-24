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

import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.csv.CSVReader;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * The interface Counters.
 */
public interface Counters {

  /**
   * New concurrent counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  @SafeVarargs
  static <TYPE> Counter<TYPE> newConcurrentCounter(TYPE... items) {
    if (items == null) {
      return new ConcurrentMapCounter<>();
    }
    return newConcurrentCounter(Arrays.asList(items));
  }

  /**
   * New concurrent counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newConcurrentCounter(@NonNull Iterable<? extends TYPE> items) {
    return new ConcurrentMapCounter<>(items);
  }

  /**
   * New concurrent counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newConcurrentCounter(@NonNull Map<? extends TYPE, ? extends Number> items) {
    return new ConcurrentMapCounter<>(items);
  }

  /**
   * New concurrent counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newConcurrentCounter(@NonNull Counter<? extends TYPE> counter) {
    return new ConcurrentMapCounter<>(counter);
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  @SafeVarargs
  static <TYPE> Counter<TYPE> newHashMapCounter(TYPE... items) {
    if (items == null) {
      return new HashMapCounter<>();
    }
    return newHashMapCounter(Arrays.asList(items));
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newHashMapCounter(@NonNull Iterable<? extends TYPE> items) {
    return new HashMapCounter<>(items);
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newHashMapCounter(@NonNull Map<? extends TYPE, ? extends Number> items) {
    return new HashMapCounter<>(items);
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newHashMapCounter(@NonNull Counter<? extends TYPE> counter) {
    return new HashMapCounter<>(counter);
  }

  /**
   * New tree map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  @SafeVarargs
  static <TYPE> Counter<TYPE> newTreeMapCounter(TYPE... items) {
    if (items == null) {
      return new TreeMapCounter<>();
    }
    return newTreeMapCounter(Arrays.asList(items));
  }

  /**
   * New tree map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newTreeMapCounter(@NonNull Iterable<? extends TYPE> items) {
    return new TreeMapCounter<>(items);
  }

  /**
   * New tree map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newTreeMapCounter(@NonNull Map<? extends TYPE, ? extends Number> items) {
    return new TreeMapCounter<>(items);
  }

  /**
   * New tree map counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> newTreeMapCounter(@NonNull Counter<? extends TYPE> counter) {
    return new TreeMapCounter<>(counter);
  }

  /**
   * Unmodifable counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> unmodifableCounter(@NonNull final Counter<TYPE> counter) {
    return new UnmodifiableCounter<>(counter);
  }

  /**
   * From csv counter.
   *
   * @param <TYPE>   the type parameter
   * @param resource the resource
   * @param keyClass the key class
   * @param supplier the supplier
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Counter<TYPE> fromCSV(@NonNull Resource resource, @NonNull Class<TYPE> keyClass, @NonNull Supplier<Counter<TYPE>> supplier) throws IOException {
    Counter<TYPE> counter = supplier.get();
    try (CSVReader reader = CSV.builder().reader(resource)) {
      reader.forEach(row -> {
        if (row.size() >= 2) {
          counter.increment(Convert.convert(row.get(0), keyClass), Double.parseDouble(row.get(1)));
        }
      });
    }
    return counter;
  }

  /**
   * Collector collector.
   *
   * @param <T> the type parameter
   * @return the collector
   */
  static <T> Collector<T, Counter<T>, Counter<T>> collector() {
    return new CounterCollector<>();
  }

  /**
   * The type Counter collector.
   *
   * @param <T> the type parameter
   */
  class CounterCollector<T> implements Collector<T, Counter<T>, Counter<T>> {

    @Override
    public Supplier<Counter<T>> supplier() {
      return Counters::newHashMapCounter;
    }

    @Override
    public BiConsumer<Counter<T>, T> accumulator() {
      return Counter::increment;
    }

    @Override
    public BinaryOperator<Counter<T>> combiner() {
      return (c1, c2) -> {
        c1.merge(c2);
        return c1;
      };
    }

    @Override
    public Function<Counter<T>, Counter<T>> finisher() {
      return c -> c;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }

  }


}//END OF Counters
