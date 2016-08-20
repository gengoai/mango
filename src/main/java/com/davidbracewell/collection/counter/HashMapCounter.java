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

import com.davidbracewell.Math2;
import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import com.google.common.collect.ForwardingIterator;
import com.google.common.collect.ForwardingSet;
import com.google.common.primitives.Doubles;
import com.google.common.util.concurrent.AtomicDouble;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p>Abstract class for counters backed by <code>java.util.Map</code> implementations. </p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode(exclude = {"sum"})
public class HashMapCounter<T> implements Counter<T>, Serializable {
  private static final long serialVersionUID = 1L;
  private final Map<T, Double> map = new HashMap<>();
  private AtomicDouble sum = new AtomicDouble(0d);


  /**
   * Instantiates a new Hash map counter.
   */
  public HashMapCounter() {

  }

  @SafeVarargs
  public HashMapCounter(T... items) {
    if (items != null) {
      incrementAll(Arrays.asList(items));
    }
  }

  /**
   * Instantiates a new Hash map counter.
   *
   * @param items the items
   */
  public HashMapCounter(Iterable<? extends T> items) {
    incrementAll(items);
  }

  /**
   * Instantiates a new Hash map counter.
   *
   * @param items the items
   */
  public HashMapCounter(Map<? extends T, ? extends Number> items) {
    merge(items);
  }

  /**
   * Instantiates a new Hash map counter.
   *
   * @param items the items
   */
  public HashMapCounter(Counter<? extends T> items) {
    merge(items);
  }

  @Override
  public double get(T item) {
    Double returnValue = map.get(item);
    return returnValue == null ? 0.0d : returnValue;
  }

  @Override
  public Counter<T> increment(T item, double amount) {
    if (amount == 0) {
      return this;
    }
    double value = map.getOrDefault(item, 0d) + amount;
    sum.addAndGet(amount);
    if (value == 0) {
      map.remove(item);
    } else {
      map.put(item, value);
    }
    return this;
  }


