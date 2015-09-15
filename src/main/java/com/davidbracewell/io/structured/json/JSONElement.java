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

import com.davidbracewell.io.structured.Element;
import com.davidbracewell.io.structured.StructuredDocument;
import com.google.gson.JsonElement;

import java.util.*;
import java.util.stream.Collectors;

/**
 * The type JSON element.
 *
 * @author David B. Bracewell
 */
public class JSONElement extends Element {

  String name;
  JsonElement node;
  JSONDocument owner;
  JSONElement parent;

  JSONElement() {
  }

  JSONElement(String name, JsonElement node, JSONElement parent, JSONDocument owner) {
    this.name = name;
    this.node = node;
    this.parent = parent;
    this.owner = owner;
  }

  void setNode(JsonElement node) {
    this.node = node;
  }

  void setOwner(JSONDocument document) {
    this.owner = document;
  }

  @Override
  public String getValue() {
    if (node.isJsonPrimitive()) {
      return node.getAsString();
    }
    return null;
  }

  @Override
  public Element getParent() {
    return parent;
  }

  @Override
  public String getName() {
    return name;
  }

  void setName(String name) {
    this.name = name;
  }

  @Override
  public String getAttribute(String attributeName) {
    if (node.isJsonObject()) {
      JsonElement element = node.getAsJsonObject().get(attributeName);
      if (element.isJsonPrimitive()) {
        return element.getAsString();
      }
    }
    return null;
  }

  @Override
  public Set<String> getAttributeNames() {
    if (node.isJsonObject()) {
      return node.getAsJsonObject().entrySet().stream().map(Map.Entry::getKey).collect(Collectors.toSet());
    }
    return Collections.emptySet();
  }

  @Override
  public boolean hasAttribute(String name) {
    if (node.isJsonObject()) {
      return getAttributeNames().contains(name);
    }
    return false;
  }

  @Override
  public boolean hasChildren() {
    return (node.isJsonArray() && node.getAsJsonArray().size() > 0) ||
        (node.isJsonObject() && !node.getAsJsonObject().entrySet().isEmpty());
  }

  @Override
  public List<Element> getChildren() {
    if (node.isJsonObject()) {

      return node.getAsJsonObject().entrySet().stream()
          .map(entry -> new JSONElement(entry.getKey(), entry.getValue(), this, owner))
          .collect(Collectors.toList());

    } else if (node.isJsonArray()) {
      int i = 0;
      List<Element> children = new LinkedList<>();
      for (JsonElement element : node.getAsJsonArray()) {
        if (element.isJsonObject() || element.isJsonArray()) {
          children.add(new JSONElement(name + "[" + i + "]", element, this, owner));
          i++;
        }
      }
      return children;
    }

    return Collections.emptyList();
  }

  @Override
  public StructuredDocument getDocument() {
    return owner;
  }

  @Override
  public String toString() {
    return node.toString();
  }

}//END OF JSONElement
