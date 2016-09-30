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

import com.davidbracewell.collection.counter.MultiCounter;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.tuple.Tuple2;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class SparkMultiCounterAccumulator<K1, K2> extends BaseSparkAccumulator<Tuple2<K1, K2>, MultiCounter<K1, K2>> implements MMultiCounterAccumulator<K1, K2> {
   private static final long serialVersionUID = 1L;

   public SparkMultiCounterAccumulator(MultiCounterAccumulatorV2 accumulatorV2) {
      super(Cast.as(accumulatorV2));
   }

   @Override
   public void merge(MultiCounter<K1, K2> other) {
      Cast.<MultiCounterAccumulatorV2<K1, K2>>as(accumulatorV2).merge(other);
   }

   @Override
   public void increment(K1 firstKey, K2 secondKey) {
      accumulatorV2.add($(firstKey, secondKey));
   }

}//END OF SparkMapAccumulator
