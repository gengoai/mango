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

package com.gengoai.stream.accumulator;

import com.gengoai.EnhancedDoubleStatistics;
import lombok.NonNull;
import lombok.Synchronized;

/**
 * <p>An implementation of a {@link MStatisticsAccumulator} for local streams</p>
 *
 * @author David B. Bracewell
 */
public class LocalMStatisticsAccumulator extends LocalMAccumulator<Double, EnhancedDoubleStatistics> implements MStatisticsAccumulator {
   private static final long serialVersionUID = 1L;

   private final EnhancedDoubleStatistics eds = new EnhancedDoubleStatistics();

   /**
    * Instantiates a new LocalMStatisticsAccumulator.
    *
    * @param name the name of the accumulator
    */
   public LocalMStatisticsAccumulator(String name) {
      super(name);
   }

   @Override
   @Synchronized
   public void add(double value) {
      eds.accept(value);
   }

   @Override
   @Synchronized
   public void add(@NonNull Double aDouble) {
      eds.accept(aDouble);
   }

   @Override
   public LocalMAccumulator<Double, EnhancedDoubleStatistics> copy() {
      LocalMStatisticsAccumulator copy = new LocalMStatisticsAccumulator(name().orElse(null));
      copy.combine(eds);
      return copy;
   }

   @Override
   public EnhancedDoubleStatistics value() {
      return eds;
   }

   @Override
   public boolean isZero() {
      return eds.getCount() == 0;
   }

   @Override
   @Synchronized
   public void merge(@NonNull MAccumulator<Double, EnhancedDoubleStatistics> other) {
      if (other instanceof LocalMAccumulator) {
         eds.combine(other.value());
      } else {
         throw new IllegalArgumentException(getClass().getName() + " cannot merge with " + other.getClass().getName());
      }
   }

   @Override
   @Synchronized
   public void reset() {
      eds.clear();
   }

   @Override
   @Synchronized
   public void combine(@NonNull EnhancedDoubleStatistics statistics) {
      this.eds.combine(statistics);
   }
}//END OF LocalStatisticsAccumulator
