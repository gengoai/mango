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

package com.davidbracewell.collection.counter;

import com.davidbracewell.conversion.Convert;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.IOException;
import java.util.EnumSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Common methods for reading counters from structured files, creating synchronized and unmodifiable wrappers, and
 * creating collectors.
 */
public interface Counters {

  /**
   * <p>Creates a Counter collector that count the items in a string generating a {@link HashMapCounter}.</p>
   *
   * @param <T> the type parameter
   * @return the collector
   */
  static <T> Collector<T, Counter<T>, Counter<T>> collector() {
    return new CounterCollector<>();
  }

  /**
   * <p>Creates a Counter collector that count the items in a string generating a Counter using the given supplier.</p>
   *
   * @param <T>      the type parameter
   * @param supplier the supplier to use for creating a counter
   * @return the collector
   */
  static <T> Collector<T, Counter<T>, Counter<T>> collector(@NonNull Supplier<Counter<T>> supplier) {
    return new CounterCollector<>(supplier);
  }

  /**
   * <p>Creates a Counter whose keys are Integers and values are associated value in the given double array.</p>
   *
   * @param array the array
   * @return the counter
   */
  static Counter<Integer> createVectorCounter(@NonNull double[] array) {
    Counter<Integer> counter = new HashMapCounter<>();
    for (int i = 0; i < array.length; i++) {
      counter.set(i, array[i]);
    }
    return counter;
  }

  /**
   * <p>Reads a resource in the given {@link StructuredFormat} which is made up of key value pairs where the key is the
   * item in the counter and the value is its count.</p>
   *
   * @param <TYPE>           the item type
   * @param structuredFormat the format of the file being read
   * @param resource         the resource of the file being read
   * @param keyType          the class of the item type
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <TYPE> Counter<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Class<TYPE> keyType) throws IOException {
    return read(structuredFormat, resource, str -> Convert.convert(str, keyType), HashMapCounter::new);
  }

  /**
   * <p>Reads a resource in the given {@link StructuredFormat} which is made up of key value pairs where the key is the
   * item in the counter and the value is its count.</p>
   *
   * @param <TYPE>           the item type
   * @param structuredFormat the format of the file being read
   * @param resource         the resource of the file being read
   * @param deserializer     Function to turn string representation of key into an item
   * @param supplier         the supplier to use for creating the initial counter
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <TYPE> Counter<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Function<String, TYPE> deserializer, @NonNull Supplier<Counter<TYPE>> supplier) throws IOException {
    Counter<TYPE> counter = supplier.get();
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
   * <p>Reads a counter from a CSV file.</p>
   *
   * @param <TYPE>   the item type
   * @param resource the resource that the counter values are written to.
   * @param keyClass the class of the item type
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <TYPE> Counter<TYPE> readCSV(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.CSV, resource, keyClass);
  }

  /**
   * <p>Reads a counter from a Json file.</p>
   *
   * @param <TYPE>   the item type
   * @param resource the resource that the counter values are written to.
   * @param keyClass the class of the item type
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <TYPE> Counter<TYPE> readJson(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.JSON, resource, keyClass);
  }

  /**
   * <p>Wraps a counter making each method call synchronized.</p>
   *
   * @param <TYPE>  the item type
   * @param counter the counter to wrap
   * @return the wrapped counter
   */
  static <TYPE> Counter<TYPE> synchronizedCounter(@NonNull Counter<TYPE> counter) {
    return new SynchronizedCounter<>(counter);
  }

  /**
   * <p>Wraps a counter making its entries unmodifiable.</p>
   *
   * @param <TYPE>  the item type
   * @param counter the counter to wrap
   * @return the wrapped counter
   */
  static <TYPE> Counter<TYPE> unmodifiableCounter(@NonNull final Counter<TYPE> counter) {
    return new UnmodifiableCounter<>(counter);
  }

  /**
   * <p>{@link Collector} implementation for Counters.</p>
   *
   * @param <T> The item type of the Counter
   */
  class CounterCollector<T> implements Collector<T, Counter<T>, Counter<T>> {

    /**
     * The Supplier.
     */
    final Supplier<Counter<T>> supplier;

    /**
     * Instantiates a new Counter collector.
     */
    public CounterCollector() {
      this(HashMapCounter::new);
    }

    /**
     * Instantiates a new Counter collector.
     *
     * @param supplier the supplier
     */
    public CounterCollector(Supplier<Counter<T>> supplier) {
      this.supplier = supplier;
    }

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
      return supplier;
    }

  }


}//END OF Counters
