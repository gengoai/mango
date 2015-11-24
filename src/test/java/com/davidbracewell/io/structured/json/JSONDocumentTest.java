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

package com.davidbracewell.io.structured.json;

import com.davidbracewell.io.Resources;
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
public class JSONDocumentTest {

  JSONDocument document;

  @Before
  public void setUp() throws Exception {
    document = new JSONDocument();
    document.read(Resources.fromClasspath("com/davidbracewell/test.json"));
  }

  @Test
  public void testSelect() throws Exception {
    final Element element = document.select(element1 -> element1.getName().equals("person.address"), false).findFirst().orElse(null);
    assertNotNull(element);
    assertTrue(element.hasAttribute("address.number"));
    assertFalse(element.hasAttribute("bigBox"));
    assertEquals("45", element.getAttribute("address.number"));
    assertTrue(element.hasChildren());

    element.getChildren().stream()
      .filter(element1 -> element1.getName().equals("address.number"))
      .allMatch(child -> {
        assertEquals("45", child.getValue());
        assertEquals(element, child.getParent());
        return true;
      });

    assertEquals(document, element.getDocument());
  }

  @Test
  public void testWrite() throws Exception {
    Resource str = Resources.fromString();
    document.write(str);
    assertEquals(document.toString().trim().replaceAll("\\s+", ""), str.readToString().trim().replaceAll("\\s+", ""));
  }

  @Test
  public void testEquals() throws Exception {
    assertEquals(document, document);
    assertNotEquals(null, document);
    assertNotEquals(document, new JSONDocument());
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
