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

package com.davidbracewell.stream;

import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.HashMapCounter;
import com.davidbracewell.stream.accumulator.Accumulatable;
import com.davidbracewell.stream.accumulator.CollectionAccumulatable;
import com.davidbracewell.stream.accumulator.CounterAccumulatable;
import com.davidbracewell.stream.accumulator.MAccumulator;
import lombok.NonNull;

import java.util.Collection;
import java.util.function.Supplier;
import java.util.stream.Stream;

/**
 * The interface Streaming context.
 *
 * @author David B. Bracewell
 */
public interface StreamingContext {

  /**
   * Double accumulator m accumulator.
   *
   * @param initialValue the initial value
   * @return the m accumulator
   */
  default MAccumulator<Double> accumulator(double initialValue) {
    return accumulator(initialValue, null);
  }

  /**
   * Double accumulator m accumulator.
   *
   * @param initialValue the initial value
   * @param name         the name
   * @return the m accumulator
   */
  MAccumulator<Double> accumulator(double initialValue, String name);

  /**
   * Int accumulator m accumulator.
   *
   * @param initialValue the initial value
   * @return the m accumulator
   */
  default MAccumulator<Integer> accumulator(int initialValue) {
    return accumulator(initialValue, null);
  }

  /**
   * Int accumulator m accumulator.
   *
   * @param initialValue the initial value
   * @param name         the name
   * @return the m accumulator
   */
  MAccumulator<Integer> accumulator(int initialValue, String name);

  /**
   * Accumulator m accumulator.
   *
   * @param <T>           the type parameter
   * @param initialValue  the initial value
   * @param accumulatable the accumulatable
   * @return the m accumulator
   */
  default <T> MAccumulator<T> accumulator(T initialValue, Accumulatable<T> accumulatable) {
    return accumulator(initialValue, accumulatable, null);
  }

  /**
   * Accumulator m accumulator.
   *
   * @param <T>           the type parameter
   * @param initialValue  the initial value
   * @param accumulatable the accumulatable
   * @param name          the name
   * @return the m accumulator
   */
  <T> MAccumulator<T> accumulator(T initialValue, Accumulatable<T> accumulatable, String name);

  /**
   * Collection accumulator m accumulator.
   *
   * @param <E>                the type parameter
   * @param <C>                the type parameter
   * @param collectionSupplier the collection supplier
   * @param name               the name
   * @return the m accumulator
   */
  default <E, C extends Collection<E>> MAccumulator<C> accumulator(Supplier<C> collectionSupplier, String name) {
    return accumulator(collectionSupplier.get(), new CollectionAccumulatable<>(), name);
  }

  /**
   * Collection accumulator m accumulator.
   *
   * @param <E>                the type parameter
   * @param <C>                the type parameter
   * @param collectionSupplier the collection supplier
   * @return the m accumulator
   */
  default <E, C extends Collection<E>> MAccumulator<C> accumulator(Supplier<C> collectionSupplier) {
    return accumulator(collectionSupplier.get(), new CollectionAccumulatable<>());
  }

  default <E> MAccumulator<Counter<E>> counterAccumulator() {
    return accumulator(new HashMapCounter<E>(), new CounterAccumulatable<>());
  }

  default <E> MAccumulator<Counter<E>> counterAccumulator(String name) {
    return accumulator(new HashMapCounter<E>(), new CounterAccumulatable<>(), name);
  }

  /**
   * Of m stream.
   *
   * @param <T>   the type parameter
   * @param items the items
   * @return the m stream
   */
  <T> MStream<T> stream(@NonNull T... items);

  /**
   * Of m stream.
   *
   * @param <T>    the type parameter
   * @param stream the stream
   * @return the m stream
   */
  <T> MStream<T> stream(@NonNull Stream<T> stream);

  /**
   * Text file m stream.
   *
   * @param location the location
   * @return the m stream
   */
  MStream<String> textFile(String location);


}//END OF StreamingContext
