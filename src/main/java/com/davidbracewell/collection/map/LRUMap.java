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

package com.davidbracewell.collection.map;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * <p>
 * A Bounded map that keeps the last recently used items.
 * </p>
 *
 * @param <K> Key type
 * @param <V> Value type
 * @author David B. Bracewell
 */
public class LRUMap<K, V> extends LinkedHashMap<K, V> {

  private static final long serialVersionUID = -2207148975128355022L;
  private final int maxSize;

  /**
   * Creates a new LRU Map.
   *
   * @param <K> the key type
   * @param <V> the value type
   * @return the map
   */
  public static <K, V> LRUMap<K, V> create(int maxSize) {
    return new LRUMap<>(maxSize);
  }

  /**
   * Instantiates a new LRU map with a max size of
   * <code>Integer.MAX_VALUE</code>.
   */
  public LRUMap() {
    this.maxSize = Integer.MAX_VALUE;
  }

  /**
   * Instantiates a new lRU map.
   *
   * @param maxSize the max size
   */
  public LRUMap(int maxSize) {
    if (maxSize <= 0) {
      throw new IllegalArgumentException("maxSize must be greater than 0.");
    }
    this.maxSize = maxSize;
  }

  @Override
  protected boolean removeEldestEntry(final Map.Entry<K, V> eldest) {
    return super.size() > maxSize;
  }

  /**
   * @return The maximum size of the map
   */
  public int maxSize() {
    return maxSize;
  }

}// END OF CLASS LRUMap
