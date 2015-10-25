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
import com.davidbracewell.reflection.Specification;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.cache.RemovalListener;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

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
   * @param specification the specification
   * @return the cache spec
   */
  public static <K, V> CacheSpec<K, V> from(String specification) {
    return CacheSpec.<K, V>create().fromString(specification);
  }

  /**
   * Sets cache type.
   *
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
   * Gets cache type.
   *
   * @return the cache type
   */
  public CacheEngine getEngine() {
    if (StringUtils.isNullOrBlank(cacheEngine)) {
      return CacheEngines.get("Default");
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
   * @return The concurrency level
   */
  public int getConcurrencyLevel() {
    return concurrencyLevel;
  }

  /**
   * @return The initial capacity
   */
  public int getInitialCapacity() {
    return initialCapacity;
  }

  /**
   * @return True - uses weak values, False normal values
   */
  public boolean isWeakValues() {
    return weakValues;
  }

  /**
   * @return True - uses weak keys, False normal keys
   */
  public boolean isWeakKeys() {
    return weakKeys;
  }

  /**
   * @return The time in milliseconds after access that an item is removed
   */
  public long getExpiresAfterAccess() {
    return expiresAfterAccess;
  }

  /**
   * @return The time in milliseconds after write that an item is removed
   */
  public long getExpiresAfterWrite() {
    return expiresAfterWrite;
  }

  /**
   * @return The name of the cache
   */
  public String getName() {
    return name;
  }

  /**
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
    if (maxSize <= 0) {
      throw new IllegalArgumentException("maxSize must be > 0");
    }
    this.maxSize = maxSize;
    return this;
  }

  public <T extends CacheSpec<K, V>> T concurrencyLevel(int concurrencyLevel) {
    Preconditions.checkArgument(concurrencyLevel > 0);
    this.concurrencyLevel = concurrencyLevel;
    return Cast.as(this);
  }

  /**
   * Sets the initial capacity
   *
   * @param initialCapacity The initial capacity
   * @return This Cache spec
   */
  public <T extends CacheSpec<K, V>> T initialCapacity(int initialCapacity) {
    Preconditions.checkArgument(initialCapacity > 0);
    this.initialCapacity = initialCapacity;
    return Cast.as(this);
  }

  /**
   * Sets to use weak values
   *
   * @return This cache spec
   */
  public <T extends CacheSpec<K, V>> T weakValues() {
    this.weakValues = true;
    return Cast.as(this);
  }

  /**
   * Sets to use weak keys
   *
   * @return This cache spec
   */
  public <T extends CacheSpec<K, V>> T weakKeys() {
    this.weakKeys = true;
    return Cast.as(this);
  }

  private long convertStringToTime(String duration) {
    Preconditions.checkArgument(!Strings.isNullOrEmpty(duration), "Duration cannot be null or empty");
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
   * @param listener The removal listener
   * @return This cache spec
   */
  public <T extends CacheSpec<K, V>> T removalListener(RemovalListener<K, V> listener) {
    this.removalListener = listener;
    return Cast.as(this);
  }


}//END OF CacheSpecification
