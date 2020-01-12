/*
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

package com.gengoai.collection.disk;

import lombok.EqualsAndHashCode;
import lombok.NonNull;
import org.mapdb.Atomic;
import org.mapdb.DB;

import java.io.File;
import java.io.Serializable;

/**
 * The type Map db handle.
 */
@EqualsAndHashCode(exclude = "store")
public final class MapDBHandle implements Serializable, AutoCloseable {
   private static final long serialVersionUID = 1L;
   private final boolean compressed;
   private final File file;
   private volatile transient DB store;

   /**
    * Instantiates a new MapDBHandle.
    *
    * @param file       the file containing the MapDB
    * @param compressed True if compression is used
    */
   public MapDBHandle(@NonNull File file, boolean compressed) {
      this.file = file;
      this.compressed = compressed;
   }

   @Override
   public void close() throws Exception {
      MapDBRegistry.close(file);
   }

   /**
    * Commits changes made to the database
    */
   public void commit() {
      getStore().commit();
      getStore().compact();
   }

   /**
    * Deletes the database files.
    */
   public void delete() {
      String path = file.getAbsolutePath();
      for (File f : new File[]{file, new File(path + ".p"), new File(path + ".t")}) {
         if (f.exists()) {
            f.delete();
         }
      }
   }

   public Atomic.Boolean getBoolean(String name) {
      return getStore().getAtomicBoolean(name);
   }

   public Atomic.Integer getInteger(String name) {
      return getStore().getAtomicInteger(name);
   }

   public Atomic.Long getLong(String name) {
      return getStore().getAtomicLong(name);
   }

   /**
    * Gets the database store object
    *
    * @return the store
    */
   protected DB getStore() {
      if (store == null) {
         synchronized (this) {
            if (store == null) {
               store = MapDBRegistry.get(file, compressed);
            }
         }
      }
      return store;
   }

   public Atomic.String getString(String name) {
      return getStore().getAtomicString(name);
   }

   public <E> Atomic.Var<E> getVar(String name) {
      return getStore().getAtomicVar(name);
   }
}//END OF MapDBHandle
