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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import lombok.NonNull;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.NoSuchElementException;
import java.util.Stack;

/**
 * An implementation of a StructuredReader that reads xml.
 *
 * @author David B. Bracewell
 */
public class XMLReader extends StructuredReader {

  private final String documentTag;
  private final XMLEventReader reader;
  private final Stack<Tuple2<String, ElementType>> stack;


  /**
   * Creates an XMLReader
   *
   * @param resource The resource to read
   * @throws StructuredIOException Something went wrong reading
   */
  public XMLReader(Resource resource) throws StructuredIOException {
    this("document", resource);
  }

  /**
   * Creates an XMLReader
   *
   * @param resource The resource to read
   * @throws StructuredIOException Something went wrong reading
   */
  public XMLReader(Reader resource) throws StructuredIOException {
    this("document", Resources.fromReader(resource));
  }

  /**
   * Creates an XMLReader
   *
   * @param documentTag The document tag
   * @param resource    The resource to read
   * @throws StructuredIOException Something went wrong reading
   */
  public XMLReader(String documentTag, @NonNull Resource resource) throws StructuredIOException {
    try {
      Preconditions.checkArgument(!Strings.isNullOrEmpty(documentTag));
      this.documentTag = documentTag;
      this.reader = XMLInputFactory.newFactory().createXMLEventReader(resource.inputStream(), "UTF-8");
      this.stack = new Stack<>();
    } catch (Exception e) {
      throw new StructuredIOException(e);
    }
  }


  @Override
  public <T> T nextObject(Class<T> clazz) throws StructuredIOException {
    try {
      JAXBContext context = JAXBContext.newInstance(clazz);
      Unmarshaller unmarshaller = context.createUnmarshaller();
      return unmarshaller.unmarshal(reader, clazz).getValue();
    } catch (JAXBException e) {
      throw new StructuredIOException(e);
    }
  }

  private XMLEvent next() throws StructuredIOException {
    peek(); // move to the next real event
    try {
      return reader.nextEvent();
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    } catch (NoSuchElementException e) {
      return null;
    }
  }


  private XMLReader validate(XMLEvent event, ElementType expectedElement, Tuple2<String, ElementType> expectedTopOfStack) throws StructuredIOException {

    if (event == null) {
      throw new StructuredIOException("Parsing error event was null");
    }

    ElementType element = xmlEventToStructuredElement(event);

    if (expectedElement != null && element != expectedElement) {
      throw new StructuredIOException("Parsing error: expected (" + expectedElement + ") found (" + element + ")");
    }

    if (expectedTopOfStack != null && (stack.isEmpty() || !stack.peek().equals(expectedTopOfStack))) {
      throw new StructuredIOException("Parsing error: expected (" + expectedTopOfStack + ") found (" + stack.peek() + ")");
    }

    return this;
  }

