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
import java.util.Map;

/**
 * The type Tuple 2.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
@Data
public class Tuple2<K, V> implements Serializable, Map.Entry<K, V>, Comparable<Tuple2<K, V>>, Tuple {

  private static final long serialVersionUID = 1L;
  /**
   * The first value
   */
  public final K v1;
  /**
   * The second value
   */
  public final V v2;

  /**
   * Instantiates a new Tuple 2.
   *
   * @param v1 the first value
   * @param v2 the second value
   */
  public Tuple2(K v1, V v2) {
    this.v1 = v1;
    this.v2 = v2;
  }

  @Override
  public int degree() {
    return 2;
  }

  @Override
  public Object[] array() {
    return new Object[]{v1, v2};
  }

  @Override
  public int compareTo(Tuple2<K, V> o) {
    if (o == null) {
      return 1;
    }
    int result = Sorting.compare(v1, o.v1);
    return result != 0 ? result : Sorting.compare(v2, o.v2);
  }

  @Override
  public K getKey() {
    return v1;
  }

  @Override
  public V getValue() {
    return v2;
  }

  @Override
  public V setValue(V value) {
    throw new UnsupportedOperationException();
  }

  /**
   * Of tuple 2.
   *
   * @param <K>   the type parameter
   * @param <V>   the type parameter
   * @param key   the key
   * @param value the value
   * @return the tuple 2
   */
  public static <K, V> Tuple2<K, V> of(K key, V value) {
    return new Tuple2<>(key, value);
  }

  /**
   * Expand tuple 3.
   *
   * @param <Z> the type parameter
   * @param z   the z
   * @return the tuple 3
   */
  public <Z> Tuple3<K, V, Z> append(Z z) {
    return Tuple3.of(v1, v2, z);
  }


  /**
   * Append tuple 4.
   *
   * @param <K2>  the type parameter
   * @param <V2>  the type parameter
   * @param other the other
   * @return the tuple 4
   */
  public <K2, V2> Tuple4<K, V, K2, V2> append(Tuple2<K2, V2> other) {
    if (other == null) {
      return Tuple4.of(v1, v2, null, null);
    }
    return Tuple4.of(v1, v2, other.v1, other.v2);
  }


  @Override
  public String toString() {
    return "(" + v1 + ", " + v2 + ")";
  }

}//END OF Tuple2
