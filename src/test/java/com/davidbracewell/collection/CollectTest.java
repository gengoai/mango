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


import com.davidbracewell.conversion.Cast;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.davidbracewell.collection.list.Lists.list;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CollectTest {

   @Test
   public void testAsIterable() throws Exception {
      List<String> list = new ArrayList<>();
      list.add("1");
      assertEquals("1", Collect.first(list).orElse(null));
   }

   @Test
   public void testCast() throws Exception {
      List<Double> list = list(1.0, 2.5, 3.0);
      List<Number> numList = Cast.cast(list);
      assertEquals(1d, numList.get(0).doubleValue(), 0d);
      assertEquals(2.5d, numList.get(1).doubleValue(), 0d);
      assertEquals(3d, numList.get(2).doubleValue(), 0d);
   }

}//END OF CollectionUtilsTest
