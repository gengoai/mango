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
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.tuple.Tuple2;
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
   * Collector collector.
   *
   * @param <T> the type parameter
   * @return the collector
   */
  static <T> Collector<T, Counter<T>, Counter<T>> collector() {
    return new CounterCollector<>();
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  @SafeVarargs
  static <TYPE> Counter<TYPE> create(TYPE... items) {
    if (items == null) {
      return new HashCounter<>();
    }
    return create(Arrays.asList(items));
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> create(@NonNull Iterable<? extends TYPE> items) {
    return new HashCounter<>(items);
  }

  /**
   * New hash map counter.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the counter
   */
  static <TYPE> Counter<TYPE> create(@NonNull Map<? extends TYPE, ? extends Number> items) {
    return new HashCounter<>(items);
  }

  /**
   * From array counter.
   *
   * @param array the array
   * @return the counter
   */
  static Counter<Integer> createVectorCounter(@NonNull double[] array) {
    Counter<Integer> counter = Counters.create();
    for (int i = 0; i < array.length; i++) {
      counter.set(i, array[i]);
    }
    return counter;
  }

  /**
   * Read counter.
   *
   * @param <TYPE>           the type parameter
   * @param structuredFormat the structured format
   * @param resource         the resource
   * @param keyType          the key type
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Counter<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Class<TYPE> keyType) throws IOException {
    return read(structuredFormat, resource, str -> Convert.convert(str, keyType));
  }

  /**
   * Read counter.
   *
   * @param <TYPE>           the type parameter
   * @param structuredFormat the structured format
   * @param resource         the resource
   * @param deserializer     the deserializer
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Counter<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Function<String, TYPE> deserializer) throws IOException {
    Counter<TYPE> counter = new HashCounter<>();
    try (StructuredReader reader = structuredFormat.createReader(resource)) {
      reader.beginDocument();
      while (reader.peek() != ElementType.END_DOCUMENT) {
        Tuple2<String, Val> keyValue = reader.nextKeyValue();
        counter.set(deserializer.apply(keyValue.v1), keyValue.v2.asDouble());
      }
      reader.endDocument();
    }
    return counter;
  }

  /**
   * Read csv counter.
   *
   * @param <TYPE>   the type parameter
   * @param resource the resource
   * @param keyClass the key class
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Counter<TYPE> readCSV(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.CSV, resource, keyClass);
  }

  /**
   * Synchronized counter counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> synchronizedCounter(@NonNull Counter<TYPE> counter) {
    return new SynchronizedCounter<>(counter);
  }

  /**
   * Unmodifiable counter.
   *
   * @param <TYPE>  the type parameter
   * @param counter the counter
   * @return the counter
   */
  static <TYPE> Counter<TYPE> unmodifiableCounter(@NonNull final Counter<TYPE> counter) {
    return new UnmodifiableCounter<>(counter);
  }

  /**
   * The type Counter collector.
   *
   * @param <T> the type parameter
   */
  class CounterCollector<T> implements Collector<T, Counter<T>, Counter<T>> {

    @Override
    public BiConsumer<Counter<T>, T> accumulator() {
      return Counter::increment;
    }

    @Override
    public Set<Characteristics> characteristics() {
      return EnumSet.of(Characteristics.UNORDERED);
    }

    @Override
    public BinaryOperator<Counter<T>> combiner() {
      return Counter::merge;
    }

    @Override
    public Function<Counter<T>, Counter<T>> finisher() {
      return c -> c;
    }

    @Override
    public Supplier<Counter<T>> supplier() {
      return Counters::create;
    }

  }


}//END OF Counters
