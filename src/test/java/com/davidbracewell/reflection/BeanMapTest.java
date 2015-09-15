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

package com.davidbracewell.reflection;

import com.google.common.collect.Iterables;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
@SuppressWarnings("ALL")
public class BeanMapTest {

  @Test
  public void testObject() throws Exception {
    BeanMap map = new BeanMap(new Object());

    assertTrue(Sets.difference(map.keySet(), Sets.newHashSet("class")).isEmpty());
    assertTrue(map.containsKey("class"));
    assertTrue(map.containsValue(Object.class));
    assertFalse(map.isEmpty());
    assertEquals(1, map.size());
    assertTrue(Sets.difference(Sets.newHashSet(map.values()), Sets.newHashSet(Object.class)).isEmpty());
    assertEquals(Class.class, map.getType("class"));

    Set<Map.Entry<String, Object>> entrySet = map.entrySet();
    assertEquals(1, entrySet.size());
    assertEquals("class", Iterables.getOnlyElement(entrySet).getKey());
    assertEquals(Object.class, Iterables.getOnlyElement(entrySet).getValue());

  }

}
