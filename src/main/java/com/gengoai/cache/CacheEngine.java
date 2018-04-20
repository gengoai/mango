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

/**
 * <p>A factory-like interface for creating a cache using a specific implementation strategy defined by a {@link
 * CacheSpec} </p>
 *
 * @author David B. Bracewell
 */
public interface CacheEngine {

  /**
   * The engine name
   *
   * @return The engine name
   */
  String name();


  /**
   * Creates a cache based on the give <code>CacheSpec</code> .
   *
   * @param <K>       the Key parameter
   * @param <V>       the Value parameter
   * @param cacheSpec the cache spec
   * @return the new cache
   */
  <K, V> Cache<K, V> create(CacheSpec<K, V> cacheSpec);


}//END OF CacheEngine
