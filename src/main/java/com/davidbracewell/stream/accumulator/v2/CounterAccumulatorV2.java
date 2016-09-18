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

package com.davidbracewell.stream.accumulator.v2;

import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.counter.Counters;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;

/**
 * @author David B. Bracewell
 */
public class CounterAccumulatorV2<K> extends AccumulatorV2<K, Counter<K>> {
   private Counter<K> counter = Counters.newCounter();

   @Override
   public boolean isZero() {
      return counter.isEmpty();
   }

   @Override
   public AccumulatorV2<K, Counter<K>> copyAndReset() {
      return new CounterAccumulatorV2<>();
   }

   @Override
   public AccumulatorV2<K, Counter<K>> copy() {
      CounterAccumulatorV2<K> copy = new CounterAccumulatorV2<>();
      copy.counter.merge(this.counter);
      return copy;
   }

   @Override
   public void reset() {
      counter.clear();
   }

   @Override
   public void add(K v) {
      counter.increment(v);
   }

   @Override
   public void merge(@NonNull AccumulatorV2<K, Counter<K>> other) {
      if (other instanceof CounterAccumulatorV2) {
         this.counter.merge(Cast.<CounterAccumulatorV2<K>>as(other).counter);
      } else {
         throw new UnsupportedOperationException("Cannot merge " + other.getClass()
                                                                        .getName() + " with a Counter Accumulator");
      }
   }

   @Override
   public Counter<K> value() {
      return counter;
   }

}//END OF CounterAccumulatorV2
