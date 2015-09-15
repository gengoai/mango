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

import com.davidbracewell.Copyable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;

/**
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public class Tuple1<V1> implements Tuple, Serializable, Comparable<Tuple1>, Copyable<Tuple1<V1>>, Cloneable {
  private static final long serialVersionUID = 1L;
  public final V1 v1;

  public Tuple1(V1 v1) {
    this.v1 = v1;
  }

  public Tuple1(@NonNull Tuple1<? extends V1> other) {
    this.v1 = other.v1;
  }


  @Override
  public int compareTo(Tuple1 o) {
    return 0;
  }

  @Override
  public Tuple1<V1> copy() {
    return new Tuple1<>(this);
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
  protected Object clone() throws CloneNotSupportedException {
    return new Tuple1(this);
  }

  @Override
  public String toString() {
    return "(" + v1 + ")";
  }

  public Tuple1<V1> append(@NonNull Tuple0 tuple0) {
    return copy();
  }

  public <V2> Tuple2<V1, V2> append(@NonNull Tuple1<V2> tuple1) {
    return Tuple2.of(v1, tuple1.v1);
  }

  public <V2, V3> Tuple3<V1, V2, V3> append(@NonNull Tuple2<V2, V3> tuple2) {
    return Tuple3.of(v1, tuple2.v1, tuple2.v2);
  }

  public <V2, V3, V4> Tuple4<V1, V2, V3, V4> append(@NonNull Tuple3<V2, V3, V4> tuple3) {
    return Tuple4.of(v1, tuple3.v1, tuple3.v2, tuple3.v3);
  }

  public static <V1> Tuple1<V1> of(V1 v1) {
    return new Tuple1<>(v1);
  }

}//END OF Tuple0
