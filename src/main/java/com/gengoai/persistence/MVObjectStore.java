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
import com.gengoai.stream.StreamingContext;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;

import static com.gengoai.tuple.Tuples.$;

/**
 * An {@link ObjectStore} that uses an MVMap to store objects.
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public class MVObjectStore<K, V> implements ObjectStore<K, V> {
   private static final long serialVersionUID = 1L;
   private volatile transient MVMap<K, V> map;
   private final File dbFile;

   /**
    * Instantiates a new MVObjectStore.
    *
    * @param db the db file
    */
   public MVObjectStore(File db) {
      this.dbFile = db;
   }

   @Override
   public void close() throws Exception {
      if (map != null) {
         map.getStore().commit();
         map.getStore().closeImmediately();
      }
   }

   private MVMap<K, V> map() {
      if (map == null) {
         synchronized (dbFile) {
            if (map == null) {
               map = new MVStore.Builder()
                        .fileName(dbFile.getAbsolutePath())
                        .open()
                        .openMap("MVObjectStore");
            }
         }
      }
      return map;
   }

   @Override
   public MStream<K> keys() {
      return StreamingContext.local()
                             .stream(map().keyIterator(map().firstKey()));
   }

   @Override
   public MStream<V> values() {
      return keys().map(k -> map().get(k));
   }

   @Override
   public MPairStream<K, V> entries() {
      return keys().mapToPair(k -> $(k, map().get(k)));
   }

   @Override
   public void put(K key, V value) {
      map().put(key, value);
   }

   @Override
   public V get(K key) {
      return map().get(key);
   }

   @Override
   public V delete(K key) {
      return map().remove(key);
   }

   @Override
   public long size() {
      return map.sizeAsLong();
   }


   @Override
   public void clear() {
      map().clear();
   }

   @Override
   public boolean containsKey(K key) {
      return map().containsKey(key);
   }

   @Override
   public boolean containsValue(V value) {
      return map().containsValue(value);
   }

   public K lastKey() {
      return map().lastKey();
   }

   @Override
   public void commit() {
      map().getStore().commit();
   }
}//END OF MVObjectStore
