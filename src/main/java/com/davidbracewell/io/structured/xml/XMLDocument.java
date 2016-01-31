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
import com.davidbracewell.io.structured.StructuredDocument;
import com.google.common.base.Preconditions;
import org.w3c.dom.Document;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.Objects;

/**
 * A Structured Document wrapping an XML Document Object Model
 *
 * @author David B. Bracewell
 */
public class XMLDocument extends XMLElement implements StructuredDocument {

  private Document document;

  /**
   * Creates an XMLDocument by reading in a give resource.
   *
   * @param resource The resource containing the XMLDocument
   * @return The XML Document
   * @throws IOException Something went wrong reading in the document.
   */
  public static XMLDocument from(Resource resource) throws IOException {
    XMLDocument doc = new XMLDocument();
    doc.read(resource);
    return doc;
  }

  private static Document newDocument() {
    try {
      return DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
    } catch (ParserConfigurationException e) {
      return null;
    }
  }

  /**
   * Instantiates a new empty XML document.
   */
  public XMLDocument() {
    this(newDocument());
  }

  /**
   * Instantiates a new XML document.
   *
   * @param dom the dom
   */
  public XMLDocument(Document dom) {
    this.document = Preconditions.checkNotNull(dom);
    setNode(this.document.getParentNode());
    setOwner(this);
  }

  @Override
  public void write(Resource resource) throws IOException {
    try {
      Preconditions.checkNotNull(resource).write(this.toString());
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void read(Resource resource) throws IOException {
    try {
      this.document = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(resource.inputStream());
      setNode(this.document.getDocumentElement());
      setOwner(this);
    } catch (Exception e) {
      throw new IOException(e);
    }
  }

  @Override
  public int hashCode() {
    return 31 * super.hashCode() + Objects.hash(document);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    if (!super.equals(obj)) {
      return false;
    }
    final XMLDocument other = (XMLDocument) obj;
    return Objects.equals(this.document, other.document);
  }


}//END OF XMLDocument
