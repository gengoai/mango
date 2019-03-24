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
 *
 */

package com.gengoai.persistence;

import com.gengoai.stream.MStream;

import java.io.Serializable;

/**
 * A collection of persisted objects
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public interface PersistentCollection<E> extends AutoCloseable, Serializable {

   /**
    * Adds an element to the collection.
    *
    * @param element the element
    */
   void add(E element);

   /**
    * Updates the element at the given index
    *
    * @param index   the index
    * @param element the element
    */
   void update(long index, E element);

   /**
    * Removes the element at the given index
    *
    * @param index the index
    * @return the element being removed
    */
   E remove(long index);

   /**
    * Removes the given element from the collection.
    *
    * @param element the element
    */
   boolean remove(E element);

   /**
    * Gets the element at the given index.
    *
    * @param index the index
    * @return the element
    */
   E get(long index);

   /**
    * Creates a stream of the elements
    *
    * @return the MStream of elements
    */
   MStream<E> stream();

   /**
    * Checks if the element is contained in the collection
    *
    * @param element the element
    * @return True - if the element is in the collection
    */
   boolean contains(E element);

   /**
    * The number of elements in the collection
    *
    * @return the number of elements in the collection.
    */
   long size();

   /**
    * Commits changes to the store.
    */
   void commit();

}//END OF PersistentCollection
