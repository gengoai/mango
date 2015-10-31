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

import java.io.Serializable;

/**
 * A tuple of degree zero.
 *
 * @author David B. Bracewell
 */
public class Tuple0 implements Tuple, Serializable, Comparable<Tuple0>, Copyable<Tuple0> {
  private static final long serialVersionUID = 1L;

  public static Tuple0 INSTANCE = new Tuple0();


  @Override
  public int compareTo(Tuple0 o) {
    return 0;
  }

  @Override
  public Tuple0 copy() {
    return new Tuple0();
  }

  @Override
  public int degree() {
    return 0;
  }

  @Override
  public Object[] array() {
    return new Object[0];
  }

  @Override
  public int hashCode() {
    return 1;
  }

  @Override
  public boolean equals(Object o) {
    return o != null && o instanceof Tuple0;
  }

  @Override
  public String toString() {
    return "()";
  }

}//END OF Tuple0
