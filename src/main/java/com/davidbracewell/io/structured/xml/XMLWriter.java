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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.io.structured.Writeable;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import java.util.Stack;

/**
 * An implementation of a StructuredWriter that writes xml.
 *
 * @author David B. Bracewell
 */
public class XMLWriter extends StructuredWriter {

  private final String documentTag;
  private final Stack<ElementType> stack;
  private final OutputStream os;
  private final XMLStreamWriter writer;

  /**
   * Creates an XML writer with the document tag "document"
   *
   * @param resource The resource to write xml to
   * @throws StructuredIOException Something went wrong writing
   */
  public XMLWriter(Resource resource) throws StructuredIOException {
    this("document", resource);
  }

  /**
   * Creates an XML writer with a given document tag
   *
   * @param documentTag The document tag to use for the xml document
   * @param resource    The resource to write xml to
   * @throws StructuredIOException Something went wrong writing
   */
  public XMLWriter(String documentTag, Resource resource) throws StructuredIOException {
    try {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(documentTag));
      stack = new Stack<>();
      this.documentTag = documentTag;
      this.os = resource.outputStream();
      this.writer = XMLOutputFactory.newFactory().createXMLStreamWriter(os, "UTF-8");
    } catch (Exception e) {
      throw new StructuredIOException(e);
    }
  }

  @Override
  public XMLWriter beginDocument() throws StructuredIOException {
    try {
      writer.writeStartDocument();
      writer.writeStartElement(documentTag);
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  /**
   * Specific to XML is the ability to write attributes.
   *
   * @param name  the name of the attribute
   * @param value The value of the attribute
   * @return This XMLWriter
   * @throws StructuredIOException Something went wrong writing
   */
  public XMLWriter writeAttribute(String name, String value) throws StructuredIOException {
    try {
      writer.writeAttribute(name, value);
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter endDocument() throws StructuredIOException {
    try {
      writer.writeEndElement();
      writer.writeEndDocument();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> StructuredWriter writeObject(T object) throws StructuredIOException {
    return writeObject(object.getClass().getName(), object);
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T> StructuredWriter writeObject(String name, T object) throws StructuredIOException {
    if (object instanceof Writeable) {
      beginObject(name);
      writeKeyValue("class", object.getClass().getName());
      Cast.<Writeable>as(object).write(this);
      endObject();
    } else if( object instanceof Map){
      for( Map.Entry entry : Cast.<Map<?,?>>as(object).entrySet()){
        writeKeyValue(Convert.convert(entry.getKey(),String.class), entry.getValue());
      }
    } else {
      try {
        JAXBElement<T> element = new JAXBElement(new QName(name), object.getClass(), object);
        JAXBContext context = JAXBContext.newInstance(object.getClass());
        Marshaller marshaller = context.createMarshaller();
        marshaller.setProperty("jaxb.fragment", Boolean.TRUE);
        marshaller.marshal(element, writer);
      } catch (JAXBException e) {
        throw new StructuredIOException(e);
      }
    }
    return this;
  }

  @Override
  public StructuredWriter writeValue(Object value) throws StructuredIOException {
    if (stack.isEmpty()) {
      throw new StructuredIOException(new IllegalStateException("Trying to write a value outside of an element"));
    }
    try {
      switch (stack.peek()) {
        case BEGIN_ARRAY:
          writer.writeStartElement("arrayElement");
          writer.writeAttribute("elementType", "value");
          writer.writeCharacters(value == null ? "null" : value.toString());
          writer.writeEndElement();
          break;
        default:
          writer.writeCharacters(value == null ? "null" : value.toString());
      }
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter writeKeyValue(String key, Object value) throws StructuredIOException {
    try {
      if (key == null) {
        writeValue(value);
      }
      writer.writeStartElement(key);
      writer.writeCharacters(value == null ? "null" : value.toString());
      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter beginObject() throws StructuredIOException {
    return beginObject("object");
  }

  @Override
  public XMLWriter beginObject(String objectName) throws StructuredIOException {
    try {
      stack.push(ElementType.BEGIN_OBJECT);
      writer.writeStartElement(objectName);
      writer.writeAttribute("elementType", "object");
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter endObject() throws StructuredIOException {
    try {
      ElementType element = stack.pop();
      if (element != ElementType.BEGIN_OBJECT) {
        throw new StructuredIOException("Write error could not end an object before ending " + element);
      }
      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter beginArray() throws StructuredIOException {
    return beginArray("array");
  }

  @Override
  public XMLWriter beginArray(String arrayName) throws StructuredIOException {
    try {
      stack.push(ElementType.BEGIN_ARRAY);
      writer.writeStartElement(arrayName);
      writer.writeAttribute("elementType", "array");
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLWriter endArray() throws StructuredIOException {
    try {
      ElementType element = stack.pop();
      if (element != ElementType.BEGIN_ARRAY) {
        throw new StructuredIOException("Write error could not end an array before ending " + element);
      }

      writer.writeEndElement();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public void flush() throws StructuredIOException {
    try {
      writer.flush();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
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
