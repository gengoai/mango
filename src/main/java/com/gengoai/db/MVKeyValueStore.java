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

import com.gengoai.conversion.Cast;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
class MVKeyValueStore<K, V> implements NavigableKeyValueStore<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final File dbFile;
   private final String namespace;
   private volatile transient MVMap<K, V> map;

   public MVKeyValueStore(File dbFile, String namespace, boolean compressed) {
      this.dbFile = dbFile;
      this.namespace = namespace;
      map = MVFactory.getMap(dbFile, namespace, compressed);
   }

   @Override
   public K higherKey(K key) {
      return map.higherKey(key);
   }

   @Override
   public K lowerKey(K key) {
      return map.lowerKey(key);
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public void close() throws Exception {
      MVFactory.close(dbFile);
   }

   @Override
   public void commit() {
      map.getStore().commit();
   }

   @Override
   public boolean containsKey(Object o) {
      return map.containsKey(o);
   }

   @Override
   public boolean containsValue(Object o) {
      return map.containsValue(o);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return map.entrySet();
   }

   @Override
   public K firstKey() {
      return map.firstKey();
   }

   @Override
   public V get(Object o) {
      return map.get(o);
   }

   @Override
   public String getNameSpace() {
      return namespace;
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Iterator<K> keyIterator(K key) {
      return map.keyIterator(key);
   }

   @Override
   public Set<K> keySet() {
      return map.keySet();
   }

   @Override
   public K lastKey() {
      return map.lastKey();
   }

   @Override
   public K floorKey(K key) {
      return map.lowerKey(key);
   }


   @Override
   public V put(K k, V v) {
      return map.put(k, v);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      map.putAll(Cast.cast(map));
   }

   private void readObject(ObjectInputStream stream) throws IOException, ClassNotFoundException {
      stream.defaultReadObject();
      this.map = MVStore.open(dbFile.getAbsolutePath()).openMap(namespace);
   }

   @Override
   public V remove(Object o) {
      return map.remove(o);
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public long sizeAsLong() {
      return map.sizeAsLong();
   }

   @Override
   public K ceilingKey(K key) {
      return map.ceilingKey(key);
   }

   @Override
   public Collection<V> values() {
      return map.values();
   }

}//END OF MVKeyValueStore
