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

package com.gengoai.collection.counter;

import com.gengoai.tuple.Tuple3;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;

/**
 * An implementation of a multi-counter that wraps its calls in synchornized
 *
 * @param <K> the first key type
 * @param <V> the second key type
 * @author David B. Bracewell
 */
final class SynchronizedMultiCounter<K, V> implements MultiCounter<K, V>, Serializable {
   private static final long serialVersionUID = 1L;

   private final MultiCounter<K, V> delegate;

   public SynchronizedMultiCounter(MultiCounter<K, V> delegate) {
      this.delegate = delegate;
   }

   @Override
   public synchronized MultiCounter<K, V> adjustValues(DoubleUnaryOperator function) {
      return delegate.adjustValues(function);
   }

   @Override
   public synchronized MultiCounter<K, V> adjustValuesSelf(DoubleUnaryOperator function) {
      return delegate.adjustValuesSelf(function);
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof SynchronizedMultiCounter)) return false;
      final SynchronizedMultiCounter other = (SynchronizedMultiCounter) o;
      final Object this$delegate = this.delegate;
      final Object other$delegate = other.delegate;
      if (this$delegate == null ? other$delegate != null : !this$delegate.equals(other$delegate)) return false;
      return true;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $delegate = this.delegate;
      result = result * PRIME + ($delegate == null ? 43 : $delegate.hashCode());
      return result;
   }

   @Override
   public synchronized Collection<Double> values() {
      return delegate.values();
   }

   @Override
   public synchronized void clear() {
      delegate.clear();
   }

   @Override
   public synchronized boolean contains(K item) {
      return delegate.contains(item);
   }

   @Override
   public synchronized boolean contains(K item1, V item2) {
      return delegate.contains(item1, item2);
   }

   @Override
   public synchronized Counter<V> get(K firstKey) {
      return delegate.get(firstKey);
   }

   @Override
   public synchronized boolean isEmpty() {
      return delegate.isEmpty();
   }

   @Override
   public synchronized Set<K> firstKeys() {
      return delegate.firstKeys();
   }

   @Override
   public synchronized List<Map.Entry<K, V>> itemsByCount(boolean ascending) {
      return delegate.itemsByCount(ascending);
   }

   @Override
   public synchronized MultiCounter<K, V> filterByValue(DoublePredicate predicate) {
      return delegate.filterByValue(predicate);
   }

   @Override
   public synchronized MultiCounter<K, V> filterByFirstKey(Predicate<K> predicate) {
      return delegate.filterByFirstKey(predicate);
   }

   @Override
   public synchronized MultiCounter<K, V> filterBySecondKey(Predicate<V> predicate) {
      return delegate.filterBySecondKey(predicate);
   }

   @Override
   public synchronized Set<Tuple3<K, V, Double>> entries() {
      return delegate.entries();
   }

   @Override
   public synchronized MultiCounter<K, V> merge(MultiCounter<K, V> other) {
      return delegate.merge(other);
   }

   @Override
   public synchronized Counter<V> remove(K item) {
      return delegate.remove(item);
   }

   @Override
   public synchronized double remove(K item1, V item2) {
      return delegate.remove(item1, item2);
   }

   @Override
   public synchronized MultiCounter<K, V> set(K item1, V item2, double amount) {
      return delegate.set(item1, item2, amount);
   }

   @Override
   public synchronized MultiCounter<K, V> set(K item, Counter<V> counter) {
      return delegate.set(item, counter);
   }

   @Override
   public synchronized int size() {
      return delegate.size();
   }

   @Override
   public String toString() {
      return delegate.toString();
   }

   @Override
   public Set<Map.Entry<K, V>> keyPairs() {
      return delegate.keyPairs();
   }
}//END OF SynchronizedMultiCounter
