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

package com.davidbracewell.tuple;

import lombok.NonNull;

import java.util.Arrays;
import java.util.Iterator;
import java.util.function.Function;

/**
 * A tuple is a collection of values of possibly different types.
 *
 * @author David B. Bracewell
 */
public interface Tuple extends Iterable<Object> {

  /**
   * The number of items in the tuple
   *
   * @return the number of items in the tuple
   */
  int degree();

  /**
   * The tuple as an array of objects
   *
   * @return an array representing the items in the tuple
   */
  Object[] array();

  @Override
  default Iterator<Object> iterator() {
    return Arrays.asList(array()).iterator();
  }

  /**
   * Map r.
   *
   * @param <R>      the type parameter
   * @param function the function
   * @return the r
   */
  default <R> R map(@NonNull Function<Tuple, R> function) {
    return function.apply(this);
  }

  /**
   * Get object.
   *
   * @param index the index
   * @return the object
   */
  default Object get(int index) {
    return array()[index];
  }

  /**
   * Shift left tuple.
   *
   * @return the tuple
   */
  default Tuple shiftLeft() {
    if (degree() < 2) {
      return Tuple0.INSTANCE;
    }
    Object[] copy = new Object[degree() - 1];
    System.arraycopy(array(), 1, copy, 0, copy.length);
    return NTuple.of(copy);
  }

  /**
   * Shift right tuple.
   *
   * @return the tuple
   */
  default Tuple shiftRight() {
    if (degree() < 2) {
      return Tuple0.INSTANCE;
    }
    Object[] copy = new Object[degree() - 1];
    System.arraycopy(array(), 0, copy, 0, copy.length);
    return NTuple.of(copy);
  }

  /**
   * Append right tuple.
   *
   * @param <T>    the type parameter
   * @param object the object
   * @return the tuple
   */
  default <T> Tuple appendRight(T object) {
    if (degree() == 0) {
      return Tuple1.of(object);
    }
    Object[] copy = new Object[degree() + 1];
    System.arraycopy(array(), 0, copy, 0, degree());
    copy[copy.length - 1] = object;
    return NTuple.of(copy);
  }

  /**
   * Append left tuple.
   *
   * @param <T>    the type parameter
   * @param object the object
   * @return the tuple
   */
  default <T> Tuple appendLeft(T object) {
    if (degree() == 0) {
      return Tuple1.of(object);
    }
    Object[] copy = new Object[degree() + 1];
    System.arraycopy(array(), 0, copy, 1, degree());
    copy[0] = object;
    return NTuple.of(copy);
  }


}//END OF Tuple
