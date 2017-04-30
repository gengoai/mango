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


import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.CSVReader;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.json.JsonReader;
import com.davidbracewell.json.Json;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;

import static com.davidbracewell.json.JsonTokenType.END_DOCUMENT;

/**
 * Common methods for reading counters from structured files, creating synchronized and unmodifiable wrappers.
 *
 * @author David B. Bracewell
 */
public interface Indexes {

   /**
    * Creates a new index using the given set of elements
    *
    * @param <TYPE>   the component type of the index
    * @param elements the elements to initialize the index with
    * @return A new index containing the given elements
    */
   @SafeVarargs
   static <TYPE> Index<TYPE> newIndex(TYPE... elements) {
      return elements == null ? new HashMapIndex<>() : newIndex(Arrays.asList(elements));
   }

   /**
    * Creates a new index using the given set of elements
    *
    * @param <TYPE>   the component type of the index
    * @param elements the elements to initialize the index with
    * @return A new index containing the given elements
    */
   static <TYPE> Index<TYPE> newIndex(@NonNull Iterable<TYPE> elements) {
      Index<TYPE> index = new HashMapIndex<>();
      index.addAll(elements);
      return index;
   }

   /**
    * <p>Wraps an index making each method call synchronized.</p>
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @param index  The index to wrap
    * @return the synchronized index
    */
   static <TYPE> Index<TYPE> synchronizedIndex(@NonNull Index<TYPE> index) {
      return new SynchronizedIndex<>(index);
   }

   /**
    * <p>Creates a new index with each method call synchronized.</p>
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @return the synchronized index
    */
   static <TYPE> Index<TYPE> synchronizedIndex() {
      return new SynchronizedIndex<>(new HashMapIndex<>());
   }


   /**
    * Wraps an index making its entries unmodifiable.
    *
    * @param <TYPE> the type parameter of the item being indexed.
    * @param index  The index to wrap
    * @return the unmodifiable index
    */
   static <TYPE> Index<TYPE> unmodifiableIndex(@NonNull final Index<TYPE> index) {
      return new UnmodifiableIndex<>(index);
   }

   /**
    * <p>Reads an Index from a CSV file.</p>
    *
    * @param <TYPE>   the component type of the Index
    * @param resource the resource containing the Index specification
    * @param keyClass the class of the index component type
    * @return An index containing the elements of the CSV file.
    * @throws IOException Something went wrong reading in the file
    */
   static <TYPE> Index<TYPE> readCsv(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
      Index<TYPE> index = newIndex();
      try (CSVReader reader = CSV.builder().reader(resource)) {
         reader.forEach(row -> {
            if (row.size() >= 1) {
               index.add(Convert.convert(row.get(0), keyClass));
            }
         });
      }
      return index;
   }

   /**
    * <p>Reads an Index from a Json file.</p>
    *
    * @param <TYPE>   the component type of the Index
    * @param resource the resource containing the Index specification
    * @param keyClass the class of the index component type
    * @return An index containing the elements of the Json file.
    * @throws IOException Something went wrong reading in the file
    */
   static <TYPE> Index<TYPE> readJson(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
      Index<TYPE> index = newIndex();
      try (JsonReader reader = Json.createReader(resource)) {
         reader.beginDocument();
         while (reader.peek() != END_DOCUMENT) {
            index.add(reader.nextValue().as(keyClass));
         }
         reader.endDocument();
      }
      return index;
   }


}//END OF Indexes
