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

package com.davidbracewell.application;/*
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

import com.davidbracewell.cli.Option;
import com.davidbracewell.collection.map.Maps;
import com.davidbracewell.config.Config;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class SwingApplicationTest {

   @Test
   public void testReflection() throws Exception {
      TestApp app1 = new TestApp();
      app1.run(new String[]{"--name=John", "--age=", "35", "--map", "{ALPHA:23}", "--action", "update", "-abc"});
   }

   private static class TestApp extends SwingApplication {
      private static final long serialVersionUID = 1L;

      @Option(description = "Name")
      public String name;
      @Option(name = "age", description = "Age")
      public int age;
      @Option(name = "phone", description = "Phone", defaultValue = "UNKNOWN")
      public String phone;
      @Option(description = "simple", defaultValue = "false")
      public boolean a;
      @Option(description = "simple", defaultValue = "false")
      public boolean b;
      @Option(description = "simple", defaultValue = "false")
      public boolean c;
      @Option(description = "map")
      public Map<String, Double> map;

      /**
       * Default Constructor
       */
      public TestApp() {
         super("TestApp", "com.davidbracewell.test");
      }

      @Override
      public void setup() throws Exception {
         assertEquals("John", name);
         assertEquals(35, age);
         assertEquals("UNKNOWN", phone);
         assertEquals(Maps.map("ALPHA", 23d), map);
         assertTrue(a);
         assertTrue(b);
         assertTrue(c);
         assertEquals("com.davidbracewell.test", getConfigPackageName());

         //Test that config parameters are set
         assertEquals("John", Config.get("name").asString());
         assertEquals("UNKNOWN", Config.get("phone").asString());
         assertEquals(35, Config.get("age").asIntegerValue());
         assertEquals(Maps.map("ALPHA", 23d), Config.get("map").asMap(String.class, Double.class));
         //Test CLI can override config parameter
         assertEquals("update", Config.get("action").asString());

         //Make sure config was loaded
         assertEquals("up", Config.get("direction").asString());


         assertArrayEquals(new String[]{"--name=John", "--age=", "35", "--map", "{ALPHA:23}", "--action", "update", "-abc"},
                           getAllArguments()
                          );
         assertArrayEquals(new String[]{"--action", "update"}, getNonSpecifiedArguments());
      }
   }


}
