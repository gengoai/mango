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

package com.davidbracewell.collection.counter;

import com.davidbracewell.conversion.Convert;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.io.structured.csv.CSVReader;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

/**
 * Common methods for reading multi-counters from structured files, creating synchronized and unmodifiable wrappers.
 */
public interface MultiCounters {


   /**
    * New multi counter multi counter.
    *
    * @param <K1>    the type parameter
    * @param <K2>    the type parameter
    * @param counter the counter
    * @return the multi counter
    */
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(@NonNull MultiCounter<? extends K1, ? extends K2> counter) {
      MultiCounter<K1, K2> mc = new HashMapMultiCounter<>();
      counter.entries().forEach(triple -> mc.increment(triple.v1, triple.v2, triple.v3));
      return mc;
   }

   /**
    * New multi counter multi counter.
    *
    * @param <K1>    the type parameter
    * @param <K2>    the type parameter
    * @param entries the entries
    * @return the multi counter
    */
   @SafeVarargs
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(Map.Entry<? extends K1, ? extends K2>... entries) {
      return entries == null ? new HashMapMultiCounter<>() : newMultiCounter(Arrays.asList(entries));
   }

   /**
    * New multi counter multi counter.
    *
    * @param <K1>    the type parameter
    * @param <K2>    the type parameter
    * @param entries the entries
    * @return the multi counter
    */
   static <K1, K2> MultiCounter<K1, K2> newMultiCounter(@NonNull Iterable<? extends Map.Entry<? extends K1, ? extends K2>> entries) {
      MultiCounter<K1, K2> mc = new HashMapMultiCounter<>();
      entries.forEach(e -> mc.increment(e.getKey(), e.getValue()));
      return mc;
   }

   /**
    * Unmodifiable multi counter multi counter.
    *
    * @param <K1>         the type parameter
    * @param <K2>         the type parameter
    * @param multiCounter the multi counter
    * @return the multi counter
    */
   static <K1, K2> MultiCounter<K1, K2> unmodifiableMultiCounter(@NonNull MultiCounter<K1, K2> multiCounter) {
      return new UnmodifiableMultiCounter<>(multiCounter);
   }

   /**
    * Synchronized multi counter multi counter.
    *
    * @param <K1>         the type parameter
    * @param <K2>         the type parameter
    * @param multiCounter the multi counter
    * @return the multi counter
    */
   static <K1, K2> MultiCounter<K1, K2> synchronizedMultiCounter(@NonNull MultiCounter<K1, K2> multiCounter) {
      return new SynchronizedMultiCounter<>(multiCounter);
   }


   static <K1, K2> MultiCounter<K1, K2> synchronizedMultiCounter() {
      return synchronizedMultiCounter(newMultiCounter());
   }

   /**
    * <p>Reads a counter from a CSV file.</p>
    *
    * @param <K1>      the type parameter
    * @param <K2>      the type parameter
    * @param resource  the resource that the counter values are written to.
    * @param key1Class the class of the item type
    * @param key2Class the key 2 class
    * @return the counter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <K1, K2> MultiCounter<K1, K2> readCsv(@NonNull Resource resource, @NonNull Class<K1> key1Class, @NonNull Class<K2> key2Class) throws IOException {
      MultiCounter<K1, K2> counter = newMultiCounter();
      try (CSVReader reader = CSV.builder().reader(resource)) {
         reader.forEach(row -> {
            if (row.size() >= 3) {
               counter.increment(Convert.convert(row.get(0), key1Class), Convert.convert(row.get(1), key2Class),
                                 Double.valueOf(row.get(2)));
            }
         });
      }
      return counter;
   }

   /**
    * <p>Reads a counter from a Json file.</p>
    *
    * @param <K1>      the type parameter
    * @param <K2>      the type parameter
    * @param resource  the resource that the counter values are written to.
    * @param key1Class the key 1 class
    * @param key2Class the key 2 class
    * @return the counter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <K1, K2> MultiCounter<K1, K2> readJson(@NonNull Resource resource, @NonNull Class<K1> key1Class, @NonNull Class<K2> key2Class) throws IOException {
      MultiCounter<K1, K2> counter = newMultiCounter();
      try (StructuredReader reader = StructuredFormat.JSON.createReader(resource)) {
         reader.beginDocument();
         while (reader.peek() != ElementType.END_DOCUMENT) {
            Map<String, Val> map = reader.nextMap();
            counter.set(map.get("k1").as(key1Class), map.get("k2").as(key2Class), map.get("v").asDoubleValue());
         }
         reader.endDocument();
      }
      return counter;
   }

}//END OF MultiCounters
