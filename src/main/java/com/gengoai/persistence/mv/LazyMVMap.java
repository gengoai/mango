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

package com.gengoai.persistence.mv;

import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentMap;

/**
 * @author David B. Bracewell
 */
class LazyMVMap<K, V> implements ConcurrentMap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final File dbLocation;
   private final String mapName;
   private volatile transient MVMap<K, V> map = null;

   public LazyMVMap(File dbLocation, String mapName) {
      this.dbLocation = dbLocation;
      this.mapName = mapName;
   }

   public MVStore getStore() {
      return map().getStore();
   }

   public K ceilingKey(K key) {
      return map().ceilingKey(key);
   }

   @Override
   public void clear() {
      map().clear();
   }

   @Override
   public boolean containsKey(Object key) {
      return map().containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return map().containsValue(value);
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return map().entrySet();
   }

   public K firstKey() {
      return map().firstKey();
   }

   public K floorKey(K key) {
      return map().floorKey(key);
   }

   @Override
   public V get(Object key) {
      return map().get(key);
   }

   @Override
   public boolean isEmpty() {
      return map().isEmpty();
   }

   public Iterator<K> keyIterator(K key) {
      return map().keyIterator(key);
   }

   @Override
   public Set<K> keySet() {
      return map().keySet();
   }

   public K lastKey() {
      return map().lastKey();
   }

   private MVMap<K, V> map() {
      if (map == null) {
         synchronized (this) {
            if (map == null) {
               MVStore store = new MVStore.Builder()
                                  .fileName(dbLocation.getAbsolutePath())

                                  .compress()
                                  .open();
               map = store.openMap(mapName);
            }
         }
      }
      return map;
   }

   @Override
   public V put(K key, V value) {
      return map().put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      map().putAll(m);
   }

   @Override
   public V putIfAbsent(K key, V value) {
      return map().putIfAbsent(key, value);
   }

   @Override
   public V remove(Object key) {
      return map().remove(key);
   }

   @Override
   public boolean remove(Object key, Object value) {
      return map().remove(key, value);
   }

   @Override
   public boolean replace(K key, V oldValue, V newValue) {
      return map.replace(key, oldValue, newValue);
   }

   @Override
   public V replace(K key, V value) {
      return map().replace(key, value);
   }

   @Override
   public int size() {
      return map().size();
   }

   public long sizeAsLong() {
      return map().sizeAsLong();
   }

   @Override
   public Collection<V> values() {
      return map().values();
   }

}//END OF LazyMVMap
