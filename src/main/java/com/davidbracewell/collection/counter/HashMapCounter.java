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
import com.google.common.collect.Iterators;
import it.unimi.dsi.fastutil.objects.Object2DoubleOpenHashMap;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of a Counter using a HashMap
 *
 * @param <T> the component type of the counter
 * @author David B. Bracewell
 */
@EqualsAndHashCode(exclude = {"sum"})
public class HashMapCounter<T> implements Counter<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Object2DoubleOpenHashMap<T> map = new Object2DoubleOpenHashMap<>();
   private double sum = 0;


   /**
    * Instantiates a new Hash map counter.
    */
   protected HashMapCounter() {

   }

   @Override
   public double get(T item) {
      return map.getOrDefault(item, 0d);
   }

   @Override
   public Counter<T> increment(T item, double amount) {
      if (amount == 0) {
         return this;
      }
      sum += amount;
      map.compute(item, (i, v) -> (v == null ? 0 : v) + amount);
      if (map.getDouble(item) == 0) {
         map.removeDouble(item);
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
      return new AbstractSet<T>() {

         @Override
         public boolean contains(Object o) {
            return map.containsKey(o);
         }

         @Override
         public Iterator<T> iterator() {
            return new KeyIterator();
         }

         @Override
         public int size() {
            return map.keySet().size();
         }

         @Override
         public boolean remove(Object o) {
            return HashMapCounter.this.remove(Cast.as(o)) != 0;
         }

         @Override
         public boolean retainAll(@NonNull Collection<?> c) {
            boolean toReturn = map.keySet().retainAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeAll(@NonNull Collection<?> c) {
            boolean toReturn = map.keySet().removeAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeIf(@NonNull Predicate<? super T> filter) {
            boolean toReturn = map.keySet().removeIf(filter);
            sum = Double.NaN;
            return toReturn;
         }
      };
   }

   class KeyIterator implements Iterator<T> {
      final Iterator<T> itr = map.keySet().iterator();
      T last = null;

      @Override
      public boolean hasNext() {
         return itr.hasNext();
      }

      @Override
      public T next() {
         last = itr.next();
         return last;
      }

      @Override
      public void remove() {
         HashMapCounter.this.sum -= HashMapCounter.this.get(last);
         itr.remove();
      }
   }


   @Override
   public Collection<Double> values() {
      return new AbstractCollection<Double>() {
         @Override
         public Iterator<Double> iterator() {
            return Iterators.transform(new KeyIterator(), HashMapCounter.this::get);
         }

         @Override
         public int size() {
            return map.values().size();
         }

         @Override
         public boolean remove(Object o) {
            boolean toReturn = map.values().remove(o);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeAll(@NonNull Collection<?> c) {
            boolean toReturn = map.values().removeAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean retainAll(@NonNull Collection<?> c) {
            boolean toReturn = map.values().retainAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeIf(@NonNull Predicate<? super Double> filter) {
            boolean toReturn = map.values().removeIf(filter);
            sum = Double.NaN;
            return toReturn;
         }
      };
   }


   @Override
   public Counter<T> merge(Counter<? extends T> other) {
      if (other != null) {
         merge(other.asMap());
      }
      return this;
   }

   @Override
   public Counter<T> merge(Map<? extends T, ? extends Number> other) {
      if (other != null) {
         for (Map.Entry<? extends T, ? extends Number> entry : other.entrySet()) {
            increment(entry.getKey(), entry.getValue().doubleValue());
         }
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
      sum = 0;
   }


   @Override
   public double remove(T item) {
      if (item == null) {
         return 0d;
      }
      Double value = map.remove(item);
      if (value != null) {
         sum -= value;
      }
      return value == null ? 0d : value;
   }


   @Override
   public Counter<T> set(T item, double count) {
      if (count == 0) {
         sum -= map.getDouble(item);
         map.removeDouble(item);
         return this;
      }
      double value = map.getDouble(item);
      map.put(item, count);
      if (value != 0) {
         sum -= value;
      }
      sum += count;
      return this;
   }


   @Override
   public Counter<T> divideBySum() {
      if (map.isEmpty()) {
         return this;
      }
      final double tmpSum = sum();
      map.entrySet().forEach(e -> e.setValue(e.getValue() / tmpSum));
      sum = 1d;
      return this;
   }

   @Override
   public Set<Map.Entry<T, Double>> entries() {
      return new AbstractSet<Map.Entry<T, Double>>() {

         @Override
         public boolean remove(Object o) {
            boolean toReturn = map.entrySet().remove(o);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeAll(@NonNull Collection<?> c) {
            boolean toReturn = map.entrySet().removeAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean retainAll(@NonNull Collection<?> c) {
            boolean toReturn = map.entrySet().retainAll(c);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public boolean removeIf(Predicate<? super Map.Entry<T, Double>> filter) {
            boolean toReturn = map.entrySet().removeIf(filter);
            sum = Double.NaN;
            return toReturn;
         }

         @Override
         public Iterator<Map.Entry<T, Double>> iterator() {
            return new Iterator<Map.Entry<T, Double>>() {
               final Iterator<Map.Entry<T, Double>> iterator = HashMapCounter.this.map.entrySet().iterator();
               Map.Entry<T, Double> entry;

               @Override
               public boolean hasNext() {
                  return iterator.hasNext();
               }


               @Override
               public Map.Entry<T, Double> next() {
                  entry = iterator.next();
                  return new Map.Entry<T, Double>() {
                     Map.Entry<T, Double> wrapped = entry;

                     @Override
                     public T getKey() {
                        return wrapped.getKey();
                     }

                     @Override
                     public Double getValue() {
                        return wrapped.getValue();
                     }

                     @Override
                     public Double setValue(Double value) {
                        if (value == null || value == 0) {
                           HashMapCounter.this.remove(wrapped.getKey());
                        }
                        Double old = wrapped.getValue();
                        HashMapCounter.this.set(wrapped.getKey(), value);
                        return old;
                     }
                  };
               }

            };
         }

         @Override
         public int size() {
            return HashMapCounter.this.size();
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
      map.entrySet().forEach(entry -> newCounter.increment(entry.getKey(), function.applyAsDouble(entry.getValue())));
      return newCounter;
   }

   @Override
   public Counter<T> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
      map.entrySet().forEach(entry -> entry.setValue(function.applyAsDouble(entry.getValue())));
      sum = Double.NaN;
      return this;
   }

   /**
    * New instance counter.
    *
    * @param <R> the type parameter
    * @return the counter
    */
   protected <R> Counter<R> newInstance() {
      return new HashMapCounter<>();
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
   public List<T> itemsByCount(boolean ascending) {
      return map.entrySet().stream()
                .sorted(ascending
                        ? Map.Entry.<T, Double>comparingByValue()
                        : Map.Entry.<T, Double>comparingByValue().reversed())
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
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
   public Counter<T> filterByKey(@NonNull Predicate<? super T> predicate) {
      Counter<T> counter = newInstance();
      map.entrySet().stream()
         .filter(e -> predicate.test(e.getKey()))
         .forEach(e -> counter.set(e.getKey(), e.getValue()));
      return counter;
   }

   @Override
   public double sum() {
      if (Double.isFinite(sum)) {
         return sum;
      }
      sum = Math2.sum(values());
      return sum;
   }

   @Override
   public <R> Counter<R> mapKeys(@NonNull Function<? super T, ? extends R> function) {
      Counter<R> result = newInstance();
      map.forEach((k, v) -> result.increment(function.apply(k), v));
      return result;
   }

   @Override
   public Counter<T> copy() {
      return this.<T>newInstance().merge(this);
   }


}//END OF AbstractMapCounter
