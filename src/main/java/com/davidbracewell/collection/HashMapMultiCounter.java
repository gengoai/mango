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

package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.tuple.Tuple2;
import com.davidbracewell.tuple.Tuple3;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Iterators;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.ibm.icu.impl.ValidIdentifiers.Datatype.t;

/**
 * The type Hash map multi counter.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class HashMapMultiCounter<K, V> implements MultiCounter<K, V>, Serializable {
  private static final long serialVersionUID = 1L;
  private final Map<K, Counter<V>> map = new HashMap<>();

  public HashMapMultiCounter() {

  }

  public HashMapMultiCounter(@NonNull MultiCounter<? extends K, ? extends V> multiCounter) {
    multiCounter.entries().forEach(t -> increment(t.v1,t.v2,t.v3));
  }


  @SafeVarargs
  public HashMapMultiCounter(@NonNull Tuple3<K, V, ? extends Number>... triples) {
    this(Arrays.asList(triples));
  }

  public HashMapMultiCounter(@NonNull Iterable<Tuple3<K, V, ? extends Number>> triples) {
    triples.forEach(triple -> increment(triple.v1, triple.v2, triple.v3.doubleValue()));
  }

  @Override
  public MultiCounter<K, V> adjustValues(@NonNull DoubleUnaryOperator function) {
    MultiCounter<K, V> tmp = newInstance();
    items().forEach(key -> tmp.set(key, get(key).adjustValues(function)));
    return tmp;
  }

  @Override
  public MultiCounter<K, V> adjustValuesSelf(@NonNull DoubleUnaryOperator function) {
    items().forEach(key -> get(key).adjustValuesSelf(function));
    return this;
  }

  @Override
  public Map<K, Counter<V>> asMap() {
    return Collections.unmodifiableMap(map);
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

  @Override
  public Collection<Double> counts() {
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
  public MultiCounter<K, V> filterByFirstKey(@NonNull Predicate<K> predicate) {
    MultiCounter<K, V> tmp = newInstance();
    Collect.stream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV1()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public MultiCounter<K, V> filterBySecondKey(@NonNull Predicate<V> predicate) {
    MultiCounter<K, V> tmp = newInstance();
    Collect.stream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV2()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public MultiCounter<K, V> filterByValue(@NonNull DoublePredicate predicate) {
    MultiCounter<K, V> tmp = newInstance();
    Collect.stream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV3()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public Counter<V> get(K item) {
    if (!map.containsKey(item)) {
      map.put(item, newCounter());
    }
    return map.get(item);
  }

  @Override
  public boolean isEmpty() {
    return map.isEmpty();
  }

  @Override
  public Set<K> items() {
    return map.keySet();
  }

  @Override
  public List<Map.Entry<K, V>> itemsByCount(boolean ascending) {
    return Collect.stream(new KeyKeyValueIterator())
      .sorted((c1, c2) -> (ascending ? 1 : -1) * Double.compare(c1.getV3(), c2.getV3()))
      .map(t -> Cast.<Map.Entry<K, V>>as(Tuple2.of(t.getV1(), t.getV2())))
      .collect(Collectors.toList());
  }

  @Override
  public MultiCounter<K, V> merge(MultiCounter<K, V> other) {
    if (other != null) {
      other.entries().stream()
        .forEach(e -> increment(e.v1, e.v2, e.v3));
    }
    return this;
  }

  /**
   * New counter.
   *
   * @return the counter
   */
  private Counter<V> newCounter() {
    return new HashMapCounter<>();
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
  public MultiCounter<K, V> set(K item1, V item2, double count) {
    get(item1).set(item2, count);
    return this;
  }

  @Override
  public MultiCounter<K, V> set(K item, @NonNull Counter<V> counter) {
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
    items().stream().limit(10).forEach(item -> {
      builder.append(item).append(":").append(get(item)).append("\n");
    });
    if (size() > 10) {
      builder.append("....");
    }
    return builder.toString().trim();
  }

  private class KeyKeyValueIterator implements Iterator<Tuple3<K, V, Double>> {

    private Iterator<K> key1Iterator = map.keySet().iterator();
    private K key1;
    private Iterator<Map.Entry<V, Double>> key2Iterator = null;

    private boolean advance() {
      while (key2Iterator == null || !key2Iterator.hasNext()) {
        if (key1Iterator.hasNext()) {
          key1 = key1Iterator.next();
          key2Iterator = get(key1).entries().iterator();
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
      Map.Entry<V, Double> key2 = key2Iterator.next();
      return Tuple3.of(key1, key2.getKey(), key2.getValue());
    }

  }


}//END OF HashMapMultiCounter
