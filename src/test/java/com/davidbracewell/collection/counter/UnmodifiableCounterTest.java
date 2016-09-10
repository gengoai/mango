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

import com.davidbracewell.collection.map.Maps;
import org.junit.Test;

import java.util.Collections;
import java.util.Objects;

/**
 * @author David B. Bracewell
 */
public class UnmodifiableCounterTest {

   final Counter<String> counter = Counters.unmodifiableCounter(
      Counters.newCounter(Maps.map("A", 1.0, "B", 2.0)));

   @Test(expected = UnsupportedOperationException.class)
   public void asMap() throws Exception {
      counter.asMap().remove("A");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void decrement() throws Exception {
      counter.decrement("A");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void decrementAmount() throws Exception {
      counter.decrement("A", 2.0);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void decrementAll() throws Exception {
      counter.decrementAll(Collections.singleton("A"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void decrementAllCount() throws Exception {
      counter.decrementAll(Collections.singleton("A"), 2.0);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void increment() throws Exception {
      counter.increment("A");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void incrementAmount() throws Exception {
      counter.increment("A", 2.0);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void incrementAll() throws Exception {
      counter.incrementAll(Collections.singleton("A"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void incrementAllCount() throws Exception {
      counter.incrementAll(Collections.singleton("A"), 2.0);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void divideBySum() throws Exception {
      counter.divideBySum();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void items() throws Exception {
      counter.items().remove("A");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void entries() throws Exception {
      counter.entries().removeIf(Objects::nonNull);
   }

   @Test(expected = UnsupportedOperationException.class)
   public void mergeMap() throws Exception {
      counter.merge(Maps.map("A", 1.0));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void mergeCounter() throws Exception {
      counter.merge(Counters.newCounter("A"));
   }


   @Test(expected = UnsupportedOperationException.class)
   public void remove() throws Exception {
      counter.remove("A");
   }

   @Test(expected = UnsupportedOperationException.class)
   public void removeAll() throws Exception {
      counter.removeAll(Collections.singleton("A"));
   }

   @Test(expected = UnsupportedOperationException.class)
   public void set() throws Exception {
      counter.set("B", 1);
   }

}