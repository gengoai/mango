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

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import org.junit.Test;

import static com.davidbracewell.tuple.Tuples.$;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class MultiCountersTest {

   @Test
   public void writeJson() throws Exception {
      MultiCounter<String, String> mc = MultiCounters.newMultiCounter($("A", "B"),
                                                                      $("A", "C"),
                                                                      $("A", "D"),
                                                                      $("B", "E"),
                                                                      $("B", "G"),
                                                                      $("B", "H")
                                                                     );
      Resource str = new StringResource();
      mc.writeJson(str);
      MultiCounter<String, String> mcPrime = MultiCounters.readJson(str, String.class, String.class);
      assertEquals(mc,mcPrime);
   }


   @Test
   public void writeCsv() throws Exception {
      MultiCounter<String, String> mc = MultiCounters.newMultiCounter($("A", "B"),
                                                                      $("A", "C"),
                                                                      $("A", "D"),
                                                                      $("B", "E"),
                                                                      $("B", "G"),
                                                                      $("B", "H")
                                                                     );
      Resource str = new StringResource();
      mc.writeCsv(str);
      MultiCounter<String, String> mcPrime = MultiCounters.readCsv(str, String.class, String.class);
      assertEquals(mc, mcPrime);
   }


}//END OF MultiCountersTest
