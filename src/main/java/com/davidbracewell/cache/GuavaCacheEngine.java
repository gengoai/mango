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

import com.davidbracewell.conversion.Cast;
import com.google.common.cache.CacheBuilder;
import lombok.NonNull;
import org.kohsuke.MetaInfServices;

import java.util.concurrent.TimeUnit;

/**
 * <p>Cache engine for creating Guava-backed caches.</p>
 *
 * @author David B. Bracewell
 */
@MetaInfServices
public class GuavaCacheEngine implements CacheEngine {

   @Override
   public String name() {
      return "Guava";
   }

   @Override
   @SuppressWarnings("unchecked")
   public <K, V> Cache<K, V> create(@NonNull CacheSpec<K, V> cacheSpec) {
      CacheBuilder<K, V> builder = cacheBuilderFromSpec(cacheSpec);
      if (cacheSpec.getLoadingFunction() == null) {
         return new GuavaCache<>(cacheSpec.getName(), builder);
      }
      return new GuavaLoadingCache<>(cacheSpec.getName(), builder, cacheSpec.getLoadingFunction());
   }

   @SuppressWarnings("unchecked")
   private <K, V> CacheBuilder<K, V> cacheBuilderFromSpec(CacheSpec<K, V> specification) {
      CacheBuilder cacheBuilder = CacheBuilder.newBuilder();
      if (specification.getMaxSize() > 0) {
         cacheBuilder.maximumSize(specification.getMaxSize());
      }
      if (specification.getConcurrencyLevel() > 0) {
         cacheBuilder.concurrencyLevel(specification.getMaxSize());
      }
      if (specification.getInitialCapacity() > 0) {
         cacheBuilder.initialCapacity(specification.getInitialCapacity());
      }
      if (specification.getExpiresAfterAccess() > 0) {
         cacheBuilder.expireAfterAccess(specification.getExpiresAfterAccess(), TimeUnit.MILLISECONDS);
      }
      if (specification.getExpiresAfterWrite() > 0) {
         cacheBuilder.expireAfterWrite(specification.getExpiresAfterWrite(), TimeUnit.MILLISECONDS);
      }
      if (specification.isWeakKeys()) {
         cacheBuilder.weakKeys();
      }
      if (specification.isWeakValues()) {
         cacheBuilder.weakValues();
      }
      if (specification.getRemovalListener() != null) {
         cacheBuilder.removalListener(specification.getRemovalListener());
      }
      return Cast.as(cacheBuilder);
   }
}//END OF GuavaCacheEngine
