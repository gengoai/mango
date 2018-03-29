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

package com.gengoai.mango.cache;

import com.gengoai.mango.conversion.Cast;
import com.gengoai.mango.reflection.Specification;
import com.gengoai.mango.string.StringUtils;
import com.google.common.cache.RemovalListener;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Common specification for a cache.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class CacheSpec<K, V> implements Specification, Serializable {
   private static final long serialVersionUID = 1L;

   private int maxSize = Integer.MAX_VALUE;
   private int concurrencyLevel = Runtime.getRuntime().availableProcessors();
   private int initialCapacity = 0;
   private boolean weakValues = false;
   private boolean weakKeys = false;
   private long expiresAfterAccess = -1;
   private long expiresAfterWrite = -1;
   private String name = "";
   private RemovalListener<K, V> removalListener = null;
   private Function<K, V> cacheFunction = null;
   private String cacheEngine;

   /**
    * Default Constructor
    */
   public CacheSpec() {

   }

   /**
    * Create cache spec.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    * @return the cache spec
    */
   public static <K, V> CacheSpec<K, V> create() {
      return new CacheSpec<>();
   }

   /**
    * From cache spec.
    *
    * @param <K>           the type parameter
    * @param <V>           the type parameter
    * @param specification the specification
    * @return the cache spec
    */
   public static <K, V> CacheSpec<K, V> from(String specification) {
      return CacheSpec.<K, V>create().fromString(specification);
   }

   /**
    * Sets cache type.
    *
    * @param engine the engine
    * @return the cache type
    */
   public CacheSpec<K, V> engine(String engine) {
      this.cacheEngine = engine;
      return this;
   }

   @Override
   public CacheSpec<K, V> fromString(String spec) {
      return Cast.as(Specification.super.fromString(spec));
   }

   /**
    * Gets cache function.
    *
    * @return the cache function
    */
   public Function<K, V> getLoadingFunction() {
      return cacheFunction;
   }

   /**
    * Loading function cache spec.
    *
    * @param loadingFunction the loading function
    * @return the cache spec
    */
   public CacheSpec<K, V> loadingFunction(Function<K, V> loadingFunction) {
      this.cacheFunction = loadingFunction;
      return this;
   }

   /**
    * Gets cache type.
    *
    * @return the cache type
    */
   public CacheEngine getEngine() {
      if (StringUtils.isNullOrBlank(cacheEngine)) {
         return CacheEngines.get("Guava");
      }
      return CacheEngines.get(cacheEngine);
   }

   /**
    * Gets max size.
    *
    * @return the max size
    */
   public int getMaxSize() {
      return maxSize;
   }

   /**
    * Gets concurrency level.
    *
    * @return The concurrency level
    */
   public int getConcurrencyLevel() {
      return concurrencyLevel;
   }

   /**
    * Gets initial capacity.
    *
    * @return The initial capacity
    */
   public int getInitialCapacity() {
      return initialCapacity;
   }

   /**
    * Is weak values boolean.
    *
    * @return True - uses weak values, False normal values
    */
   public boolean isWeakValues() {
      return weakValues;
   }

   /**
    * Is weak keys boolean.
    *
    * @return True - uses weak keys, False normal keys
    */
   public boolean isWeakKeys() {
      return weakKeys;
   }

   /**
    * Gets expires after access.
    *
    * @return The time in milliseconds after access that an item is removed
    */
   public long getExpiresAfterAccess() {
      return expiresAfterAccess;
   }

   /**
    * Gets expires after write.
    *
    * @return The time in milliseconds after write that an item is removed
    */
   public long getExpiresAfterWrite() {
      return expiresAfterWrite;
   }

   /**
    * Gets name.
    *
    * @return The name of the cache
    */
   public String getName() {
      return name;
   }

   /**
    * Gets removal listener.
    *
    * @return The listener to call when an item is removed
    */
   public RemovalListener<K, V> getRemovalListener() {
      return removalListener;
   }

   /**
    * Sets max size.
    *
    * @param maxSize the max size
    * @return the max size
    */
   public CacheSpec<K, V> maxSize(int maxSize) {
      checkArgument(maxSize > 0, "maxSize must be > 0");
      this.maxSize = maxSize;
      return this;
   }

   /**
    * Concurrency level t.
    *
    * @param <T>              the type parameter
    * @param concurrencyLevel the concurrency level
    * @return the t
    */
   public <T extends CacheSpec<K, V>> T concurrencyLevel(int concurrencyLevel) {
      checkArgument(concurrencyLevel > 0, "Concurrency Level must be > 0");
      this.concurrencyLevel = concurrencyLevel;
      return Cast.as(this);
   }

   /**
    * Sets the initial capacity
    *
    * @param <T>             the type parameter
    * @param initialCapacity The initial capacity
    * @return This Cache spec
    */
   public <T extends CacheSpec<K, V>> T initialCapacity(int initialCapacity) {
      checkArgument(initialCapacity > 0, "Capacity must be > 0");
      this.initialCapacity = initialCapacity;
      return Cast.as(this);
   }

   /**
    * Sets to use weak values
    *
    * @param <T> the type parameter
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T weakValues() {
      this.weakValues = true;
      return Cast.as(this);
   }

   /**
    * Sets to use weak keys
    *
    * @param <T> the type parameter
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T weakKeys() {
      this.weakKeys = true;
      return Cast.as(this);
   }

   private long convertStringToTime(String duration) {
      checkArgument(StringUtils.isNotNullOrBlank(duration), "Duration cannot be null or empty");
      duration = duration.trim().toLowerCase();
      long time = Integer.valueOf(duration.substring(0, duration.length() - 1));
      switch (duration.charAt(duration.length() - 1)) {
         case 'm':
            return TimeUnit.MINUTES.toMillis(time);
         case 'd':
            return TimeUnit.DAYS.toMillis(time);
         case 'h':
            return TimeUnit.HOURS.toMillis(time);
         case 's':
            return TimeUnit.SECONDS.toMillis(time);
      }
      throw new IllegalArgumentException();
   }

   /**
    * The time in milliseconds after write that an item is removed
    *
    * @param <T>      the type parameter
    * @param duration duration is a string in the form d+[dhms] where d is days, h is hours, m is minutes, and s is
    *                 seconds
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T expiresAfterWrite(String duration) {
      this.expiresAfterWrite = convertStringToTime(duration);
      return Cast.as(this);
   }

   /**
    * The time in milliseconds after access that an item is removed
    *
    * @param <T>      the type parameter
    * @param duration duration is a string in the form d+[dhms] where d is days, h is hours, m is minutes, and s is
    *                 seconds
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T expiresAfterAccess(String duration) {
      this.expiresAfterAccess = convertStringToTime(duration);
      return Cast.as(this);
   }

   /**
    * Sets the name
    *
    * @param <T>  the type parameter
    * @param name The name
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T name(String name) {
      this.name = name;
      return Cast.as(this);
   }

   /**
    * Sets the removal listener
    *
    * @param <T>      the type parameter
    * @param listener The removal listener
    * @return This cache spec
    */
   public <T extends CacheSpec<K, V>> T removalListener(RemovalListener<K, V> listener) {
      this.removalListener = listener;
      return Cast.as(this);
   }


}//END OF CacheSpecification
