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

package com.davidbracewell.stream.accumulator;

import com.davidbracewell.EnhancedDoubleStatistics;
import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
public class StatisticsAccumulatorV2 extends AccumulatorV2<Double, EnhancedDoubleStatistics> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final EnhancedDoubleStatistics statistics = new EnhancedDoubleStatistics();

   @Override
   public boolean isZero() {
      return statistics.getCount() == 0;
   }

   @Override
   public AccumulatorV2<Double, EnhancedDoubleStatistics> copy() {
      StatisticsAccumulatorV2 copy = new StatisticsAccumulatorV2();
      copy.statistics.combine(statistics);
      return copy;
   }

   public void combine(@NonNull EnhancedDoubleStatistics statistics) {
      this.statistics.combine(statistics);
   }

   @Override
   public void reset() {
      statistics.clear();
   }

   @Override
   public void add(Double v) {
      statistics.accept(v);
   }

   public void add(double v) {
      statistics.accept(v);
   }

   @Override
   public void merge(@NonNull AccumulatorV2<Double, EnhancedDoubleStatistics> other) {
      Preconditions.checkArgument(other instanceof StatisticsAccumulatorV2);
      statistics.combine(Cast.<StatisticsAccumulatorV2>as(other).statistics);
   }

   @Override
   public EnhancedDoubleStatistics value() {
      return statistics;
   }
}//END OF StatisticsAccumulatorV2
