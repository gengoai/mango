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

import com.davidbracewell.collection.Streams;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>An element represents a sub-tree of a document object model. It allows for selecting child elements
 * and getting its text, name, values, and attributes</p>
 *
 * @author David B. Bracewell
 */
public abstract class Element {


  /**
   * Gets the value of a specific attribute on the element.
   *
   * @param attributeName the attribute name
   * @return the attribute value or null if the attribute does not exist
   */
  public abstract String getAttribute(String attributeName);

  /**
   * Gets a set of the attribute names.
   *
   * @return the attribute names associated with the element
   */
  public abstract Set<String> getAttributeNames();

  /**
   * Gets the children of the element.
   *
   * @return the children or an empty list if there are no children.
   */
  public abstract List<Element> getChildren();

  /**
   * Gets the document owning the element
   *
   * @return The document
   */
  public abstract StructuredDocument getDocument();

  /**
   * Gets the name of the element.
   *
   * @return the name
   */
  public abstract String getName();

  /**
   * Gets the parent.
   *
   * @return the parent
   */
  public abstract Element getParent();

  /**
   * Gets the text of the element
   *
   * @return the text
   */
  public abstract String getValue();

  /**
   * Determines if an attribute by the given name is contained in the element
   *
   * @param name the name
   * @return True if the element has the attribute, false if not
   */
  public abstract boolean hasAttribute(String name);

  /**
   * Determines if the element has children
   *
   * @return True if the element has children
   */
  public abstract boolean hasChildren();

  public final Stream<Element> select(@NonNull Predicate<Element> predicate) {
    return select(predicate, false);
  }

  /**
   * Selects children matching the given predicate. When recursive is set to true, it will select over all descendants.
   *
   * @param predicate the predicate
   * @param recursive true if to select over all descendants, false immediate children
   * @return the iterable of the descendants matching the predicate
   */
  public final Stream<Element> select(@NonNull Predicate<Element> predicate, boolean recursive) {
    return stream(recursive).filter(predicate);
  }

  public final Stream<Element> selectWithAttribute(@NonNull String attributeName) {
    return selectWithAttribute(attributeName, false);
  }

  public final Stream<Element> selectWithAttribute(@NonNull String attributeName, boolean recursive) {
    return stream(recursive).filter(e -> e.hasAttribute(attributeName));
  }

  public final Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate) {
    return selectWithAttributeValue(attributeName, attributeValuePredicate, false);
  }

  public final Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate, boolean recursive) {
    return stream(recursive).filter(e -> e.hasAttribute(attributeName))
        .filter(e -> attributeValuePredicate.test(e.getAttribute(attributeName)));
  }

  public final Stream<Element> stream(boolean recursive) {
    if (recursive) {
      return Streams.from(new RecursiveElementIterator());
    }
    return getChildren().stream();
  }

  /**
   * The type Recursive element iterator.
   */
  protected class RecursiveElementIterator implements Iterator<Element> {
    Queue<Element> nodes = Lists.newLinkedList();

    private RecursiveElementIterator() {
      nodes.add(Element.this);
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

    @Override
    public void remove() {
      throw new UnsupportedOperationException();
    }

  }//END OF Element$RecursiveElementIterator

}//END OF StructuredFragment
