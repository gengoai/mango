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

package com.gengoai.collection;

import com.gengoai.collection.list.PrimitiveArrayList;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.ListIterator;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class PrimitiveArrayListTest {

   List<Double> doubleList;
   List<Integer> intList;
   List<Float> floatList;

   @Before
   public void setUp() throws Exception {
      double[] array = {1.0, 2.0, 3.0};
      doubleList = Lists.ofPrimitive(array, Double.class);
      intList = Lists.ofPrimitive(array, Integer.class);
      floatList = Lists.ofPrimitive(array, Float.class);
   }

   @Test
   public void testGet() throws Exception {
      assertTrue(doubleList.get(0) == 1.0d);
      assertTrue(doubleList.get(1) == 2.0d);
      assertTrue(doubleList.get(2) == 3.0d);

      assertTrue(intList.get(0) == 1);
      assertTrue(intList.get(1) == 2);
      assertTrue(intList.get(2) == 3);

      assertTrue(floatList.get(0) == 1.0f);
      assertTrue(floatList.get(1) == 2.0f);
      assertTrue(floatList.get(2) == 3.0f);
   }

   @Test
   public void testIsEmpty() throws Exception {
      assertFalse(doubleList.isEmpty());
      assertFalse(floatList.isEmpty());
      assertFalse(intList.isEmpty());
      PrimitiveArrayList<Character> pl = new PrimitiveArrayList<>(new char[]{}, Character.class);
      assertTrue(pl.isEmpty());
   }

   @Test
   public void testIterator() throws Exception {
      for (Double aDoubleList : doubleList) {
      }
   }

   @Test
   public void testListIterator() throws Exception {
      ListIterator<Double> itr = doubleList.listIterator();

      assertTrue(itr.hasNext());
      assertEquals((Double) 1.0, itr.next());
      itr.set(34d);
      assertEquals((Double) 34d, doubleList.get(0));

      itr.next();
      assertTrue(itr.hasPrevious());
      assertEquals((Double) 34.0, itr.previous());
      assertEquals((Double) 2.0, itr.next());
      assertEquals((Double) 3.0, itr.next());


      itr = doubleList.listIterator(1);
      assertEquals((Double) 34.0, itr.next());

   }

   @Test
   public void testSet() throws Exception {
      doubleList.set(0, 100d);
      assertTrue(doubleList.get(0) == 100d);
   }

   @Test
   public void testSize() throws Exception {
      assertTrue(doubleList.size() == 3);
      assertTrue(floatList.size() == 3);
      assertTrue(intList.size() == 3);
   }

   @Test
   public void testSubList() throws Exception {
      List<Double> subList = doubleList.subList(1, 3);
      assertTrue(subList.size() == 2);
      assertEquals(2.0d, subList.get(0), 0d);
      assertEquals(3.0d, subList.get(1), 0d);
   }
}//END OF PrimitiveArrayListTest
