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

import org.junit.Test;

/**
 * @author David B. Bracewell
 */
public class UnmodifiableMultiCounterTest extends BaseMultiCounterTest {

   @Override
   public MultiCounter<String, String> getEmptyCounter() {
      return MultiCounters.unmodifiableMultiCounter(super.getEmptyCounter());
   }

   @Override
   public MultiCounter<String, String> getEntryCounter() {
      return MultiCounters.unmodifiableMultiCounter(super.getEntryCounter());
   }

   @Test(expected = UnsupportedOperationException.class)
   @Override
   public void get() throws Exception {
      super.get();
   }

   @Test(expected = UnsupportedOperationException.class)
   @Override
   public void clear() throws Exception {
      super.clear();
   }

   @Test(expected = UnsupportedOperationException.class)
   @Override
   public void incrementAndSet() throws Exception {
      super.incrementAndSet();
   }

   @Test(expected = UnsupportedOperationException.class)
   @Override
   public void remove() throws Exception {
      super.remove();
   }

   @Test(expected = UnsupportedOperationException.class)
   @Override
   public void adjustValues() throws Exception {
      super.adjustValues();
   }
}//END OF MultiCounterTest
