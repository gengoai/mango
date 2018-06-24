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

import com.gengoai.collection.Iterators;
import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import com.gengoai.tuple.Tuple2;
import com.gengoai.tuple.Tuple3;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * Implementation of a MultiCounter using a HashMaps.
 *
 * @param <K> the first key type
 * @param <V> the second type
 * @author David B. Bracewell
 */
public class HashMapMultiCounter<K, V> implements MultiCounter<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<K, Counter<V>> map = new HashMap<>();


   @Override
   public MultiCounter<K, V> adjustValues(DoubleUnaryOperator function) {
      MultiCounter<K, V> tmp = newInstance();
      firstKeys().forEach(key -> tmp.set(key, get(key).adjustValues(function)));
      return tmp;
   }

   @Override
   public MultiCounter<K, V> adjustValuesSelf(DoubleUnaryOperator function) {
      firstKeys().forEach(key -> get(key).adjustValuesSelf(function));
      return this;
   }

   protected boolean canEqual(Object other) {
      return other instanceof HashMapMultiCounter;
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public boolean contains(K item) {
      return map.containsKey(item);
   }

   @Override
   public boolean contains(K item1, V item2) {
      return map.containsKey(item1) && map.get(item1).contains(item2);
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof HashMapMultiCounter)) return false;
      final HashMapMultiCounter other = (HashMapMultiCounter) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$map = this.map;
      final Object other$map = other.map;
      if (this$map == null ? other$map != null : !this$map.equals(other$map)) return false;
      return true;
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $map = this.map;
      result = result * PRIME + ($map == null ? 43 : $map.hashCode());
      return result;
   }

   @Override
   public Collection<Double> values() {
      return new AbstractCollection<Double>() {
         @Override
         public Iterator<Double> iterator() {
            return Iterators.transform(new KeyKeyValueIterator(), Tuple3::getV3);
         }

         @Override
         public int size() {
            return HashMapMultiCounter.this.size();
         }
      };
   }

   @Override
   public Set<Tuple3<K, V, Double>> entries() {
      return new AbstractSet<Tuple3<K, V, Double>>() {

         @Override
         public Iterator<Tuple3<K, V, Double>> iterator() {
            return new KeyKeyValueIterator();
         }

         @Override
         public int size() {
            return HashMapMultiCounter.this.size();
         }
      };
   }

   @Override
   public MultiCounter<K, V> filterByFirstKey(Predicate<K> predicate) {
      MultiCounter<K, V> tmp = newInstance();
      Streams.asStream(new KeyKeyValueIterator())
             .filter(t -> predicate.test(t.getV1()))
             .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
      return tmp;
   }

   @Override
   public MultiCounter<K, V> filterBySecondKey(Predicate<V> predicate) {
      MultiCounter<K, V> tmp = newInstance();
      Streams.asStream(new KeyKeyValueIterator())
             .filter(t -> predicate.test(t.getV2()))
             .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
      return tmp;
   }

   @Override
   public MultiCounter<K, V> filterByValue(DoublePredicate predicate) {
      MultiCounter<K, V> tmp = newInstance();
      Streams.asStream(new KeyKeyValueIterator())
             .filter(t -> predicate.test(t.getV3()))
             .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
      return tmp;
   }

   @Override
   public Counter<V> get(K firstKey) {
      return new WrappedCounter(firstKey);
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<K> firstKeys() {
      return map.keySet();
   }

   @Override
   public List<Map.Entry<K, V>> itemsByCount(boolean ascending) {
      return Streams.asStream(new KeyKeyValueIterator())
                    .sorted((c1, c2) -> (ascending ? 1 : -1) * Double.compare(c1.getV3(), c2.getV3()))
                    .map(t -> Cast.<Map.Entry<K, V>>as(Tuple2.of(t.getV1(), t.getV2())))
                    .collect(Collectors.toList());
   }

   @Override
   public MultiCounter<K, V> merge(MultiCounter<K, V> other) {
      if (other != null) {
         other.entries().forEach(e -> increment(e.v1, e.v2, e.v3));
      }
      return this;
   }

   /**
    * New instance.
    *
    * @return the multi counter
    */
   private MultiCounter<K, V> newInstance() {
      return new HashMapMultiCounter<>();
   }

   @Override
   public Counter<V> remove(K item) {
      Counter<V> c = get(item);
      map.remove(item);
      return c;
   }

   @Override
   public double remove(K item1, V item2) {
      double v = get(item1).remove(item2);
      if (get(item1).isEmpty()) {
         map.remove(item1);
      }
      return v;
   }

   @Override
   public MultiCounter<K, V> set(K item1, V item2, double amount) {
      get(item1).set(item2, amount);
      return this;
   }

   @Override
   public MultiCounter<K, V> set(K item, Counter<V> counter) {
      map.put(item, counter);
      return this;
   }

   @Override
   public int size() {
      return map.values().parallelStream().mapToInt(Counter::size).sum();
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder();
      firstKeys().stream().limit(10).forEach(item -> {
         builder.append(item).append(":").append(get(item)).append("\n");
      });
      if (size() > 10) {
         builder.append("....");
      }
      return builder.toString().trim();
   }

   @Override
   public Set<Map.Entry<K, V>> keyPairs() {
      return new AbstractSet<Map.Entry<K, V>>() {

         @Override
         public boolean contains(Object o) {
            if (o instanceof Map.Entry) {
               Map.Entry<K, V> e = Cast.as(o);
               return HashMapMultiCounter.this.contains(e.getKey(), e.getValue());
            }
            return false;
         }

         @Override
         public Iterator<Map.Entry<K, V>> iterator() {
            return Iterators.transform(new KeyKeyValueIterator(),
                                       t -> new AbstractMap.SimpleImmutableEntry<>(t.v1, t.v2));
         }

         @Override
         public int size() {
            return HashMapMultiCounter.this.size();
         }
      };
   }

   private class KeyKeyValueIterator implements Iterator<Tuple3<K, V, Double>> {

      private Iterator<Map.Entry<K, Counter<V>>> entryIterator = map.entrySet().iterator();
      private Map.Entry<K, Counter<V>> entry = null;
      private Iterator<V> key2Iterator = null;

      private boolean advance() {
         while (key2Iterator == null || !key2Iterator.hasNext()) {
            if (entryIterator.hasNext()) {
               entry = entryIterator.next();
               key2Iterator = entry.getValue().items().iterator();
            } else {
               return false;
            }
         }
         return true;
      }


      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public Tuple3<K, V, Double> next() {
         if (!advance()) {
            throw new NoSuchElementException();
         }
         V key2 = key2Iterator.next();
         return Tuple3.of(entry.getKey(), key2, entry.getValue().get(key2));
      }

      @Override
      public void remove() {
         key2Iterator.remove();
         if (entry.getValue().isEmpty()) {
            entryIterator.remove();
         }
      }
   }


   class WrappedCounter extends ForwardingCounter<V> {
      private static final long serialVersionUID = 1L;
      private final K key;

      WrappedCounter(K key) {
         this.key = key;
      }

      @Override
      protected Counter<V> delegate() {
         return map.get(key);
      }


      private Counter<V> createIfNeeded() {
         Counter<V> counter = delegate();
         if (counter == null) {
            map.put(key, new HashMapCounter<>());
         }
         return delegate();
      }

      private void removeIfEmpty() {
         Counter<V> counter = delegate();
         if (counter != null && counter.isEmpty()) {
            map.remove(key);
         }
      }

      @Override
      public void clear() {
         super.clear();
         removeIfEmpty();
      }

      @Override
      public Counter<V> increment(V item, double amount) {
         createIfNeeded().increment(item, amount);
         return this;
      }

      @Override
      public double remove(V item) {
         double value = super.remove(item);
         removeIfEmpty();
         return value;
      }

      @Override
      public Counter<V> removeAll(Iterable<V> items) {
         super.removeAll(items);
         removeIfEmpty();
         return this;
      }

      @Override
      public Counter<V> set(V item, double count) {
         if (count == 0) {
            remove(item);
         } else {
            createIfNeeded().set(item, count);
         }
         return this;
      }

   }

}//END OF HashMapMultiCounter
