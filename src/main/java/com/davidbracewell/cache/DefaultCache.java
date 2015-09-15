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

package com.davidbracewell.cache;

import com.davidbracewell.collection.LRUMap;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The type Default cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class DefaultCache<K, V> implements Cache<K, V>, Serializable {

  private static final long serialVersionUID = 1L;
  private final Map<K, V> cache;
  private final String name;

  /**
   * Default constructor
   *
   * @param specification The specification
   */
  public DefaultCache(@NonNull CacheSpec<K, V> specification) {
    this.name = specification.getName();
    if (specification.getMaxSize() < Integer.MAX_VALUE) {
      cache = Collections.synchronizedMap(new LRUMap<>(specification.getMaxSize()));
    } else {
      cache = new ConcurrentHashMap<>();
    }
  }

  @Override
  public void close() throws Exception {

  }

  @Override
  public boolean containsKey(K key) {
    return cache.containsKey(key);
  }

  @Override
  public V get(K key) {
    return cache.get(key);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return cache.putIfAbsent(key, value);
  }

  @Override
  public void put(K key, V value) {
    cache.put(key, value);
  }

  @Override
  public long size() {
    return cache.size();
  }

  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public void invalidate(K key) {
    cache.remove(key);
  }

}//END OF DefaultCache
