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

package com.davidbracewell.collection;

import com.google.common.collect.HashMultimap;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * The type Inverted index.
 *
 * @param <DOCUMENT> the type parameter
 * @param <KEY>      the type parameter
 * @author David B. Bracewell
 */
public class InvertedIndex<DOCUMENT, KEY> implements Serializable {

  private static final long serialVersionUID = 1L;
  private final HashMultimap<KEY, Integer> index;
  private final List<DOCUMENT> documents;
  private final Function<? super DOCUMENT, Collection<KEY>> documentMapper;

  /**
   * Instantiates a new Inverted index.
   *
   * @param documentMapper the document mapper
   */
  public InvertedIndex(@NonNull Function<? super DOCUMENT, Collection<KEY>> documentMapper) {
    this.index = HashMultimap.create();
    this.documents = new ArrayList<>();
    this.documentMapper = documentMapper;
  }

  /**
   * Add void.
   *
   * @param doc the doc
   */
  public void add(DOCUMENT doc) {
    if (doc != null) {
      documents.add(doc);
      int id = documents.size() - 1;
      documentMapper.apply(doc).forEach(key -> index.put(key, id));
    }
  }

  /**
   * Add all.
   *
   * @param documents the documents
   */
  public void addAll(Iterable<? extends DOCUMENT> documents) {
    if (documents != null) {
      documents.forEach(this::add);
    }
  }

  /**
   * Query list.
   *
   * @param document the document
   * @param filter   the collector
   * @return the list
   */
  public List<DOCUMENT> query(DOCUMENT document, Predicate<DOCUMENT> filter) {
    if (document == null || filter == null) {
      return Collections.emptyList();
    }
    return query(documentMapper.apply(document), filter);
  }

  /**
   * Query list.
   *
   * @param document the document
   * @return the list
   */
  public List<DOCUMENT> query(DOCUMENT document) {
    return query(document, t -> true);
  }

  /**
   * Query list.
   *
   * @param keys   the keys
   * @param filter the collector
   * @return the list
   */
  public List<DOCUMENT> query(Collection<KEY> keys, Predicate<DOCUMENT> filter) {
    if (keys == null || filter == null) {
      return Collections.emptyList();
    }
    return keys.stream()
        .flatMap(key -> index.get(key).stream())
        .distinct()
        .map(documents::get)
        .filter(filter)
        .collect(Collectors.toList());
  }

  /**
   * Query list.
   *
   * @param keys the keys
   * @return the list
   */
  public List<DOCUMENT> query(Collection<KEY> keys) {
    return query(keys, d -> true);
  }


  /**
   * Gets documents.
   *
   * @return the documents
   */
  public List<DOCUMENT> getDocuments() {
    return Collections.unmodifiableList(documents);
  }

}//END OF InvertedIndex
