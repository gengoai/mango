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

package com.gengoai.tuple;

import com.gengoai.conversion.Cast;

/**
 * The type Tuple 3.
 *
 * @param <A> the type parameter
 * @param <B> the type parameter
 * @param <C> the type parameter
 * @author David B. Bracewell
 */

public class Tuple3<A, B, C> extends Tuple {
  private static final long serialVersionUID = 1L;
  /**
   * the first value
   */
  public final A v1;
  /**
   * the second value
   */
  public final B v2;
  /**
   * the third value
   */
  public final C v3;

  /**
   * Instantiates a new Tuple 3.
   *
   * @param v1 the first value
   * @param v2 the second value
   * @param v3 the third value
   */
  public Tuple3(A v1, B v2, C v3) {
    this.v1 = v1;
    this.v2 = v2;
    this.v3 = v3;
  }

  /**
   * Of tuple 3.
   *
   * @param <A> the type parameter
   * @param <B> the type parameter
   * @param <C> the type parameter
   * @param a   the a
   * @param b   the b
   * @param c   the c
   * @return the tuple 3
   */
  public static <A, B, C> Tuple3<A, B, C> of(A a, B b, C c) {
    return new Tuple3<>(a, b, c);
  }

  @Override
  public Tuple copy() {
    return new Tuple3<>(this.v1, this.v2, this.v3);
  }

  @Override
  public int degree() {
    return 3;
  }

  @Override
  public Object[] array() {
    return new Object[]{v1, v2, v3};
  }


  @Override
  public <T> Tuple4<T, A, B, C> appendRight(T object) {
    return Tuple4.of(object, v1, v2, v3);
  }

  @Override
  public <T> Tuple4<A, B, C, T> appendLeft(T object) {
    return Tuple4.of(v1, v2, v3, object);
  }

  public A getV1() {
    return this.v1;
  }

  public B getV2() {
    return this.v2;
  }

  public C getV3() {
    return this.v3;
  }

  @Override
  public Tuple2<B, C> shiftLeft() {
    return Tuple2.of(v2, v3);
  }

  @Override
  public Tuple2<A, B> shiftRight() {
    return Tuple2.of(v1, v2);
  }

  @Override
  public String toString() {
    return "(" + v1 + ", " + v2 + "," + v3 + ")";
  }

  @Override
  public <T> T get(int i) {
    switch (i){
      case 0: return Cast.as(v1);
      case 1: return Cast.as(v2);
      case 2: return Cast.as(v3);
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

}//END OF Tuple2