  @Override
  public Map<T, Double> asMap() {
    return Collections.unmodifiableMap(map);
  }


  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }


  @Override
  public int size() {
    return map.size();
  }


  @Override
  public Set<T> items() {
    return Collections.unmodifiableSet(map.keySet());
  }


  @Override
  public Collection<Double> counts() {
    return Collections.unmodifiableCollection(map.values());
  }


  @Override
  public Counter<T> merge(Counter<? extends T> other) {
    merge(Preconditions.checkNotNull(other).asMap());
    return this;
  }


  @Override
  public Counter<T> removeZeroCounts() {
    for (Iterator<Map.Entry<T, Double>> entryItr = map.entrySet().iterator(); entryItr.hasNext(); ) {
      Map.Entry<T, Double> entry = entryItr.next();
      if (entry.getValue() == 0.0d) {
        entryItr.remove();
      }
    }
    return this;
  }


  @Override
  public Counter<T> merge(Map<? extends T, ? extends Number> other) {
    Preconditions.checkNotNull(other);
    for (Map.Entry<? extends T, ? extends Number> entry : other.entrySet()) {
      increment(entry.getKey(), entry.getValue().doubleValue());
    }
    return this;
  }

  @Override
  public boolean contains(T item) {
    return map.containsKey(item);
  }

  @Override
  public String toString() {
    return map.toString();
  }


  @Override
  public void clear() {
    map.clear();
    sum.set(0d);
  }


  @Override
  public double remove(T item) {
    Preconditions.checkNotNull(item);
    double value = get(item);
    map.remove(item);
    sum.addAndGet(-value);
    return value;
  }


  @Override
  public Counter<T> set(T item, double count) {
    sum.addAndGet(-get(item));
    if (count == 0) {
      map.remove(item);
      return this;
    }
    map.put(item, count);
    sum.addAndGet(count);
    return this;
  }


  @Override
  public Counter<T> divideBySum() {
    if (map.isEmpty()) {
      return this;
    }
    final double tmpSum = sum();
    for (T key : map.keySet()) {
      map.put(key, map.get(key) / tmpSum);
    }
    sum.set(1d);
    return this;
  }

  @Override
  public Set<Map.Entry<T, Double>> entries() {
    return new ForwardingSet<Map.Entry<T, Double>>() {
      @Override
      protected Set<Map.Entry<T, Double>> delegate() {
        return map.entrySet();
      }

      @Override
      public boolean remove(Object object) {
        if (super.remove(object)) {
          sum.addAndGet(-Cast.<Map.Entry<T, Double>>as(object).getValue());
          return true;
        }
        return false;
      }

      @Override
      public boolean removeAll(Collection<?> collection) {
        return standardRemoveAll(collection);
      }

      @Override
      public Iterator<Map.Entry<T, Double>> iterator() {
        return new ForwardingIterator<Map.Entry<T, Double>>() {
          final Iterator<Map.Entry<T, Double>> iterator = map.entrySet().iterator();
          Map.Entry<T, Double> entry;

          @Override
          protected Iterator<Map.Entry<T, Double>> delegate() {
            return iterator;
          }

          @Override
          public Map.Entry<T, Double> next() {
            entry = super.next();
            return entry;
          }

          @Override
          public void remove() {
            super.remove();
            sum.addAndGet(-entry.getValue());
          }
        };
      }

    };
  }

  @Override
  public Counter<T> removeAll(Iterable<T> items) {
    if (items != null) {
      items.forEach(this::remove);
    }
    return this;
  }

  @Override
  public Counter<T> adjustValues(@NonNull DoubleUnaryOperator function) {
    Counter<T> newCounter = newInstance();
    for (Map.Entry<T, Double> entry : map.entrySet()) {
      double value = function.applyAsDouble(entry.getValue());
      if (value != 0d && Doubles.isFinite(value)) {
        newCounter.set(entry.getKey(), value);
      }
    }
    return newCounter;
  }

  @Override
  public Counter<T> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
    map.entrySet().forEach(entry -> entry.setValue(function.applyAsDouble(entry.getValue())));
    sum.set(Math2.sum(map.values()));
    return this;
  }

  /**
   * New instance counter.
   *
   * @param <R> the type parameter
   * @return the counter
   */
  protected <R> Counter<R> newInstance() {
    return new HashMapCounter<R>();
  }

  @Override
  public Counter<T> topN(int n) {
    Counter<T> cprime = newInstance();
    itemsByCount(false).stream()
                       .limit(n)
                       .forEach(t -> cprime.set(t, get(t)));
    return cprime;
  }

  @Override
  public Counter<T> bottomN(int n) {
    Counter<T> cprime = newInstance();
    itemsByCount(true).stream()
                      .limit(n)
                      .forEach(t -> cprime.set(t, get(t)));
    return cprime;
  }

  @Override
  public Counter<T> filterByValue(@NonNull DoublePredicate doublePredicate) {
    Counter<T> counter = newInstance();
    map.entrySet().stream()
       .filter(e -> doublePredicate.test(e.getValue()))
       .forEach(e -> counter.set(e.getKey(), e.getValue()));
    return counter;
  }

  @Override
  public Counter<T> filterByKey(@NonNull Predicate<T> predicate) {
    Counter<T> counter = newInstance();
    map.entrySet().stream()
       .filter(e -> predicate.test(e.getKey()))
       .forEach(e -> counter.set(e.getKey(), e.getValue()));
    return counter;
  }

  @Override
  public double sum() {
    return sum.get();
  }

  @Override
  public <R> Counter<R> mapKeys(@NonNull Function<T, R> function) {
    Counter<R> result = newInstance();
    map.forEach((k, v) -> result.increment(function.apply(k), v));
    return result;
  }

  @Override
  public Counter<T> copy() {
    return this.<T>newInstance().merge(this);
  }


}//END OF AbstractMapCounter
