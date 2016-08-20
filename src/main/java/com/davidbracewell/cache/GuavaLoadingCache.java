
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

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * The type Guava loading cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class GuavaLoadingCache<K, V> implements AutoCalculatingCache<K, V>, Serializable {
  private static final long serialVersionUID = 1L;
  private final LoadingCache<K, V> cache;
  private final String name;

  public GuavaLoadingCache(@NonNull String name, @NonNull CacheBuilder<K, V> builder, @NonNull final Function<K, V> cacheLoader) {
    this.name = name;
    this.cache = builder.build(new CacheLoader<K, V>() {
      @Override
      public V load(K key) throws Exception {
        return cacheLoader.apply(key);
      }
    });
  }

  @Override
  public void clear() {
    cache.invalidateAll();
  }

  @Override
  public boolean containsKey(K key) {
    return cache.asMap().containsKey(key);
  }

  @Override
  @SneakyThrows
  public V putIfAbsent(K key, final V value) {
    return cache.get(key, () -> value);
  }

  @Override
  public void put(K key, final V value) {
    cache.put(key, value);
  }

  @Override
  public long size() {
    return cache.size();
  }

  @Override
  public void invalidateAll(Iterable<? extends K> keys) {
    cache.invalidateAll(keys);
  }

  @Override
  public void invalidate(K key) {
    cache.invalidate(key);
  }

  @Override
  @SneakyThrows
  public V get(K key) {
    return cache.get(key);
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void refresh(K key) throws ExecutionException {
    cache.refresh(key);
  }

}//END OF GuavaCache
