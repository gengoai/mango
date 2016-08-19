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

import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * <p> A fixed size list backed by a primitive array. The items in the array are converted through calling the {@link
 * Class#cast(Object)}* method of provided Class object. </p>  <p> This implementation does not support add, remove
 * or toArray methods. A {@link UnsupportedOperationException} will be thrown if these methods are called. </p>
 *
 * @param <E> The type of element the primitive will be cast as
 * @author David B. Bracewell
 * @version $Id$
 */
public class PrimitiveArrayList<E> extends AbstractList<E> implements Serializable {

  private static final long serialVersionUID = -2756365804776754936L;
  private Object array = null;
  private Class<E> objectType = null;
  private int arraySize = 0;

  /**
   * Constructs a fixed size primitive backed list.
   *
   * @param array      The primitive array
   * @param objectType Class information for the object to convert the                   primitive to
   * @throws NullPointerException     if either of the arguments are null
   * @throws IllegalArgumentException if the array object is not an array or                                  the
   *                                  component type is not a primitive
   */
  public PrimitiveArrayList(@NonNull Object array, @NonNull Class<E> objectType) {
    Preconditions.checkArgument(array.getClass().isArray(), "The object must be an array of primitives.");
    Preconditions.checkArgument(array.getClass().getComponentType().isPrimitive(), "The object must be an array of primitives.");
    this.array = array;
    this.arraySize = Array.getLength(array);
    this.objectType = objectType;
  }

  @Override
  public E get(int index) {
    Object o = Array.get(array, index);
    if (o instanceof Number) {
      Number num = (Number) o;
      if (objectType.equals(Double.class)) {
        return objectType.cast(num.doubleValue());
      } else if (objectType.equals(Integer.class)) {
        return objectType.cast(num.intValue());
      } else if (objectType.equals(Short.class)) {
        return objectType.cast(num.shortValue());
      } else if (objectType.equals(Long.class)) {
        return objectType.cast(num.longValue());
      } else if (objectType.equals(Float.class)) {
        return objectType.cast(num.floatValue());
      }
    }
    return objectType.cast(Array.get(array, index));
  }

  @Override
  public boolean isEmpty() {
    return arraySize == 0;
  }

  @Override
  public Iterator<E> iterator() {
    return new PrimitiveArrayIterator<>(array, objectType);
  }

  @Override
  public ListIterator<E> listIterator() {
    return new PrimitiveArrayListIterator<>(array);
  }

  @Override
  public ListIterator<E> listIterator(int index) {
    return new PrimitiveArrayListIterator<>(array, index);
  }

  @Override
  public E set(int index, E element) {
    E old = get(index);
    Array.set(array, index, element);
    return old;
  }

  @Override
  public int size() {
    return arraySize;
  }

  @Override
  public List<E> subList(int fromIndex, int toIndex) {
    Object a2 = Array.newInstance(array.getClass().getComponentType(), (toIndex - fromIndex));
    System.arraycopy(array, fromIndex, a2, 0, (toIndex - fromIndex));
    return new PrimitiveArrayList<>(a2, objectType);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder("[");
    for (int i = 0; i < size(); i++) {
      sb.append(get(i));
      if (i > 0) {
        sb.append(",");
      }
      if (i < (size() - 1)) {
        sb.append(" ");
      }
    }
    return sb.append("]").toString();
  }

}// END OF CLASS PrimitiveArrayList
