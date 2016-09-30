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

import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;

/**
 * <p>Counter accumulator implementation for Spark streams</p>
 *
 * @param <E> the component type parameter of the cunter
 * @author David B. Bracewell
 */
public class SparkCounterAccumulator<E> extends BaseSparkAccumulator<E, Counter<E>> implements MCounterAccumulator<E> {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new Spark counter accumulator.
    *
    * @param accumulatorV2 the spark accumulator to wrap
    */
   public SparkCounterAccumulator(AccumulatorV2<E, Counter<E>> accumulatorV2) {
      super(accumulatorV2);
   }

   @Override
   public void increment(E item, double amount) {
      Cast.<CounterAccumulatorV2<E>>as(accumulatorV2).increment(item, amount);
   }

   @Override
   public void merge(@NonNull Counter<E> counter) {
      Cast.<CounterAccumulatorV2<E>>as(accumulatorV2).merge(counter);
   }

}//END OF SparkCounterAccumulator
