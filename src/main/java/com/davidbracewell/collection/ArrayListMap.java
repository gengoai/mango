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
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import com.google.common.collect.Ordering;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type Array list map.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class ArrayListMap<K, V> implements Map<K, V>, Serializable {
  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_INITIAL_SIZE = 10;
  private volatile ArrayList<K> keys;
  private volatile ArrayList<V> values;
  private Comparator<? super K> comparator;

  /**
   * Instantiates a new Array list map.
   */
  public ArrayListMap() {
    this(DEFAULT_INITIAL_SIZE);
  }

  /**
   * Instantiates a new Array list map.
   *
   * @param comparator the comparator
   */
  public ArrayListMap(Comparator<? super K> comparator) {
    this(DEFAULT_INITIAL_SIZE, comparator);
  }

  /**
   * Instantiates a new Array list map.
   *
   * @param initialSize the initial size
   */
  public ArrayListMap(int initialSize) {
    this(initialSize, null);
  }


  /**
   * Instantiates a new Array list map.
   *
   * @param initialSize the initial size
   * @param comparator  the comparator
   */
  public ArrayListMap(int initialSize, Comparator<? super K> comparator) {
    this.keys = new ArrayList<>(initialSize);
    this.values = new ArrayList<>(initialSize);
    this.comparator = comparator;
  }


  @Override
  public int size() {
    return keys.size();
  }

  @Override
  public boolean isEmpty() {
    return keySet().isEmpty();
  }

  @Override
  public boolean containsKey(Object key) {
    return search(key) >= 0;
  }

  @Override
  public boolean containsValue(Object value) {
    return values.contains(value);
  }

  @Override
  public V get(Object key) {
    int index = search(key);
    if (index >= 0) {
      return values.get(index);
    }
    return null;
  }

  private int search(Object key) {
    if (comparator == null) {
      return Collections.binarySearch(keys, Cast.as(key), Cast.as(Ordering.natural()));
    }
    return Collections.binarySearch(keys, Cast.as(key), comparator);
  }

  @Override
  public V put(Object key, Object value) {
    int index = search(key);
    if (index >= 0) {
      return values.set(index, Cast.as(value));
    }
    index = -index - 1;
    if (index <= keys.size()) {
      keys.add(index, Cast.as(key));
      values.add(index, Cast.as(value));
    } else {
      keys.add(Cast.as(key));
      values.add(Cast.as(value));
    }
    return null;
  }

  @Override
  public V remove(Object key) {
    int index = search(key);
    if (index >= 0) {
      keys.remove(index);
      return values.remove(index);
    }
    return null;
  }

  @Override
  public void putAll(Map<? extends K, ? extends V> m) {
    if (m != null) {
      m.entrySet().forEach(e -> put(e.getKey(), e.getValue()));
    }
  }

  @Override
  public void clear() {
    keys.clear();
    values.clear();
  }

  @Override
  public Set<K> keySet() {
    return new AbstractSet<K>() {
      @Override
      public Iterator<K> iterator() {
        return Iterators.transform(new IndexIterator(), Entry::getKey);
      }

      @Override
      public int size() {
        return keys.size();
      }
    };
  }

  @Override
  public Collection<V> values() {
    return Collections.unmodifiableCollection(values);
  }

  @Override
  public Set<Entry<K, V>> entrySet() {
    return new AbstractSet<Entry<K, V>>() {
      @Override
      public Iterator<Entry<K, V>> iterator() {
        return new IndexIterator();
      }

      @Override
      public int size() {
        return keys.size();
      }
    };
  }


  @Override
  public int hashCode() {
    return Objects.hash(keys, values);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final ArrayListMap other = (ArrayListMap) obj;
    return Objects.equals(this.keys, other.keys)
      && Objects.equals(this.values, other.values);
  }

  @Override
  public String toString() {
    return "{" + entrySet().stream()
      .map(e -> e.getKey() + ":" + e.getValue())
      .collect(Collectors.joining(", "))
      + "}";
  }

  /**
   * Trim to size.
   */
  public void trimToSize() {
    keys.trimToSize();
    values.trimToSize();
  }

  private class IndexIterator implements Iterator<Entry<K, V>> {
    private final ListIterator<K> keyIterator = keys.listIterator();
    private int index = -1;

    @Override
    public boolean hasNext() {
      return keyIterator.hasNext();
    }

    @Override
    public Entry<K, V> next() {
      if (!keyIterator.hasNext()) {
        throw new NoSuchElementException();
      }
      index = keyIterator.nextIndex();
      K key = keyIterator.next();
      V value = values.get(index);
      return Tuple2.of(key, value);
    }

    @Override
    public void remove() {
      Preconditions.checkArgument(index >= 0 && index < keys.size());
      values.remove(index);
      keys.remove(index);
    }
  }

  private void writeObject(ObjectOutputStream out) throws IOException {
    out.writeObject(keys);
    out.writeObject(values);
  }

  private void readObject(ObjectInputStream in) throws IOException,
    ClassNotFoundException {
    keys = Cast.as(in.readObject());
    values = Cast.as(in.readObject());
  }



}//END OF ArrayListMap
