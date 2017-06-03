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

package com.davidbracewell.collection.index;

import com.davidbracewell.collection.Streams;
import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
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
   private final HashMultimap<KEY,Integer> index;
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
      this.documentMapper = Preconditions.checkNotNull(documentMapper);
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
         for (KEY key : documentMapper.apply(doc)) {
            index.put(key, id);
         }
      }
   }

   /**
    * Add all.
    *
    * @param documents the documents
    */
   public void addAll(Iterable<? extends DOCUMENT> documents) {
      if (documents != null) {
         for (DOCUMENT doc : documents) {
            add(doc);
         }
      }
   }

   /**
    * Query list.
    *
    * @param document the document
    * @param filter   the filter
    * @return the list
    */
   public Set<DOCUMENT> query(@NonNull DOCUMENT document, @NonNull Predicate<? super DOCUMENT> filter) {
      return query(documentMapper.apply(document), filter);
   }

   /**
    * Query list.
    *
    * @param document the document
    * @return the list
    */
   public Set<DOCUMENT> query(DOCUMENT document) {
      return query(document, d -> true);
   }

   /**
    * Query list.
    *
    * @param keys   the keys
    * @param filter the filter
    * @return the list
    */
   public Set<DOCUMENT> query(@NonNull Iterable<KEY> keys, @NonNull Predicate<? super DOCUMENT> filter) {
      return Streams.asParallelStream(keys)
                    .flatMap(key -> index.get(key).stream())
                    .map(documents::get)
                    .filter(filter)
                    .collect(Collectors.toSet());
   }

   /**
    * Query list.
    *
    * @param keys the keys
    * @return the list
    */
   public Set<DOCUMENT> query(Iterable<KEY> keys) {
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


   @SafeVarargs
   public final Set<DOCUMENT> query(@NonNull KEY... keys) {
      return query(Arrays.asList(keys), d -> true);
   }

   @SafeVarargs
   public final Set<DOCUMENT> query(@NonNull Predicate<? super DOCUMENT> filter, @NonNull KEY... keys) {
      return query(Arrays.asList(keys), filter);
   }


}//END OF InvertedIndex
