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


import com.davidbracewell.io.resource.Resource;
import lombok.NonNull;

import java.io.IOException;
import java.util.function.Predicate;
import java.util.stream.Stream;

/**
 * <p>A structured document is an in-memory model of a structured resource, such as XML or JSON. It wraps the document
 * object model for the underlying type allowing a common interface for dealing data regardless of its original
 * format.</p>
 * <p>Structured Documents are modeled using a tree structure which is the normal representation of most underlying
 * types. A Structured Document is made up of a root which has many children. The "nodes" are represented as
 * Elements.</p>
 *
 * @author David B. Bracewell
 */
public interface StructuredDocument {

  /**
   * Writes the document.
   *
   * @param resource the resource to write the document to
   * @throws IOException something went wrong in writing the document.
   */
  void write(Resource resource) throws IOException;

  /**
   * Reads a document from a given resource overriding the document currently loaded.
   *
   * @param resource the resource to read from
   * @throws IOException something went wrong reading in the document.
   */
  void read(Resource resource) throws IOException;

  /**
   * Gets the root element of the document.
   *
   * @return the root
   */
  Element getRoot();

  /**
   * Select stream.
   *
   * @param predicate the predicate
   * @return the stream
   */
  default Stream<Element> select(@NonNull Predicate<Element> predicate) {
    return select(predicate, false);
  }

  /**
   * Select stream.
   *
   * @param predicate the predicate
   * @param recursive the recursive
   * @return the stream
   */
  default Stream<Element> select(@NonNull Predicate<Element> predicate, boolean recursive) {
    return getRoot().select(predicate, recursive);
  }

  /**
   * Select with attribute value.
   *
   * @param attributeName the attribute name
   * @param attributeValuePredicate the attribute value predicate
   * @return the stream
   */
  default Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate) {
    return selectWithAttributeValue(attributeName, attributeValuePredicate, false);
  }

  /**
   * Select with attribute value.
   *
   * @param attributeName the attribute name
   * @param attributeValuePredicate the attribute value predicate
   * @param recursive the recursive
   * @return the stream
   */
  default Stream<Element> selectWithAttributeValue(@NonNull String attributeName, @NonNull Predicate<? super String> attributeValuePredicate, boolean recursive) {
    return getRoot().selectWithAttributeValue(attributeName, attributeValuePredicate, recursive);
  }

  /**
   * Select with attribute.
   *
   * @param attributeName the attribute name
   * @return the stream
   */
  default Stream<Element> selectWithAttribute(@NonNull String attributeName) {
    return selectWithAttribute(attributeName, false);
  }

  /**
   * Select with attribute.
   *
   * @param attributeName the attribute name
   * @param recursive the recursive
   * @return the stream
   */
  default Stream<Element> selectWithAttribute(@NonNull String attributeName, boolean recursive) {
    return getRoot().stream(recursive).filter(e -> e.hasAttribute(attributeName));
  }

}//END OF StructuredDocument
