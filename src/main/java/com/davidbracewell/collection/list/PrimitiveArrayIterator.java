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
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Iterator;

/**
 * <p>
 * An <code>Iterator</code> that iterators over an array of primitives.
 * </p>
 *
 * @param <E> The type to convert the primitive to
 * @author David B. Bracewell
 */
public final class PrimitiveArrayIterator<E> implements Iterator<E>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Object array;
   private final int length;
   private final Class<E> rClass;
   private int index = 0;

   protected PrimitiveArrayIterator(@NonNull Object array, @NonNull Class<E> rClass) {
      Preconditions.checkArgument(array.getClass().isArray(), "Object must be an array.");
      Preconditions.checkArgument(array.getClass().getComponentType().isPrimitive(),
                                  "Object must be a primitive array.");
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
   public static Iterator<Double> doubleArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Double.class);
   }

   /**
    * Integer array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Integer> integerArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Integer.class);
   }

   /**
    * Short array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Short> shortArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Short.class);
   }

   /**
    * Long array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Long> longArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Long.class);
   }

   /**
    * Float array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Float> floatArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Float.class);
   }

   /**
    * Byte array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Byte> byteArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Byte.class);
   }

   /**
    * Boolean array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Boolean> booleanArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Boolean.class);
   }

   /**
    * Character array iterator.
    *
    * @param array the array
    * @return the primitive array iterator
    */
   public static Iterator<Character> characterArrayIterator(Object array) {
      return new PrimitiveArrayIterator<>(array, Character.class);
   }

   @Override
   public boolean hasNext() {
      return (index < length);
   }

   @Override
   @SneakyThrows
   public E next() {
      Preconditions.checkElementIndex(index, length);
      Object val = Array.get(array, index);
      index++;
      return Convert.convert(val, rClass);
   }


}//END OF PrimitiveArrayIterator
