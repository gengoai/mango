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

package com.gengoai.mango.tuple;

import com.gengoai.mango.conversion.Cast;
import lombok.Getter;
import lombok.NonNull;

/**
 * A tuple of degree one.
 *
 * @param <V1> the type parameter
 * @author David B. Bracewell
 */
public class Tuple1<V1> extends Tuple {
  private static final long serialVersionUID = 1L;
  /**
   * The V 1.
   */
  @Getter
  public final V1 v1;

  /**
   * Instantiates a new Tuple 1.
   *
   * @param v1 the v 1
   */
  public Tuple1(V1 v1) {
    this.v1 = v1;
  }

  /**
   * Instantiates a new Tuple 1.
   *
   * @param other the other
   */
  public Tuple1(@NonNull Tuple1<? extends V1> other) {
    this.v1 = other.v1;
  }

  /**
   * Of tuple 1.
   *
   * @param <V1> the type parameter
   * @param v1   the v 1
   * @return the tuple 1
   */
  public static <V1> Tuple1<V1> of(V1 v1) {
    return new Tuple1<>(v1);
  }

  @Override
  public Tuple1<V1> copy() {
    return of(v1);
  }

  @Override
  public int degree() {
    return 1;
  }

  @Override
  public Object[] array() {
    return new Object[]{v1};
  }

  @Override
  public String toString() {
    return "(" + v1 + ")";
  }

  @Override
  public <T> Tuple2<T, V1> appendLeft(T object) {
    return Tuple2.of(object, v1);
  }

  @Override
  public <T> Tuple2<V1, T> appendRight(T object) {
    return Tuple2.of(v1, object);
  }

  @Override
  public <T> T get(int i) {
    switch (i) {
      case 0:
        return Cast.as(v1);
      default:
        throw new ArrayIndexOutOfBoundsException();
    }
  }

}//END OF Tuple0
