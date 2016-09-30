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

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author David B. Bracewell
 */
public class LocalLongAccumulator implements MLongAccumulator {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final AtomicLong longValue;

  public LocalLongAccumulator() {
    this(0, null);
  }

  public LocalLongAccumulator(String name) {
    this(0L, name);
  }

  public LocalLongAccumulator(long longValue) {
    this(longValue, null);
  }

  public LocalLongAccumulator(long longValue, String name) {
    this.name = name;
    this.longValue = new AtomicLong(longValue);
  }


  @Override
  public void add(long value) {
    longValue.addAndGet(value);
  }

  @Override
  public Long value() {
    return longValue.longValue();
  }

  @Override
  public boolean isZero() {
    return false;
  }

  @Override
  public void merge(@NonNull MAccumulator<Long, Long> other) {
    longValue.addAndGet(other.value());
  }

  @Override
  public Optional<String> name() {
    return Optional.of(name);
  }

  @Override
  public void reset() {
    longValue.set(0);
  }
}// END OF LocalMLongAccumulator
