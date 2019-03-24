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

import com.gengoai.stream.MPairStream;
import com.gengoai.stream.MStream;

import java.io.Serializable;

/**
 * An ObjectStore stores data (values) with an associated key allowing basic CRUD operations.
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public interface ObjectStore<K, V> extends Serializable, AutoCloseable {

   /**
    * Gets the keys of the ObjectStore as an MStream
    *
    * @return the MStream of keys
    */
   MStream<K> keys();

   /**
    * Gets the values of the ObjectStream as an MStream
    *
    * @return the MStream of values
    */
   MStream<V> values();

   /**
    * Gets the entries of the ObjectStream as an MPairStream
    *
    * @return the MPairStream of keys and values
    */
   MPairStream<K, V> entries();

   /**
    * Creates a new value in the object store with the associated value.
    *
    * @param key   the key
    * @param value the value
    */
   void put(K key, V value);

   /**
    * Gets the value for the key.
    *
    * @param key the key
    * @return the value
    */
   V get(K key);

   /**
    * Deletes the value for the associated key
    *
    * @param key the key to delete
    * @return the value associated with the key
    */
   V delete(K key);

   /**
    * The number of items in the store
    *
    * @return the number of items in the store
    */
   long size();

   /**
    * Clears the objects from the store
    */
   void clear();

   /**
    * Checks if the key is in the object store
    *
    * @param key the key
    * @return True - if the key is in the object store
    */
   boolean containsKey(K key);

   /**
    * Checks if the value is in the object store
    *
    * @param value the value
    * @return True - if the value is in the object store
    */
   boolean containsValue(V value);


   /**
    * Commits changes to the store.
    */
   void commit();

}//END OF ObjectStore
