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

package com.gengoai.conversion.impl;

import com.gengoai.conversion.ArrayConverter;
import com.gengoai.conversion.Val;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;

import static com.gengoai.collection.Maps.hashMapOf;
import static com.gengoai.tuple.Tuples.$;
import static org.junit.Assert.*;

public class ArrayConverterTest {


   @Test
   public void applyTest() throws Exception {
      Assert.assertArrayEquals(new Integer[]{1, 3, 4},
                               new ArrayConverter<>(Integer.class).apply(new Object[]{"1", 3.0, 4}));

      //Test null output
      assertNull(new ArrayConverter<>(Double.class).apply(null));
      assertNull(new ArrayConverter<>(Double.class).apply(new Object[]{new ArrayList<String>()}));

      //Test null in output
      assertArrayEquals(new Integer[]{null, 3, 4},
                        new ArrayConverter<>(Integer.class).apply(new Object[]{"ABCD", 3.0, 4}));


      //Test val versions
      assertNull(Val.of(new ArrayList<>()).asArray(Double.class));
      assertArrayEquals(new Double[]{23.4, 16.2}, Val.of(new Object[]{"23.4", 16.2}).asDoubleArray());
      assertArrayEquals(new Float[]{23.4f, 16.2f}, Val.of(new Object[]{"23.4", 16.2}).asFloatArray());
      assertArrayEquals(new Integer[]{23, 16}, Val.of(new Object[]{"23.4", 16.2}).asIntegerArray());
      assertArrayEquals(new Short[]{23, 16}, Val.of(new Object[]{"23.4", 16.2}).asShortArray());
      assertArrayEquals(new Long[]{23L, 16L}, Val.of(new Object[]{"23.4", 16.2}).asLongArray());
      assertArrayEquals(new Long[]{23L, null}, Val.of(new Object[]{"23.4", new ArrayList<>()}).asLongArray());
      assertArrayEquals(new Boolean[]{true, false, true}, Val.of(new Object[]{1.0, "blah", true}).asBooleanArray());
      assertArrayEquals(new String[]{"{1=3}", "12.3"}, Val.of(new Object[]{
         hashMapOf($(1, 3)),
         12.3
      }).asStringArray());
   }

   @Test
   public void testByte() throws Exception {
      int a = 'a';
      int b = 'b';
      int c = 'c';
      assertArrayEquals(new Byte[]{'a', 'b', 'c'}, new ArrayConverter<>(Byte.class).apply(new Object[]{a, b, c}));
      assertArrayEquals(new Byte[]{'a', 'b', 'c'},
                        new ArrayConverter<>(Byte.class).apply(new Character[]{'a', 'b', 'c'}));
      assertArrayEquals(new Byte[]{'a', 'b', 'c'}, new ArrayConverter<>(Byte.class).apply(new Object[]{"a", "b", 'c'}));
      assertNull(Val.of(new ArrayList<>()).asArray(Byte.class));
      assertArrayEquals(new Byte[]{'a', 'b', 'c'}, Val.of(new Object[]{a, b, c}).asByteArray());
   }


   @Test
   public void testChar() throws Exception {
      int a = 'a';
      int b = 'b';
      int c = 'c';
      assertArrayEquals(new Character[]{'a', 'b', 'c'},
                        new ArrayConverter<>(Character.class).apply(new Object[]{a, b, c}));
      assertArrayEquals(new Character[]{'a', 'b', 'c'},
                        new ArrayConverter<>(Character.class).apply(new Character[]{'a', 'b', 'c'}));
      assertArrayEquals(new Character[]{'a', 'b', 'c'},
                        new ArrayConverter<>(Character.class).apply(new Object[]{"a", "b", 'c'}));
      assertNull(Val.of(new ArrayList<>()).asArray(Character.class));
      assertArrayEquals(new Character[]{'a', 'b', 'c'}, Val.of(new Object[]{a, b, c}).asCharacterArray());
   }

}