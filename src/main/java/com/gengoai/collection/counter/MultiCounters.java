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

package com.gengoai.collection.counter;

import com.gengoai.conversion.Converter;
import com.gengoai.io.CSV;
import com.gengoai.io.CSVReader;
import com.gengoai.io.resource.Resource;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonReader;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Common methods for reading multi-counters from structured files, creating synchronized and unmodifiable wrappers.
 */
public interface MultiCounters {


   /**
    * Creates a copy of the specified multi-counter
    *
    * @param <K1>    the component type of the first key
    * @param <K2>    the component type of the second key
    * @param counter the counter to copy
    * @return A new MultiCounter that is a copy of the given MultiCounter
    */
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(MultiCounter<? extends K1, ? extends K2> counter) {
      MultiCounter<K1, K2> mc = new HashMapMultiCounter<>();
      counter.entries().forEach(triple -> mc.increment(triple.v1, triple.v2, triple.v3));
      return mc;
   }

   /**
    * Creates a new MultiCounter using the given map entries.
    *
    * @param <K1>    the component type of the first key
    * @param <K2>    the component type of the second key
    * @param entries the entries to increment in the counter.
    * @return A new MultiCounter with counts of the given entries
    */
   @SafeVarargs
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(Map.Entry<? extends K1, ? extends K2>... entries) {
      return entries == null ? new HashMapMultiCounter<>() : newMultiCounter(Arrays.asList(entries));
   }

   /**
    * Creates a new MultiCounter using the given map entries.
    *
    * @param <K1>    the component type of the first key
    * @param <K2>    the component type of the second key
    * @param entries the entries to increment in the counter.
    * @return A new MultiCounter with counts of the given entries
    */
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(Iterable<? extends Map.Entry<? extends K1, ? extends K2>> entries) {
      MultiCounter<K1, K2> mc = new HashMapMultiCounter<>();
      entries.forEach(e -> mc.increment(e.getKey(), e.getValue()));
      return mc;
   }

   /**
    * Creates a new MultiCounter using the given map.
    *
    * @param <K1> the component type of the first key
    * @param <K2> the component type of the second key
    * @param map  A map whose keys are the entries of the counter and values are the counts.
    * @return A new MultiCounter with counts of the given entries
    */
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(Map<? extends Map.Entry<? extends K1, ? extends K2>, ? extends Number> map) {
      MultiCounter<K1, K2> mc = new HashMapMultiCounter<>();
      map.entrySet().forEach(e -> mc.increment(e.getKey().getKey(), e.getKey().getValue(), e.getValue().doubleValue()));
      return mc;
   }

   /**
    * <p>Wraps a counter making each method call synchronized.</p>
    *
    * @param <K1>         the component type of the first key
    * @param <K2>         the component type of the second key
    * @param multiCounter the multi counter to wrap
    * @return the synchronized multi-counter
    */
   static <K1, K2> MultiCounter<K1, K2> synchronizedMultiCounter(MultiCounter<K1, K2> multiCounter) {
      return new SynchronizedMultiCounter<>(multiCounter);
   }

   /**
    * <p>Creates a new multi-counter where each method call is synchronized.</p>
    *
    * @param <K1> the component type of the first key
    * @param <K2> the component type of the second key
    * @return the synchronized multi-counter
    */
   static <K1, K2> MultiCounter<K1, K2> synchronizedMultiCounter() {
      return synchronizedMultiCounter(newMultiCounter());
   }

   /**
    * <p>Reads a counter from a CSV file.</p>
    *
    * @param <K1>      the component type of the first key
    * @param <K2>      the component type of the second key
    * @param resource  the resource that the counter values are read from.
    * @param key1Class the class of first key
    * @param key2Class the class of the second key
    * @return the new MultiCounter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <K1, K2> MultiCounter<K1, K2> readCsv(Resource resource, Class<K1> key1Class, Class<K2> key2Class) throws IOException {
      MultiCounter<K1, K2> counter = newMultiCounter();
      try (CSVReader reader = CSV.builder().reader(resource)) {
         reader.forEach(row -> {
            if (row.size() >= 3) {
               counter.increment(Converter.convertSilently(row.get(0), key1Class),
                                 Converter.convertSilently(row.get(1), key2Class),
                                 Double.valueOf(row.get(2)));
            }
         });
      }
      return counter;
   }

   /**
    * <p>Reads a counter from a Json file.</p>
    *
    * @param <K1>      the component type of the first key
    * @param <K2>      the component type of the second key
    * @param resource  the resource that the counter values are read from.
    * @param key1Class the class of first key
    * @param key2Class the class of the second key
    * @return the new MultiCounter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <K1, K2> MultiCounter<K1, K2> readJson(Resource resource, Class<K1> key1Class, Class<K2> key2Class) throws IOException {
      MultiCounter<K1, K2> counter = newMultiCounter();
      try (JsonReader reader = Json.createReader(resource)) {
         reader.beginDocument();
         while (reader.hasNext()) {
            Map<String, JsonEntry> map = reader.nextMap();
            counter.set(map.get("k1").getAs(key1Class),
                        map.get("k2").getAs(key2Class),
                        map.get("v").getAs(Double.class));
         }
         reader.endDocument();
      }
      return counter;
   }

}//END OF MultiCounters
