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
import com.davidbracewell.io.structured.AbstractStructuredReaderTest;
import org.junit.Test;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public class JSONReaderTest extends AbstractStructuredReaderTest {

   public JSONReaderTest() throws IOException {
      super(new JSONReader(Resources.fromClasspath("com/davidbracewell/test.json")));
   }



   //  @Test
//  public void readerTest() throws Exception {
//    Resource resource = Resources.fromClasspath("com/davidbracewell/test.json");
//    try (JSONReader reader = new JSONReader(resource)) {
//      reader.beginDocument();
//
//      Tuple2<String, Val> keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "person.name");
//      assertEquals(keyValue.getValue().asString(), "Fred");
//      keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "person.age");
//      assertEquals(keyValue.getValue().asString(), "56.0");
//
//      assertEquals(reader.beginArray(), "person.children");
//      reader.beginObject();
//      assertEquals(reader.nextKeyValue().getValue().asString(), "Sara");
//      reader.endObject();
//      reader.beginObject();
//      assertEquals(reader.nextKeyValue().getValue().asString(), "John");
//      reader.endObject();
//      reader.endArray();
//
//      assertEquals(reader.beginObject(), "person.address");
//      keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "address.number");
//      assertEquals(keyValue.getValue().asString(), "45.0");
//      keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "address.street");
//      assertEquals(keyValue.getValue().asString(), "Elm Street");
//      keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "address.city");
//      assertEquals(keyValue.getValue().asString(), "New Haven");
//      keyValue = reader.nextKeyValue();
//      assertEquals(keyValue.getKey(), "address.state");
//      assertEquals(keyValue.getValue().asString(), "Texas");
//
//      reader.endObject();
//      reader.endDocument();
//    }
//
//
//  }

}
