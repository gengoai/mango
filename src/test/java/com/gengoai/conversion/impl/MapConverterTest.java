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

import com.gengoai.collection.map.Maps;
import com.gengoai.conversion.Convert;
import com.gengoai.conversion.Val;
import com.gengoai.tuple.Tuple2;
import com.google.common.collect.BiMap;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class MapConverterTest {

   @Test
   public void testMapConvert() throws Exception {
      assertNull(Convert.convert(null, BiMap.class, String.class, String.class));

      //Map Conversion
      Map<String, Integer> gold = Maps.map("A", 1);
      Assert.assertEquals(gold, Val.of(Maps.map("A", "1")).asMap(String.class, Integer.class));

      //String conversion
      assertEquals(gold, Val.of("{A=1}").asMap(String.class, Integer.class));

      //Entry conversion
      assertEquals(gold, Val.of(Tuple2.of("A", 1)).asMap(String.class, Integer.class));

      //Entry[] conversion
      List<Tuple2<String, Integer>> list = new ArrayList<>();
      list.add(Tuple2.of("A", 1));
      assertEquals(gold, Val.of(list).asMap(String.class, Integer.class));

   }
}//END OF MapConverterTest