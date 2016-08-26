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

import com.davidbracewell.collection.Collect;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

/**
 * @author David B. Bracewell
 */
public interface Multimap<K, V> {

   Map<K, Collection<V>> asMap();

   Collection<V> get(Object key);

   boolean containsKey(Object key);

   default boolean containsEntry(Object key, Object value) {
      return get(key).contains(value);
   }

   boolean containsValue(Object value);

   void clear();

   Collection<Map.Entry<K, V>> entrySet();

   boolean isEmpty();

   int size();

   Set<K> keySet();

   Collection<V> values();

   default boolean put(K key, V value) {
      return get(key).add(value);
   }

   default boolean putAll(K key, Iterable<? extends V> values) {
      if (values == null) {
         return false;
      }
      return Collect.addAll(get(key), values);
   }

   default boolean putAll(Multimap<? extends K, ? extends V> multimap) {
      if (multimap == null) {
         return false;
      }
      boolean allAdded = true;
      for (K key : multimap.keySet()) {
         if (!putAll(key, multimap.get(key))) {
            allAdded = false;
         }
      }
      return allAdded;
   }

   default boolean remove(Object key, Object value) {
      return get(key).remove(value);
   }

   Collection<V> removeAll(Object key);

   Collection<V> replaceAll(K key, Iterable<? extends V> values);


   default void forEach(@NonNull BiConsumer<? super K, ? super V> consumer) {
      entrySet().forEach(e -> consumer.accept(e.getKey(), e.getValue()));
   }


}//END OF Multimap
