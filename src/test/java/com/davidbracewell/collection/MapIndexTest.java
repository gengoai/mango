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

import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.Iterator;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class MapIndexTest {

  Index<String> index;

  @Before
  public void setUp() throws Exception {
    index = Indexes.newIndex(Arrays.asList("A", "B", "C", "D", "E"));
  }

  @Test
  public void testSize() throws Exception {
    assertEquals(5, index.size());
    assertFalse(index.isEmpty());
    index.clear();
    assertTrue(index.isEmpty());
  }

  @Test
  public void testContains() throws Exception {
    assertTrue(index.contains("A"));
    assertTrue(index.contains("E"));
    assertFalse(index.contains("F"));
    assertFalse(index.contains("Z"));
  }

  @Test
  public void testIterator() throws Exception {
    Iterator<String> itr = index.iterator();
    assertEquals("A", itr.next());
    itr.remove();
    assertEquals(4, index.size());
  }

  @Test
  public void testAdd() throws Exception {
    assertEquals(5, index.add("G"));
    assertEquals(0, index.add("A"));
  }

  @Test
  public void testAddAll() throws Exception {
    assertFalse(index.contains("Z"));
    assertFalse(index.contains("Y"));
    index.addAll(Arrays.asList("Z", "Y"));
    assertTrue(index.contains("Z"));
    assertTrue(index.contains("Y"));
  }


  @Test
  public void testGet() throws Exception {
    assertEquals("A", index.get(0));
    assertNull(index.get(-100));
    assertNull(index.get(100));
  }

  @Test
  public void testRemove() throws Exception {
    assertEquals("A", index.remove(0));
    assertEquals(0, index.remove("B"));
  }

  @Test
  public void testIndexOf() throws Exception {
    assertEquals(0, index.indexOf("A"));
    assertEquals(-1, index.indexOf("Z"));
  }

  @Test
  public void testAsList() throws Exception {
    assertEquals(Arrays.asList("A", "B", "C", "D", "E"), index.asList());
  }

}//END OF HashMapIndexTest
