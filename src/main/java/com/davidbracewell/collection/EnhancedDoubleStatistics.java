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

package com.davidbracewell.collection;

import com.davidbracewell.function.SerializableDoubleConsumer;
import lombok.ToString;

/**
 * The type Enhanced double statistics.
 *
 * @author David B. Bracewell
 */
@ToString(exclude = "sumOfSq")
public final class EnhancedDoubleStatistics implements SerializableDoubleConsumer {
  private static final long serialVersionUID = 1L;
  private double min = Double.POSITIVE_INFINITY;
  private double max = Double.NEGATIVE_INFINITY;
  private double sum = 0;
  private double sumOfSq = 0;
  private int count = 0;

  @Override
  public void accept(double value) {
    min = Math.min(min, value);
    max = Math.max(max, value);
    sum += value;
    sumOfSq += value * value;
    count++;
  }

  public void clear(){
    this.min = Double.POSITIVE_INFINITY;
    this.max = Double.NEGATIVE_INFINITY;
    this.sum = 0;
    this.sumOfSq = 0;
    this.count = 0;
  }

  /**
   * Combine void.
   *
   * @param other the other
   */
  public void combine(EnhancedDoubleStatistics other) {
    count += other.count;
    sum += other.sum;
    sumOfSq += other.sumOfSq;
    min = Math.min(min, other.min);
    max = Math.max(max, other.max);
  }

  /**
   * Gets count.
   *
   * @return the count
   */
  public double getCount() {
    return count;
  }

  /**
   * Gets sum.
   *
   * @return the sum
   */
  public double getSum() {
    return sum;
  }

  /**
   * Gets sum of squares.
   *
   * @return the sum of squares
   */
  public double getSumOfSquares() {
    return sumOfSq;
  }

  /**
   * Gets average.
   *
   * @return the average
   */
  public double getAverage() {
    return getCount() > 0 ? getSum() / getCount() : 0;
  }

  /**
   * Gets min.
   *
   * @return the min
   */
  public double getMin() {
    return min;
  }

  /**
   * Gets max.
   *
   * @return the max
   */
  public double getMax() {
    return max;
  }

  /**
   * Gets sample standard deviation.
   *
   * @return the sample standard deviation
   */
  public double getSampleStandardDeviation() {
    if (getCount() <= 0) {
      return Double.NaN;
    } else if ( getCount() == 1){
      return 0d;
    }
    return Math.sqrt(getSampleVariance());
  }

  /**
   * Gets sample variance.
   *
   * @return the sample variance
   */
  public double getSampleVariance() {
    if (getCount() <= 1) {
      return 0d;
    }
    return Math.abs(getSumOfSquares() - getAverage() * getSum()) / (getCount() - 1);
  }

  /**
   * Gets population standard deviation.
   *
   * @return the population standard deviation
   */
  public double getPopulationStandardDeviation() {
    if (getCount() <= 1) {
      return 0d;
    }
    return Math.sqrt(getPopulationVariance());
  }

  /**
   * Gets population variance.
   *
   * @return the population variance
   */
  public double getPopulationVariance() {
    if (getCount() <= 1) {
      return 0d;
    }
    return Math.abs(getSumOfSquares() - getAverage() * getSum()) / getCount();
  }


}//END OF EnhancedDoubleStatistics
