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

import java.io.Serializable;
import java.util.*;

/**
 * The type Abstract navigable key value store.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public abstract class AbstractNavigableKeyValueStore<K, V> implements NavigableKeyValueStore<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final String namespace;
   private final boolean readOnly;

   /**
    * Instantiates a new Abstract navigable key value store.
    *
    * @param namespace the namespace
    * @param readOnly  the read only
    */
   protected AbstractNavigableKeyValueStore(String namespace, boolean readOnly) {
      this.namespace = namespace;
      this.readOnly = readOnly;
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
   public boolean containsKey(Object o) {
      return delegate().containsKey(o);
   }

   @Override
   public boolean containsValue(Object o) {
      return delegate().containsValue(o);
   }

   /**
    * Delegate navigable map.
    *
    * @return the navigable map
    */
   protected abstract NavigableMap<K, V> delegate();

   @Override
   public Set<Entry<K, V>> entrySet() {
      return delegate().entrySet();
   }

   @Override
   public K firstKey() {
      return delegate().firstKey();
   }

   @Override
   public K floorKey(K key) {
      return delegate().floorKey(key);
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
      return new Iterator<K>() {
         private K ck = ceilingKey(key);

         @Override
         public boolean hasNext() {
            return ck != null;
         }

         @Override
         public K next() {
            if (ck == null) {
               throw new NoSuchElementException();
            }
            K n = ck;
            ck = higherKey(n);
            return n;
         }
      };
   }

   @Override
   public Set<K> keySet() {
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
      return delegate().put(k, v);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> map) {
      delegate().putAll(map);
   }

   @Override
   public V remove(Object o) {
      return delegate().remove(o);
   }

   @Override
   public int size() {
      return delegate().size();
   }

   @Override
   public long sizeAsLong() {
      return size();
   }

   @Override
   public Collection<V> values() {
      return delegate().values();
   }
}//END OF InMemoryKeyValueStore
