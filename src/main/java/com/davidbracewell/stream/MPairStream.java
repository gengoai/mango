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

import com.davidbracewell.function.*;

import java.util.List;
import java.util.Map;

/**
 * The interface M pair stream.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author David B. Bracewell
 */
public interface MPairStream<T, U> extends AutoCloseable {

  /**
   * For each.
   *
   * @param consumer the consumer
   */
  void forEach(SerializableBiConsumer<? super T, ? super U> consumer);

  /**
   * Map m stream.
   *
   * @param <R>      the type parameter
   * @param function the function
   * @return the m stream
   */
  <R> MStream<R> map(SerializableBiFunction<? super T, ? super U, ? extends R> function);

  /**
   * Map to pair m pair stream.
   *
   * @param <R>      the type parameter
   * @param <V>      the type parameter
   * @param function the function
   * @return the m pair stream
   */
  <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function);

  /**
   * Filter m pair stream.
   *
   * @param predicate the predicate
   * @return the m pair stream
   */
  MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate);

  /**
   * Group by key m pair stream.
   *
   * @return the m pair stream
   */
  MPairStream<T, Iterable<U>> groupByKey();

  /**
   * Collect as map map.
   *
   * @return the map
   */
  Map<T, U> collectAsMap();

  /**
   * Collect as list list.
   *
   * @return the list
   */
  List<Map.Entry<T, U>> collectAsList();

  /**
   * Join m pair stream.
   *
   * @param <V>    the type parameter
   * @param stream the stream
   * @return the m pair stream
   */
  <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? super T, ? super V> stream);


  /**
   * Reduce by key m pair stream.
   *
   * @param operator the operator
   * @return the m pair stream
   */
  MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator);

  /**
   * Filter by key m pair stream.
   *
   * @param predicate the predicate
   * @return the m pair stream
   */
  MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate);

  /**
   * Filter by value m pair stream.
   *
   * @param predicate the predicate
   * @return the m pair stream
   */
  MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate);

  long count();

}//END OF MPairStream
