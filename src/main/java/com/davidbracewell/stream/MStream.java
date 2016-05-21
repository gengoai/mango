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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.Resource;
import com.google.common.collect.Ordering;
import lombok.NonNull;

import java.io.Closeable;
import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;

/**
 * The interface M stream.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public interface MStream<T> extends Closeable {

  /**
   * Filter m stream.
   *
   * @param predicate the predicate
   * @return the m stream
   */
  MStream<T> filter(SerializablePredicate<? super T> predicate);

  /**
   * Map m stream.
   *
   * @param <R>      the type parameter
   * @param function the function
   * @return the m stream
   */
  <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function);

  /**
   * Flat map m stream.
   *
   * @param <R>    the type parameter
   * @param mapper the mapper
   * @return the m stream
   */
  <R> MStream<R> flatMap(SerializableFunction<? super T, Iterable<? extends R>> mapper);

  /**
   * Flat map to pair m pair stream.
   *
   * @param <R>      the type parameter
   * @param <U>      the type parameter
   * @param function the function
   * @return the m pair stream
   */
  <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function);

  /**
   * Map to pair m pair stream.
   *
   * @param <R>      the type parameter
   * @param <U>      the type parameter
   * @param function the function
   * @return the m pair stream
   */
  <R, U> MPairStream<R, U> mapToPair(SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function);

  /**
   * Group by m pair stream.
   *
   * @param <U>      the type parameter
   * @param function the function
   * @return the m pair stream
   */
  <U> MPairStream<U, Iterable<T>> groupBy(SerializableFunction<? super T, ? extends U> function);

  /**
   * Collect r.
   *
   * @param <R>       the type parameter
   * @param collector the collector
   * @return the r
   */
  <R> R collect(Collector<? super T, T, R> collector);

  /**
   * Collect list.
   *
   * @return the list
   */
  List<T> collect();

  /**
   * Reduce optional.
   *
   * @param reducer the reducer
   * @return the optional
   */
  Optional<T> reduce(SerializableBinaryOperator<T> reducer);

  /**
   * Fold t.
   *
   * @param zeroValue the zero value
   * @param operator  the operator
   * @return the t
   */
  T fold(T zeroValue, SerializableBinaryOperator<T> operator);

  /**
   * For each.
   *
   * @param consumer the consumer
   */
  void forEach(SerializableConsumer<? super T> consumer);

  void forEachLocal(SerializableConsumer<? super T> consumer);

  /**
   * Iterator iterator.
   *
   * @return the iterator
   */
  Iterator<T> iterator();

  /**
   * First optional.
   *
   * @return the optional
   */
  Optional<T> first();

  /**
   * Sample m stream.
   *
   * @param number the number
   * @return the m stream
   */
  MStream<T> sample(int number);

  /**
   * Size long.
   *
   * @return the long
   */
  long count();

  /**
   * Is empty boolean.
   *
   * @return the boolean
   */
  boolean isEmpty();

  /**
   * Count by value map.
   *
   * @return the map
   */
  Map<T, Long> countByValue();

  /**
   * Distinct m stream.
   *
   * @return the m stream
   */
  MStream<T> distinct();

  /**
   * Limit m stream.
   *
   * @param number the number
   * @return the m stream
   */
  MStream<T> limit(long number);

  /**
   * Take list.
   *
   * @param n the n
   * @return the list
   */
  List<T> take(int n);

  /**
   * Skip m stream.
   *
   * @param n the n
   * @return the m stream
   */
  MStream<T> skip(long n);

  /**
   * On close.
   *
   * @param closeHandler the close handler
   */
  void onClose(Runnable closeHandler);

  /**
   * Max optional.
   *
   * @return the optional
   */
  default Optional<T> max() {
    return max(Cast.as(Ordering.natural()));
  }

  /**
   * Min optional.
   *
   * @return the optional
   */
  default Optional<T> min() {
    return min(Cast.as(Ordering.natural()));
  }

  /**
   * Sorted m stream.
   *
   * @param ascending the ascending
   * @return the m stream
   */
  MStream<T> sorted(boolean ascending);

  /**
   * Max optional.
   *
   * @param comparator the comparator
   * @return the optional
   */
  Optional<T> max(Comparator<? super T> comparator);

  /**
   * Min optional.
   *
   * @param comparator the comparator
   * @return the optional
   */
  Optional<T> min(Comparator<? super T> comparator);

  /**
   * Zip m pair stream.
   *
   * @param <U>   the type parameter
   * @param other the other
   * @return the m pair stream
   */
  <U> MPairStream<T, U> zip(MStream<U> other);

  /**
   * Zip with index m pair stream.
   *
   * @return the m pair stream
   */
  MPairStream<T, Long> zipWithIndex();

  /**
   * Map to double m double stream.
   *
   * @param function the function
   * @return the m double stream
   */
  MDoubleStream mapToDouble(ToDoubleFunction<? super T> function);

  /**
   * Cache m stream.
   *
   * @return the m stream
   */
  MStream<T> cache();

  /**
   * Union m stream.
   *
   * @param other the other
   * @return the m stream
   */
  MStream<T> union(MStream<T> other);

  /**
   * Save as text file.
   *
   * @param location the location
   */
  void saveAsTextFile(Resource location);

  /**
   * Save as text file.
   *
   * @param location the location
   */
  default void saveAsTextFile(@NonNull String location) {
    saveAsTextFile(Resources.from(location));
  }

  /**
   * Parallel m stream.
   *
   * @return the m stream
   */
  MStream<T> parallel();

  /**
   * Shuffle m stream.
   *
   * @return the m stream
   */
  default MStream<T> shuffle() {
    return shuffle(new Random());
  }

  MStream<T> shuffle(Random random);


}//END OF MStream
