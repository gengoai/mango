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
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * Implementation of a Counter using a HashMap
 *
 * @param <T> the component type of the counter
 * @author David B. Bracewell
 */
@EqualsAndHashCode(exclude = {"sum"})
public class HashMapCounter<T> implements Counter<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<T, Double> map = new HashMap<>();
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
      map.merge(item, amount, Math2::add);
      if (map.get(item) == 0) {
         map.remove(item);
      }
      return this;
   }


   @Override
   public Map<T, Double> asMap() {
      return new AbstractMap<T, Double>() {
         @Override
         public boolean containsKey(Object key) {
            return map.containsKey(key);
         }

         @Override
         public Set<Entry<T, Double>> entrySet() {
            return entries();
         }
      };
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

//         @Override
//         public boolean remove(Object o) {
//            boolean toReturn = map.values().remove(o);
//            sum = Double.NaN;
//            return toReturn;
//         }

//         @Override
//         public boolean removeAll(@NonNull Collection<?> c) {
//            boolean toReturn = map.values().removeAll(c);
//            sum = Double.NaN;
//            return toReturn;
//         }
//
//         @Override
//         public boolean retainAll(@NonNull Collection<?> c) {
//            boolean toReturn = map.values().retainAll(c);
//            sum = Double.NaN;
//            return toReturn;
//         }
//
//         @Override
//         public boolean removeIf(@NonNull Predicate<? super Double> filter) {
//            boolean toReturn = map.values().removeIf(filter);
//            sum = Double.NaN;
//            return toReturn;
//         }
      };
   }


   @Override
   public Counter<T> merge(Counter<? extends T> other) {
      if (other != null) {
         other.forEach(this::increment);
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
      double value = map.getOrDefault(item, 0d);
      sum -= value;
      map.remove(item);
      return value;
   }


   @Override
   public Counter<T> set(T item, double count) {
      if (count == 0) {
         remove(item);
         return this;
      }
      sum -= map.getOrDefault(item, 0d);
      map.put(item, count);
      sum += count;
      return this;
   }


   @Override
   public Counter<T> divideBySum() {
      if (map.isEmpty()) {
         return this;
      }
      final double tmpSum = sum();
      map.replaceAll((key, value) -> value / tmpSum);
      sum = 1d;
      return this;
   }

   @Override
   public Set<Map.Entry<T, Double>> entries() {
      return new AbstractSet<Map.Entry<T, Double>>() {

         @Override
         public boolean remove(Object o) {
            if (o instanceof Map.Entry) {
               Map.Entry entry = Cast.as(o);
               if (entry.getValue() instanceof Number
                      && map.getOrDefault(entry.getKey(), 0d) == Cast.<Number>as(entry.getValue()).doubleValue()) {
                  map.remove(entry.getKey());
                  sum = Double.NaN;
                  return true;
               }
            }
            return false;
         }

         @Override
         public boolean removeAll(@NonNull Collection<?> c) {
            int size = map.size();
            new HashSet<>(map.entrySet()
                             .stream()
                             .filter(p -> c.contains($(p.getKey(), p.getValue())))
                             .collect(Collectors.toSet()))
               .forEach(p -> map.remove(p.getKey()));
            sum = Double.NaN;
            return (map.size() - size) == c.size();
         }

         @Override
         public boolean retainAll(@NonNull Collection<?> c) {
            new HashSet<>(map.entrySet()
                             .stream()
                             .filter(e -> !c.contains($(e.getKey(), e.getValue())))
                             .collect(Collectors.toSet()))
               .forEach(p -> map.remove(p.getKey()));
            sum = Double.NaN;
            return map.size() == c.size();
         }

         @Override
         public boolean removeIf(Predicate<? super Map.Entry<T, Double>> filter) {
            int size = map.size();
            new HashSet<>(map.keySet()
                             .stream()
                             .filter(p -> filter.test($(p, map.get(p))))
                             .collect(Collectors.toSet()))
               .forEach(map::remove);
            sum = Double.NaN;
            return size != map.size();
         }

         @Override
         public Iterator<Map.Entry<T, Double>> iterator() {
            return new Iterator<Map.Entry<T, Double>>() {
               final Iterator<Map.Entry<T, Double>> iterator = map.entrySet().iterator();
               Map.Entry<T, Double> entry;

               @Override
               public boolean hasNext() {
                  return iterator.hasNext();
               }


               @Override
               public Map.Entry<T, Double> next() {
                  entry = iterator.next();
                  return new Map.Entry<T, Double>() {
                     Map.Entry<T, Double> wrapped = new HashMap.SimpleEntry<>(entry.getKey(), entry.getValue());

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
      map.entrySet().forEach(
         entry -> newCounter.increment(entry.getKey(), function.applyAsDouble(entry.getValue())));
      return newCounter;
   }

   @Override
   public Counter<T> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
      map.replaceAll((key, value) -> function.applyAsDouble(value));
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
      return map.entrySet()
                .stream()
                .sorted((e1, e2) -> (ascending ? 1 : -1) * Double.compare(e1.getValue(), e2.getValue()))
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
      map.entrySet()
         .stream()
         .filter(e -> doublePredicate.test(e.getValue()))
         .forEach(e -> counter.set(e.getKey(), e.getValue()));
      return counter;
   }

   @Override
   public Counter<T> filterByKey(@NonNull Predicate<? super T> predicate) {
      Counter<T> counter = newInstance();
      map.entrySet()
         .stream()
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
      map.entrySet()
         .stream().forEach(e -> result.increment(function.apply(e.getKey()), e.getValue()));
      return result;
   }

   @Override
   public Counter<T> copy() {
      return this.<T>newInstance().merge(this);
   }


}//END OF AbstractMapCounter
