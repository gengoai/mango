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
import lombok.NonNull;

import java.io.Serializable;
import java.util.concurrent.ExecutionException;

/**
 * <p>Cache implementation in which nothing is retained</p>
 *
 * @param <K> the key type
 * @param <V> the value type
 * @author David B. Bracewell
 */
public class NoOptCache<K, V> implements Cache<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final String name;


   /**
    * Default Constructor
    *
    * @param specification The cache specification
    */
   public NoOptCache(@NonNull CacheSpec<K, V> specification) {
      this.name = specification.getName();
   }

   @Override
   public boolean containsKey(K key) {
      return false;
   }

   @Override
   public V get(K key) {
      return null;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public void put(K key, V value) {

   }

   @Override
   public V get(K key, @NonNull SerializableSupplier<? extends V> supplier) throws ExecutionException {
      return supplier.get();
   }

   @Override
   public long size() {
      return 0;
   }

   @Override
   public void invalidateAll(Iterable<? extends K> keys) {
   }

   @Override
   public void invalidate(K key) {
   }

   @Override
   public void invalidateAll() {
   }

}//END OF NoOptCache
