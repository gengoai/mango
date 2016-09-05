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

package com.davidbracewell.collection.counter;

import com.davidbracewell.Math2;
import com.davidbracewell.collection.Sets;
import com.davidbracewell.collection.list.Lists;
import com.davidbracewell.collection.map.Maps;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Random;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CounterTest {

   @Test
   public void valueChange() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 1.0, "B", 2.0, "C", 1.0));

      counter.decrement("A");
      assertEquals(0, counter.get("A"), 0);

      counter.increment("A");
      assertEquals(1, counter.get("A"), 0);

      counter.increment("A", 0);
      assertEquals(1, counter.get("A"), 0);

      counter.increment("Z", 100);
      assertEquals(100, counter.get("Z"), 0);

      counter.decrement("Z", 100);
      assertEquals(0, counter.get("Z"), 0);

      counter.decrement("Z", 0);
      assertEquals(0, counter.get("Z"), 0);

      counter.decrementAll(Arrays.asList("B", "C"));
      assertEquals(0, counter.get("C"), 0);
      assertEquals(1, counter.get("B"), 0);

      counter.incrementAll(Arrays.asList("B", "C"));
      assertEquals(1, counter.get("C"), 0);
      assertEquals(2, counter.get("B"), 0);

      counter.incrementAll(Arrays.asList("B", "C"), 4);
      assertEquals(5, counter.get("C"), 0);
      assertEquals(6, counter.get("B"), 0);

      counter.decrementAll(Arrays.asList("B", "C"), 4);
      assertEquals(1, counter.get("C"), 0);
      assertEquals(2, counter.get("B"), 0);
   }

   @Test
   public void minMax() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 2.0, "B", 5.0, "C", 1.0));
      assertEquals("B", counter.max());
      assertEquals(5, counter.maximumCount(), 0);
      assertEquals("C", counter.min());
      assertEquals(1, counter.minimumCount(), 0);

      assertEquals(5.0, counter.topN(1).get("B"), 0.0);
      assertEquals(1.0, counter.bottomN(1).get("C"), 0.0);
   }

   @Test
   public void stats() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 2.0, "B", 5.0, "C", 3.0));
      assertEquals(3.3, counter.average(), 0.04);
      assertEquals(10.0, counter.sum(), 0.0);
      assertEquals(1.5, counter.standardDeviation(), 0.03);
      assertEquals(6.16, counter.magnitude(), 0.01);
   }

   @Test
   public void itemsByCount() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 2.0, "B", 5.0, "C", 1.0));
      assertEquals(Lists.list("B", "A", "C"), counter.itemsByCount(false));
      assertEquals(Lists.list("C", "A", "B"), counter.itemsByCount(true));
   }

   @Test
   public void remove() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Arrays.asList("A", "A", "B"));
      counter.removeAll(null);
      assertEquals(3, counter.sum(), 0);
      counter.removeAll(Collections.singleton("A"));
      assertEquals(1, counter.sum(), 0);
      assertEquals(1, counter.remove("B"), 0);
      assertEquals(0, counter.remove("Z"), 0);
      assertEquals(0, counter.remove(null), 0);
      assertTrue(counter.isEmpty());
   }

   @Test
   public void sample() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      assertTrue(counter.contains(counter.sample()));
      double bCount = 0;
      Random rnd = new Random(1234);
      for (int i = 0; i < 50; i++) {
         if (counter.sample(rnd).equals("B")) {
            bCount++;
         }
      }
      assertTrue(bCount / 50.0 >= 0.10);
   }

   @Test
   public void csv() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      Resource r = new StringResource();
      counter.writeCsv(r);
      Counter<String> fromCSV = Counters.readCsv(r, String.class);
      assertEquals(counter, fromCSV);
   }

   @Test
   public void json() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      Resource r = new StringResource();
      counter.writeJson(r);
      Counter<String> fromJSON = Counters.readJson(r, String.class);
      assertEquals(counter, fromJSON);
   }

   @Test
   public void contains() throws Exception {
      Counter<String> counterOne = Counters.newHashMapCounter("a", "b", "c", "a", "b", "a");
      assertFalse(counterOne.contains("d"));
      assertTrue(counterOne.contains("a"));
      assertTrue(counterOne.contains("b"));
      assertTrue(counterOne.contains("c"));
   }

   @Test
   public void items() throws Exception {
      Counter<String> counterOne = Counters.newHashMapCounter("a", "b", "c", "a", "b", "a");
      assertEquals(Sets.set("a", "b", "c"), counterOne.items());
   }

   @Test
   public void values() throws Exception {
      Counter<String> counterOne = Counters.newHashMapCounter("a", "b", "c", "a", "b", "a");
      Collection<Double> values = counterOne.values();
      assertEquals(3, values.size(), 0);
      assertEquals(6, Math2.sum(values), 0);

      assertEquals(3, counterOne.get("a"), 0);
      assertEquals(2, counterOne.get("b"), 0);
      assertEquals(1, counterOne.get("c"), 0);
      assertEquals(0, counterOne.get("z"), 0);

      assertEquals(6, counterOne.sum(), 0);
   }

   @Test
   public void merge() throws Exception {
      Counter<String> c1 = Counters.newHashMapCounter();

      assertTrue(c1.isEmpty());

      Counter<String> c2 = Counters.newHashMapCounter("a");

      c1.merge(c2);
      assertEquals(1.0, c1.get("a"), 0.0);

      c1.merge(c2.asMap());
      assertEquals(2.0, c1.get("a"), 0.0);


      assertEquals(1, c1.size());
   }

   @Test
   public void clear() throws Exception {
      Counter<String> counterOne = Counters.newHashMapCounter("a", "b", "c", "a", "b", "a");
      counterOne.clear();
      assertEquals(0, counterOne.size());
      assertEquals(0.0, counterOne.sum(), 0.0);
   }

   @Test
   public void set() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter();

      counter.set("A", 100);
      assertEquals(100, counter.get("A"), 0);

      counter.set("Z", 0);
      assertEquals(0, counter.get("Z"), 0);
   }


   @Test
   public void divideBySum() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      counter.divideBySum();
      assertEquals(1.0, counter.sum(), 0.0);
      assertEquals(.5, counter.get("B"), 0.0);
      assertEquals(.4, counter.get("A"), 0.0);
      assertEquals(.1, counter.get("C"), 0.0);

      counter.clear();
      counter.divideBySum();
      assertEquals(0, counter.sum(), 0);
   }

   @Test
   public void adjustValues() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));

      Counter<String> adjusted = counter.adjustValues(d -> d * d);
      assertEquals(42, adjusted.sum(), 0.0);
      assertEquals(25, adjusted.get("B"), 0.0);
      assertEquals(16, adjusted.get("A"), 0.0);
      assertEquals(1, adjusted.get("C"), 0.0);

      counter.adjustValuesSelf(d -> d * d);
      assertEquals(42, counter.sum(), 0.0);
      assertEquals(25, counter.get("B"), 0.0);
      assertEquals(16, counter.get("A"), 0.0);
      assertEquals(1, counter.get("C"), 0.0);
   }

   @Test
   public void filterByValue() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      Counter<String> filtered = counter.filterByValue(d -> d < 5.0);
      assertEquals(5, filtered.sum(), 0.0);
      assertEquals(0, filtered.get("B"), 0.0);
      assertEquals(4, filtered.get("A"), 0.0);
      assertEquals(1, filtered.get("C"), 0.0);
   }

   @Test
   public void filterByKey() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      Counter<String> filtered = counter.filterByKey(d -> !d.equals("B"));
      assertEquals(5, filtered.sum(), 0.0);
      assertEquals(0, filtered.get("B"), 0.0);
      assertEquals(4, filtered.get("A"), 0.0);
      assertEquals(1, filtered.get("C"), 0.0);
   }

   @Test
   public void mapKeys() throws Exception {
      Counter<String> counter = Counters.newHashMapCounter(Maps.map("A", 4.0, "B", 5.0, "C", 1.0));
      Counter<String> mapped = counter.mapKeys(String::toLowerCase);
      assertEquals(10, mapped.sum(), 0.0);
      assertEquals(5, mapped.get("b"), 0.0);
      assertEquals(4, mapped.get("a"), 0.0);
      assertEquals(1, mapped.get("c"), 0.0);
   }
}
