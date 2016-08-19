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

import com.davidbracewell.collection.list.PrimitiveArrayIterator;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class PrimitiveArrayIteratorTest {
  @Test
  public void testDoubleArrayIterator() throws Exception {
    PrimitiveArrayIterator<Double> itr = PrimitiveArrayIterator.doubleArrayIterator(new double[]{2.0, 1.0});
    assertEquals((Double) 2.0, itr.next());
    assertEquals((Double) 1.0, itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testIntegerArrayIterator() throws Exception {
    PrimitiveArrayIterator<Integer> itr = PrimitiveArrayIterator.integerArrayIterator(new int[]{2, 1});
    assertEquals((Integer) 2, itr.next());
    assertEquals((Integer) 1, itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testShortArrayIterator() throws Exception {
    PrimitiveArrayIterator<Short> itr = PrimitiveArrayIterator.shortArrayIterator(new short[]{2, 1});
    assertEquals(Short.valueOf((short) 2), itr.next());
    assertEquals(Short.valueOf((short) 1), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testLongArrayIterator() throws Exception {
    PrimitiveArrayIterator<Long> itr = PrimitiveArrayIterator.longArrayIterator(new long[]{2L, 1L});
    assertEquals((Long) 2L, itr.next());
    assertEquals((Long) 1L, itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testFloatArrayIterator() throws Exception {
    PrimitiveArrayIterator<Float> itr = PrimitiveArrayIterator.floatArrayIterator(new float[]{2.0f, 1.0f});
    assertEquals((Float) 2.0f, itr.next());
    assertEquals((Float) 1.0f, itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testByteArrayIterator() throws Exception {
    PrimitiveArrayIterator<Byte> itr = PrimitiveArrayIterator.byteArrayIterator(new byte[]{2, 1});
    assertEquals(Byte.valueOf((byte) 2), itr.next());
    assertEquals(Byte.valueOf((byte) 1), itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testBooleanArrayIterator() throws Exception {
    PrimitiveArrayIterator<Boolean> itr = PrimitiveArrayIterator.booleanArrayIterator(new boolean[]{false, true});
    assertEquals(false, itr.next());
    assertEquals(true, itr.next());
    assertFalse(itr.hasNext());
  }

  @Test
  public void testCharacterArrayIterator() throws Exception {
    PrimitiveArrayIterator<Character> itr = PrimitiveArrayIterator.characterArrayIterator("ab".toCharArray());
    assertEquals((Character) 'a', itr.next());
    assertEquals((Character) 'b', itr.next());
    assertFalse(itr.hasNext());
  }
}
