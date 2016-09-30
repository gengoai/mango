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
import com.davidbracewell.collection.counter.MultiCounters;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.Optional;

/**
 * @author David B. Bracewell
 */
public class LocalMultiCounterAccumulator<K1, K2> implements MMultiCounterAccumulator<K1, K2> {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final MultiCounter<K1, K2> counter = MultiCounters.synchronizedMultiCounter();

   public LocalMultiCounterAccumulator() { this(null);}

   public LocalMultiCounterAccumulator(String name) {this.name = name;}

   @Override
   public void add(@NonNull Tuple2<K1, K2> objects) {
      counter.increment(objects.v1, objects.v2);
   }

   @Override
   public MultiCounter<K1, K2> value() {
      return counter;
   }

   @Override
   public boolean isZero() {
      return false;
   }

   @Override
   public void merge(@NonNull MAccumulator<Tuple2<K1, K2>, MultiCounter<K1, K2>> other) {
      Preconditions.checkArgument(LocalMultiCounterAccumulator.class == other.getClass(),
                                  "Only other " + this.getClass().getSimpleName() + " can be merged");
      counter.merge(other.value());
   }

   @Override
   public Optional<String> name() {
      return Optional.ofNullable(name);
   }

   @Override
   public void reset() {
      counter.clear();
   }

   @Override
   public void increment(K1 firstKey, K2 secondKey) {
      counter.increment(firstKey, secondKey);
   }

   @Override
   public void increment(K1 firstKey, K2 secondKey, double value) {

   }

   @Override
   public void merge(@NonNull MultiCounter<K1, K2> other) {
      this.counter.merge(other);
   }
}//END OF LocalMapAccumulator
