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

import com.gengoai.config.Config;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CacheManagerTest {


   @Before
   public void setUp() throws Exception {
      Config.initializeTest();
   }

   @Test
   public void testGetGlobalCache() throws Exception {
      assertTrue(CacheManager.getGlobalCache() != null);
   }

   @Test
   public void testGetCacheNames() throws Exception {
      CacheManager.getGlobalCache(); //Make sure the cache is loaded.
      assertTrue(CacheManager.exists(CacheManager.GLOBAL_CACHE));
      assertTrue(CacheManager.getCacheNames().contains(CacheManager.GLOBAL_CACHE));
   }

   @Test
   public void testGetCache() throws Exception {
      Config.setProperty("testCache", "maxSize:1000");
      Cache<String, String> cache = CacheManager.get("testCache");
      cache.put("element", "value");
      assertTrue(cache.containsKey("element"));
      assertEquals(1, cache.size());
   }

   @Test
   public void register() throws Exception {
      Cache<String, String> cache = CacheManager.register(CacheSpec.<String, String>create()
                                                             .engine("Guava")
                                                         );
      cache.put("A", "D");
      cache.get("A", () -> "B");
      assertEquals("D", cache.get("A"));
   }
}
