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

package com.davidbracewell.io.structured.xml;

import com.davidbracewell.io.Resources;
import com.davidbracewell.io.resource.ByteArrayResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.Element;
import com.google.common.collect.Sets;
import org.junit.Before;
import org.junit.Test;

import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class XMLDocumentTest {

  XMLDocument document;

  @Before
  public void setUp() throws Exception {
    document = new XMLDocument();
    document.read(Resources.fromClasspath("com/davidbracewell/test.xml"));
  }

  @Test
  public void testSelect() throws Exception {
//    Element element = Iterables.getFirst(document.select(StructuredQueries.nameMatch("person.address"), false), null);
//    assertNotNull(element);
//    assertTrue(element.hasAttribute("elementType"));
//    assertFalse(element.hasAttribute("bigBox"));
//    assertEquals("object", element.getAttribute("elementType"));
//    assertTrue(element.hasChildren());
//    for (Element child : element.getChildren()) {
//      if (child.getName().equals("address.number")) {
//        assertEquals("45", child.getValue());
//        assertEquals(element, child.getParent());
//      }
//    }
//    assertEquals(document, element.getDocument());
//
//
//    element = Iterables.getFirst(
//        document.select(StructuredQueries.attributeValueMatch("elementType", "object"), false),
//        null);
//    assertNotNull(element);
//    assertEquals("person.address", element.getName());
//
//    element = Iterables.getFirst(
//        document.select(StructuredQueries.hasAttribute("elementType"), false),
//        null);
//    assertNotNull(element);
//    assertEquals("person.children", element.getName());
//
//    element = Iterables.getFirst(
//        document.select(StructuredQueries.valueMatch("Texas"), true),
//        null);
//    assertNotNull(element);
//    assertEquals("address.state", element.getName());

  }

  @Test
  public void testWrite() throws Exception {
    Resource str = new ByteArrayResource();
    document.write(str);
    assertEquals(document.toString().trim(), str.readToString().trim());
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(document, document);
    assertNotEquals(null, document);
    assertNotEquals(document, new XMLDocument());
  }

  @Test
  public void testSelectRecursive() throws Exception {
    final Set<String> names = Sets.newHashSet("Fred", "Sara", "John");
    final Set<String> gathered = document.select(element -> element.getName().equals("person.name"), true)
        .map(Element::getValue)
        .collect(Collectors.toSet());
    assertTrue(Sets.difference(names, gathered).isEmpty());
  }


}//END OF XMLDocumentTest
