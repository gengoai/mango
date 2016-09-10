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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredWriter;
import lombok.NonNull;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * <p>An index represents a mapping from an Item to an id. </p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public interface Index<E> extends Iterable<E>, Copyable<Index<E>> {

   /**
    * <p>Adds an item to the index. If the item is already in the index, the item's id is returned.</p>
    *
    * @param item the item to add
    * @return The index of the item
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
   int indexOf(E item);

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
    * Size int.
    *
    * @return The number of items in the index
    */
   int size();

   /**
    * Is empty.
    *
    * @return True if there are no items in the index
    */
   boolean isEmpty();

   /**
    * Removes the item with the given id from the index
    *
    * @param id The id to remove
    * @return A mapping of old to new ids
    */
   E remove(int id);

   /**
    * Removes the item from the index
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
    * As list.
    *
    * @return The index as an unmodifiable list.
    */
   List<E> asList();

   /**
    * Sets the item at a given index value. Implementations should throw runtime exceptions if the new value is already
    * in the index or the given index (int) is too large for the index.
    *
    * @param index    the index
    * @param newValue the new value
    * @return The old value
    */
   E set(int index, E newValue);

   /**
    * Stream stream.
    *
    * @return the stream
    */
   default Stream<E> stream() {
      return StreamSupport.stream(spliterator(), false);
   }

   /**
    * Parallel stream.
    *
    * @return the stream
    */
   default Stream<E> parallelStream() {
      return StreamSupport.stream(spliterator(), true);
   }

   /**
    * Write csv.
    *
    * @param output the output
    * @throws IOException the io exception
    */
   default void writeCSV(@NonNull Resource output) throws IOException {
      write(StructuredFormat.CSV, output);
   }

   /**
    * Write csv.
    *
    * @param output the output
    * @throws IOException the io exception
    */
   default void writeJson(@NonNull Resource output) throws IOException {
      write(StructuredFormat.JSON, output);
   }


   /**
    * Write.
    *
    * @param structuredFormat the structured format
    * @param output           the output
    * @throws IOException the io exception
    */
   default void write(@NonNull StructuredFormat structuredFormat, @NonNull Resource output) throws IOException {
      write(structuredFormat, output, item -> Convert.convert(item, String.class));
   }

   /**
    * Write.
    *
    * @param structuredFormat the structured format
    * @param output           the output
    * @param keySerializer    the key serializer
    * @throws IOException the io exception
    */
   default void write(@NonNull StructuredFormat structuredFormat, @NonNull Resource output, @NonNull Function<? super E, String> keySerializer) throws IOException {
      try (StructuredWriter writer = structuredFormat.createWriter(output)) {
         writer.beginDocument(true);
         for (E item : asList()) {
            writer.writeValue(keySerializer.apply(item));
         }
         writer.endDocument();
      }
   }


}//END OF Index
