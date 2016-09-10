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
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import javax.xml.namespace.QName;
import javax.xml.stream.EventFilter;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * An implementation of a StructuredReader that reads xml.
 *
 * @author David B. Bracewell
 */
public class XMLReader extends StructuredReader {
   private final String documentTag;
   private final XMLEventReader reader;
   private final Stack<Tuple2<String, ElementType>> stack;
   private final LinkedList<XMLEvent> buffer = new LinkedList<>();
   private ElementType documentType;
   private String readerText;


   static class WhitespaceFilter implements EventFilter {
      @Override
      public boolean accept(XMLEvent event) {
         return !(event.isCharacters() && ((Characters) event)
                                             .isWhiteSpace());
      }
   }

   /**
    * Creates an XMLReader
    *
    * @param resource The resource to read
    * @throws IOException Something went wrong reading
    */
   public XMLReader(Resource resource) throws IOException {
      this("document", resource);
   }

   /**
    * Creates an XMLReader
    *
    * @param resource The resource to read
    * @throws IOException Something went wrong reading
    */
   public XMLReader(Reader resource) throws IOException {
      this("document", Resources.fromReader(resource));
   }


   private XMLEvent nextValidEvent() throws IOException {
      while (true) {
         try {
            XMLEvent event = reader.nextEvent();
            ElementType element = xmlEventToStructuredElement(event);
            if (element != ElementType.OTHER) {
               return event;
            }

         } catch (XMLStreamException e) {
            throw new IOException(e);
         }
      }
   }

   private XMLEvent consume() throws IOException {
      if (buffer.isEmpty()) {
         return nextValidEvent();
      }
      return buffer.removeFirst();
   }

   private XMLEvent peekEvent() throws IOException {
      if (buffer.isEmpty()) {
         buffer.add(nextValidEvent());
      }
      return buffer.getFirst();
   }


   /**
    * Creates an XMLReader
    *
    * @param documentTag The document tag
    * @param resource    The resource to read
    * @throws IOException Something went wrong reading
    */
   public XMLReader(String documentTag, @NonNull Resource resource) throws IOException {
      try {
         Preconditions.checkArgument(StringUtils.isNotNullOrBlank(documentTag));
         this.documentTag = documentTag;
         XMLInputFactory factory = XMLInputFactory.newFactory();
         this.reader = factory.createFilteredReader(factory.createXMLEventReader(resource.inputStream(), "UTF-8"),
                                                    new WhitespaceFilter()
                                                   );
         this.stack = new Stack<>();
      } catch (Exception e) {
         throw new IOException(e);
      }
   }

   @Override
   public String peekName() throws IOException {
      XMLEvent event = peekEvent();
      if (event == null || !(event instanceof StartElement)) {
         return StringUtils.EMPTY;
      }
      return ((StartElement) event).getName().getLocalPart();
   }

   private XMLReader validate(XMLEvent event, ElementType expectedElement, Tuple2<String, ElementType> expectedTopOfStack) throws IOException {
      if (event == null) {
         throw new IOException("Parsing error event was null");
      }
      ElementType element = xmlEventToStructuredElement(event);

      if (expectedElement != null && element != expectedElement) {
         throw new IOException("Parsing error: expected (" + expectedElement + ") found (" + element + ")");
      }

      if (expectedTopOfStack != null && (stack.isEmpty() || !stack.peek().equals(expectedTopOfStack))) {
         throw new IOException("Parsing error: expected (" + expectedTopOfStack + ") found (" + stack.peek() + ")");
      }

      return this;
   }

   @Override
   public XMLReader beginDocument() throws IOException {
      try {
         XMLEvent event = consume();
         if (event == null) {
            throw new IOException();
         }
         if (event.isStartDocument()) {
            event = reader.nextTag();
         }
         if (!event.isStartElement() || !((StartElement) event).getName().toString().equals(documentTag)) {
            throw new IOException("document tag does not match : expected (<" + documentTag + ">)");
         }
         documentType = xmlEventToStructuredElement(event);
         stack.push(Tuple2.of(documentTag, ElementType.BEGIN_DOCUMENT));
      } catch (XMLStreamException e) {
         throw new IOException(e);
      }
      return this;
   }