  @Override
  public XMLReader beginDocument() throws StructuredIOException {
    try {
      XMLEvent event = next();
      if (event.isStartDocument()) {
        event = reader.nextTag();
      }
      if (!event.isStartElement() || !((StartElement) event).getName().toString().equals(documentTag)) {
        throw new StructuredIOException("document tag does not match : expected (<" + documentTag + ">)");
      }
      stack.push(Tuple2.of(documentTag, ElementType.BEGIN_DOCUMENT));
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public XMLReader endDocument() throws StructuredIOException {
    XMLEvent event = next();
    return validate(event, ElementType.END_DOCUMENT, Tuple2.of(documentTag, ElementType.BEGIN_DOCUMENT));
  }

  private ElementType xmlEventToStructuredElement(XMLEvent event) {
    if (event == null) {
      return ElementType.END_DOCUMENT;
    }

    if (event.isStartDocument()) {
      return ElementType.BEGIN_DOCUMENT;
    }

    if (event.isEndDocument()) {
      return ElementType.END_DOCUMENT;
    }


    if (event.isStartElement()) {
      StartElement element = (StartElement) event;

      if (element.getName().getLocalPart().equals(documentTag)) {
        return ElementType.BEGIN_DOCUMENT;
      }

      QName elementType = new QName("", "elementType");
      if (element.getAttributeByName(elementType) == null) {
        return ElementType.NAME;
      }

      String typeName = element.getAttributeByName(elementType).getValue();
      if (typeName.equalsIgnoreCase("array")) {
        return ElementType.BEGIN_ARRAY;
      }
      if (typeName.equalsIgnoreCase("object")) {
        return ElementType.BEGIN_OBJECT;
      }
      if (typeName.equalsIgnoreCase("value")) {
        return ElementType.VALUE;
      }

      return ElementType.NAME;
    }

    if (event.isEndElement()) {
      EndElement element = (EndElement) event;
      String elementName = element.getName().getLocalPart();
      Tuple2<String, ElementType> top = stack.peek();

      if (top.equals(Tuple2.of(elementName, ElementType.BEGIN_DOCUMENT))) {
        return ElementType.END_DOCUMENT;
      }
      if (top.equals(Tuple2.of(elementName, ElementType.BEGIN_ARRAY))) {
        return ElementType.END_ARRAY;
      }
      if (top.equals(Tuple2.of(elementName, ElementType.BEGIN_OBJECT))) {
        return ElementType.END_OBJECT;
      }
    }

    return ElementType.OTHER;
  }

  public ElementType peek() throws StructuredIOException {
    while (true) {
      try {
        XMLEvent event = reader.peek();
        ElementType element = xmlEventToStructuredElement(event);
        if (element != ElementType.OTHER) {
          return element;
        }
        reader.nextEvent();
      } catch (XMLStreamException e) {
        throw new StructuredIOException(e);
      }
    }
  }


  @Override
  public String beginObject() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.BEGIN_OBJECT, null);
    String name = ((StartElement) event).getName().getLocalPart();
    stack.push(Tuple2.of(name, ElementType.BEGIN_OBJECT));
    return name;
  }

  @Override
  public void endObject() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.END_OBJECT, Tuple2.of(((EndElement) event).getName().getLocalPart(), ElementType.BEGIN_OBJECT));
    stack.pop();
  }

  @Override
  public String beginArray() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.BEGIN_ARRAY, null);
    String name = ((StartElement) event).getName().getLocalPart();
    stack.push(Tuple2.of(name, ElementType.BEGIN_ARRAY));
    return name;
  }

  @Override
  public void endArray() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.END_ARRAY, Tuple2.of(((EndElement) event).getName().getLocalPart(), ElementType.BEGIN_ARRAY));
    stack.pop();
  }

  @Override
  public boolean hasNext() throws StructuredIOException {
    return reader.hasNext();
  }

  private String handleNullables(String text) {
    if (text == null || text.equals("null")) {
      return null;
    }
    return text;
  }

  @Override
  public Tuple2<String, Val> nextKeyValue() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.NAME, null);
    try {
      String key = ((StartElement) event).getName().getLocalPart();
      String value = reader.getElementText();
      return Tuple2.of(key, new Val(handleNullables(value)));
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
  }

  public ElementType skip() throws StructuredIOException {
    ElementType element = peek();
    switch (element) {
      case BEGIN_ARRAY:
        beginArray();
        while (peek() != ElementType.END_ARRAY) {
          skip();
        }
        endArray();
        break;
      case BEGIN_OBJECT:
        beginObject();
        while (peek() != ElementType.END_OBJECT) {
          skip();
        }
        endObject();
        break;
      default:
        next();
    }

    return element;
  }

  @Override
  public Val nextValue() throws StructuredIOException {
    XMLEvent event = next();
    validate(event, ElementType.VALUE, null);
    try {
      return new Val(handleNullables(reader.getElementText()));
    } catch (XMLStreamException e) {
      throw new StructuredIOException(e);
    }
  }

  @Override
  public void close() throws IOException {
    try {
      reader.close();
    } catch (XMLStreamException e) {
      throw new IOException(e);
    }
  }

}// END OF XMLReader
