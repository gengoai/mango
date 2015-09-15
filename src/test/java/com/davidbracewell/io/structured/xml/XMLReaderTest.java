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

import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.Resources;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class XMLReaderTest {

  @Test
  public void testReader() throws Exception {
    XMLReader reader = new XMLReader(Resources.fromClasspath("com/davidbracewell/test.xml"));
    reader.beginDocument();

    Tuple2<String, Val> keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.name");
    assertEquals(keyValue.getValue().asString(), "Fred");
    keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.age");
    assertEquals(keyValue.getValue().asString(), "56");

    assertEquals(reader.beginArray(), "person.children");
    keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.name");
    assertEquals(keyValue.getValue().asString(), "Sara");
    keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.name");
    assertEquals(keyValue.getValue().asString(), "John");
    reader.endArray();

    assertEquals(reader.beginObject(), "person.address");
    Map<String, Val> map = reader.nextMap();
    assertEquals((Integer) 45, map.get("address.number").asInteger());
    assertEquals("Elm Street", map.get("address.street").asString());
    assertEquals("New Haven", map.get("address.city").asString());
    assertEquals("Texas", map.get("address.state").asString());
    reader.endObject();

    assertEquals(reader.beginObject(), "person.hobbies");
    map = reader.nextMap();
    assertEquals(Lists.newArrayList("Music", "Movies"), map.get("list").asList(String.class));
    reader.endObject();

    reader.endDocument();
    reader.close();
  }

  @Test
  public void testReaderSkip() throws Exception {
    XMLReader reader = new XMLReader(Resources.fromClasspath("com/davidbracewell/test.xml"));
    reader.beginDocument();

    Tuple2<String, Val> keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.name");
    assertEquals(keyValue.getValue().asString(), "Fred");
    keyValue = reader.nextKeyValue();
    assertEquals(keyValue.getKey(), "person.age");
    assertEquals(keyValue.getValue().asString(), "56");

    Assert.assertEquals(reader.skip(), ElementType.BEGIN_ARRAY);
    assertEquals(reader.skip(), ElementType.BEGIN_OBJECT);
    assertEquals(reader.skip(), ElementType.BEGIN_OBJECT);

    reader.endDocument();
    reader.close();
  }

  @Test(expected = StructuredIOException.class)
  public void testReadError() throws Exception {
    XMLReader reader = new XMLReader(Resources.fromClasspath("iknowledge/espresso/test.xml"));
    reader.beginDocument();
    reader.beginArray();
  }
}
