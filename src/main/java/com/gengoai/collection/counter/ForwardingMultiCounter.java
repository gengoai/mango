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
 * A MultiCounter that forwards all of its calls to a delegate counter.
 *
 * @param <K> the first key type
 * @param <V> the second key type
 * @author David B. Bracewell
 */
public abstract class ForwardingMultiCounter<K, V> implements MultiCounter<K, V>, Serializable {
   private static final long serialVersionUID = 1L;

   protected abstract MultiCounter<K, V> delegate();

   @Override
   public MultiCounter<K, V> adjustValues(DoubleUnaryOperator function) {
      return delegate().adjustValues(function);
   }

   @Override
   public MultiCounter<K, V> adjustValuesSelf(DoubleUnaryOperator function) {
      return delegate().adjustValuesSelf(function);
   }

   @Override
   public Collection<Double> values() {
      return delegate().values();
   }

   @Override
   public void clear() {
      delegate().clear();
   }

   @Override
   public boolean contains(K item) {
      return delegate().contains(item);
   }

   @Override
   public boolean contains(K item1, V item2) {
      return delegate().contains(item1, item2);
   }

   @Override
   public Counter<V> get(K firstKey) {
      return delegate().get(firstKey);
   }

   @Override
   public boolean isEmpty() {
      return delegate().isEmpty();
   }

   @Override
   public Set<K> firstKeys() {
      return delegate().firstKeys();
   }

   @Override
   public List<Map.Entry<K, V>> itemsByCount(boolean ascending) {
      return delegate().itemsByCount(ascending);
   }

   @Override
   public MultiCounter<K, V> filterByValue(DoublePredicate predicate) {
      return delegate().filterByValue(predicate);
   }

   @Override
   public MultiCounter<K, V> filterByFirstKey(Predicate<K> predicate) {
      return delegate().filterByFirstKey(predicate);
   }

   @Override
   public MultiCounter<K, V> filterBySecondKey(Predicate<V> predicate) {
      return delegate().filterBySecondKey(predicate);
   }

   @Override
   public Set<Tuple3<K, V, Double>> entries() {
      return delegate().entries();
   }

   @Override
   public MultiCounter<K, V> merge(MultiCounter<K, V> other) {
      return delegate().merge(other);
   }

   @Override
   public Counter<V> remove(K item) {
      return delegate().remove(item);
   }

   @Override
   public double remove(K item1, V item2) {
      return delegate().remove(item1, item2);
   }

   @Override
   public MultiCounter<K, V> set(K item1, V item2, double amount) {
      return delegate().set(item1, item2, amount);
   }

   @Override
   public MultiCounter<K, V> set(K item, Counter<V> counter) {
      return delegate().set(item, counter);
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public String toString() {
      return delegate().toString();
   }

   @Override
   public Set<Map.Entry<K, V>> keyPairs() {
      return delegate().keyPairs();
   }

}//END OF ForwardingMultiCounter
