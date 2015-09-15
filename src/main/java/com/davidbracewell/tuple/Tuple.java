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

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public interface Tuple extends Iterable<Object> {

  int degree();

  Object[] array();

  @Override
  default Iterator<Object> iterator() {
    return Arrays.asList(array()).iterator();
  }

  /**
   * Tuple triple.
   *
   * @param <F>    the type parameter
   * @param <S>    the type parameter
   * @param <T>    the type parameter
   * @param first  the first
   * @param second the second
   * @param third  the third
   * @return the triple
   */
  static <F, S, T> Tuple3<F, S, T> tuple(F first, S second, T third) {
    return Tuple3.of(first, second, third);
  }

  /**
   * Tuple tuple 4.
   *
   * @param <F>    the type parameter
   * @param <S>    the type parameter
   * @param <T>    the type parameter
   * @param <D>    the type parameter
   * @param first  the first
   * @param second the second
   * @param third  the third
   * @param fourth the fourth
   * @return the tuple 4
   */
  static <F, S, T, D> Tuple4<F, S, T, D> tuple(F first, S second, T third, D fourth) {
    return Tuple4.of(first, second, third, fourth);
  }

  /**
   * Tuple pair.
   *
   * @param <F>    the type parameter
   * @param <S>    the type parameter
   * @param first  the first
   * @param second the second
   * @return the pair
   */
  static <F, S> Tuple2<F, S> tuple(F first, S second) {
    return Tuple2.of(first, second);
  }


}//END OF Tuple
