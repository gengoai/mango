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

package com.gengoai.mango.collection.index;

import com.gengoai.mango.io.resource.Resource;
import com.gengoai.mango.io.resource.StringResource;
import com.gengoai.mango.io.resource.Resource;
import com.gengoai.mango.io.resource.StringResource;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class IndexesTest {

   @Test
   public void csv() throws Exception {
      Index<String> i1 = Indexes.newIndex("A", "B", "C", "D", "E");
      Resource str = new StringResource();

      i1.writeCSV(str);
      Index<String> i2 = Indexes.readCsv(str, String.class);
      assertEquals(i1, i2);
   }

   @Test
   public void json() throws Exception {
      Index<String> i1 = Indexes.newIndex("A", "B", "C", "D", "E");
      Resource str = new StringResource();

      i1.writeJson(str);
      Index<String> i2 = Indexes.readJson(str, String.class);
      assertEquals(i1, i2);
   }


}//END OF IndexesTest
