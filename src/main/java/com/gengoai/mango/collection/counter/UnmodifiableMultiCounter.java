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

package com.gengoai.mango.collection.counter;

import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Map;
import java.util.function.DoubleUnaryOperator;

/**
 * An implementation of a multi-counter that is unmodifiable
 *
 * @param <K> the first key type
 * @param <V> the second key type
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
final class UnmodifiableMultiCounter<K, V> extends ForwardingMultiCounter<K, V> {
   private static final long serialVersionUID = 292069831932346092L;
   private final MultiCounter<K, V> backing;

   /**
    * Instantiates a new Unmodifiable multi counter.
    *
    * @param backing the backing
    */
   public UnmodifiableMultiCounter(@NonNull MultiCounter<K, V> backing) {
      this.backing = backing;
   }

   @Override
   protected MultiCounter<K, V> delegate() {
      return backing;
   }

   @Override
   public MultiCounter<K, V> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
      throw new UnsupportedOperationException();
   }

   @Override
   public Counter<V> get(K firstKey) {
      return Counters.unmodifiableCounter(super.get(firstKey));
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> set(K item1, V item2, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> set(K item, @NonNull Counter<V> counter) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> decrement(K item1, V item2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> decrement(K item1, V item2, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> decrementAll(K item, Iterable<? extends V> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> decrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> divideBySum() {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> divideByKeySum() {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> increment(K item1, V item2) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> increment(K item1, V item2, double amount) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> incrementAll(K item, Iterable<? extends V> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> incrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
      throw new UnsupportedOperationException();
   }

   @Override
   public MultiCounter<K, V> removeAll(Iterable<K> items) {
      throw new UnsupportedOperationException();
   }
}//END OF UnmodifiableMultiCounter
