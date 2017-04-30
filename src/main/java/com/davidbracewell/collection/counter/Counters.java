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
import com.davidbracewell.io.CSVReader;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.json.JsonReader;
import com.davidbracewell.json.Json;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Stream;

import static com.davidbracewell.json.JsonTokenType.END_DOCUMENT;

/**
 * Common methods for reading counters from structured files, creating synchronized and unmodifiable wrappers.
 */
public interface Counters {

   /**
    * <p>Creates a new {@link HashMapMultiCounter} which is initialized with the given items</p>
    *
    * @param <T>   the component type of the counter
    * @param items the items to add to the counter
    * @return the counter
    */
   @SafeVarargs
   static <T> Counter<T> newCounter(T... items) {
      Counter<T> counter = new HashMapCounter<>();
      if (items != null) {
         counter.incrementAll(Arrays.asList(items));
      }
      return counter;
   }

   /**
    * <p>Creates a new {@link HashMapMultiCounter} which is initialized with the given items</p>
    *
    * @param <T>      the component type of the counter
    * @param iterable the items to add to the counter
    * @return the counter
    */
   static <T> Counter<T> newCounter(@NonNull Iterable<? extends T> iterable) {
      Counter<T> counter = new HashMapCounter<>();
      counter.incrementAll(iterable);
      return counter;
   }

   /**
    * <p>Creates a new {@link HashMapMultiCounter} which is initialized with the given items</p>
    *
    * @param <T>    the component type of the counter
    * @param stream the items to add to the counter
    * @return the counter
    */
   static <T> Counter<T> newCounter(@NonNull Stream<? extends T> stream) {
      Counter<T> counter = new HashMapCounter<>();
      stream.forEach(counter::increment);
      return counter;
   }

   /**
    * <p>Creates a new {@link HashMapMultiCounter} which is initialized by merging with the given map</p>
    *
    * @param <T> the component type of the counter
    * @param map the items and counts to merge with counter
    * @return the counter
    */
   static <T> Counter<T> newCounter(@NonNull Map<? extends T, ? extends Number> map) {
      Counter<T> counter = new HashMapCounter<>();
      counter.merge(map);
      return counter;
   }

   /**
    * <p>Creates a new {@link HashMapMultiCounter} which is initialized by merging with the given counter</p>
    *
    * @param <T>   the component type of the counter
    * @param other the items and counts to merge with counter
    * @return the counter
    */
   static <T> Counter<T> newCounter(@NonNull Counter<? extends T> other) {
      Counter<T> counter = new HashMapCounter<>();
      counter.merge(other);
      return counter;
   }

   /**
    * <p>Reads a counter from a CSV file.</p>
    *
    * @param <TYPE>   the component type of the counter
    * @param resource the resource that the counter values are read from.
    * @param keyClass the class of the item type
    * @return the counter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <TYPE> Counter<TYPE> readCsv(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
      Counter<TYPE> counter = Counters.newCounter();
      try (CSVReader reader = CSV.builder().reader(resource)) {
         reader.forEach(row -> {
            if (row.size() >= 2) {
               counter.increment(Convert.convert(row.get(0), keyClass), Double.valueOf(row.get(1)));
            }
         });
      }
      return counter;
   }

   /**
    * <p>Reads a counter from a Json file.</p>
    *
    * @param <TYPE>   the component type of the counter
    * @param resource the resource that the counter values are read from.
    * @param keyClass the class of the item type
    * @return the counter
    * @throws IOException Something went wrong reading in the counter.
    */
   static <TYPE> Counter<TYPE> readJson(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
      Counter<TYPE> counter = Counters.newCounter();
      try (JsonReader reader = Json.createReader(resource)) {
         reader.beginDocument();
         while (reader.peek() != END_DOCUMENT) {
            Tuple2<String, Val> keyValue = reader.nextKeyValue();
            counter.set(Convert.convert(keyValue.v1, keyClass), keyValue.v2.asDouble());
         }
         reader.endDocument();
      }
      return counter;
   }

   /**
    * <p>Wraps a counter making each method call synchronized.</p>
    *
    * @param <TYPE>  the item type
    * @param counter the counter to wrap
    * @return the wrapped counter
    */
   static <TYPE> Counter<TYPE> synchronizedCounter(@NonNull Counter<TYPE> counter) {
      return new SynchronizedCounter<>(counter);
   }

   /**
    * <p>Wraps a counter making each method call synchronized.</p>
    *
    * @param <TYPE> the item type
    * @return the wrapped counter
    */
   static <TYPE> Counter<TYPE> synchronizedCounter() {
      return new SynchronizedCounter<>(newCounter());
   }

   /**
    * <p>Wraps a counter making its entries unmodifiable.</p>
    *
    * @param <TYPE>  the item type
    * @param counter the counter to wrap
    * @return the wrapped counter
    */
   static <TYPE> Counter<TYPE> unmodifiableCounter(@NonNull final Counter<TYPE> counter) {
      return new UnmodifiableCounter<>(counter);
   }


}//END OF Counters
