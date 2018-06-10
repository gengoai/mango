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

import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Convert;
import com.gengoai.io.CSV;
import com.gengoai.io.CSVReader;
import com.gengoai.io.CSVWriter;
import com.gengoai.io.resource.Resource;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import com.gengoai.string.StringUtils;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.function.Supplier;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.tuple.Tuples.$;

/**
 * <p>Convenience methods for creating, reading, and manipulating maps.</p>
 *
 * @author David B. Bracewell
 */
public final class Maps {

   private Maps() {
      throw new IllegalAccessError();
   }

   public static <K, V> Map<K, V> asMap(Iterable<? extends K> keys, Function<? super K, ? extends V> valueMapper) {
      Map<K, V> map = new HashMap<>();
      keys.forEach(key -> map.put(key, valueMapper.apply(key)));
      return map;
   }

   public static <K, V> Builder<K, V> builder() {
      return new Builder<>();
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
      if (clazz == Map.class || clazz == HashMap.class) {
         return new HashMap<>();
      } else if (clazz == LinkedHashMap.class) {
         return new LinkedHashMap<>();
      } else if (clazz == TreeMap.class) {
         return new TreeMap<>();
      } else if (clazz == ConcurrentMap.class || clazz == ConcurrentHashMap.class) {
         return new ConcurrentHashMap<>();
      }
      try {
         return Reflect.onClass(clazz).create().get();
      } catch (ReflectionException e) {
         throw new RuntimeException(e);
      }
   }

   @SafeVarargs
   public static <K, V> Map<K, V> hashMapOf(Map.Entry<? extends K, ? extends V>... objects) {
      return mapOf(HashMap::new, objects);
   }

   public static <K, V> Map<K, V> hashMapOf(Object... objects) {
      return mapOf(HashMap::new, objects);
   }

   public static void main(String[] args) throws Exception {
      Map<String, String> m = mapOf(HashMap::new,
                                    $("A", "B"),
                                    $("C", "D"),
                                    $("E", "F")
                                   );
      System.out.println(m);
      putAll(m, "A", "E", "Z", "H", "LL", "<<");
      System.out.println(m);
      System.out.println(Maps.maxEntry(m));

   }

   @SafeVarargs
   public static <K, V> Map<K, V> mapOf(Supplier<? extends Map<K, V>> supplier, Map.Entry<? extends K, ? extends V>... objects) {
      Map<K, V> map = supplier.get();
      for (Map.Entry<? extends K, ? extends V> entry : objects) {
         map.put(entry.getKey(), entry.getValue());
      }
      return map;
   }

   public static <K, V> Map<K, V> mapOf(Supplier<? extends Map<K, V>> supplier, Object... objects) {
      checkArgument(objects.length % 2 == 0, "Must have an equal number of values and keys");
      Map<K, V> map = supplier.get();
      for (int i = 0; i < objects.length; i += 2) {
         map.put(Cast.as(objects[i]), Cast.as(objects[i + 1]));
      }
      return map;
   }

   public static <K, V> Optional<Map.Entry<K, V>> maxEntry(Map<K, V> map, Comparator<? super V> comparator) {
      return map.entrySet()
                .parallelStream()
                .max((e1, e2) -> comparator.compare(e1.getValue(), e2.getValue()));
   }

   public static <K, V extends Comparable> Optional<Map.Entry<K, V>> maxEntry(Map<K, V> map) {
      return maxEntry(map, Sorting.natural());
   }

   public static <K, V extends Comparable> K maxKeyByValue(Map<K, V> map) {
      return maxEntry(map).map(Map.Entry::getKey).orElse(null);
   }

   public static <K, V> Optional<Map.Entry<K, V>> minEntry(Map<K, V> map, Comparator<? super V> comparator) {
      return map.entrySet()
                .parallelStream()
                .min((e1, e2) -> comparator.compare(e1.getValue(), e2.getValue()));
   }

   public static <K, V extends Comparable> Optional<Map.Entry<K, V>> minEntry(Map<K, V> map) {
      return minEntry(map, Sorting.natural());
   }

   public static <K, V extends Comparable<? super V>> K minKeyByValue(Map<K, V> map) {
      return minEntry(map).map(Map.Entry::getKey).orElse(null);
   }

