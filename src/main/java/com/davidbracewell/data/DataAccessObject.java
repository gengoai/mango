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

package com.davidbracewell.data;

import java.io.IOException;
import java.util.List;

/**
 * Interface providing basic CRUD (Create, Read, Update, and Delete) operations for objects backed by various data
 * stores, e.g. SQL
 *
 * @author David B. Bracewell
 */
public interface DataAccessObject<PRIMARY_KEY, OBJECT_TYPE> {

  /**
   * Puts an object into the data store
   *
   * @param object the object to insert
   * @return the primary key of the object
   */
  PRIMARY_KEY put(OBJECT_TYPE object) throws IOException;

  /**
   * Puts all objects in the data store
   *
   * @param objects the objects
   * @return the list of primary of keys
   */
  List<PRIMARY_KEY> putAll(Iterable<? extends OBJECT_TYPE> objects) throws IOException;

  /**
   * Gets an object by primary key
   *
   * @param primaryKey the primary key
   * @return the object or null if none matches the primary key
   */
  OBJECT_TYPE get(PRIMARY_KEY primaryKey) throws IOException;

  /**
   * Gets all objects associated with the primary keys.
   *
   * @param primaryKeys the primary keys
   * @return All objects for the given primary keys
   */
  List<OBJECT_TYPE> getAll(Iterable<? extends PRIMARY_KEY> primaryKeys) throws IOException;

  /**
   * Updates an object.
   *
   * @param primaryKey the primary key
   * @param object     the object
   */
  void update(PRIMARY_KEY primaryKey, OBJECT_TYPE object) throws IOException;

  /**
   * Removes an object
   *
   * @param primaryKey the primary key
   * @return the object that was removed
   */
  OBJECT_TYPE remove(PRIMARY_KEY primaryKey) throws IOException;

}//END OF DataAccessObject
