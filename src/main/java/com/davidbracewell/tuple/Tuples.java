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

/**
 * The interface Tuples.
 *
 * @author David B. Bracewell
 */
public interface Tuples {

  /**
   * Creates a triple
   *
   * @param <F>    the first type parameter
   * @param <S>    the second type parameter
   * @param <T>    the third type parameter
   * @param first  the first item
   * @param second the second item
   * @param third  the third item
   * @return the triple
   */
  static <F, S, T> Tuple3<F, S, T> tuple(F first, S second, T third) {
    return Tuple3.of(first, second, third);
  }

  /**
   * Creates a tuple with degree four.
   *
   * @param <F>    the first type parameter
   * @param <S>    the second type parameter
   * @param <T>    the third type parameter
   * @param <D>    the fourth type parameter
   * @param first  the first item
   * @param second the second item
   * @param third  the third item
   * @param fourth the fourth item
   * @return the quadruple
   */
  static <F, S, T, D> Tuple4<F, S, T, D> tuple(F first, S second, T third, D fourth) {
    return Tuple4.of(first, second, third, fourth);
  }

  /**
   * Creates a pair.
   *
   * @param <F>    the first type parameter
   * @param <S>    the second type parameter
   * @param first  the first item
   * @param second the second item
   * @return the pair
   */
  static <F, S> Tuple2<F, S> tuple(F first, S second) {
    return Tuple2.of(first, second);
  }

  /**
   * Creates a tuple of degree zero.
   *
   * @return the tuple with degree zero.
   */
  static Tuple0 tuple() {
    return Tuple0.INSTANCE;
  }

  /**
   * Creates a tuple of degree one.
   *
   * @param <F>   the first type parameter
   * @param first the first item
   * @return the tuple of degree one.
   */
  static <F> Tuple1<F> tuple(F first) {
    return Tuple1.of(first);
  }

  /**
   * Tuple tuple.
   *
   * @param items the items
   * @return the tuple
   */
  static Tuple tuple(Object... items) {
    return NTuple.of(items);
  }

}//END OF Tuples
