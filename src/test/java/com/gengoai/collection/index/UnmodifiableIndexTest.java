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

package com.gengoai.collection.index;

import org.junit.Test;

/**
 * @author David B. Bracewell
 */
public class UnmodifiableIndexTest extends BaseIndexTest {

   @Override
   public Index<String> getIndex() {
      return Indexes.unmodifiableIndex(super.getIndex());
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testAdd() throws Exception {
      super.testAdd();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testAddAll() throws Exception {
      super.testAddAll();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testSize() throws Exception {
      super.testAddAll();
   }

   @Test(expected = UnsupportedOperationException.class)
   public void testRemove() throws Exception {
      super.testAddAll();
   }

}//END OF HashMapIndexTest
