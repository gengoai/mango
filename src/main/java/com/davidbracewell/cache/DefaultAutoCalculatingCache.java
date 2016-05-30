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
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * The type Default auto calculating cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class DefaultAutoCalculatingCache<K, V> implements AutoCalculatingCache<K, V>, Serializable {
  private static final long serialVersionUID = 1L;
  private final Map<K, V> cache;
  private final String name;
  private final Function<K, V> function;

  /**
   * Default constructor
   *
   * @param specification The specification
   */
  public DefaultAutoCalculatingCache(@NonNull CacheSpec<K, V> specification) {
    this.name = specification.getName();
    this.function = specification.getLoadingFunction();
    if (specification.getMaxSize() < Integer.MAX_VALUE) {
      cache = Collections.synchronizedMap(new LRUMap<>(specification.getMaxSize()));
    } else {
      cache = new ConcurrentHashMap<>();
    }
  }


  @Override
  public void clear() {
    cache.clear();
  }

  @Override
  public boolean containsKey(K key) {
    return cache.containsKey(key);
  }

  @Override
  public V get(K key) {
    return cache.computeIfAbsent(key, function);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void invalidate(K key) {
    cache.remove(key);
  }

  @Override
  public void put(K key, V value) {
    cache.put(key, value);
  }

  @Override
  public V putIfAbsent(K key, V value) {
    return cache.putIfAbsent(key, value);
  }

  @Override
  public void refresh(K key) throws ExecutionException {
    cache.put(key, function.apply(key));
  }

  @Override
  public long size() {
    return cache.size();
  }


}//END OF DefaultAutoCalculatingCache
