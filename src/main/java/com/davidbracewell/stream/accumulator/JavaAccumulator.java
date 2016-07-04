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

import lombok.NonNull;

import java.util.concurrent.atomic.AtomicReference;

/**
 * The type Java accumulator.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class JavaAccumulator<T> implements MAccumulator<T> {
  private static final long serialVersionUID = 1L;
  /**
   * The Accumulatable.
   */
  final Accumulatable<T> accumulatable;
  /**
   * The Value reference.
   */
  final AtomicReference<T> valueReference;

  final String name;

  /**
   * Instantiates a new Java accumulator.
   *
   * @param accumulatable the accumulatable
   * @param initialValue  the initial value
   */
  public JavaAccumulator(@NonNull Accumulatable<T> accumulatable, T initialValue, String name) {
    this.accumulatable = accumulatable;
    this.valueReference = new AtomicReference<>(accumulatable.zero(initialValue));
    this.name = name;
  }

  @Override
  public void add(T value) {
    this.valueReference.accumulateAndGet(value, accumulatable::addAccumulator);
  }

  @Override
  public T value() {
    return this.valueReference.get();
  }

  @Override
  public void setValue(T value) {
    this.valueReference.getAndSet(value);
  }


  @Override
  public String name() {
    return name;
  }

}//END OF JavaAccumulator
