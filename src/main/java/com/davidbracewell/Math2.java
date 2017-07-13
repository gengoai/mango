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

import com.davidbracewell.collection.Streams;
import com.google.common.math.DoubleMath;
import lombok.NonNull;
import org.apache.commons.math3.util.FastMath;

import java.math.BigDecimal;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.LongStream;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * <p>Commonly needed math routines and methods that work over arrays and iterable. </p>
 *
 * @author David B. Bracewell
 */
public interface Math2 {

   /**
    * Adds two doubles (useful as a method reference)
    *
    * @param v1 value 1
    * @param v2 value 2
    * @return the sum of value 1 and value 2
    */
   static double add(double v1, double v2) {
      return v1 + v2;
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
      checkArgument(max > min, "upper bound must be > lower bound");
      if (value < min) {
         return min;
      } else if (value > max) {
         return max;
      }
      return value;
   }

   /**
    * Divides two doubles (useful as a method reference)
    *
    * @param v1 value 1
    * @param v2 value 2
    * @return the result of value 1 divided by value 2
    */
   static double divide(double v1, double v2) {
      return v1 / v2;
   }

   /**
    * Calculates the base 2 log of a given number
    *
    * @param number the number to calculate the base 2 log of
    * @return the base 2 log of the given number
    */
   static double log2(double number) {
      return DoubleMath.log2(number);
   }

   /**
    * Multiplies two doubles (useful as a method reference)
    *
    * @param v1 value 1
    * @param v2 value 2
    * @return the result of value 1 * value 2
    */
   static double multiply(double v1, double v2) {
      return v1 * v2;
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
      checkArgument(originalMax > originalMin, "original upper bound must be > original lower bound");
      checkArgument(newMax > newMin, "new upper bound must be > new lower bound");
      return ((value - originalMin) / (originalMax - originalMin)) * (newMax - newMin) + newMin;
   }

   static double safeLog(double d) {
      if (Double.isFinite(d)) {
         return (d <= 0d || d <= -0d) ? -10 : FastMath.log(d);
      }
      return 0d;
   }

   /**
    * Subtracts two doubles (useful as a method reference)
    *
    * @param v1 value 1
    * @param v2 value 2
    * @return the result of value 1 minus value 2
    */
   static double subtract(double v1, double v2) {
      return v1 - v2;
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
      return DoubleStream.of(values).parallel().collect(EnhancedDoubleStatistics::new,
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
      return IntStream.of(values).parallel().mapToDouble(i -> i).collect(EnhancedDoubleStatistics::new,
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
      return LongStream.of(values).parallel().mapToDouble(i -> i).collect(EnhancedDoubleStatistics::new,
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
                    .parallel()
                    .mapToDouble(Number::doubleValue)
                    .collect(EnhancedDoubleStatistics::new,
                             EnhancedDoubleStatistics::accept,
                             EnhancedDoubleStatistics::combine);
   }

   /**
    * Truncates a double to a given precision.
    *
    * @param value     the value to truncate
    * @param precision the number of decimal places
    * @return the double
    */
   static double truncate(double value, int precision) {
      return BigDecimal.valueOf(value).setScale(precision, BigDecimal.ROUND_HALF_UP).doubleValue();
   }

}//END OF Math2
