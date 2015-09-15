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
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * A <code>ListIterator</code> over elements in an array. Arrays of primitives are supported as long
 * as the generic type is the Object version of the primitive.
 * </p>
 * <p>
 * The iterator does not support <code>remove()</code> or <code>add()</code> instead it will throw
 * an <code>UnsupportedOperationException</code>
 * </p>
 *
 * @param <E> The type if element in the array
 * @author David B. Bracewell
 * @version $Id$
 */
public class PrimitiveArrayListIterator<E> implements ListIterator<E> {

  private Object array;
  private int index = 0;
  private int last = 0;
  private int length = 0;

  /**
   * Instantiates a new Primitive array list iterator.
   *
   * @param array the array
   */
  public PrimitiveArrayListIterator(@NonNull Object array) {
    if (!array.getClass().isArray()) {
      throw new IllegalArgumentException("Must pass an array");
    }
    if (!array.getClass().getComponentType().isPrimitive()) {
      throw new IllegalArgumentException("Object must be a primitive array");
    }
    this.array = array;
    this.length = Array.getLength(array);
  }

  /**
   * Instantiates a new Primitive array list iterator.
   *
   * @param array the array
   * @param index the index
   */
  public PrimitiveArrayListIterator(@NonNull Object array, int index) {
    if (!array.getClass().isArray()) {
      throw new IllegalArgumentException("Must pass an array");
    }
    if (!array.getClass().getComponentType().isPrimitive()) {
      throw new IllegalArgumentException("Object must be a primitive array");
    }
    if (index < 0) {
      throw new IllegalArgumentException("The starting index should be non-negative.");
    }
    this.array = array;
    this.index = index;
    this.last = index;
    this.length = Array.getLength(array);
  }

  @Override
  public void add(E arg0) {
    throw new UnsupportedOperationException();
  }

  @Override
  public boolean hasNext() {
    return index < length;
  }

  @Override
  public boolean hasPrevious() {
    return index > 0;
  }

  @Override
  public E next() {
    if (!hasNext()) {
      throw new NoSuchElementException();
    }
    last = index;
    return Cast.as(Array.get(array, index++));
  }

  @Override
  public int nextIndex() {
    return index;
  }

  @Override
  public E previous() {
    if (!hasPrevious()) {
      throw new NoSuchElementException();
    }
    last = index;
    return Cast.as(Array.get(array, index - 1));
  }

  @Override
  public int previousIndex() {
    return index == 0 ? -1 : index;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void set(E arg0) {
    Array.set(array, last, arg0);
  }

}// END OF CLASS ArrayListIterator
