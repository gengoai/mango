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

import com.gengoai.collection.list.Lists;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class GuavaCacheTest {

   Cache<String, String> cache;

   @Before
   public void setUp() throws Exception {
      cache = CacheManager.register(CacheSpec.<String, String>create()
                                       .name("GuavaCacheTest")
                                       .engine("Guava")
                                       .maxSize(10)
                                   );
   }


   @Test
   public void get() throws Exception {
      cache.invalidateAll();
      assertNull(cache.get("A"));
      assertEquals("B", cache.get("A", () -> "B"));
   }


   @Test
   public void contains() throws Exception {
      cache.put("A", "B");
      assertTrue(cache.containsKey("A"));
      assertFalse(cache.containsKey("Z"));
   }


   @Test
   public void invalidate() throws Exception {
      cache.invalidate("A");
      assertTrue(cache.isEmpty());

      assertEquals("C", cache.get("A", () -> "C"));
      cache.invalidateAll(Lists.list("A", "B", "C"));
      assertTrue(cache.isEmpty());

      assertEquals("C", cache.get("A", () -> "C"));
      cache.invalidateAll();
      assertTrue(cache.isEmpty());
   }

}