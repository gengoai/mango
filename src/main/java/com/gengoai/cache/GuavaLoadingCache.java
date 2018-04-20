
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

package com.gengoai.cache;

import com.gengoai.function.SerializableSupplier;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;

/**
 * <p>Guava-backed loading cache implementation.</p>
 *
 * @param <K> the key type
 * @param <V> the value parameter
 * @author David B. Bracewell
 */
public class GuavaLoadingCache<K, V> implements AutoCalculatingCache<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final LoadingCache<K, V> cache;
   private final String name;

   /**
    * Instantiates a new Guava loading cache.
    *
    * @param name        the name
    * @param builder     the builder
    * @param cacheLoader the cache loader
    */
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
   public boolean containsKey(K key) {
      return cache.asMap().containsKey(key);
   }

   @Override
   public V get(K key, SerializableSupplier<? extends V> supplier) throws ExecutionException {
      return cache.get(key, supplier::get);
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

   @Override
   public void invalidateAll() {
      cache.invalidateAll();
   }


}//END OF GuavaCache
