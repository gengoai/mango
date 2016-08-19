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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.tuple.Tuple2;
import com.davidbracewell.tuple.Tuple3;
import lombok.NonNull;

import java.io.Serializable;
import java.util.AbstractCollection;
import java.util.AbstractSet;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.davidbracewell.collection.CollectionHelpers.asStream;

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


  /**
   * Instantiates a new Hash map multi counter.
   */
  public HashMapMultiCounter() {

  }

  /**
   * Instantiates a new Hash map multi counter.
   *
   * @param multiCounter the multi counter
   */
  public HashMapMultiCounter(@NonNull MultiCounter<? extends K, ? extends V> multiCounter) {
    multiCounter.entries().forEach(t -> increment(t.v1, t.v2, t.v3));
  }

  /**
   * Instantiates a new Hash map multi counter.
   *
   * @param triples the triples
   */
  @SafeVarargs
  public HashMapMultiCounter(@NonNull Tuple3<K, V, ? extends Number>... triples) {
    this(Arrays.asList(triples));
  }

  /**
   * Instantiates a new Hash map multi counter.
   *
   * @param triples the triples
   */
  public HashMapMultiCounter(@NonNull Iterable<Tuple3<K, V, ? extends Number>> triples) {
    triples.forEach(triple -> increment(triple.v1, triple.v2, triple.v3.doubleValue()));
  }

  /**
   * Create hash map multi counter.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @return the hash map multi counter
   */
  public static <K, V> HashMapMultiCounter<K, V> create() {
    return new HashMapMultiCounter<>();
  }

  /**
   * Create hash map multi counter.
   *
   * @param <K>          the type parameter
   * @param <V>          the type parameter
   * @param multiCounter the multi counter
   * @return the hash map multi counter
   */
  public static <K, V> HashMapMultiCounter<K, V> create(MultiCounter<? extends K, ? extends V> multiCounter) {
    if (multiCounter == null) {
      return new HashMapMultiCounter<>();
    }
    return new HashMapMultiCounter<>(multiCounter);
  }

  /**
   * Create hash map multi counter.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param triples the triples
   * @return the hash map multi counter
   */
  public static <K, V> HashMapMultiCounter<K, V> create(Tuple3<K, V, ? extends Number>... triples) {
    if (triples == null) {
      return new HashMapMultiCounter<>();
    }
    return new HashMapMultiCounter<>(triples);
  }

  /**
   * Create hash map multi counter.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param triples the triples
   * @return the hash map multi counter
   */
  public static <K, V> HashMapMultiCounter<K, V> create(Iterable<Tuple3<K, V, ? extends Number>> triples) {
    if (triples == null) {
      return new HashMapMultiCounter<>();
    }
    return new HashMapMultiCounter<>(triples);
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
        return asStream(new KeyKeyValueIterator()).map(Tuple3::getV3).iterator();
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
    asStream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV1()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public MultiCounter<K, V> filterBySecondKey(@NonNull Predicate<V> predicate) {
    MultiCounter<K, V> tmp = newInstance();
    asStream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV2()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public MultiCounter<K, V> filterByValue(@NonNull DoublePredicate predicate) {
    MultiCounter<K, V> tmp = newInstance();
    asStream(new KeyKeyValueIterator())
      .filter(t -> predicate.test(t.getV3()))
      .forEach(t -> tmp.set(t.getV1(), t.getV2(), t.getV3()));
    return tmp;
  }

  @Override
  public Counter<V> get(K item) {
    return new WrappedCounter(item);
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
    return asStream(new KeyKeyValueIterator())
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


  class WrappedCounter extends ForwardingCounter<V> {
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
      if(count == 0 ){
        remove(item);
      } else {
        createIfNeeded().set(item,count);
      }
      return this;
    }

  }

}//END OF HashMapMultiCounter