   /**
    * <p>Creates a HashMap from a string converting the keys and values using {@link Convert#getConverter(Class)}.
    * Empty or null  strings result in an empty Map. The string format should be in csv where the commas separate the
    * key-value pairs. Keys and values are the separated using either <code>:</code> or <code>=</code> depending on
    * which one is present and appears first. </p>
    *
    * @param <K>        The key type
    * @param <V>        The value type
    * @param input      The input string
    * @param keyClass   The key class
    * @param valueClass The value class
    * @return The resulting map
    */
   public static <K, V> Map<K, V> parseString(String input, Class<K> keyClass, Class<V> valueClass) {
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
   public static <K, V> Map<K, V> parseString(String input, Function<Object, K> keyConverter, Function<Object, V> valueConverter) {
      if (StringUtils.isNullOrBlank(input)) {
         return Collections.emptyMap();
      }
      String str = input.replaceAll("^\\s*\\{", "").replaceAll("}$\\s*", "");
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
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      return map;
   }

   public static <K, V> void putAll(Map<K, V> map, Map.Entry<? extends K, ? extends V>... entries) {
      for (Map.Entry<? extends K, ? extends V> entry : entries) {
         map.put(entry.getKey(), entry.getValue());
      }
   }

   public static <K, V> void putAll(Map<K, V> map, Object... objects) {
      checkArgument(objects.length % 2 == 0, "Must have an equal number of values and keys");
      for (int i = 0; i < objects.length; i += 2) {
         map.put(Cast.as(objects[i]), Cast.as(objects[i + 1]));
      }
   }

   /**
    * Reads in a map in csv format from the given resource.
    *
    * @param <K>            the type parameter
    * @param <V>            the type parameter
    * @param input          the resource to read from
    * @param keyConverter   the function to use to convert the cell to a key
    * @param valueConverter the function to use to convert the cell to a value
    * @return The map containing entries stored in the csv file
    * @throws IOException Something went wrong reading in the file
    */
   public static <K, V> Map<K, V> readCsv(Resource input, Function<Object, K> keyConverter, Function<Object, V> valueConverter) throws IOException {
      Map<K, V> map = new HashMap<>();
      try (CSVReader reader = CSV.builder().reader(input)) {
         reader.forEach(row -> row.forEach(cell -> {
                                              if (row.size() >= 2) {
                                                 map.put(keyConverter.apply(row.get(0)), valueConverter.apply(row.get(1)));
                                              }
                                           }
                                          ));
      }
      return map;
   }

   @SafeVarargs
   public static <K, V> Map<K, V> sortedMapOf(Map.Entry<? extends K, ? extends V>... objects) {
      return mapOf(TreeMap::new, objects);
   }

   public static <K, V> Map<K, V> sortedMapOf(Object... objects) {
      return mapOf(TreeMap::new, objects);
   }

   /**
    * Writes the given map in csv format to the given resource
    *
    * @param <K>    the key type
    * @param <V>    the value type
    * @param map    the map to write
    * @param output the resource to write to
    * @throws IOException Something went wrong writing to the resource
    */
   public static <K, V> void writeCsv(Map<K, V> map, Resource output) throws IOException {
      try (CSVWriter writer = CSV.builder().writer(output)) {
         for (Map.Entry<K, V> kvEntry : map.entrySet()) {
            writer.write(Convert.convert(kvEntry.getKey(), String.class),
                         Convert.convert(kvEntry.getValue(), String.class));
         }
      }
   }

   public static class Builder<K, V> {
      private final List<Map.Entry<K, V>> entries = new ArrayList<>();

      public Map<K, V> build() {
         return build(HashMap::new);
      }

      public Map<K, V> build(Supplier<? extends Map<K, V>> mapSupplier) {
         Map<K, V> map = mapSupplier.get();
         entries.forEach(entry -> map.put(entry.getKey(), entry.getValue()));
         return map;
      }

      public Builder<K, V> put(K key, V value) {
         entries.add($(key, value));
         return this;
      }

      public Builder<K, V> putAll(Map<? extends K, ? extends V> other) {
         other.forEach(this::put);
         return this;
      }

   }

}//END OF Maps
