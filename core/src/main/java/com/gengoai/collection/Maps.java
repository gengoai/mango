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

package com.gengoai.collection;

import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * <p>Convenience methods for creating, reading, and manipulating maps.</p>
 *
 * @author David B. Bracewell
 */
public final class Maps {

   /**
    * Creates a map from an iterable of keys and a function that returns a value given the key.
    *
    * @param <K>         the key type parameter
    * @param <V>         the value type parameter
    * @param keys        the keys
    * @param valueMapper the function to use to generate values from keys
    * @return the map
    */
   public static <K, V> Map<K, V> asHashMap(Iterable<? extends K> keys, Function<? super K, ? extends V> valueMapper) {
      Map<K, V> map = new HashMap<>();
      keys.forEach(key -> map.put(key, valueMapper.apply(key)));
      return map;
   }

   /**
    * Creates an instance of the given map class.
    *
    * @param <K>   the key type
    * @param <V>   the value type
    * @param clazz the map class
    * @return An instance of the specified map class
    */
   public static <K, V> Map<K, V> create(Class<? extends Map> clazz) {
      if(clazz == Map.class || clazz == HashMap.class) {
         return new HashMap<>();
      } else if(clazz == LinkedHashMap.class) {
         return new LinkedHashMap<>();
      } else if(clazz == TreeMap.class || clazz == SortedMap.class) {
         return new TreeMap<>();
      } else if(clazz == ConcurrentMap.class || clazz == ConcurrentHashMap.class) {
         return new ConcurrentHashMap<>();
      }
      try {
         return Reflect.onClass(clazz).create().get();
      } catch(ReflectionException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Creates a <code>HashMap</code> from entries
    *
    * @param <K>     the key type parameter
    * @param <V>     the value type parameter
    * @param objects the entries
    * @return the map
    */
   @SafeVarargs
   public static <K, V> Map<K, V> hashMapOf(Map.Entry<? extends K, ? extends V>... objects) {
      return mapOf(HashMap::new, objects);
   }

   /**
    * Creates a map of type returned via the givne supplier from the given entries
    *
    * @param <K>      the key type parameter
    * @param <V>      the value type parameter
    * @param supplier the new map supplier
    * @param objects  the entries
    * @return the map
    */
   @SafeVarargs
   public static <K, V> Map<K, V> mapOf(Supplier<? extends Map<K, V>> supplier,
                                        Map.Entry<? extends K, ? extends V>... objects
                                       ) {
      Map<K, V> map = supplier.get();
      for(Map.Entry<? extends K, ? extends V> entry : objects) {
         map.put(entry.getKey(), entry.getValue());
      }
      return map;
   }

   /**
    * Puts all given entries into the given map
    *
    * @param <K>     the key type parameter
    * @param <V>     the value type parameter
    * @param map     the map to add the entries to
    * @param entries the entries to add
    */
   @SafeVarargs
   public static <K, V> Map<K, V> putAll(Map<K, V> map, Map.Entry<? extends K, ? extends V>... entries) {
      for(Map.Entry<? extends K, ? extends V> entry : entries) {
         map.put(entry.getKey(), entry.getValue());
      }
      return map;
   }

   /**
    * Sorts the entries in the map
    *
    * @param <K>        the key type parameter
    * @param <V>        the value type parameter
    * @param map        the map to sort
    * @param comparator The comparator to use when comparing entries.
    * @return the list of sorted map entries
    */
   public static <K, V> List<Map.Entry<K, V>> sortEntries(Map<K, V> map, Comparator<Map.Entry<K, V>> comparator) {
      return map.entrySet()
                .parallelStream()
                .sorted(comparator)
                .collect(Collectors.toList());
   }

   /**
    * Sorts the entries in the map by key
    *
    * @param <K>       the key type parameter
    * @param <V>       the value type parameter
    * @param map       the map to sort
    * @param ascending True sort in ascending order, False in descending order
    * @return the list of sorted map entries
    */
   public static <K extends Comparable<? super K>, V> List<Map.Entry<K, V>> sortEntriesByKey(Map<K, V> map,
                                                                                             boolean ascending) {
      final Comparator<Map.Entry<K, V>> comparator = ascending
                                                     ? Map.Entry.comparingByKey()
                                                     : Map.Entry.<K, V>comparingByKey().reversed();
      return sortEntries(map, comparator);
   }

   /**
    * Sorts the entries in the map by value
    *
    * @param <K>       the key type parameter
    * @param <V>       the value type parameter
    * @param map       the map to sort
    * @param ascending True sort in ascending order, False in descending order
    * @return the list of sorted map entries
    */
   public static <K, V extends Comparable<? super V>> List<Map.Entry<K, V>> sortEntriesByValue(Map<K, V> map,
                                                                                               boolean ascending) {
      final Comparator<Map.Entry<K, V>> comparator = ascending
                                                     ? Map.Entry.comparingByValue()
                                                     : Map.Entry.<K, V>comparingByValue().reversed();
      return sortEntries(map, comparator);
   }

   /**
    * Creates a <code>TreeMap</code> from entries
    *
    * @param <K>     the key type parameter
    * @param <V>     the value type parameter
    * @param objects the entries
    * @return the map
    */
   @SafeVarargs
   public static <K, V> Map<K, V> sortedMapOf(Map.Entry<? extends K, ? extends V>... objects) {
      return mapOf(TreeMap::new, objects);
   }

   public static <K> Iterator<K> tailKeyIterator(@NonNull final NavigableMap<K, ?> map, @NonNull K key) {
      return new Iterator<K>() {
         private K ck = map.ceilingKey(key);

         @Override
         public boolean hasNext() {
            return ck != null;
         }

         @Override
         public K next() {
            if(ck == null) {
               throw new NoSuchElementException();
            }
            K n = ck;
            ck = map.higherKey(n);
            return n;
         }
      };
   }

   private Maps() {
      throw new IllegalAccessError();
   }

}//END OF Maps
