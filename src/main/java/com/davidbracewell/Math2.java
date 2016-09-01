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
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.DoubleUnaryOperator;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.davidbracewell.Validations.validateArgument;
import static com.davidbracewell.tuple.Tuples.$;

/**
 * <p>Commonly needed math routines and methods that work over arrays and iterable. </p>
 *
 * @author David B. Bracewell
 */
public interface Math2 {


  /**
   * <p>Adjusts the values in a given double array using the supplied operator. </p>
   *
   * @param operator the operator to use for adjusting a value
   * @param values   the values to be adjusted
   * @return An array of doubles containing f(x) where f is the operator and x is a value in the supplied array.
   * @throws NullPointerException if the operator or values are null
   */
  static double[] adjust(@NonNull DoubleUnaryOperator operator, @NonNull double... values) {
    return DoubleStream.of(values).map(operator).toArray();
  }


  /**
   * <p>Adjusts the numbers in a given iterable using the supplied operator. </p>
   *
   * @param operator the operator to use for adjusting a value
   * @param values   the values to be adjusted
   * @return A List of Double containing f(x) where f is the operator and x is a value in the supplied iterable.
   * @throws NullPointerException if the operator or values are null
   */
  static List<Double> adjust(@NonNull DoubleUnaryOperator operator, @NonNull Iterable<? extends Number> values) {
    return Streams.asStream(values)
                  .mapToDouble(n -> operator.applyAsDouble(n.doubleValue()))
                  .mapToObj(d -> d)
                  .collect(Collectors.toList());
  }


  /**
   * <p>Determines the maximum value and its index in supplied array of doubles</p>
   *
   * @param values the values to find the arg max of
   * @return A tuple of (index, max value) or (-1, NEGATIVE_INFINITY) if the supplied array is empty.
   * @throws NullPointerException if the values are null
   */
  static Tuple2<Integer, Double> argMax(@NonNull double... values) {
    double max = Double.NEGATIVE_INFINITY;
    int index = -1;
    for (int i = 0; i < values.length; i++) {
      if (values[i] > max) {
        index = i;
        max = values[i];
      }
    }
    return $(index, max);
  }


  /**
   * <p>Determines the minimum value and its index in supplied array of doubles</p>
   *
   * @param values the values to find the arg min of
   * @return A tuple of (index, min value) or (-1, POSITIVE_INFINITY) if the supplied array is empty.
   * @throws NullPointerException if the values are null
   */
  static Tuple2<Integer, Double> argMin(@NonNull double... values) {
    double min = Double.POSITIVE_INFINITY;
    int index = -1;
    for (int i = 0; i < values.length; i++) {
      if (values[i] < min) {
        index = i;
        min = values[i];
      }
    }
    return $(index, min);
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


  /**
   * <p>Rescales a value from an old range to a new range, e.g. change the value 2 in a 1 to 5 scale to the value 3.25
   * in a 1 to 10 scale</p>
   *
   * @param value       the value to rescale
   * @param originalMin the lower bound of the original range
   * @param originalMax the upper bound of the original range
   * @param newMin      the lower bound of the new range
   * @param newMax      the upper bound of the new range
   * @return the given value rescaled to fall between newMin and new Max
   * @throws IllegalArgumentException if originalMax <= originalMin or newMax <= newMin
   */
  static double rescale(double value, double originalMin, double originalMax, double newMin, double newMax) {
    validateArgument(originalMax > originalMin, "original upper bound must be > original lower bound");
    validateArgument(newMax > newMin, "new upper bound must be > new lower bound");
    return ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin;
  }

  /**
   * <p>Clips a value to ensure it falls between the lower or upper bound of range.</p>
   *
   * @param value the value to clip
   * @param min   the lower bound of the range
   * @param max   the upper bound of the range
   * @return the clipped value
   */
  static double clip(double value, double min, double max) {
    validateArgument(max > min, "upper bound must be > lower bound");
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    }
    return value;
  }


  /**
   * <p>Sums the numbers in a given iterable treating them as doubles.</p>
   *
   * @param values the iterable of numbers to sum
   * @return the sum of the iterable
   * @throws NullPointerException if the values are null
   */
  static double sum(@NonNull Iterable<? extends Number> values) {
    return summaryStatistics(values).getSum();
  }


  /**
   * <p>Sums the numbers in the given array.</p>
   *
   * @param values the values to sum
   * @return the sum of the values
   * @throws NullPointerException if the values are null
   */
  static double sum(@NonNull double... values) {
    return DoubleStream.of(values).sum();
  }

  /**
   * <p>Sums the numbers in the given array.</p>
   *
   * @param values the values to sum
   * @return the sum of the values
   * @throws NullPointerException if the values are null
   */
  static int sum(@NonNull int... values) {
    return IntStream.of(values).sum();
  }

  /**
   * <p>Sums the numbers in the given array.</p>
   *
   * @param values the values to sum
   * @return the sum of the values
   * @throws NullPointerException if the values are null
   */
  static long sum(@NonNull long... values) {
    return LongStream.of(values).sum();
  }


  /**
   * <p>Calculates the summary statistics for the values in the given array.</p>
   *
   * @param values the values to calculate summary statistics over
   * @return the summary statistics of the given array
   * @throws NullPointerException if the values are null
   */
  static EnhancedDoubleStatistics summaryStatistics(@NonNull double... values) {
    return DoubleStream.of(values).collect(EnhancedDoubleStatistics::new,
                                           EnhancedDoubleStatistics::accept,
                                           EnhancedDoubleStatistics::combine);
  }


  /**
   * <p>Calculates the summary statistics for the values in the given array.</p>
   *
   * @param values the values to calculate summary statistics over
   * @return the summary statistics of the given array
   * @throws NullPointerException if the values are null
   */
  static EnhancedDoubleStatistics summaryStatistics(@NonNull int... values) {
    return IntStream.of(values).mapToDouble(i -> i).collect(EnhancedDoubleStatistics::new,
                                                            EnhancedDoubleStatistics::accept,
                                                            EnhancedDoubleStatistics::combine);
  }

  /**
   * <p>Calculates the summary statistics for the values in the given array.</p>
   *
   * @param values the values to calculate summary statistics over
   * @return the summary statistics of the given array
   * @throws NullPointerException if the values are null
   */
  static EnhancedDoubleStatistics summaryStatistics(@NonNull long... values) {
    return LongStream.of(values).mapToDouble(i -> i).collect(EnhancedDoubleStatistics::new,
                                                             EnhancedDoubleStatistics::accept,
                                                             EnhancedDoubleStatistics::combine);
  }

  /**
   * <p>Calculates the summary statistics for the values in the given iterable.</p>
   *
   * @param values the values to calculate summary statistics over
   * @return the summary statistics of the given iterable
   * @throws NullPointerException if the iterable is null
   */
  static EnhancedDoubleStatistics summaryStatistics(@NonNull Iterable<? extends Number> values) {
    return Streams.asStream(values)
                  .mapToDouble(Number::doubleValue)
                  .collect(EnhancedDoubleStatistics::new,
                           EnhancedDoubleStatistics::accept,
                           EnhancedDoubleStatistics::combine);
  }


}//END OF Math2
