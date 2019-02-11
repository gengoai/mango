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

package com.gengoai.collection;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.*;
import java.util.function.BiPredicate;

/**
 * @author David B. Bracewell
 */
public class DiskMap<K, V> implements Map<K, V>, Serializable, AutoCloseable {
   public static final String DEFAULT_MAP_NAME = "DEFAULT_MAP";
   private static final long serialVersionUID = 1L;
   private final File dbFile;
   private final String mapName;
   private volatile transient MVMap<K, V> map;


   public DiskMap(File dbFile) {
      this(dbFile, DEFAULT_MAP_NAME);
   }

   public DiskMap(File dbFile, String mapName) {
      this.dbFile = dbFile;
      this.mapName = mapName;
      this.map = MVStore.open(dbFile.getAbsolutePath()).openMap(mapName);
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public void close() throws Exception {
      commit();
      this.map.getStore().closeImmediately();
   }

   public void commit() {
      this.map.getStore().commit();
   }

   public void compact() {
      map.getStore().compactRewriteFully();
   }

   @Override
   public boolean containsKey(Object o) {
      return map.containsKey(o);
   }

   @Override
   public boolean containsValue(Object o) {
      return map.containsKey(o);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return map.entrySet();
   }

   public K firstKey() {
      return map.firstKey();
   }

   @Override
   public V get(Object o) {
      return map.get(o);
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   public Iterator<K> keyIterator(K key) {
      return map.keyIterator(key);
   }

   @Override
   public Set<K> keySet() {
      return map.keySet();
   }

   public K lastKey() {
      return map.lastKey();
   }

   public K lowerKey(K key) {
      return map.lowerKey(key);
   }

   public Iterator<K> prefixIterator(K prefix, BiPredicate<K, K> isPrefix) {
      K startKey = map.ceilingKey(prefix);
      if (startKey == null) {
         return Collections.emptyIterator();
      }
      return new Iterator<K>() {
         private final Iterator<K> backing = map.keyIterator(startKey);
         private K nextKey = null;

         private boolean advance() {
            if (nextKey != null) {
               return true;
            }
            if (backing.hasNext()) {
               nextKey = backing.next();
               if (isPrefix.test(nextKey, prefix)) {
                  return true;
               }
               nextKey = null;
            }
            return false;
         }

         @Override
         public boolean hasNext() {
            return advance();
         }

         @Override
         public K next() {
            advance();
            K n = nextKey;
            nextKey = null;
            return n;
         }
      };
   }

   @Override
   public V put(K k, V v) {
      return map.put(k, v);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      this.map.putAll(map);
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.map = MVStore.open(dbFile.getAbsolutePath()).openMap(mapName);
   }

   public V replace(K key, V value) {
      return map.replace(key, value);
   }

   @Override
   public V remove(Object o) {
      return map.remove(o);
   }

   @Override
   public int size() {
      return map.size();
   }

   public long sizeAsLong() {
      return map.sizeAsLong();
   }

   public K upperKey(K key) {
      return map.ceilingKey(key);
   }

   @Override
   public Collection<V> values() {
      return map.values();
   }

}//END OF DiskMap
