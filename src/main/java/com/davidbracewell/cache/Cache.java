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


/**
 * <p>A generic cache interface that allows multiple implementations, definition through specification, management, and
 * auto cached interfaces.</p>
 *
 * @param <K> the Key parameter
 * @param <V> the Value parameter
 * @author David B. Bracewell
 */
public interface Cache<K, V> {

  /**
   * Clears the cache of all stored keys and values
   */
  void clear();

  /**
   * Determines if a key is in the cache or not
   *
   * @param key The key to check
   * @return True if the key is in the cache, False if not
   */
  boolean containsKey(K key);

  /**
   * Gets the value associated with a key
   *
   * @param key The key
   * @return The value associated with the key or null
   */
  V get(K key);

  /**
   * Gets name.
   *
   * @return The name of the cache
   */
  String getName();

  /**
   * Removes a single key
   *
   * @param key The key to remove
   */
  void invalidate(K key);

  /**
   * Clears the cache of all given keys
   *
   * @param keys The keys to remove
   */
  default void invalidateAll(Iterable<? extends K> keys) {
    if (keys != null) {
      keys.forEach(this::invalidate);
    }
  }

  /**
   * Adds a key value pair to the cache overwriting any value that is there
   *
   * @param key   The key
   * @param value The value
   */
  void put(K key, V value);

  /**
   * Adds a key value pair if the key is not already in the cache
   *
   * @param key   The key
   * @param value The value
   * @return The old value if put, null if not
   */
  V putIfAbsent(K key, V value);

  /**
   * Size long.
   *
   * @return The current size of the cache
   */
  long size();


}//END OF Cache
