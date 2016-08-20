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

import com.davidbracewell.collection.Streams;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.io.structured.csv.CSVWriter;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * The interface Maps.
 *
 * @author David B. Bracewell
 */
public interface Maps {

  /**
   * As map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param entries the entries
   * @return the map
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <K, V> Map<K, V> map(Map.Entry<K, V>... entries) {
    return createMap(HashMap::new, entries);
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1) {
    return createMap(HashMap::new, $(key1, value1));
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2) {
    return createMap(HashMap::new, $(key1, value1), $(key2, value2));
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3) {
    return createMap(HashMap::new, $(key1, value1), $(key2, value2), $(key3, value3));
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
    return createMap(HashMap::new, $(key1, value1), $(key2, value2), $(key3, value3), $(key4, value4));
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
    return createMap(HashMap::new, $(key1, value1), $(key2, value2), $(key3, value3), $(key4, value4), $(key5, value5));
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
    return createMap(HashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6)
    );
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
    return createMap(HashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7)
    );
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8) {
    return createMap(HashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8)
    );
  }

  /**
   * As map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @param key9   the key 9
   * @param value9 the value 9
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9) {
    return createMap(HashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9)
    );
  }

  /**
   * As map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param key1    the key 1
   * @param value1  the value 1
   * @param key2    the key 2
   * @param value2  the value 2
   * @param key3    the key 3
   * @param value3  the value 3
   * @param key4    the key 4
   * @param value4  the value 4
   * @param key5    the key 5
   * @param value5  the value 5
   * @param key6    the key 6
   * @param value6  the value 6
   * @param key7    the key 7
   * @param value7  the value 7
   * @param key8    the key 8
   * @param value8  the value 8
   * @param key9    the key 9
   * @param value9  the value 9
   * @param key10   the key 10
   * @param value10 the value 10
   * @return the map
   */
  static <K, V> Map<K, V> map(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9, K key10, V value10) {
    return createMap(HashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9),
                     $(key10, value10)
    );
  }

  static <K, V> void put(@NonNull Map<K, V> map, Map.Entry<K, V> entry) {
    if (entry != null) {
      map.put(entry.getKey(), entry.getValue());
    }
  }


  /**
   * As tree map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param entries the entries
   * @return the map
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <K, V> Map<K, V> treeMap(Map.Entry<K, V>... entries) {
    return createMap(TreeMap::new, entries);
  }


  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1) {
    return createMap(TreeMap::new, $(key1, value1));
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2) {
    return createMap(TreeMap::new, $(key1, value1), $(key2, value2));
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3) {
    return createMap(TreeMap::new, $(key1, value1), $(key2, value2), $(key3, value3));
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
    return createMap(TreeMap::new, $(key1, value1), $(key2, value2), $(key3, value3), $(key4, value4));
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
    return createMap(TreeMap::new, $(key1, value1), $(key2, value2), $(key3, value3), $(key4, value4), $(key5, value5));
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
    return createMap(TreeMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6)
    );
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
    return createMap(TreeMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7)
    );
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8) {
    return createMap(TreeMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8)
    );
  }

  /**
   * As tree map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @param key9   the key 9
   * @param value9 the value 9
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9) {
    return createMap(TreeMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9)
    );
  }

  /**
   * As tree map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param key1    the key 1
   * @param value1  the value 1
   * @param key2    the key 2
   * @param value2  the value 2
   * @param key3    the key 3
   * @param value3  the value 3
   * @param key4    the key 4
   * @param value4  the value 4
   * @param key5    the key 5
   * @param value5  the value 5
   * @param key6    the key 6
   * @param value6  the value 6
   * @param key7    the key 7
   * @param value7  the value 7
   * @param key8    the key 8
   * @param value8  the value 8
   * @param key9    the key 9
   * @param value9  the value 9
   * @param key10   the key 10
   * @param value10 the value 10
   * @return the map
   */
  static <K, V> Map<K, V> treeMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9, K key10, V value10) {
    return createMap(TreeMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9),
                     $(key10, value10)
    );
  }


  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1) {
    return createMap(LinkedHashMap::new, $(key1, value1));
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2) {
    return createMap(LinkedHashMap::new, $(key1, value1), $(key2, value2));
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3) {
    return createMap(LinkedHashMap::new, $(key1, value1), $(key2, value2), $(key3, value3));
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4) {
    return createMap(LinkedHashMap::new, $(key1, value1), $(key2, value2), $(key3, value3), $(key4, value4));
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5)
    );
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6)
    );
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7)
    );
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8)
    );
  }

  /**
   * As linked hash map map.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param key1   the key 1
   * @param value1 the value 1
   * @param key2   the key 2
   * @param value2 the value 2
   * @param key3   the key 3
   * @param value3 the value 3
   * @param key4   the key 4
   * @param value4 the value 4
   * @param key5   the key 5
   * @param value5 the value 5
   * @param key6   the key 6
   * @param value6 the value 6
   * @param key7   the key 7
   * @param value7 the value 7
   * @param key8   the key 8
   * @param value8 the value 8
   * @param key9   the key 9
   * @param value9 the value 9
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9)
    );
  }

  /**
   * As linked hash map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param key1    the key 1
   * @param value1  the value 1
   * @param key2    the key 2
   * @param value2  the value 2
   * @param key3    the key 3
   * @param value3  the value 3
   * @param key4    the key 4
   * @param value4  the value 4
   * @param key5    the key 5
   * @param value5  the value 5
   * @param key6    the key 6
   * @param value6  the value 6
   * @param key7    the key 7
   * @param value7  the value 7
   * @param key8    the key 8
   * @param value8  the value 8
   * @param key9    the key 9
   * @param value9  the value 9
   * @param key10   the key 10
   * @param value10 the value 10
   * @return the map
   */
  static <K, V> Map<K, V> linkedHashMap(K key1, V value1, K key2, V value2, K key3, V value3, K key4, V value4, K key5, V value5, K key6, V value6, K key7, V value7, K key8, V value8, K key9, V value9, K key10, V value10) {
    return createMap(LinkedHashMap::new,
                     $(key1, value1),
                     $(key2, value2),
                     $(key3, value3),
                     $(key4, value4),
                     $(key5, value5),
                     $(key6, value6),
                     $(key7, value7),
                     $(key8, value8),
                     $(key9, value9),
                     $(key10, value10)
    );
  }


  /**
   * As linked hash map map.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param entries the entries
   * @return the map
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <K, V> Map<K, V> linkedHashMap(Map.Entry<K, V>... entries) {
    return createMap(LinkedHashMap::new, entries);
  }


  /**
   * As map map.
   *
   * @param <K>         the type parameter
   * @param <V>         the type parameter
   * @param mapSupplier the map supplier
   * @param entries     the entries
   * @return the map
   */
  @SafeVarargs
  @SuppressWarnings("varargs")
  static <K, V> Map<K, V> createMap(@NonNull Supplier<Map<K, V>> mapSupplier, Map.Entry<K, V>... entries) {
    if (entries == null) {
      return Collections.emptyMap();
    }
    final Map<K, V> map = mapSupplier.get();
    Streams.asStream(entries).forEach(e -> map.put(e.getKey(), e.getValue()));
    return map;
  }


  /**
   * <p>Creates a HashMap from a string converting the keys and values using {@link Convert#getConverter(Class)}. Empty
   * or null  strings result in an empty Map. The string format should be in csv where the commas separate the
   * key-value
   * pairs. Keys and values are the separated using either <code>:</code> or <code>=</code> depending on which one is
   * present and appears first. </p>
   *
   * @param <K>        The key type
   * @param <V>        The value type
   * @param input      The input string
   * @param keyClass   The key class
   * @param valueClass The value class
   * @return The resulting map
   */
  static <K, V> Map<K, V> parseString(String input, @NonNull Class<K> keyClass, @NonNull Class<V> valueClass) {
    return parseString(input, Convert.getConverter(keyClass), Convert.getConverter(valueClass));
  }

  /**
   * <p>Creates a HashMap from a string converting the keys and values using the supplied functions. Empty or null
   * strings result in an empty Map. The string format should be in csv where the commas separate the key-value pairs.
   * Keys and values are the separated using either <code>:</code> or <code>=</code> depending on which one is present
   * and appears first. </p>
   *
   * @param <K>            The key type
   * @param <V>            The value type
   * @param input          The input string
   * @param keyConverter   The function to convert an object to the key type
   * @param valueConverter The function to convert an object to the value type
   * @return The resulting map
   */
  static <K, V> Map<K, V> parseString(String input, @NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter) {
    if (StringUtils.isNullOrBlank(input)) {
      return Collections.emptyMap();
    }
    String str = input.replaceFirst("^\\s*\\{", "").replaceFirst("}$\\s*", "");
    Map<K, V> map = new HashMap<>();

    try (CSVReader reader = CSV.builder().reader(new StringReader(str))) {
      reader.forEach(row ->
                       row.forEach(cell -> {
                         int ci = cell.indexOf(':');
                         int ei = cell.indexOf('=');
                         char delimiter = ei == -1 || (ci != -1 && ci < ei) ? ':' : '=';
                         List<String> keyValuePair = StringUtils.split(cell, delimiter);
                         String key = keyValuePair.size() > 0 ? keyValuePair.get(0) : null;
                         String value = keyValuePair.size() > 1 ? keyValuePair.get(1) : null;
                         map.put(keyConverter.apply(key), valueConverter.apply(value));
                       })
      );
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }

    return map;
  }

  /**
   * Read csv map.
   *
   * @param <K>            the type parameter
   * @param <V>            the type parameter
   * @param input          the input
   * @param keyConverter   the key converter
   * @param valueConverter the value converter
   * @return the map
   * @throws IOException the io exception
   */
  static <K, V> Map<K, V> readCSV(@NonNull Resource input, @NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter) throws IOException {
    Map<K, V> map = new HashMap<>();
    try (CSVReader reader = CSV.builder().reader(input)) {
      reader.forEach(row -> row.forEach(cell -> {
                       if (row.size() >= 2) {
                         map.put(keyConverter.apply(row.get(0)), valueConverter.apply(row.get(1)));
                       }
                     })
      );
    }
    return map;
  }

  /**
   * Write csv.
   *
   * @param <K>    the type parameter
   * @param <V>    the type parameter
   * @param map    the map
   * @param output the output
   * @throws IOException the io exception
   */
  static <K, V> void writeCSV(@NonNull Map<K, V> map, @NonNull Resource output) throws IOException {
    try (CSVWriter writer = CSV.builder().writer(output)) {
      for (Map.Entry<K, V> kvEntry : map.entrySet()) {
        writer.write(Convert.convert(kvEntry.getKey(), String.class),
                     Convert.convert(kvEntry.getValue(), String.class)
        );
      }
    }
  }

  /**
   * <p>Fills a map with an iterable converting the even elements of the iterable to the keys and the odd elements to
   * the values using the given key and value converters. A null or empty iterable results in an empty map. </p>
   *
   * @param <K>            The key type
   * @param <V>            The value type
   * @param map            The map to fill
   * @param iterable       The iterable to convert into a map
   * @param keyConverter   The converter to use for the keys (even elements)
   * @param valueConverter The converter to use for the values (odd elements)
   * @return The map.
   */
  static <K, V> Map<K, V> fillMap(@NonNull Map<K, V> map, Iterable<?> iterable, @NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter) {
    if (iterable == null) {
      return map;
    }
    for (Iterator<?> iterator = iterable.iterator(); iterator.hasNext(); ) {
      Object key = iterator.next();
      if (!iterator.hasNext()) {
        throw new IllegalArgumentException("Size of iterable must be divisible by 2");
      }
      Object value = iterator.next();
      map.put(keyConverter.apply(key), valueConverter.apply(value));
    }
    return map;
  }


  static <K, V, R> Map<R, V> transformKeys(@NonNull Map<? extends K, ? extends V> map, @NonNull Function<? super K, ? extends R> transform) {
    return map.entrySet().stream()
              .map(e -> $(transform.apply(e.getKey()), e.getValue()))
              .collect(Collectors.toMap(Tuple2::getKey,
                                        Tuple2::getValue,
                                        (v1, v2) -> v1,
                                        Unchecked.supplier(() -> Reflect.onClass(map.getClass()).create().get())
                       )
              );
  }


  static <K, V, R> Map<K, R> transformValues(@NonNull Map<? extends K, ? extends V> map, @NonNull Function<? super V, ? extends R> transform) {
    return map.entrySet().stream()
              .map(e -> $(e.getKey(), transform.apply(e.getValue())))
              .collect(Collectors.toMap(Tuple2::getKey,
                                        Tuple2::getValue,
                                        (v1, v2) -> v1,
                                        Unchecked.supplier(() -> Reflect.onClass(map.getClass()).create().get())
                       )
              );
  }


}//END OF Maps
