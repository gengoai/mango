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

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An implementation of a Counter that wraps all calls in synchronized.
 *
 * @param <TYPE> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode
final class SynchronizedCounter<TYPE> implements Counter<TYPE>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Counter<TYPE> delegate;

   /**
    * Instantiates a new Synchronized counter.
    *
    * @param delegate the delegate
    */
   public SynchronizedCounter(Counter<TYPE> delegate) {
      this.delegate = delegate;
   }

   @Override
   public synchronized <R> Counter<R> mapKeys(Function<? super TYPE, ? extends R> function) {
      return delegate.mapKeys(function);
   }

   @Override
   public synchronized Counter<TYPE> adjustValues(DoubleUnaryOperator function) {
      return delegate.adjustValues(function);
   }

   @Override
   public synchronized Counter<TYPE> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
      return delegate.adjustValuesSelf(function);
   }

   @Override
   public synchronized Map<TYPE, Double> asMap() {
      return delegate.asMap();
   }

   @Override
   public synchronized double average() {
      return delegate.average();
   }

   @Override
   public synchronized Counter<TYPE> bottomN(int n) {
      return delegate.bottomN(n);
   }

   @Override
   public synchronized void clear() {
      delegate.clear();
   }

   @Override
   public synchronized boolean contains(TYPE item) {
      return delegate.contains(item);
   }

   @Override
   public synchronized Collection<Double> values() {
      return delegate.values();
   }

   @Override
   public synchronized Counter<TYPE> decrement(TYPE item) {
      return delegate.decrement(item);
   }

   @Override
   public synchronized Counter<TYPE> decrement(TYPE item, double amount) {
      return delegate.decrement(item, amount);
   }

   @Override
   public synchronized Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable) {
      return delegate.decrementAll(iterable);
   }

   @Override
   public synchronized Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable, double amount) {
      return delegate.decrementAll(iterable, amount);
   }

   @Override
   public synchronized Counter<TYPE> divideBySum() {
      return delegate.divideBySum();
   }

   @Override
   public synchronized double get(TYPE item) {
      return delegate.get(item);
   }

   @Override
   public synchronized Counter<TYPE> increment(TYPE item) {
      return delegate.increment(item);
   }

   @Override
   public synchronized Counter<TYPE> increment(TYPE item, double amount) {
      return delegate.increment(item, amount);
   }

   @Override
   public synchronized Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable) {
      return delegate.incrementAll(iterable);
   }

   @Override
   public synchronized Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable, double amount) {
      return delegate.incrementAll(iterable, amount);
   }

   @Override
   public synchronized boolean isEmpty() {
      return delegate.isEmpty();
   }

   @Override
   public synchronized Set<TYPE> items() {
      return delegate.items();
   }

   @Override
   public synchronized List<TYPE> itemsByCount(boolean ascending) {
      return delegate.itemsByCount(ascending);
   }

   @Override
   public synchronized Set<Map.Entry<TYPE, Double>> entries() {
      return delegate.entries();
   }

   @Override
   public synchronized double magnitude() {
      return delegate.magnitude();
   }

   @Override
   public synchronized TYPE max() {
      return delegate.max();
   }

   @Override
   public synchronized double maximumCount() {
      return delegate.maximumCount();
   }

   @Override
   public synchronized Counter<TYPE> merge(Counter<? extends TYPE> other) {
      return delegate.merge(other);
   }

   @Override
   public synchronized Counter<TYPE> merge(Map<? extends TYPE, ? extends Number> other) {
      return delegate.merge(other);
   }

   @Override
   public synchronized TYPE min() {
      return delegate.min();
   }

   @Override
   public synchronized double minimumCount() {
      return delegate.minimumCount();
   }

   @Override
   public synchronized double remove(TYPE item) {
      return delegate.remove(item);
   }

   @Override
   public synchronized Counter<TYPE> removeAll(Iterable<TYPE> items) {
      return delegate.removeAll(items);
   }

   @Override
   public synchronized TYPE sample() {
      return delegate.sample();
   }

   @Override
   public synchronized Counter<TYPE> set(TYPE item, double count) {
      return delegate.set(item, count);
   }

   @Override
   public synchronized int size() {
      return delegate.size();
   }

   @Override
   public synchronized double standardDeviation() {
      return delegate.standardDeviation();
   }

   @Override
   public synchronized double sum() {
      return delegate.sum();
   }

   @Override
   public synchronized Counter<TYPE> topN(int n) {
      return delegate.topN(n);
   }

   @Override
   public synchronized Counter<TYPE> filterByKey(@NonNull Predicate<? super TYPE> predicate) {
      return delegate.filterByKey(predicate);
   }

   @Override
   public synchronized Counter<TYPE> filterByValue(@NonNull DoublePredicate doublePredicate) {
      return delegate.filterByValue(doublePredicate);
   }

   @Override
   public Counter<TYPE> copy() {
      return Counters.synchronizedCounter(delegate.copy());
   }

   @Override
   public String toString() {
      return delegate.toString();
   }


}//END OF ForwardingCounter
