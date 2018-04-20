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

import com.gengoai.collection.map.NormalizedStringMap;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * <p>Manages the various cache engines providing methods to retrieve an engine by name and to retrieve all engine
 * names.</p>
 *
 * @author David B. Bracewell
 */
public final class CacheEngines {

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
    * Gets the cache engine associated with the given name
    *
    * @param name the name of the cache engine
    * @return the cache engine associated with the given name
    * @throws IllegalArgumentException If the name is not a valid cache engine name
    */
   public static CacheEngine get(String name) {
      if (!engines.containsKey(name)) {
         throw new IllegalArgumentException(name + " is not a valid cache engine name.");
      }
      return engines.get(name);
   }

   /**
    * Gets the names of all available cache engines.
    *
    * @return the names of all available cache engines.
    */
   public static Collection<String> getNames() {
      return Collections.unmodifiableSet(engines.keySet());
   }


}//END OF CacheEngines
