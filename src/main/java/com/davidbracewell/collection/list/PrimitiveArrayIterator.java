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

import com.davidbracewell.conversion.Convert;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Array;
import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * <p>
 * An <code>Iterator</code> that iterators over an array of primitives.
 * </p>
 *
 * @param <E> The type to convert the primitive to
 * @author David B. Bracewell
 */
public class PrimitiveArrayIterator<E> implements Iterator<E> {

  private final Object array;
  private final int length;
  private final Class<E> rClass;
  private int index = 0;

  protected PrimitiveArrayIterator(@NonNull Object array, @NonNull Class<E> rClass) {
    if (!array.getClass().isArray()) {
      throw new IllegalArgumentException("Must pass an array");
    }
    if (!array.getClass().getComponentType().isPrimitive()) {
      throw new IllegalArgumentException("Object must be a primitive array");
    }
    this.array = array;
    this.length = Array.getLength(array);
    this.rClass = rClass;
  }

  /**
   * Double array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Double> doubleArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Double.class);
  }

  /**
   * Integer array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Integer> integerArrayIterator(
    Object array
                                                                    ) {
    return new PrimitiveArrayIterator<>(array, Integer.class);
  }

  /**
   * Short array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Short> shortArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Short.class);
  }

  /**
   * Long array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Long> longArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Long.class);
  }

  /**
   * Float array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Float> floatArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Float.class);
  }

  /**
   * Byte array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Byte> byteArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Byte.class);
  }

  /**
   * Boolean array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Boolean> booleanArrayIterator(
    Object array
                                                                    ) {
    return new PrimitiveArrayIterator<>(array, Boolean.class);
  }

  /**
   * Character array iterator.
   *
   * @param array the array
   * @return the primitive array iterator
   */
  public static PrimitiveArrayIterator<Character> characterArrayIterator(Object array) {
    return new PrimitiveArrayIterator<>(array, Character.class);
  }

  @Override
  public boolean hasNext() {
    return (index < length);
  }

  @Override
  @SneakyThrows
  public E next() {
    if (index >= length) {
      throw new NoSuchElementException();
    }
    Object val = Array.get(array, index);
    index++;
    return Convert.convert(val, rClass);
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }

}//END OF PrimitiveArrayIterator
