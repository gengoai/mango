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

import com.davidbracewell.tuple.Tuple2;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
@SuppressWarnings("ALL")
public class BeanMapTest {

  @Test
  @SuppressWarnings("unchecked")
  public void testObject() throws Exception {
    BeanMap map = new BeanMap(Tuple2.of("A", "B"));
    assertFalse(map.containsKey("class"));
    assertFalse(map.containsValue(Object.class));
    assertFalse(map.isEmpty());

    assertTrue(map.containsKey("v1"));
    assertTrue(map.containsKey("v2"));

    assertEquals("A", map.get("v1"));
    assertEquals("B", map.get("v2"));

    assertNull(map.put("v1", "G"));

  }

}
