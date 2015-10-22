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

import java.util.OptionalDouble;
import java.util.PrimitiveIterator;

/**
 * The interface M double stream.
 *
 * @author David B. Bracewell
 */
public interface MDoubleStream extends AutoCloseable {

  /**
   * Sum double.
   *
   * @return the double
   */
  double sum();

  /**
   * First optional double.
   *
   * @return the optional double
   */
  OptionalDouble first();

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
  <T> MStream<T> mapToObj(SerializableDoubleFunction<? extends T> function);

  /**
   * Distinct m double stream.
   *
   * @return the m double stream
   */
  MDoubleStream distinct();

  /**
   * All match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean allMatch(SerializableDoublePredicate predicate);

  /**
   * Any match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean anyMatch(SerializableDoublePredicate predicate);

  /**
   * None match boolean.
   *
   * @param predicate the predicate
   * @return the boolean
   */
  boolean noneMatch(SerializableDoublePredicate predicate);

  /**
   * Filter m double stream.
   *
   * @param predicate the predicate
   * @return the m double stream
   */
  MDoubleStream filter(SerializableDoublePredicate predicate);

  /**
   * For each.
   *
   * @param consumer the consumer
   */
  void forEach(SerializableDoubleConsumer consumer);

  /**
   * Iterator primitive iterator . of double.
   *
   * @return the primitive iterator . of double
   */
  PrimitiveIterator.OfDouble iterator();

  /**
   * Limit m double stream.
   *
   * @param n the n
   * @return the m double stream
   */
  MDoubleStream limit(int n);

  /**
   * Skip m double stream.
   *
   * @param n the n
   * @return the m double stream
   */
  MDoubleStream skip(int n);

  /**
   * Map to long m long stream.
   *
   * @param function the function
   * @return the m long stream
   */
  MLongStream mapToLong(SerializableDoubleToLongFunction function);

  /**
   * Map m double stream.
   *
   * @param mapper the mapper
   * @return the m double stream
   */
  MDoubleStream map(SerializableDoubleUnaryOperator mapper);

  /**
   * Min optional double.
   *
   * @return the optional double
   */
  OptionalDouble min();

  /**
   * Max optional double.
   *
   * @return the optional double
   */
  OptionalDouble max();

  /**
   * Stddev double.
   *
   * @return the double
   */
  double stddev();

  /**
   * Mean double.
   *
   * @return the double
   */
  double mean();

  /**
   * Statistics enhanced double statistics.
   *
   * @return the enhanced double statistics
   */
  EnhancedDoubleStatistics statistics();

  /**
   * Reduce optional double.
   *
   * @param operator the operator
   * @return the optional double
   */
  OptionalDouble reduce(SerializableDoubleBinaryOperator operator);

  /**
   * Reduce double.
   *
   * @param zeroValue the zero value
   * @param operator  the operator
   * @return the double
   */
  double reduce(double zeroValue, SerializableDoubleBinaryOperator operator);

  /**
   * Sorted m double stream.
   *
   * @return the m double stream
   */
  MDoubleStream sorted();

  /**
   * To array double [ ].
   *
   * @return the double [ ]
   */
  double[] toArray();

  /**
   * Peek m double stream.
   *
   * @param action the action
   * @return the m double stream
   */
  MDoubleStream peek(SerializableDoubleConsumer action);

  /**
   * Flat map m double stream.
   *
   * @param mapper the mapper
   * @return the m double stream
   */
  MDoubleStream flatMap(SerializableDoubleFunction<double[]> mapper);

}//END OF MDoubleStream
