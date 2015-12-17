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

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredWriter;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Stack;

/**
 * An implementation of a StructuredWriter that writes xml.
 *
 * @author David B. Bracewell
 */
public class XMLWriter implements StructuredWriter {

  private final String documentTag;
  private final Stack<ElementType> stack;
  private final OutputStream os;
  private final XMLStreamWriter writer;

  /**
   * Creates an XML writer with the document tag "document"
   *
   * @param resource The resource to write xml to
   * @throws IOException Something went wrong writing
   */
  public XMLWriter(Resource resource) throws IOException {
    this("document", resource);
  }

  /**
   * Creates an XML writer with a given document tag
   *
   * @param documentTag The document tag to use for the xml document
   * @param resource    The resource to write xml to
   * @throws IOException Something went wrong writing
   */
  public XMLWriter(String documentTag, Resource resource) throws IOException {
    try {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(documentTag));
      stack = new Stack<>();
      this.documentTag = documentTag;
      this.os = resource.outputStream();
      this.writer = XMLOutputFactory.newFactory().createXMLStreamWriter(os, "UTF-8");
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public XMLWriter beginDocument() throws IOException {
    try {
      writer.writeStartDocument();
      writer.writeStartElement(documentTag);
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  /**
   * Specific to XML is the ability to write attributes.
   *
   * @param name  the name of the attribute
   * @param value The value of the attribute
   * @return This XMLWriter
   * @throws IOException Something went wrong writing
   */
  public XMLWriter writeAttribute(String name, String value) throws IOException {
    try {
      writer.writeAttribute(name, value);
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public void endDocument() throws IOException {
    try {
      writer.writeEndElement();
      writer.writeEndDocument();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }

  @Override
  public StructuredWriter writeNull() throws IOException {
    try {
      writer.writeCharacters("null");
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public StructuredWriter writeNumber(Number number) throws IOException {
    try {
      writer.writeCharacters(number.toString());
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public StructuredWriter writeString(String string) throws IOException {
    try {
      writer.writeCharacters(string);
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public StructuredWriter writeBoolean(boolean value) throws IOException {
    try {
      writer.writeCharacters(Boolean.toString(value));
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter writeKeyValue(String key, Object value) throws IOException {
    try {
      if (key == null) {
        writeValue(value);
      }
      writer.writeStartElement(key);
      writeValue(value);
      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter beginObject() throws IOException {
    return beginObject("object");
  }

  @Override
  public XMLWriter beginObject(String objectName) throws IOException {
    try {
      stack.push(ElementType.BEGIN_OBJECT);
      writer.writeStartElement(objectName);
      writer.writeAttribute("elementType", "object");
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter endObject() throws IOException {
    try {
      ElementType element = stack.pop();
      if (element != ElementType.BEGIN_OBJECT) {
        throw new IOException("Write error could not end an object before ending " + element);
      }
      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter beginArray() throws IOException {
    return beginArray("array");
  }

  @Override
  public XMLWriter beginArray(String arrayName) throws IOException {
    try {
      stack.push(ElementType.BEGIN_ARRAY);
      writer.writeStartElement(arrayName);
      writer.writeAttribute("elementType", "array");
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter endArray() throws IOException {
    try {
      ElementType element = stack.pop();
      if (element != ElementType.BEGIN_ARRAY) {
        throw new IOException("Write error could not end an array before ending " + element);
      }
      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
    return this;
  }

  @Override
  public boolean inArray() {
    return stack.peek() == ElementType.BEGIN_ARRAY;
  }

  @Override
  public boolean inObject() {
    return stack.peek() == ElementType.BEGIN_OBJECT || stack.peek() == ElementType.BEGIN_DOCUMENT;
  }

  @Override
  public void flush() throws IOException {
    try {
      writer.flush();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      os.close();
      writer.close();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }

}//END OF XMLWriter
