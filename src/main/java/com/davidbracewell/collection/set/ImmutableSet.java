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

package com.davidbracewell.collection.set;

import com.davidbracewell.conversion.Cast;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import static com.davidbracewell.collection.Streams.asStream;

/**
 * The type Immutable map.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public final class ImmutableSet<E> implements Set<E>, Serializable {
  private static final long serialVersionUID = 1L;
  private static final ImmutableSet<Object> EMPTY = new ImmutableSet<>(new Object[0]);
  private final Object[] entries;
  private final int size;


  private ImmutableSet(Object[] entries) {
    this.size = (int) asStream(entries).parallel().distinct().count();
    this.entries = new Object[size()];
    for (Object entry : entries) {
      addElement(entry);
    }
  }

  @SafeVarargs
  @SuppressWarnings("varargs")
  public static <E> Set<E> of(E... elements) {
    if (elements == null || elements.length == 0) {
      return Cast.as(EMPTY);
    }
    return new ImmutableSet<>(elements);
  }

  private void addElement(Object key) {
    int hash = key.hashCode() % size;
    while (entries[hash] != null && !entries[hash].equals(key)) {
      hash = (hash + 1) % size;
    }
    entries[hash] = key;
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean isEmpty() {
    return size == 0;
  }

  @Override
  public boolean contains(Object o) {
    return findKey(o) != -1;
  }

  @Override
  public Iterator<E> iterator() {
    return asStream(entries).map(Cast::<E>as).iterator();
  }

  @Override
  public Object[] toArray() {
    return Arrays.copyOf(entries, entries.length);
  }

  @Override
  public <T> T[] toArray(T[] a) {
    if (a.length != entries.length) {
      a = (T[]) Array.newInstance(a.getClass().getComponentType(), entries.length);
    }
    System.arraycopy(entries, 0, a, 0, entries.length);
    return a;
  }

  @Override
  public boolean add(E e) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean remove(Object o) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void clear() {
    throw new UnsupportedOperationException();
  }

  private int findKey(Object key) {
    int hash = key.hashCode() % size;
    int count = 0;
    while (entries[hash] != null && !entries[hash].equals(key)) {
      hash = (hash + 1) % size;
      if (count == size) {
        return -1;
      }
      count++;
    }
    return hash;
  }

  @Override
  public String toString() {
    return Arrays.toString(entries);
  }


}// END OF ImmutableMap
