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

package com.davidbracewell.collection;

import com.davidbracewell.tuple.Tuple3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Concurrent map multi counter.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class ConcurrentMapMultiCounter<K, V> extends AbstractMapMultiCounter<K, V> {
  private static final long serialVersionUID = 1L;
  private final Map<K, Counter<V>> map = new ConcurrentHashMap<>();

  /**
   * Instantiates a new Concurrent map multi counter.
   */
  public ConcurrentMapMultiCounter() {
    super(new ConcurrentHashMap<>());
  }

  /**
   * Instantiates a new Concurrent map multi counter.
   *
   * @param triples the triples
   */
  public ConcurrentMapMultiCounter(Tuple3<K, V, ? extends Number>... triples) {
    super(new ConcurrentHashMap<>(), triples);
  }

  @Override
  protected Counter<V> newCounter() {
    return Counters.newConcurrentCounter();
  }

  @Override
  protected MultiCounter<K, V> newInstance() {
    return new ConcurrentMapMultiCounter<>();
  }

}//END OF ConcurrentMapMultiCounter
