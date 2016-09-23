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

import lombok.NonNull;

import java.util.Optional;
import java.util.concurrent.atomic.DoubleAdder;

/**
 * The type Local double accumulator.
 *
 * @author David B. Bracewell
 */
public class LocalDoubleAccumulator implements MDoubleAccumulator {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final DoubleAdder value;

   /**
    * Instantiates a new Local double accumulator.
    *
    * @param value the value
    */
   public LocalDoubleAccumulator(double value) {
      this(value, null);
   }

   /**
    * Instantiates a new Local double accumulator.
    *
    * @param value the value
    * @param name  the name
    */
   public LocalDoubleAccumulator(double value, String name) {
      this.name = name;
      this.value = new DoubleAdder();
      this.value.add(value);
   }

   @Override
   public Double value() {
      return value.doubleValue();
   }

   @Override
   public void merge(@NonNull MAccumulator<Double, Double> other) {
      add(other.value());
   }

   @Override
   public Optional<String> name() {
      return Optional.of(name);
   }

   @Override
   public void reset() {
      value.reset();
   }

   @Override
   public void add(double value) {
      this.value.add(value);
   }

}// END OF LocalMDoubleAccumulator
