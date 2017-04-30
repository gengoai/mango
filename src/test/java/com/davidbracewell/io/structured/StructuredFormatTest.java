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

package com.davidbracewell.io.structured;

import com.davidbracewell.collection.list.Lists;
import com.davidbracewell.collection.map.Maps;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.Resources;
import com.davidbracewell.json.Json;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class StructuredFormatTest {

   @Test
   public void loadAndDump() throws Exception {
      Map<String, Object> map = Maps.map("A", 1.0,
                                         "B", 2.0,
                                         "C", 3.0,
                                         "D", Lists.list(1.0, 2.0, 3.0),
                                         "E", Maps.map("A", "B"));

      String json = Json.dumps(map);
      Map<String, Val> jsonMap = Json.loads(Resources.fromString(json));
      assertEquals(1.0, jsonMap.get("A").asDoubleValue(), 0);
      assertEquals(2.0, jsonMap.get("B").asDoubleValue(), 0);
      assertEquals(3.0, jsonMap.get("C").asDoubleValue(), 0);
      assertEquals(map.get("D"), jsonMap.get("D").asList(Double.class));
      assertEquals(map.get("E"), jsonMap.get("E").asMap(String.class, String.class));
   }


}