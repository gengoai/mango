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

package com.davidbracewell.collection;

import com.davidbracewell.collection.map.Maps;
import com.davidbracewell.collection.map.NormalizedStringMap;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class NormalizedStringMapTest {

   final NormalizedStringMap<Double> map = new NormalizedStringMap<>();

   @Before
   public void setUp() throws Exception {
      map.put("ARG", 1.0);
      map.put("arg", 2.0);
      map.put("jose", 2.0);
      map.put("josè", 2.0);
      map.put("old", 34.0);
      map.put("new", 34.0);
   }

   @Test
   public void testSize() throws Exception {
      assertEquals(4, map.size());
      map.clear();
      assertTrue(map.isEmpty());
   }

   @Test
   public void testContainsKey() throws Exception {
      assertTrue(map.containsKey("arg"));
      assertTrue(map.containsKey("ARG"));
      assertTrue(map.containsKey("Arg"));
      assertTrue(map.containsKey("aRG"));
      assertTrue(map.containsKey("Josè"));
      assertFalse(map.containsKey(null));
   }


   @Test
   public void testContainsValue() throws Exception {
      assertTrue(map.containsValue(2.0));
   }

   @Test
   public void testKeySet() throws Exception {
      assertEquals(4, map.keySet().size());
      Set<String> keySet = map.keySet();
      assertTrue(keySet.containsAll(Arrays.asList(
            "new", "ARG", "old", "jose"
                                                 )));
   }

   @Test
   public void testRemove() throws Exception {
      assertEquals((Double) 2.0, map.remove("Josè"));
      assertNull(map.remove(null));
      assertNull(map.remove("not in the map"));
      assertNull(map.remove(1.0d));
   }

   @Test
   public void testGet() throws Exception {
      assertEquals((Double) 2.0d, map.get("arg"));
      assertEquals((Double) 2.0d, map.get("Josè"));
      assertNull(map.get(null));
      assertNull(map.get("not in the map"));
      assertNull(map.get(1.0d));
   }

   @Test
   public void testPut() throws Exception {
      assertNull(map.put("void", 1.0));
      assertEquals((Double) 1.0, map.put("voId", 2.0));
      map.putAll(Maps.map("ARG", 4.0, "lasting", 2.0));
      assertEquals((Double) 4.0, map.get("arg"));
      assertEquals((Double) 2.0, map.get("lasting"));
   }

}
