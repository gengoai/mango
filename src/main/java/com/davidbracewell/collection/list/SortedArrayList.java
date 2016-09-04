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

package com.davidbracewell.collection.list;

import com.davidbracewell.collection.Sorting;
import com.davidbracewell.conversion.Cast;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * The type Sorted array list.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public class SortedArrayList<E> implements List<E>, Serializable {
  private static final long serialVersionUID = 1L;
  private static final int DEFAULT_SIZE = 10;

  private final Comparator<? super E> comparator;
  private final ArrayList<E> backing;

  /**
   * Instantiates a new Sorted array list.
   */
  public SortedArrayList() {
    this(null, DEFAULT_SIZE);
  }

  /**
   * Instantiates a new Sorted array list.
   *
   * @param size the size
   */
  public SortedArrayList(int size) {
    this(null, size);
  }

  /**
   * Instantiates a new Sorted array list.
   *
   * @param comparator  the comparator
   * @param initialSize the initial size
   */
  public SortedArrayList(Comparator<? super E> comparator, int initialSize) {
    this.backing = new ArrayList<>(initialSize > 0 ? initialSize : DEFAULT_SIZE);
    this.comparator = comparator;
  }

  /**
   * Instantiates a new Sorted array list.
   *
   * @param collection the collection
   */
  public SortedArrayList(@NonNull Collection<E> collection) {
    this(null, collection.size());
    this.backing.addAll(collection);
    sort();
  }

  /**
   * Instantiates a new Sorted array list.
   *
   * @param comparator the comparator
   * @param collection the collection
   */
  public SortedArrayList(Comparator<? super E> comparator, @NonNull Collection<E> collection) {
    this(comparator, collection.size());
    this.backing.addAll(collection);
    sort();
  }

  private void sort() {
    if (comparator == null) {
      Collections.sort(this.backing, Cast.as(Sorting.natural()));
    } else {
      Collections.sort(this.backing, comparator);
    }
  }

  private int search(Object object) {
    if (comparator == null) {
      return Collections.binarySearch(backing, Cast.as(object), Cast.as(Sorting.natural()));
    }
    return Collections.binarySearch(backing, Cast.as(object), comparator);
  }

  @Override
  public E get(int index) {
    return backing.get(index);
  }

  @Override
  public E set(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(int index, E element) {
    throw new UnsupportedOperationException();
  }

  @Override
  public E remove(int index) {
    return backing.remove(index);
  }

  @Override
  public int indexOf(Object o) {
    return search(o);
  }

  @Override
  public int lastIndexOf(Object o) {
    int index = search(0);
    while (index >= 0 && index + 1 < backing.size() && backing.get(index + 1).equals(o)) {
      index++;
    }
    return index;
  }

  @Override
  public ListIterator<E> listIterator() {
    return new WrappedListIterator<>(this.backing.listIterator());
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return new WrappedListIterator<>(this.backing.listIterator(index));
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    Preconditions.checkState(toIndex > fromIndex, "Ending index must be greater than starting index.");
    SortedArrayList<E> sub = new SortedArrayList<>(comparator, toIndex - fromIndex);
    sub.addAll(backing.subList(fromIndex, toIndex));
    return sub;
  }

  @Override
  public int size() {
    return this.backing.size();
  }

  @Override
  public boolean isEmpty() {
    return this.backing.isEmpty();
  }

  @Override
  public boolean contains(Object o) {
    return search(o) >= 0;
  }

  @Override
  public Iterator<E> iterator() {
    return this.backing.iterator();
  }

  @Override
  public Object[] toArray() {
    return this.backing.toArray();
  }

  @Override
  public <T> T[] toArray(T[] a) {
    return this.backing.toArray(a);
  }

  @Override
  public boolean add(E e) {
    int index = search(e);
    if (index >= 0) {
      backing.add(index, e);
      return true;
    }
    index = -index - 1;
    if (index <= backing.size()) {
      backing.add(index, e);
      return true;
    } else {
      return backing.add(e);
    }
  }

  @Override
  public boolean remove(Object o) {
    return this.remove(o);
  }

  @Override
  public boolean containsAll(Collection<?> c) {
    if (c == null) {
      return false;
    }
    for (Object o : c) {
      if (!contains(o)) {
        return false;
      }
    }
    return true;
  }

  @Override
  public boolean addAll(Collection<? extends E> c) {
    if (c == null) {
      return false;
    }
    boolean good = true;
    for (E e : c) {
      if (!add(e)) {
        good = false;
      }
    }
    return good;
  }

  @Override
  public boolean addAll(int index, Collection<? extends E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean removeAll(Collection<?> c) {
    return this.backing.removeAll(c);
  }

  @Override
  public boolean retainAll(Collection<?> c) {
    return this.backing.retainAll(c);
  }

  @Override
  public void clear() {
    this.backing.clear();
  }

  public void trimToSize() {
    this.backing.trimToSize();
  }

  @Override
  public int hashCode() {
    return Objects.hash(comparator, backing);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final SortedArrayList other = (SortedArrayList) obj;
    return Objects.equals(this.comparator, other.comparator)
      && Objects.equals(this.backing, other.backing);
  }

  @Override
  public String toString() {
    return this.backing.toString();
  }

  private static class WrappedListIterator<E> implements ListIterator<E> {
    private final ListIterator<E> wrapped;

    private WrappedListIterator(ListIterator<E> wrapped) {
      this.wrapped = wrapped;
    }

    @Override
    public boolean hasNext() {
      return wrapped.hasNext();
    }

    @Override
    public E next() {
      return wrapped.next();
    }

    @Override
    public boolean hasPrevious() {
      return wrapped.hasPrevious();
    }

    @Override
    public E previous() {
      return wrapped.previous();
    }

    @Override
    public int nextIndex() {
      return wrapped.nextIndex();
    }

    @Override
    public int previousIndex() {
      return wrapped.previousIndex();
    }

    @Override
    public void remove() {
      wrapped.remove();
    }

    @Override
    public void set(E e) {
      throw new UnsupportedOperationException();
    }

    @Override
    public void add(E e) {
      throw new UnsupportedOperationException();
    }
  }

}//END OF SortedArrayList
