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
 * The type Tuple 4.
 *
 * @param <A> the type parameter
 * @param <B> the type parameter
 * @param <C> the type parameter
 * @param <D> the type parameter
 * @author David B. Bracewell
 */
@Data
public class Tuple4<A, B, C, D> implements Tuple, Serializable, Comparable<Tuple4<A, B, C, D>> {
  private static final long serialVersionUID = 1L;
  /**
   * the first value
   */
  public final A v1;
  /**
   * The second value
   */
  public final B v2;
  /**
   * The third value
   */
  public final C v3;
  /**
   * The fourth value
   */
  public final D v4;

  /**
   * Instantiates a new Tuple 4.
   *
   * @param a the first value
   * @param b the second value
   * @param c the third value
   * @param d the fourth value
   */
  public Tuple4(A a, B b, C c, D d) {
    this.v1 = a;
    this.v2 = b;
    this.v3 = c;
    this.v4 = d;
  }

  /**
   * Of tuple 4.
   *
   * @param <A> the type parameter
   * @param <B> the type parameter
   * @param <C> the type parameter
   * @param <D> the type parameter
   * @param a   the a
   * @param b   the b
   * @param c   the c
   * @param d   the d
   * @return the tuple 4
   */
  public static <A, B, C, D> Tuple4<A, B, C, D> of(A a, B b, C c, D d) {
    return new Tuple4<>(a, b, c, d);
  }

  @Override
  public int degree() {
    return 4;
  }

  @Override
  public Object[] array() {
    return new Object[]{v1, v2, v3, v4};
  }


  @Override
  public int compareTo(Tuple4<A, B, C, D> o) {
    if (o == null) {
      return 1;
    }
    int result = Sorting.compare(v1, o.v1);
    if (result != 0) return result;
    result = Sorting.compare(v2, o.v2);
    if (result != 0) return result;
    result = Sorting.compare(v3, o.v3);
    if (result != 0) return result;
    return Sorting.compare(v4, o.v4);
  }

  @Override
  public String toString() {
    return "(" + v1 + ", " + v2 + "," + v3 + "," + v4 + ")";
  }


}//END OF Tuple2
