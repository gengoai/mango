package com.davidbracewell;/*
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

import com.davidbracewell.application.CommandLineApplication;
import com.davidbracewell.cli.Option;
import com.davidbracewell.collection.Collect;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ApplicationTest {

  private static class TestApp extends CommandLineApplication {

    /**
     * Default Constructor
     */
    public TestApp() {
      super("TestApp");
    }


    @Option(description = "Name")
    public String name;


    @Option(name = "age", description = "Age")
    public int age;


    @Option(name = "phone", description = "Phone", defaultValue = "UNKNOWN")
    public String phone;


    @Option(description = "map")
    public Map<String, Double> map;

    @Override
    protected void programLogic() throws Exception {
      assertEquals("John", name);
      assertEquals(35, age);
      assertEquals("UNKNOWN", phone);
      assertEquals(Collect.map("ALPHA",23d), map);
    }
  }

  @Test
  public void testReflection() throws Exception {
    TestApp app1 = new TestApp();
    app1.run(new String[]{"--name=John", "--age=", "35", "--map", "{ALPHA:23}"});
  }


}
