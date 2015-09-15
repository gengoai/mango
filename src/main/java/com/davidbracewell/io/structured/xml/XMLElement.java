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

import com.davidbracewell.io.structured.Element;
import com.davidbracewell.io.structured.StructuredDocument;
import com.davidbracewell.string.StringUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * An implementation of an Element wrapping an Node in an XML DOM.
 *
 * @author David B. Bracewell
 */
public class XMLElement extends Element {

  private Node node;
  private XMLDocument owner;

  XMLElement() {

  }

  XMLElement(Node node, XMLDocument owner) {
    this.node = node;
    this.owner = owner;
  }

  void setNode(Node node) {
    this.node = node;
  }

  void setOwner(XMLDocument owner) {
    this.owner = owner;
  }

  @Override
  public String getValue() {
    return node.getTextContent().trim();
  }

  @Override
  public String getName() {
    return node.getNodeName();
  }

  @Override
  public String getAttribute(String attributeName) {
    if (node.hasAttributes()) {
      Node attributeNode = node.getAttributes().getNamedItem(attributeName);
      return attributeNode == null ? null : attributeNode.getNodeValue();
    }
    return null;
  }

  @Override
  public Set<String> getAttributeNames() {
    Set<String> names = Sets.newHashSet();
    if (node.hasAttributes()) {
      NamedNodeMap map = node.getAttributes();
      for (int i = 0; i < map.getLength(); i++) {
        names.add(map.item(i).getNodeName());
      }
    }
    return names;
  }

  @Override
  public boolean hasAttribute(String name) {
    return node.hasAttributes() && node.getAttributes().getNamedItem(name) != null;
  }

  @Override
  public List<Element> getChildren() {
    List<Element> children = Lists.newArrayList();
    NodeList nodeList = node.getChildNodes();
    for (int i = 0; i < nodeList.getLength(); i++) {
      if (nodeList.item(i).getNodeType() == Node.ELEMENT_NODE) {
        children.add(new XMLElement(nodeList.item(i), owner));
      }
    }
    return children;
  }

  @Override
  public boolean hasChildren() {
    return node.hasChildNodes();
  }

  @Override
  public int hashCode() {
    return Objects.hash(node);
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) {
      return true;
    }
    if (obj == null || getClass() != obj.getClass()) {
      return false;
    }
    final XMLElement other = (XMLElement) obj;
    return Objects.equals(this.node, other.node);
  }

  @Override
  public Element getParent() {
    return node.getParentNode() == null ? null : new XMLElement(node.getParentNode(), owner);
  }

  @Override
  public String toString() {
    if (node == null) {
      return StringUtils.EMPTY;
    }
    try {
      StringWriter writer = new StringWriter();
      StreamResult result = new StreamResult(writer);
      Transformer transformer;
      transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
      if (!(this instanceof XMLDocument)) {
        transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
      }
      transformer.transform(new DOMSource(node), result);
      return writer.toString();
    } catch (Exception e) {
      return StringUtils.EMPTY;
    }
  }

  @Override
  public StructuredDocument getDocument() {
    return owner;
  }

}//END OF XMLStructuredFragment
