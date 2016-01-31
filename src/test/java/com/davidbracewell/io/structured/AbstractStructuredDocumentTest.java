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

import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public abstract class AbstractStructuredDocumentTest {

  final StructuredDocument document;


  protected AbstractStructuredDocumentTest(StructuredDocument document) {
    this.document = document;
  }

  @Test
  public void test() throws Exception {
    assertEquals("Fred", document.getFirstValueOfKey("person.name").asString());
    assertEquals(56, document.getFirstValueOfKey("person.age").asIntegerValue());

    Element children = document.select(e -> e.getName().equals("person.children")).findFirst().orElse(null);
    assertNotNull(children);
    assertEquals(Arrays.asList("Sara", "John"), children.getValue().asList(String.class));

    Optional<Element> address = document.selectFirstByName("person.address");
    assertTrue(address.isPresent());
    Map<String,Val> addressMap = address.get().getValue().cast();
    assertEquals(45, addressMap.get("address.number").asIntegerValue());
    assertEquals("Elm Street", addressMap.get("address.street").asString());
    assertEquals("New Haven", addressMap.get("address.city").asString());
    assertEquals("Texas", addressMap.get("address.state").asString());

    Element hobbies = document.select(e -> e.getName().equals("person.hobbies")).findFirst().orElse(null);
    assertNotNull(hobbies);
    assertEquals(Arrays.asList("Music", "Movies"), hobbies.getValue().asList(String.class));


  }
}