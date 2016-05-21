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

import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CounterTest {

  @Test
  public void testContainsAndItems() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    counterOne.set("e", 2.0);

    Set<String> items = counterOne.items();
    assertTrue(items.contains("a"));
    assertTrue(items.contains("b"));
    assertTrue(items.contains("c"));
    assertTrue(items.contains("e"));
    assertFalse(items.contains("d"));

    assertFalse(counterOne.contains("d"));
    assertTrue(counterOne.contains("a"));
    assertTrue(counterOne.contains("b"));
    assertTrue(counterOne.contains("c"));
    assertTrue(counterOne.contains("e"));
  }

  @Test
  public void testGetAverageValue() throws Exception {
    Counter<String> counterOne = Counters.synchronizedCounter(Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a")));
    assertEquals((Double) counterOne.average(), (Double) 2.0d);
    counterOne.increment("d", 2.0);
    assertEquals((Double) counterOne.average(), (Double) 2.0d);
    counterOne.increment("c", -1.0);
    counterOne.increment("d", -2.0);
    assertEquals((Double) counterOne.average(), (Double) 2.5d);
    counterOne.removeZeroCounts();
    assertEquals((Double) counterOne.average(), (Double) 2.5d);
  }

  @Test
  public void testGetMaximumValue() throws Exception {
    assertEquals((Double) Counters.newHashMapCounter().maximumCount(), (Double) 0.0d);
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertEquals((Double) counterOne.maximumCount(), (Double) 3.0d);
    counterOne.increment("d");
    counterOne.increment("d", 10);
    assertEquals((Double) counterOne.maximumCount(), (Double) 11.0d);
  }

  @Test
  public void testGetMinimumValue() throws Exception {
    assertEquals((Double) Counters.newHashMapCounter().minimumCount(), (Double) 0.0d);
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertEquals((Double) counterOne.minimumCount(), (Double) 1.0d);
    counterOne.increment("a", -4);
    assertEquals((Double) counterOne.minimumCount(), (Double) (-1.0d));
  }

  @Test
  public void testCopyConstructor() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    Counter<String> counterTwo = Counters.newHashMapCounter(counterOne);

    counterOne.increment("a");
    counterOne.increment("D");
    assertEquals((Double) counterOne.get("a"), (Double) 4.0d);
    assertEquals((Double) counterOne.get("D"), (Double) 1.0d);

    assertEquals((Double) 3.0d, (Double) counterTwo.get("a"));
    assertEquals((Double) 0.0d, (Double) counterTwo.get("D"));
    assertEquals((Double) 2.0d, (Double) counterTwo.get("b"));
    assertEquals((Double) 1.0d, (Double) counterTwo.get("c"));


    counterTwo = Counters.newHashMapCounter(counterOne.asMap());
    assertEquals((Double) counterTwo.get("a"), (Double) 4.0d);
    assertEquals((Double) counterTwo.get("D"), (Double) 1.0d);
    assertEquals((Double) counterTwo.get("b"), (Double) 2.0d);
    assertEquals((Double) counterTwo.get("c"), (Double) 1.0d);
  }

  @Test
  public void testGetStandardDeviation() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a", "c", "c", "b"));
    assertEquals((Double) counterOne.standardDeviation(), (Double) (0.0d));
  }

  @Test
  public void testGetTotalCount() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertEquals((Double) counterOne.sum(), (Double) (6.0d));
  }

  @Test
  public void testIncrementAll() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    counterOne.incrementAll(Arrays.asList("a", "d", "e", "e"));
    assertEquals((Double) counterOne.get("a"), (Double) 4.0d);
    assertEquals((Double) counterOne.get("b"), (Double) 2.0d);
    assertEquals((Double) counterOne.get("c"), (Double) 1.0d);
    assertEquals((Double) counterOne.get("d"), (Double) 1.0d);
    assertEquals((Double) counterOne.get("e"), (Double) 2.0d);
  }

  @Test
  public void testIsEmpty() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertFalse(counterOne.isEmpty());
    counterOne.clear();
    assertTrue(counterOne.isEmpty());
  }

  @Test
  public void testKeysByValue() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    List<String> asc = counterOne.itemsByCount(true);
    assertEquals(asc.get(0), "c");
    assertEquals(asc.get(1), "b");
    assertEquals(asc.get(2), "a");
    List<String> desc = counterOne.itemsByCount(false);
    assertEquals(desc.get(2), "c");
    assertEquals(desc.get(1), "b");
    assertEquals(desc.get(0), "a");
  }


  @Test
  public void testMagnitude() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertTrue((counterOne.magnitude() - 3.7417) < 0.001);
  }


  @Test
  public void testMerge() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    Counter<String> counterTwo = Counters.newHashMapCounter(Arrays.asList("c", "c", "c", "b"));
    counterOne.merge(counterTwo);
    assertEquals((Double) counterOne.get("c"), (Double) 4.0d);
    assertEquals((Double) counterOne.get("a"), (Double) 3.0d);
    assertEquals((Double) counterOne.get("b"), (Double) 3.0d);
  }

  @Test
  public void testRemove() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    counterOne.remove("c");
    assertEquals((Double) counterOne.get("c"), (Double) 0.0d);
    counterOne.remove("d");
  }

  @Test
  public void testSize() throws Exception {
    Counter<String> counterOne = Counters.newHashMapCounter(Arrays.asList("a", "b", "c", "a", "b", "a"));
    assertEquals(counterOne.size(), 3);
    counterOne.clear();
    assertEquals(counterOne.size(), 0);
  }


}
