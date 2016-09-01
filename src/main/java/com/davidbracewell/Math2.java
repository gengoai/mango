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
import lombok.NonNull;

import java.util.Collection;
import java.util.Comparator;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

/**
 * <p>Common math routines.</p>
 *
 * @author David B. Bracewell
 */
public interface Math2 {


  /**
   * Rescale double.
   *
   * @param value       the value
   * @param originalMin the original min
   * @param originalMax the original max
   * @param newMin      the new min
   * @param newMax      the new max
   * @return the double
   */
  static double rescale(double value, double originalMin, double originalMax, double newMin, double newMax) {
    return ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin;
  }

  /**
   * Clip double.
   *
   * @param value the value
   * @param min   the min
   * @param max   the max
   * @return the double
   */
  static double clip(double value, double min, double max) {
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    }
    return value;
  }


  /**
   * Clip int.
   *
   * @param value the value
   * @param min   the min
   * @param max   the max
   * @return the int
   */
  static int clip(int value, int min, int max) {
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    }
    return value;
  }


  /**
   * Clip long.
   *
   * @param value the value
   * @param min   the min
   * @param max   the max
   * @return the long
   */
  static long clip(long value, long min, long max) {
    if (value < min) {
      return min;
    } else if (value > max) {
      return max;
    }
    return value;
  }

  /**
   * Sum double.
   *
   * @param iterable the iterable
   * @return the double
   */
  static double sum(@NonNull Iterable<? extends Number> iterable) {
    return summaryStatistics(iterable).getSum();
  }


  /**
   * Sum double.
   *
   * @param doubles the doubles
   * @return the double
   */
  static double sum(double... doubles) {
    return summaryStatistics(doubles).getSum();
  }

  /**
   * Sum double.
   *
   * @param ints the ints
   * @return the double
   */
  static double sum(int... ints) {
    return summaryStatistics(ints).getSum();
  }

  /**
   * Sum double.
   *
   * @param longs the longs
   * @return the double
   */
  static double sum(long... longs) {
    return summaryStatistics(longs).getSum();
  }


  /**
   * Summary statistics double summary statistics.
   *
   * @param doubles the doubles
   * @return the double summary statistics
   */
  static EnhancedDoubleStatistics summaryStatistics(double... doubles) {
    DoubleStream stream = doubles == null ? DoubleStream.empty() : DoubleStream.of(doubles);
    return stream.collect(EnhancedDoubleStatistics::new,
                          EnhancedDoubleStatistics::accept,
                          EnhancedDoubleStatistics::combine);
  }


  /**
   * Summary statistics double summary statistics.
   *
   * @param ints the ints
   * @return the double summary statistics
   */
  static EnhancedDoubleStatistics summaryStatistics(int... ints) {
    DoubleStream stream = ints == null ? DoubleStream.empty() : IntStream.of(ints).mapToDouble(i -> i);
    return stream.collect(EnhancedDoubleStatistics::new,
                          EnhancedDoubleStatistics::accept,
                          EnhancedDoubleStatistics::combine);
  }

  /**
   * Summary statistics double summary statistics.
   *
   * @param longs the longs
   * @return the double summary statistics
   */
  static EnhancedDoubleStatistics summaryStatistics(long... longs) {
    DoubleStream stream = longs == null ? DoubleStream.empty() : LongStream.of(longs).mapToDouble(i -> i);
    return stream.collect(EnhancedDoubleStatistics::new,
                          EnhancedDoubleStatistics::accept,
                          EnhancedDoubleStatistics::combine);
  }

  /**
   * Analyze enhanced double statistics.
   *
   * @param iterable the iterable
   * @return the enhanced double statistics
   */
  static EnhancedDoubleStatistics summaryStatistics(Iterable<? extends Number> iterable) {
    if (iterable == null) {
      return new EnhancedDoubleStatistics();
    }

    return Streams.asStream(iterable)
                  .mapToDouble(Number::doubleValue)
                  .collect(EnhancedDoubleStatistics::new,
                           EnhancedDoubleStatistics::accept,
                           EnhancedDoubleStatistics::combine);
  }


  /**
   * Arg max long.
   *
   * @param doubles the doubles
   * @return the long
   */
  static long argMax(@NonNull double... doubles) {
    double max = Double.NEGATIVE_INFINITY;
    int index = -1;
    for (int i = 0; i < doubles.length; i++) {
      if (doubles[i] > max) {
        index = i;
        max = doubles[i];
      }
    }
    return index;
  }


  /**
   * Arg min long.
   *
   * @param doubles the doubles
   * @return the long
   */
  static long argMin(@NonNull double... doubles) {
    double min = Double.POSITIVE_INFINITY;
    int index = -1;
    for (int i = 0; i < doubles.length; i++) {
      if (doubles[i] < min) {
        index = i;
        min = doubles[i];
      }
    }
    return index;
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
