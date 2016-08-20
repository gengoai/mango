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

package com.davidbracewell;

import com.davidbracewell.collection.Sorting;
import com.davidbracewell.collection.Streams;
import com.davidbracewell.conversion.Cast;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;

/**
 * @author David B. Bracewell
 */
public interface Math2 {

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
    return Streams.asStream(iterable)
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

}//END OF Math2
