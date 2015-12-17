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
import com.davidbracewell.io.resource.StringResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author David B. Bracewell
 */
public class JSONWriterTest {

  @Test
  public void writerTest() throws Exception {
    Resource resource = Resources.fromString("");
    try (JSONWriter writer = new JSONWriter(resource)) {
      writer.beginDocument();
      writer.writeKeyValue("name", "value");
      writer.beginArray("array").writeValue("value1").endArray();
      writer.beginObject("innerObject");
      writer.writeKeyValue("arg", null);
      writer.endObject();
      writer.writeKeyValue("int", 3);
      writer.endDocument();
    }

    assertEquals("{\"name\":\"value\",\"array\":[\"value1\"],\"innerObject\":{\"arg\":null},\"int\":3}", resource.readToString().trim());


    resource = new StringResource();
    try (JSONWriter writer = new JSONWriter(resource)) {
      writer.beginDocument();
//      writer.writeObject(Tuple2.of("String1", 34d));
      writer.endDocument();
    }
    assertEquals("{\"List\":[\"String1\",34.0]}", resource.readToString().trim());


    try (JSONReader reader = new JSONReader(resource)) {
      reader.beginDocument();
//      assertEquals(reader.nextObject(List.class), Arrays.asList("String1", 34d));
      reader.endDocument();
    }
  }

}
