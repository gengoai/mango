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

package com.davidbracewell.collection.counter;

import lombok.EqualsAndHashCode;

import java.util.Collections;
import java.util.Map;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
final class UnmodifiableCounter<TYPE> extends ForwardingCounter<TYPE> {
   private static final long serialVersionUID = 1L;
   private final Counter<TYPE> backing;

   UnmodifiableCounter(Counter<TYPE> backing) {
      this.backing = backing;
   }

   @Override
   public Map<TYPE, Double> asMap() {
      return Collections.unmodifiableMap(super.asMap());
   }

   @Override
   public Counter<TYPE> decrement(TYPE item) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> decrement(TYPE item, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   protected Counter<TYPE> delegate() {
      return backing;
   }

   @Override
   public Counter<TYPE> divideBySum() {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> increment(TYPE item) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> increment(TYPE item, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Set<TYPE> items() {
      return Collections.unmodifiableSet(super.items());
   }

   @Override
   public Set<Map.Entry<TYPE, Double>> entries() {
      return Collections.unmodifiableSet(delegate().entries());
   }

   @Override
   public Counter<TYPE> merge(Counter<? extends TYPE> other) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> merge(Map<? extends TYPE, ? extends Number> other) {
      throw new UnsupportedOperationException();
   }

   @Override
   public double remove(TYPE item) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> removeAll(Iterable<TYPE> items) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<TYPE> set(TYPE item, double count) {
      throw new UnsupportedOperationException();
   }

   @Override
   public String toString() {
      return delegate().toString();
   }


}//END OF UnmodifiableCounter
