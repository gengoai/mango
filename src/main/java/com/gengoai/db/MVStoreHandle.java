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

package com.gengoai.db;

import lombok.NonNull;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * The type Mv store handle.
 *
 * @author David B. Bracewell
 */
public class MVStoreHandle implements Serializable, AutoCloseable {
   private final boolean compressed;
   private final File file;
   private volatile transient MVStore store;

   /**
    * Instantiates a new Mv store handle.
    *
    * @param file       the file
    * @param compressed the compressed
    */
   public MVStoreHandle(@NonNull File file, boolean compressed) {
      this.file = file;
      this.compressed = compressed;
   }

   @Override
   public void close() throws Exception {
      MVStoreRegistry.close(file);
   }

   /**
    * Gets map.
    *
    * @param <K>       the type parameter
    * @param <V>       the type parameter
    * @param namespace the namespace
    * @return the map
    */
   public <K, V> MVMap<K, V> getMap(String namespace) {
      return getStore().openMap(namespace);
   }

   /**
    * Gets store.
    *
    * @return the store
    */
   protected MVStore getStore() {
      if (store == null) {
         synchronized (this) {
            if (store == null) {
               store = MVStoreRegistry.get(file, compressed);
            }
         }
      }
      return store;
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.store = MVStoreRegistry.get(file, compressed);
   }
}//END OF MVStoreHandle
