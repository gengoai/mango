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

import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * @author David B. Bracewell
 */
public abstract class ForwardingCounter<TYPE> implements Counter<TYPE>, Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   public <R> Counter<R> mapKeys(Function<? super TYPE, ? extends R> function) {
      if (delegate() == null) {
         return new HashMapCounter<>();
      }
      return delegate().mapKeys(function);
   }

   @Override
   public Counter<TYPE> adjustValues(DoubleUnaryOperator function) {
      if (delegate() == null) {
         return this;
      }
      return delegate().adjustValues(function);
   }

   @Override
   public Counter<TYPE> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
      if (delegate() == null) {
         return this;
      }
      return delegate().adjustValuesSelf(function);
   }

   @Override
   public Map<TYPE, Double> asMap() {
      if (delegate() == null) {
         return Collections.emptyMap();
      }
      return delegate().asMap();
   }

   @Override
   public double average() {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().average();
   }

   @Override
   public Counter<TYPE> bottomN(int n) {
      if (delegate() == null) {
         return this;
      }
      return delegate().bottomN(n);
   }

   @Override
   public void clear() {
      if (delegate() != null) {
         delegate().clear();
      }
   }

   @Override
   public boolean contains(TYPE item) {
      return delegate() != null && delegate().contains(item);
   }

   @Override
   public Collection<Double> values() {
      if (delegate() == null) {
         return Collections.emptyList();
      }
      return delegate().values();
   }

   @Override
   public Counter<TYPE> decrement(TYPE item) {
      return decrement(item, 1);
   }

   @Override
   public Counter<TYPE> decrement(TYPE item, double amount) {
      return increment(item, -amount);
   }

   @Override
   public Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable) {
      if (iterable != null) {
         iterable.forEach(this::decrement);
      }
      return this;
   }

   @Override
   public Counter<TYPE> decrementAll(Iterable<? extends TYPE> iterable, double amount) {
      if (iterable != null) {
         iterable.forEach(i -> decrement(i, amount));
      }
      return this;
   }

   protected abstract Counter<TYPE> delegate();

   @Override
   public Counter<TYPE> divideBySum() {
      if (delegate() == null) {
         return this;
      }
      delegate().divideBySum();
      return this;
   }

   @Override
   public double get(TYPE item) {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().get(item);
   }

   @Override
   public Counter<TYPE> increment(TYPE item) {
      return increment(item, 1);
   }

   @Override
   public Counter<TYPE> increment(TYPE item, double amount) {
      return delegate().increment(item, amount);
   }

   @Override
   public Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable) {
      if (iterable != null) {
         iterable.forEach(this::increment);
      }
      return this;
   }

   @Override
   public Counter<TYPE> incrementAll(Iterable<? extends TYPE> iterable, double amount) {
      if (iterable != null) {
         iterable.forEach(i -> increment(i, amount));
      }
      return this;
   }

   @Override
   public boolean isEmpty() {
      return delegate() == null || delegate().isEmpty();
   }

   @Override
   public Set<TYPE> items() {
      if (delegate() == null) {
         return Collections.emptySet();
      }
      return delegate().items();
   }

   @Override
   public List<TYPE> itemsByCount(boolean ascending) {
      if (delegate() == null) {
         return Collections.emptyList();
      }
      return delegate().itemsByCount(ascending);
   }

   @Override
   public Set<Map.Entry<TYPE, Double>> entries() {
      if (delegate() == null) {
         return Collections.emptySet();
      }
      return delegate().entries();
   }

   @Override
   public double magnitude() {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().magnitude();
   }

   @Override
   public TYPE max() {
      if (delegate() == null) {
         return null;
      }
      return delegate().max();
   }

   @Override
   public double maximumCount() {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().maximumCount();
   }

   @Override
   public Counter<TYPE> merge(Counter<? extends TYPE> other) {
      return delegate().merge(other);
   }

   @Override
   public Counter<TYPE> merge(Map<? extends TYPE, ? extends Number> other) {
      return delegate().merge(other);
   }

   @Override
   public TYPE min() {
      if (delegate() == null) {
         return null;
      }
      return delegate().min();
   }

   @Override
   public double minimumCount() {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().minimumCount();
   }

   @Override
   public double remove(TYPE item) {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().remove(item);
   }

   @Override
   public Counter<TYPE> removeAll(Iterable<TYPE> items) {
      if (delegate() == null) {
         return this;
      }
      return delegate().removeAll(items);
   }


   @Override
   public TYPE sample() {
      if (delegate() == null) {
         return null;
      }
      return delegate().sample();
   }

   @Override
   public Counter<TYPE> set(TYPE item, double count) {
      return delegate().set(item, count);
   }

   @Override
   public int size() {
      if (delegate() == null) {
         return 0;
      }
      return delegate().size();
   }

   @Override
   public double standardDeviation() {
      if (delegate() == null) {
         return Double.NaN;
      }
      return delegate().standardDeviation();
   }

   @Override
   public double sum() {
      if (delegate() == null) {
         return 0d;
      }
      return delegate().sum();
   }

   @Override
   public Counter<TYPE> topN(int n) {
      if (delegate() == null) {
         return this;
      }
      return delegate().topN(n);
   }

   @Override
   public Counter<TYPE> filterByKey(@NonNull Predicate<TYPE> predicate) {
      if (delegate() == null) {
         return this;
      }
      return delegate().filterByKey(predicate);
   }

   @Override
   public Counter<TYPE> filterByValue(@NonNull DoublePredicate doublePredicate) {
      if (delegate() == null) {
         return this;
      }
      return delegate().filterByValue(doublePredicate);
   }

   @Override
   public String toString() {
      if (delegate() == null) {
         return "{}";
      }
      return delegate().toString();
   }

   @Override
   public Counter<TYPE> copy() {
      if (delegate() == null) {
         return this;
      }
      return delegate().copy();
   }

}//END OF ForwardingCounter
