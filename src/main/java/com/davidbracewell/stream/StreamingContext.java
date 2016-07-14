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

import com.davidbracewell.collection.Collect;
import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.HashMapCounter;
import com.davidbracewell.collection.HashMapMultiCounter;
import com.davidbracewell.collection.MultiCounter;
import com.davidbracewell.config.Config;
import com.davidbracewell.function.SerializableSupplier;
import com.davidbracewell.stream.accumulator.Accumulatable;
import com.davidbracewell.stream.accumulator.CollectionAccumulatable;
import com.davidbracewell.stream.accumulator.CounterAccumulatable;
import com.davidbracewell.stream.accumulator.MAccumulator;
import com.davidbracewell.stream.accumulator.MapAccumulatable;
import com.davidbracewell.stream.accumulator.MultiCounterAccumulatable;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.Supplier;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * The interface Streaming context.
 *
 * @author David B. Bracewell
 */
public interface StreamingContext {

  /**
   * Get streaming context.
   *
   * @return the streaming context
   */
  static StreamingContext get() {
    return get(Config.get("streams.distributed").asBooleanValue(false));
  }

  /**
   * Get streaming context.
   *
   * @param distributed the distributed
   * @return the streaming context
   */
  static StreamingContext get(boolean distributed) {
    if (distributed) {
      return distributed();
    }
    return local();
  }

  /**
   * Local streaming context.
   *
   * @return the streaming context
   */
  static StreamingContext local() {
    return JavaStreamingContext.INSTANCE;
  }

  /**
   * Distributed streaming context.
   *
   * @return the streaming context
   */
  static StreamingContext distributed() {
    return SparkStreamingContext.INSTANCE;
  }


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


  /**
   * Map accumulator m accumulator.
   *
   * @param <K>      the type parameter
   * @param <V>      the type parameter
   * @param supplier the supplier
   * @return the m accumulator
   */
  default <K, V> MAccumulator<Map<K, V>> mapAccumulator(@NonNull SerializableSupplier<Map<K, V>> supplier) {
    return accumulator(null, new MapAccumulatable<>(supplier));
  }

  /**
   * Map accumulator m accumulator.
   *
   * @param <K>      the type parameter
   * @param <V>      the type parameter
   * @param supplier the supplier
   * @param name     the name
   * @return the m accumulator
   */
  default <K, V> MAccumulator<Map<K, V>> mapAccumulator(@NonNull SerializableSupplier<Map<K, V>> supplier, String name) {
    return accumulator(null, new MapAccumulatable<>(supplier), name);
  }

  /**
   * Counter accumulator m accumulator.
   *
   * @param <E> the type parameter
   * @return the m accumulator
   */
  default <E> MAccumulator<Counter<E>> counterAccumulator() {
    return accumulator(new HashMapCounter<E>(), new CounterAccumulatable<>());
  }

  /**
   * Multi counter accumulator m accumulator.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @return the m accumulator
   */
  default <K, V> MAccumulator<MultiCounter<K, V>> multiCounterAccumulator() {
    return accumulator(new HashMapMultiCounter<K, V>(), new MultiCounterAccumulatable<>());
  }


  /**
   * Multi counter accumulator m accumulator.
   *
   * @param <K>  the type parameter
   * @param <V>  the type parameter
   * @param name the name
   * @return the m accumulator
   */
  default <K, V> MAccumulator<MultiCounter<K, V>> multiCounterAccumulator(String name) {
    return accumulator(new HashMapMultiCounter<K, V>(), new MultiCounterAccumulatable<>(), name);
  }

  /**
   * Counter accumulator m accumulator.
   *
   * @param <E>  the type parameter
   * @param name the name
   * @return the m accumulator
   */
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
  <T> MStream<T> stream(T... items);

  /**
   * Of m stream.
   *
   * @param <T>    the type parameter
   * @param stream the stream
   * @return the m stream
   */
  <T> MStream<T> stream(Stream<T> stream);


  /**
   * Stream m pair stream.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param map the map
   * @return the m pair stream
   */
  <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map);

  /**
   * Pair stream m pair stream.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param tuples the tuples
   * @return the m pair stream
   */
  <K,V> MPairStream<K,V> pairStream(Collection<Entry<K,V>> tuples);

  /**
   * Pair stream m pair stream.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param tuples the tuples
   * @return the m pair stream
   */
  default <K,V> MPairStream<K,V> pairStream(Tuple2<K,V>... tuples){
    if( tuples == null ){
      return emptyPair();
    }
    return pairStream(Arrays.asList(tuples));
  }

  /**
   * Stream m stream.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @return the m stream
   */
  <T> MStream<T> stream(Collection<? extends T> collection);


  /**
   * Stream m stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the m stream
   */
  <T> MStream<T> stream(Iterable<? extends T> iterable);

  /**
   * Stream m stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the m stream
   */
  default <T> MStream<T> stream(Iterator<? extends T> iterator) {
    if (iterator == null) {
      return empty();
    }
    return stream(Collect.asIterable(iterator));
  }

  /**
   * Double stream m double stream.
   *
   * @param doubleStream the double stream
   * @return the m double stream
   */
  MDoubleStream doubleStream(DoubleStream doubleStream);

  /**
   * Double stream m double stream.
   *
   * @param values the values
   * @return the m double stream
   */
  MDoubleStream doubleStream(double... values);

  /**
   * Text file m stream.
   *
   * @param location the location
   * @return the m stream
   */
  MStream<String> textFile(String location);

  /**
   * Range m stream.
   *
   * @param startInclusive the start inclusive
   * @param endExclusive   the end exclusive
   * @return the m stream
   */
  MStream<Integer> range(int startInclusive, int endExclusive);

  /**
   * Empty m stream.
   *
   * @param <T> the type parameter
   * @return the m stream
   */
  <T> MStream<T> empty();

  /**
   * Empty pair m pair stream.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @return the m pair stream
   */
  default <K,V> MPairStream<K,V> emptyPair(){
    return empty().mapToPair(k -> null);
  }

}//END OF StreamingContext
