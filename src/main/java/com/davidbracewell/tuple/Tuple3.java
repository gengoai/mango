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

import com.davidbracewell.collection.Sorting;
import lombok.Data;

import java.io.Serializable;

/**
 * The type Tuple 3.
 *
 * @param <A> the type parameter
 * @param <B> the type parameter
 * @param <C> the type parameter
 * @author David B. Bracewell
 */
@Data
public class Tuple3<A, B, C> implements Tuple, Serializable, Comparable<Tuple3<A, B, C>> {
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
   * Expand tuple 4.
   *
   * @param <D> the type parameter
   * @param d   the d
   * @return the tuple 4
   */
  public <D> Tuple4<A, B, C, D> append(D d) {
    return Tuple4.of(v1, v2, v3, d);
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
  public int degree() {
    return 3;
  }

  @Override
  public Object[] array() {
    return new Object[]{v1, v2, v3};
  }

  @Override
  public int compareTo(Tuple3<A, B, C> o) {
    if (o == null) {
      return 1;
    }
    int result = Sorting.compare(v1, o.v1);
    if (result != 0) return result;
    result = Sorting.compare(v2, o.v2);
    if (result != 0) return result;
    return Sorting.compare(v3, o.v3);
  }

  @Override
  public String toString() {
    return "(" + v1 + ", " + v2 + "," + v3 + ")";
  }
}//END OF Tuple2
