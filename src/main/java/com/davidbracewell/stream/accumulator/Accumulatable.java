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

package com.davidbracewell.stream.accumulator;

import java.io.Serializable;

/**
 * The interface Accumulatable.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public interface Accumulatable<T> extends Serializable {


  /**
   * Add accumulator t.
   *
   * @param t1 the t 1
   * @param t2 the t 2
   * @return the t
   */
  T addAccumulator(T t1, T t2);

  /**
   * Add in place t.
   *
   * @param t1 the t 1
   * @param t2 the t 2
   * @return the t
   */
  default T addInPlace(T t1, T t2) {
    return addAccumulator(t1, t2);
  }

  /**
   * Zero t.
   *
   * @param zeroValue the zero value
   * @return the t
   */
  T zero(T zeroValue);


}//END OF Accumulatable
