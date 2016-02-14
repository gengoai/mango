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

import com.davidbracewell.conversion.Val;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public abstract class AbstractStructuredReaderTest {

  final StructuredReader reader;


  protected AbstractStructuredReaderTest(StructuredReader reader) {
    this.reader = reader;
  }

  @Test
  public void test() throws Exception {
    reader.beginDocument();
    assertEquals("Fred", reader.nextKeyValue("person.name").asString());
    assertEquals(56, reader.nextKeyValue("person.age").asIntegerValue());

    List<String> children = reader.nextCollection(ArrayList::new, String.class);
    assertEquals(Arrays.asList("Sara", "John"), children);

    Map<String, Val> address = reader.nextMap("person.address");
    assertEquals(45, address.get("address.number").asIntegerValue());
    assertEquals("Elm Street", address.get("address.street").asString());
    assertEquals("New Haven", address.get("address.city").asString());
    assertEquals("Texas", address.get("address.state").asString());


    List<String> hobbies = reader.nextCollection(ArrayList::new, String.class);
    assertEquals(Arrays.asList("Music", "Movies"), hobbies);

    reader.endDocument();
    reader.close();
  }
}