   @Override
   public void endDocument() throws IOException {
      XMLEvent event = consume();
      validate(event, ElementType.END_DOCUMENT, Tuple2.of(documentTag, ElementType.BEGIN_DOCUMENT));
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

         QName elementType = new QName("", "type");

         if (element.getAttributeByName(elementType) == null) {
            return ElementType.NAME;
         }

         String typeName = element.getAttributeByName(elementType).getValue();
         if (typeName.equalsIgnoreCase("array") || element.getName().toString().equals("array")) {
            return ElementType.BEGIN_ARRAY;
         }
         if (typeName.equalsIgnoreCase("object") || element.getName().toString().equals("object")) {
            return ElementType.BEGIN_OBJECT;
         }
         if (typeName.equalsIgnoreCase("value") || element.getName().toString().equals("value")) {
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

         return ElementType.END_KEY_VALUE;
      }

      return ElementType.OTHER;
   }


   public ElementType peek() throws IOException {
      if (!StringUtils.isNullOrBlank(readerText)) {
         return ElementType.END_KEY_VALUE;
      }
      return xmlEventToStructuredElement(peekEvent());
   }

   @Override
   public String beginObject() throws IOException {
      XMLEvent event = consume();
      validate(event, ElementType.BEGIN_OBJECT, null);
      String name = ((StartElement) event).getName().getLocalPart();
      stack.push(Tuple2.of(name, ElementType.BEGIN_OBJECT));
      return name;
   }

   @Override
   public StructuredReader endObject() throws IOException {
      XMLEvent event = consume();
      validate(event,
               ElementType.END_OBJECT,
               Tuple2.of(((EndElement) event).getName().getLocalPart(), ElementType.BEGIN_OBJECT)
              );
      stack.pop();
      return this;
   }

   @Override
   public ElementType getDocumentType() {
      return documentType;
   }

   @Override
   public String beginArray() throws IOException {
      XMLEvent event = consume();
      validate(event, ElementType.BEGIN_ARRAY, null);
      String name = ((StartElement) event).getName().getLocalPart();
      stack.push(Tuple2.of(name, ElementType.BEGIN_ARRAY));
      return name;
   }

   @Override
   public StructuredReader endArray() throws IOException {
      XMLEvent event = consume();
      validate(event,
               ElementType.END_ARRAY,
               Tuple2.of(((EndElement) event).getName().getLocalPart(), ElementType.BEGIN_ARRAY)
              );
      stack.pop();
      return this;
   }

   @Override
   public boolean hasNext() throws IOException {
      return reader.hasNext();
   }

   private String handleNullables(String text) {
      if (text == null || text.equals("null")) {
         return null;
      }
      return text;
   }

   private void setReaderText() {
      try {
         this.readerText = handleNullables(reader.getElementText());
      } catch (XMLStreamException e) {

      }
   }

   @Override
   public Tuple2<String, Val> nextKeyValue() throws IOException {
      XMLEvent event = consume();
      String key = ((StartElement) event).getName().getLocalPart();
      switch (xmlEventToStructuredElement(event)) {
         case NAME:
            setReaderText();
            break;
         case BEGIN_OBJECT:
            stack.push($(key, ElementType.BEGIN_OBJECT));
            Val v = nextValue();
            System.out.println(stack);
            endObject();
            return $(key, v);
         case BEGIN_ARRAY:
            stack.push($(key, ElementType.BEGIN_ARRAY));
            List<Val> array = new ArrayList<>();
            while (peek() != ElementType.END_ARRAY) {
               array.add(nextValue());
            }
            endArray();
            return $(key, Val.of(array));
      }
      return Tuple2.of(key, nextValue());
   }

   @Override
   public <T> Tuple2<String, T> nextKeyValue(Class<T> clazz) throws IOException {
      XMLEvent event = consume();
      String key = ((StartElement) event).getName().getLocalPart();
      switch (xmlEventToStructuredElement(event)) {
         case NAME:
            setReaderText();
            break;
         case BEGIN_OBJECT:
            stack.push($(key, ElementType.BEGIN_OBJECT));
            T v = nextValue(clazz);
            endObject();
            return Tuple2.of(key, v);
      }
      return Tuple2.of(key, nextValue(clazz));
   }

   public ElementType skip() throws IOException {
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
            consume();
      }

      return element;
   }

   @Override
   protected Val nextSimpleValue() throws IOException {
      Val v;
      if (!StringUtils.isNullOrBlank(readerText)) {
         v = Val.of(readerText);
         readerText = null;
      } else if (peek() == ElementType.VALUE) {
         consume();
         try {
            return Val.of(reader.getElementText());
         } catch (XMLStreamException e) {
            throw new IOException(e);
         }
      } else {
         throw new IOException("Error");
      }
      return v;
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
