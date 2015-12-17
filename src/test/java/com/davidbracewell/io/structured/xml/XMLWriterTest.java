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

import com.davidbracewell.io.resource.ByteArrayResource;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.resource.StringResource;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * @author David B. Bracewell
 */
public class XMLWriterTest {


  @Test
  public void xmlWriterText() throws Exception {
    Resource resource = new ByteArrayResource();
    try (XMLWriter writer = new XMLWriter(resource)) {
      writer.beginDocument();
      writer.beginObject("test");
      writer.writeAttribute("attribute", "isSet");
      writer.writeValue("Value1");
      writer.endObject();
      writer.writeKeyValue("Key", "Value");
      writer.beginObject();
      writer.writeKeyValue("language", "ENGLISH");
      writer.writeKeyValue("length", 123);
      writer.endObject();
      writer.beginArray();
      writer.writeValue("Value2");
      writer.writeValue("Value3");
      writer.endArray();
      writer.endDocument();
    }
    assertEquals("<?xml version=\"1.0\" ?><document><test elementType=\"object\" attribute=\"isSet\">Value1</test><Key>Value</Key><object elementType=\"object\"><language>ENGLISH</language><length>123</length></object><array elementType=\"array\"><arrayElement elementType=\"value\">Value2</arrayElement><arrayElement elementType=\"value\">Value3</arrayElement></array></document>",
        resource.readToString().trim());


    resource = new StringResource();
    try (XMLWriter writer = new XMLWriter(resource)) {
      writer.beginDocument();
      TestBean bean = new TestBean();
      bean.setName("testing");
//      writer.writeObject(bean);
      writer.endDocument();
    }
    assertEquals("<?xml version=\"1.0\" ?><document><com.davidbracewell.io.structured.xml.TestBean><name>testing</name></com.davidbracewell.io.structured.xml.TestBean></document>",
        resource.readToString().trim()
    );


  }


}
