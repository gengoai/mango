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

package com.davidbracewell.io;

import com.davidbracewell.cache.CacheEngine;
import com.davidbracewell.collection.NormalizedStringMap;

import java.util.Map;
import java.util.ServiceLoader;

/**
 * The type Cache engines.
 * @author David B. Bracewell
 */
public abstract class CacheEngines {

  private static final Map<String, CacheEngine> engines = new NormalizedStringMap<>();

  static {
    for (CacheEngine engine : ServiceLoader.load(CacheEngine.class)) {
      engines.put(engine.name(), engine);
    }
  }


  private CacheEngines() {
    throw new IllegalAccessError();
  }


  /**
   * Get cache engine.
   *
   * @param name the name
   * @return the cache engine
   */
  public static CacheEngine get(String name) {
    if (!engines.containsKey(name)) {
      throw new IllegalArgumentException(name + " is not a valid cache engine name.");
    }
    return engines.get(name);
  }


}//END OF CacheEngines
