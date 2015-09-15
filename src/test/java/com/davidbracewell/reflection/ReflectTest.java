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

package com.davidbracewell.reflection;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ReflectTest {

  public static class TestClass {
    private String name;
    public int age;

    public TestClass(String name, int age) {
      this.name = name;
      this.age = age;
    }

    private TestClass() {

    }

    public String getName() {
      return name;
    }

    public void setName(String name) {
      this.name = name;
    }

    private String makeme() {
      return name + " := " + age;
    }

  }


  @Test
  public void test() throws Exception {
    TestClass tc = new TestClass("Jamie", 100);
    Reflect r = Reflect.onObject(tc);

    //test name
    assertEquals("Jamie", r.invoke("getName").get());

    r = r.allowPrivilegedAccess();


    //Create an no-age jane
    assertEquals("jane", r.create().set("name", "jane").get("name").get());

    //Create a no-named 34 year old
    assertEquals(34, (Number)r.set("age", 34).get("age").get());

    //Create a 100 year old jane
    assertEquals("jane", r.create("jane", 100).get("name").get());

    //Create a Test class and set the age to 100
    assertEquals(100, (Number)r.create("jane", 100).get("age").get());

    //Create a Test class and set the name to 89
    assertEquals("89", r.create().set("name", 89).get("name").get());

    //Test privaleged method call
    assertEquals("jane := 100", r.create("jane", 100).invoke("makeme").get());
  }

  @Test(expected = ReflectionException.class)
  public void testNoMethodException() throws Exception {
    TestClass tc = new TestClass("Jamie", 100);
    Reflect r = Reflect.onObject(tc);

    //test age
    assertEquals(100, (Number)r.invoke("getAge").get());
  }

}//END OF ReflectTest
