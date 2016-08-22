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

package com.davidbracewell.collection.map;

import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public class HashBiMap<K, V> implements BiMap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final HashMap<K, V> keyValue;
   private final HashMap<V, K> valueKey;

   public HashBiMap() {
      this(new HashMap<>(), new HashMap<>());
   }

   private HashBiMap(HashMap<K, V> keyValue, HashMap<V, K> valueKey) {
      this.keyValue = keyValue;
      this.valueKey = valueKey;
   }

   @Override
   public K getKey(V value) {
      return valueKey.get(value);
   }

   @Override
   public int size() {
      return keyValue.size();
   }

   @Override
   public boolean isEmpty() {
      return keyValue.isEmpty();
   }

   @Override
   public boolean containsKey(Object key) {
      return keyValue.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return valueKey.containsKey(value);
   }

   @Override
   public V get(Object key) {
      return keyValue.get(key);
   }

   @Override
   public V put(K key, V value) {
      K oldKey = valueKey.put(value, key);
      V oldValue = keyValue.put(key, value);

      if (oldKey != null && !oldKey.equals(key)) {
         keyValue.remove(oldKey);
      }
      if (oldValue != null && !oldValue.equals(value)) {
         valueKey.remove(oldKey);
      }

      return oldValue;
   }

   @Override
   public V remove(Object key) {
      V value = keyValue.remove(key);
      valueKey.remove(value);
      return value;
   }

   @Override
   public K removeValue(Object value) {
      K key = valueKey.remove(value);
      keyValue.remove(key);
      return key;
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> m) {
      m.forEach(this::put);
   }

   @Override
   public void clear() {
      keyValue.clear();
      valueKey.clear();
   }

   @Override
   public Set<K> keySet() {
      return new AbstractSet<K>() {
         @Override
         public Iterator<K> iterator() {
            return new Iterator<K>() {
               private Iterator<K> itr = keyValue.keySet().iterator();
               private K key = null;

               @Override
               public boolean hasNext() {
                  return itr.hasNext();
               }

               @Override
               public K next() {
                  key = itr.next();
                  return key;
               }

               @Override
               public void remove() {
                  HashBiMap.this.remove(key);
               }
            };
         }

         @Override
         public int size() {
            return keyValue.size();
         }
      };
   }

   @Override
   public Collection<V> values() {
      return new AbstractSet<V>() {
         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               private Iterator<V> itr = valueKey.keySet().iterator();
               private V key = null;

               @Override
               public boolean hasNext() {
                  return itr.hasNext();
               }

               @Override
               public V next() {
                  key = itr.next();
                  return key;
               }

               @Override
               public void remove() {
                  HashBiMap.this.removeValue(key);
               }
            };
         }

         @Override
         public int size() {
            return keyValue.size();
         }
      };
   }

   @Override
   public Set<Entry<K, V>> entrySet() {
      return new AbstractSet<Entry<K, V>>() {
         @Override
         public Iterator<Entry<K, V>> iterator() {
            return new Iterator<Entry<K, V>>() {
               private Iterator<Entry<K, V>> itr = keyValue.entrySet().iterator();
               private Entry<K, V> entry = null;

               @Override
               public boolean hasNext() {
                  return itr.hasNext();
               }

               @Override
               public Entry<K, V> next() {
                  entry = itr.next();
                  return entry;
               }

               @Override
               public void remove() {
                  HashBiMap.this.remove(entry.getKey());
               }
            };
         }

         @Override
         public int size() {
            return keyValue.size();
         }
      };
   }

   @Override
   public String toString() {
      return keyValue.toString();
   }

}//END OF HashBiMap
