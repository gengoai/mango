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

import com.davidbracewell.Copyable;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.CSVWriter;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.json.JsonWriter;
import com.davidbracewell.json.Json;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>An index represents a mapping from an Item to an id. All ids are positive integers.</p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public interface Index<E> extends Iterable<E>, Copyable<Index<E>> {

   /**
    * <p>Adds an item to the index. If the item is already in the index, the item's id is returned otherwise the newly
    * generated id is returned.</p>
    *
    * @param item the item to add
    * @return The id of the item
    */
   int add(E item);

   /**
    * <p>Adds all the items in the iterable to the index</p>
    *
    * @param items The items to add.
    */
   void addAll(Iterable<E> items);

   /**
    * Gets the id of the item or -1 if it is not in the index.
    *
    * @param item The item whose id we want
    * @return The id of the item or -1 if it is not in the index
    */
   int getId(E item);

   /**
    * Gets the item with the given id.
    *
    * @param id The id
    * @return The item associated with the id or null if there is none.
    */
   E get(int id);

   /**
    * Clears the index
    */
   void clear();

   /**
    * The number of items in the index
    *
    * @return The number of items in the index
    */
   int size();

   /**
    * Determines if the index is empty
    *
    * @return True if there are no items in the index
    */
   boolean isEmpty();

   /**
    * Removes the item with the given id from the index. Note that his operation is expensive and can cause other ids to
    * change.
    *
    * @param id The id to remove
    * @return A mapping of old to new ids
    */
   E remove(int id);

   /**
    * Removes the item from the index. Note that his operation is expensive and can cause other ids to
    * change.
    *
    * @param item The item to remove
    * @return A mapping of old to new ids
    */
   int remove(E item);

   /**
    * Determines if an item is in the index
    *
    * @param item The item
    * @return True if the item is in the index, False if not
    */
   boolean contains(E item);

   /**
    * Creates a stream over the items in the index
    *
    * @return the stream of items
    */
   default Stream<E> stream() {
      return StreamSupport.stream(spliterator(), false);
   }

   /**
    * Creates a parallel stream over the items in the index
    *
    * @return the stream of items
    */
   default Stream<E> parallelStream() {
      return StreamSupport.stream(spliterator(), true);
   }

   /**
    * Retrieves an unmodifiable list view of the index
    *
    * @return an unmodifiable list view of the index
    */
   List<E> asList();

   /**
    * Writes the index to a csv file.
    *
    * @param output the resource to write to
    * @throws IOException Something went wrong writing to the output
    */
   default void writeCSV(@NonNull Resource output) throws IOException {
      try (CSVWriter writer = CSV.builder().writer(output)) {
         for (E item : this) {
            writer.write(Convert.convert(item, String.class));
         }
      }
   }

   /**
    * Writes the index to a json file.
    *
    * @param output the resource to write to
    * @throws IOException Something went wrong writing to the output
    */
   default void writeJson(@NonNull Resource output) throws IOException {
      try (JsonWriter writer = Json.createWriter(output)) {
         writer.beginDocument(true);
         for (E item : this) {
            writer.value(Convert.convert(item, String.class));
         }
         writer.endDocument();
      }
   }


}//END OF Index
