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

import com.davidbracewell.Copyable;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.reflection.Specification;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.io.Serializable;

/**
 * <p>A specification for constructing MapDB {@link DB}s. Allows reuse of the same DB if only the database name has
 * changed.</p>
 *
 * @author David B. Bracewell
 */
public final class MapDBSpec implements Specification, Copyable<MapDBSpec>, Serializable {
   private static final long serialVersionUID = 1L;
   private Resource file;
   private boolean inMemory = true;
   private boolean tempFile = false;
   private String dbName = "DATABASE";
   private transient DB database;
   private boolean isDirty = true;
   private boolean useCompression = false;
   private int cacheSize = 0;
   private boolean readOnly = false;


   /**
    * Default Constructor
    */
   public MapDBSpec() {

   }

   /**
    * Copy constructor
    *
    * @param other The MapDBSpec to copy
    */
   public MapDBSpec(MapDBSpec other) {
      this.file = Preconditions.checkNotNull(other).file;
      this.inMemory = other.inMemory;
      this.tempFile = other.tempFile;
      this.dbName = other.dbName;
      this.database = other.database;
      this.isDirty = other.isDirty;
      this.useCompression = other.useCompression;
      this.cacheSize = other.cacheSize;
      this.readOnly = other.readOnly;
   }

   @Override
   public MapDBSpec copy() {
      return new MapDBSpec(this);
   }

   /**
    * Marks the DB as read only
    *
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec readOnly() {
      this.readOnly = true;
      return this;
   }

   /**
    * Marks the DB as read and write
    *
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec readWrite() {
      this.readOnly = false;
      return this;
   }

   /**
    * Sets the database name
    *
    * @param name the name
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec databaseName(String name) {
      this.dbName = name;
      return this;
   }

   /**
    * Gets database name.
    *
    * @return the database name
    */
   public String getDatabaseName() {
      return dbName;
   }

   /**
    * Should MapDB use compression
    *
    * @return True use compression, False not
    */
   public boolean isUseCompression() {
      return useCompression;
   }

   /**
    * Sets the file backing the MapDB
    *
    * @param file the file
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec file(@NonNull Resource file) {
      this.file = file;
      inMemory = false;
      tempFile = false;
      isDirty = true;
      return this;
   }

   /**
    * Use compression.
    *
    * @param useCompression True use compression, False do not
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec useCompression(boolean useCompression) {
      this.useCompression = useCompression;
      isDirty = true;
      return this;
   }

   /**
    * Sets for creating of in-memory off-heap Db
    *
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec inMemory() {
      this.inMemory = true;
      this.file = null;
      this.tempFile = false;
      isDirty = true;
      return this;
   }

   /**
    * Sets creation of a temporary file based DB
    *
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec tempFile() {
      this.tempFile = true;
      this.inMemory = false;
      this.file = null;
      this.isDirty = true;
      return this;
   }

   /**
    * Sets the cache size of the DB.
    *
    * @param size the size
    * @return This instance of the MapDBSpec
    */
   public MapDBSpec cacheSize(int size) {
      this.cacheSize = size;
      this.isDirty = true;
      return this;
   }

   /**
    * Creates the DB. Will reuse a previously constructed instance if there is one and the only allows change, if any,
    * is to the database name.
    *
    * @return the MapDB DB
    */
   public DB createDB() {
      if (isDirty || database == null) {
         synchronized (this) {
            if (isDirty || database == null) {
               isDirty = false;
               DBMaker<?> maker;
               if (inMemory) {
                  maker = DBMaker.newMemoryDirectDB();
               } else if (tempFile) {
                  maker = DBMaker.newTempFileDB();
               } else {
                  maker = DBMaker.newFileDB(file.asFile()
                                                .orElseThrow(() ->
                                                                new RuntimeException(file.path() + " is not located on a local file system")));
               }
               maker = maker.closeOnJvmShutdown();
               if (useCompression) {
                  maker = maker.compressionEnable();
               }
               if (cacheSize > 0) {
                  maker = maker.cacheLRUEnable().cacheSize(cacheSize);
               }
               if (readOnly) {
                  maker = maker.readOnly();
               }
               maker = maker.transactionDisable();
               this.database = maker.make();
            }
         }
      }
      return database;
   }


}//END OF MapDBSpec
