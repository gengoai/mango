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

package com.davidbracewell.io.structured;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.conversion.Val;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>An element represents a sub-tree of a document object model. It allows for selecting child elements
 * and getting its text, name, values, and attributes</p>
 *
 * @author David B. Bracewell
 */
public interface Element {

  default Val getFirstValueOfKey(String key) {
    return selectValuesForKey(key).stream().findFirst().orElse(Val.NULL);
  }

  default List<Val> selectValuesForKey(String key) {
    return select(element -> element.getName().equals(key)).map(Element::getValue).collect(Collectors.toList());
  }

  boolean isObject();

  boolean isArray();

  boolean isKeyValue();


  /**
   * Gets the value of a specific attribute on the element.
   *
   * @param attributeName the attribute name
   * @return the attribute value or null if the attribute does not exist
   */
  String getAttribute(String attributeName);

  /**
   * Gets a set of the attribute names.
   *
   * @return the attribute names associated with the element
   */
  Set<String> getAttributeNames();

  /**
   * Gets the children of the element.
   *
   * @return the children or an empty list if there are no children.
   */
  List<Element> getChildren();

  /**
   * Gets the document owning the element
   *
   * @return The document
   */
  StructuredDocument getDocument();

  /**
   * Gets the name of the element.
   *
   * @return the name
   */
  String getName();

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  Element getParent();

  /**
   * Gets the text of the element
   *
   * @return the text
   */
  Val getValue();

  /**
   * Determines if an attribute by the given name is contained in the element
   *
   * @param name the name
   * @return True if the element has the attribute, false if not
   */
  boolean hasAttribute(String name);

  /**
   * Determines if the element has children
   *
   * @return True if the element has children
   */
  boolean hasChildren();

  default Stream<Element> select(@NonNull Predicate<Element> predicate) {
    return select(predicate, false);
  }

  /**
   * Selects children matching the given predicate. When recursive is set to true, it will select over all descendants.
   *
   * @param predicate the predicate
   * @param recursive true if to select over all descendants, false immediate children
   * @return the iterable of the descendants matching the predicate
   */
  default Stream<Element> select(@NonNull Predicate<Element> predicate, boolean recursive) {
    return stream(recursive).filter(predicate);
  }

  default Stream<Element> selectWithAttribute(@NonNull String attributeName) {
    return selectWithAttribute(attributeName, false);
  }

  default Stream<Element> selectWithAttribute(@NonNull String attributeName, boolean recursive) {
    return stream(recursive).filter(e -> e.hasAttribute(attributeName));
  }

  default Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate) {
    return selectWithAttributeValue(attributeName, attributeValuePredicate, false);
  }

  default Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate, boolean recursive) {
    return stream(recursive).filter(e -> e.hasAttribute(attributeName))
      .filter(e -> attributeValuePredicate.test(e.getAttribute(attributeName)));
  }


  default Optional<Element> selectFirstByName(@NonNull String name) {
    return selectByName(name, false).findFirst();
  }


  default Optional<Element> selectFirstByName(@NonNull String name, boolean recursive) {
    return selectByName(name, recursive).findFirst();
  }

  default Stream<Element> selectByName(@NonNull String name) {
    return selectByName(name, false);
  }

  default Stream<Element> selectByName(@NonNull String name, boolean recursive) {
    return stream(recursive).filter(e -> e.getName().equals(name));
  }

  default Stream<Element> stream(boolean recursive) {
    if (recursive) {
      return Collect.from(new RecursiveElementIterator(this));
    }
    return getChildren().stream();
  }

  /**
   * The type Recursive element iterator.
   */
  class RecursiveElementIterator implements Iterator<Element> {
    Queue<Element> nodes = Lists.newLinkedList();

    private RecursiveElementIterator(Element element) {
      if (element instanceof StructuredDocument) {
        nodes.addAll(element.getChildren());
      } else {
        nodes.add(element);
      }
    }

    private void advance(Element node) {
      nodes.addAll(node.getChildren());
    }

    @Override
    public boolean hasNext() {
      return !nodes.isEmpty();
    }

    @Override
    public Element next() {
      Element node = nodes.remove();
      advance(node);
      return node;
    }

  }//END OF Element$RecursiveElementIterator

}//END OF StructuredFragment
