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

import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.HashMapCounter;

/**
 * @author David B. Bracewell
 */
public class CounterAccumulatable<E> implements Accumulatable<Counter<E>> {
  private static final long serialVersionUID = 1L;

  @Override
  public Counter<E> addInPlace(Counter<E> t1, Counter<E> t2) {
    return t1.merge(t2);
  }

  @Override
  public Counter<E> addAccumulator(Counter<E> t1, Counter<E> t2) {
    Counter<E> copy = new HashMapCounter<>(t1);
    return copy.merge(t2);
  }

  @Override
  public Counter<E> zero(Counter<E> zeroValue) {
    return zeroValue;
  }

}//END OF CounterAccumulatable
