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
 *
 */

package com.gengoai.io;

import com.gengoai.io.resource.Resource;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.stream.*;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.dom.DOMResult;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

import static com.gengoai.Validation.notNull;
import static com.gengoai.Validation.notNullOrBlank;

/**
 * Common methods for parsing and handling XML files
 *
 * @author David B. Bracewell
 */
public final class Xml {

   /**
    * An EventFilter that ignores character elements that are white space
    */
   public static final EventFilter WHITESPACE_FILTER = event -> !(event.isCharacters() && event.asCharacters()
                                                                                               .isWhiteSpace());

   private Xml() {
      throw new IllegalAccessError();
   }

   private static Optional<String> getTagName(XMLEvent event) {
      if (event.isStartElement()) {
         return Optional.of(event.asStartElement().getName().getLocalPart());
      }
      if (event.isEndElement()) {
         return Optional.of(event.asEndElement().getName().getLocalPart());
      }
      return Optional.empty();
   }

   /**
    * Parses the given XML resource creating sub-documents (DOM) from the elements with the given tag name. An example
    * of usage for this method is constructing DOM documents from each "page" element in the Wikipedia dump.
    *
    * @param xmlResource the xml resource
    * @param tag         the tag to create documents on
    * @return the iterable
    * @throws IOException        the io exception
    * @throws XMLStreamException the xml stream exception
    */
   public static Iterable<Document> parse(Resource xmlResource, String tag) throws IOException, XMLStreamException {
      return parse(xmlResource, tag, null);
   }

   /**
    * Parses the given XML resource creating sub-documents (DOM) from the elements with the given tag name and filtering
    * out events using the given event filter. An example of usage for this method is constructing DOM documents from
    * each "page" element in the Wikipedia dump.
    *
    * @param xmlResource the xml resource
    * @param tag         the tag to create documents on
    * @param eventFilter the event filter
    * @return the iterable
    * @throws IOException        the io exception
    * @throws XMLStreamException the xml stream exception
    */
   public static Iterable<Document> parse(Resource xmlResource,
                                          String tag,
                                          EventFilter eventFilter) throws IOException, XMLStreamException {
      notNull(xmlResource, "Must specify a resource");
      notNullOrBlank(tag, "Must specify a valid xml tag to capture");
      XMLInputFactory factory = XMLInputFactory.newFactory();
      final XMLEventReader reader;
      if (eventFilter == null) {
         reader = factory.createXMLEventReader(xmlResource.inputStream(), "UTF-8");
      } else {
         reader = factory.createFilteredReader(factory.createXMLEventReader(xmlResource.inputStream(), "UTF-8"),
                                               eventFilter);
      }
      return () -> new XmlIterator(tag, reader);
   }

   private static class XmlIterator implements Iterator<Document> {
      private final MonitoredObject<XMLEventReader> monitoredObject;
      private final XMLEventReader reader;
      private final String tag;
      Document document;
      private boolean isClosed = false;

      private XmlIterator(String tag, XMLEventReader reader) {
         this.monitoredObject = ResourceMonitor.monitor(reader, r -> {
            try {
               r.close();
            } catch (XMLStreamException e) {
               e.printStackTrace();
            }
         });
         this.tag = tag;
         this.reader = reader;
      }

      private boolean advance() throws Exception {
         if (document != null) {
            return true;
         }
         if (!reader.hasNext()) {
            return false;
         }
         XMLEvent event;
         while ((event = reader.nextEvent()).getEventType() != XMLEvent.END_DOCUMENT) {
            if (getTagName(event).map(n -> n.equals(tag)).orElse(false)) {
               document = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
               final XMLEventWriter writer = XMLOutputFactory.newInstance()
                                                             .createXMLEventWriter(new DOMResult(document));
               writer.add(event);
               while ((event = reader.nextEvent()).getEventType() != XMLEvent.END_DOCUMENT) {
                  writer.add(event);
                  if (getTagName(event).map(n -> n.equals(tag)).orElse(false)) {
                     break;
                  }
               }
               return true;
            }
         }
         return document == null;
      }

      @Override
      public boolean hasNext() {
         try {
            return advance();
         } catch (Exception e) {
            return false;
         }
      }

      @Override
      public Document next() {
         try {
            advance();
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
         Document toReturn = document;
         document = null;
         return toReturn;
      }
   }

}//END OF Xml
