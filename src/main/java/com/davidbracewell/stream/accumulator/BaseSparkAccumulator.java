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

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;
import scala.runtime.AbstractFunction0;

import java.util.Optional;

/**
 * @author David B. Bracewell
 */
public class BaseSparkAccumulator<IN, OUT> implements MAccumulator<IN, OUT> {
   private static final long serialVersionUID = 1L;
   protected final AccumulatorV2<IN, OUT> accumulatorV2;

   public BaseSparkAccumulator(@NonNull AccumulatorV2<IN, OUT> accumulatorV2) {
      this.accumulatorV2 = accumulatorV2;
   }

   @Override
   public void add(IN in) {
      accumulatorV2.add(in);
   }

   @Override
   public boolean isZero() {
      return accumulatorV2.isZero();
   }

   @Override
   public void merge(@NonNull MAccumulator<IN, OUT> other) {
      Preconditions.checkArgument(this.getClass() == other.getClass(),
                                  "Only other " + this.getClass().getSimpleName() + " can be merged");
      accumulatorV2.merge(Cast.<BaseSparkAccumulator<IN, OUT>>as(other).accumulatorV2);
   }

   @Override
   public Optional<String> name() {
      return Optional.ofNullable(accumulatorV2.name().getOrElse(new AbstractFunction0<String>() {
         @Override
         public String apply() {
            return null;
         }
      }));
   }

   @Override
   public void reset() {
      accumulatorV2.reset();
   }

   @Override
   public OUT value() {
      return accumulatorV2.value();
   }

}// END OF BaseSparkAccumulator
