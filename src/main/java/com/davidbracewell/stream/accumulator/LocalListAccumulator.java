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

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author David B. Bracewell
 */
public class LocalListAccumulator<T> implements MListAccumulator<T> {
  private static final long serialVersionUID = 1L;
  private final List<T> list = new CopyOnWriteArrayList<>();
  private final String name;

  public LocalListAccumulator() {
    this(null);
  }

  public LocalListAccumulator(String name) {
    this.name = name;
  }

  @Override
  public void add(T t) {
    list.add(t);
  }

  @Override
  public List<T> value() {
    return list;
  }

  @Override
  public void merge(MAccumulator<T, List<T>> other) {
    if (other != null) {
      this.list.addAll(other.value());
    }
  }

  @Override
  public Optional<String> name() {
    return Optional.of(name);
  }

  @Override
  public void reset() {
    this.list.clear();
  }


}// END OF LocalMListAccumulator
