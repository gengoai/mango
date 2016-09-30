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
import com.davidbracewell.collection.counter.Counters;
import lombok.NonNull;

import java.util.Optional;

/**
 * <p>A counter accumulator implementation ofr local Java streams</p>
 *
 * @param <T> the component type parameter of the counter
 * @author David B. Bracewell
 */
public class LocalCounterAccumulator<T> implements MCounterAccumulator<T> {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final Counter<T> counter = Counters.synchronizedCounter();

   /**
    * Instantiates a new Local counter accumulator.
    *
    * @param name the name of the counter
    */
   public LocalCounterAccumulator(String name) {
      this.name = name;
   }

   @Override
   public void add(T t) {
      counter.increment(t);
   }

   @Override
   public void increment(T item, double amount) {
      counter.increment(item, amount);
   }

   @Override
   public void merge(@NonNull Counter<T> counter) {
      this.counter.merge(counter);
   }

   @Override
   public void merge(@NonNull MAccumulator<T, Counter<T>> other) {
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
   public Counter<T> value() {
      return counter;
   }

}// END OF LocalCounterAccumulator
