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

package com.gengoai.collection;

import com.gengoai.Copyable;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonSerializable;

import java.lang.reflect.Type;
import java.util.List;
import java.util.stream.Stream;

import static com.gengoai.reflection.Types.getOrObject;

/**
 * <p>An index represents a mapping from an Item to an id. All ids are positive integers.</p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public interface Index<E> extends Iterable<E>, Copyable<Index<E>>, JsonSerializable {

   /**
    * From json index.
    *
    * @param <T>   the type parameter
    * @param entry the entry
    * @param types the types
    * @return the index
    */
   static <T> Index<T> fromJson(JsonEntry entry, Type... types) {
      return Indexes.indexOf(entry.getAsArray(getOrObject(0, types)));
   }

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
    * Retrieves an unmodifiable list view of the index
    *
    * @return an unmodifiable list view of the index
    */
   List<E> asList();

   /**
    * Clears the index
    */
   void clear();

   /**
    * Determines if an item is in the index
    *
    * @param item The item
    * @return True if the item is in the index, False if not
    */
   boolean contains(E item);

   /**
    * Gets the item with the given id.
    *
    * @param id The id
    * @return The item associated with the id or null if there is none.
    */
   E get(int id);

   /**
    * Gets the id of the item or -1 if it is not in the index.
    *
    * @param item The item whose id we want
    * @return The id of the item or -1 if it is not in the index
    */
   int getId(E item);

   /**
    * Determines if the index is empty
    *
    * @return True if there are no items in the index
    */
   boolean isEmpty();

   /**
    * The number of items in the index
    *
    * @return The number of items in the index
    */
   int size();


   @Override
   default JsonEntry toJson() {
      return JsonEntry.array(this);
   }


   /**
    * Stream stream.
    *
    * @return the stream
    */
   default Stream<E> stream() {
      return Streams.asStream(this);
   }

}//END OF Index
