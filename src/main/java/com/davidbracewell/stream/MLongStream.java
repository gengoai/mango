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

import com.davidbracewell.collection.EnhancedDoubleStatistics;
import com.davidbracewell.function.*;

import java.util.OptionalLong;
import java.util.PrimitiveIterator;

/**
 * The interface M long stream.
 *
 * @author David B. Bracewell
 */
public interface MLongStream extends AutoCloseable {
  /**
   * Sum long.
   *
   * @return the long
   */
  long sum();

  /**
   * First optional long.
   *
   * @return the optional long
   */
  OptionalLong first();

  /**
   * Count long.
   *
   * @return the long
   */
  long count();

  /**
   * Map to obj m stream.
   *
   * @param <T>      the type parameter
   * @param function the function
   * @return the m stream
   */
  <T> MStream<T> mapToObj(SerializableLongFunction<? extends T> function);

  /**
   * Distinct m long stream.
   *
   * @return the m long stream
   */
  MLongStream distinct();

  /**
   * All match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean allMatch(SerializableLongPredicate predicate);

  /**
   * Any match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean anyMatch(SerializableLongPredicate predicate);

  /**
   * None match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean noneMatch(SerializableLongPredicate predicate);

  /**
   * Filter m long stream.
   *
   * @param predicate the predicate
   * @return the m long stream
   */
  MLongStream filter(SerializableLongPredicate predicate);

  /**
   * For each.
   *
   * @param consumer the consumer
   */
  void forEach(SerializableLongConsumer consumer);

  /**
   * Iterator primitive iterator . of double.
   *
   * @return the primitive iterator . of double
   */
  PrimitiveIterator.OfLong iterator();

  /**
   * Limit m long stream.
   *
   * @param n the n
   * @return the m long stream
   */
  MLongStream limit(int n);

  /**
   * Skip m long stream.
   *
   * @param n the n
   * @return the m long stream
   */
  MLongStream skip(int n);

  /**
   * Map to double m double stream.
   *
   * @param function the function
   * @return the m double stream
   */
  MDoubleStream mapToDouble(SerializableLongToDoubleFunction function);

  /**
   * Map m long stream.
   *
   * @param mapper the mapper
   * @return the m long stream
   */
  MLongStream map(SerializableLongUnaryOperator mapper);

  /**
   * Min optional long.
   *
   * @return the optional long
   */
  OptionalLong min();

  /**
   * Max optional long.
   *
   * @return the optional long
   */
  OptionalLong max();

  /**
   * Stddev long.
   *
   * @return the long
   */
  double stddev();

  /**
   * Mean long.
   *
   * @return the long
   */
  double mean();

  /**
   * Statistics enhanced double statistics.
   *
   * @return the enhanced double statistics
   */
  EnhancedDoubleStatistics statistics();

  /**
   * Reduce optional long.
   *
   * @param operator the operator
   * @return the optional long
   */
  OptionalLong reduce(SerializableLongBinaryOperator operator);

  /**
   * Reduce long.
   *
   * @param zeroValue the zero value
   * @param operator  the operator
   * @return the long
   */
  long reduce(long zeroValue, SerializableLongBinaryOperator operator);

  /**
   * Sorted m long stream.
   *
   * @return the m long stream
   */
  MLongStream sorted();

  /**
   * To array long [ ].
   *
   * @return the long [ ]
   */
  long[] toArray();

  /**
   * Peek m long stream.
   *
   * @param action the action
   * @return the m long stream
   */
  MLongStream peek(SerializableLongConsumer action);

  /**
   * Flat map m long stream.
   *
   * @param mapper the mapper
   * @return the m long stream
   */
  MLongStream flatMap(SerializableLongFunction<long[]> mapper);


}//END OF MLongStream
