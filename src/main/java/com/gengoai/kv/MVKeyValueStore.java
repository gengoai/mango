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

package com.gengoai.kv;

import com.gengoai.collection.Iterators;
import com.gengoai.conversion.Cast;
import com.gengoai.io.MonitoredObject;
import com.gengoai.io.ResourceMonitor;
import org.h2.mvstore.MVMap;

import java.io.File;
import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
class MVKeyValueStore<K, V> implements NavigableKeyValueStore<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final MonitoredObject<MVStoreHandle> handle;
   private final String namespace;
   private final boolean readOnly;
   private volatile transient MVMap<K, V> map;

   public MVKeyValueStore(File dbFile, String namespace, boolean compressed, boolean readOnly) {
      this.namespace = namespace;
      this.readOnly = readOnly;
      this.handle = ResourceMonitor.monitor(new MVStoreHandle(dbFile, compressed));
   }

   @Override
   public K ceilingKey(K key) {
      return delegate().ceilingKey(key);
   }

   @Override
   public void clear() {
      delegate().clear();
   }

   @Override
   public void close() throws Exception {
      handle.object.close();
   }

   @Override
   public void commit() {
      delegate().getStore().commit();
   }

   @Override
   public boolean containsKey(Object o) {
      return delegate().containsKey(o);
   }

   @Override
   public boolean containsValue(Object o) {
      return delegate().containsValue(o);
   }

   protected MVMap<K, V> delegate() {
      if (map == null) {
         synchronized (this) {
            if (map == null) {
               map = this.handle.object.getMap(namespace);
            }
         }
      }
      return map;
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      if (readOnly) {
         return Collections.unmodifiableSet(delegate().entrySet());
      }
      return delegate().entrySet();
   }

   @Override
   public K firstKey() {
      return delegate().firstKey();
   }

   @Override
   public K floorKey(K key) {
      return delegate().lowerKey(key);
   }

   @Override
   public V get(Object o) {
      return delegate().get(o);
   }

   @Override
   public String getNameSpace() {
      return namespace;
   }

   @Override
   public K higherKey(K key) {
      return delegate().higherKey(key);
   }

   @Override
   public boolean isEmpty() {
      return delegate().isEmpty();
   }

   @Override
   public boolean isReadOnly() {
      return readOnly;
   }

   @Override
   public Iterator<K> keyIterator(K key) {
      if (readOnly) {
         return Iterators.unmodifiableIterator(delegate().keyIterator(key));
      }
      return delegate().keyIterator(key);
   }

   @Override
   public Set<K> keySet() {
      if (readOnly) {
         return Collections.unmodifiableSet(delegate().keySet());
      }
      return delegate().keySet();
   }

   @Override
   public K lastKey() {
      return delegate().lastKey();
   }

   @Override
   public K lowerKey(K key) {
      return delegate().lowerKey(key);
   }

   @Override
   public V put(K k, V v) {
      if (readOnly) {
         throw new IllegalStateException("Cannot put to read-only KeyValue Store");
      }
      return delegate().put(k, v);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      if (readOnly) {
         throw new IllegalStateException("Cannot putAll to read-only KeyValue Store");
      }
      delegate().putAll(Cast.cast(map));
   }

   @Override
   public V remove(Object o) {
      if (readOnly) {
         throw new IllegalStateException("Cannot remove from read-only KeyValue Store");
      }
      return delegate().remove(o);
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public long sizeAsLong() {
      return delegate().sizeAsLong();
   }

   @Override
   public Collection<V> values() {
      if (readOnly) {
         return Collections.unmodifiableCollection(delegate().values());
      }
      return delegate().values();
   }

}//END OF MVKeyValueStore
