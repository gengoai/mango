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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.IOException;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The interface Multi counters.
 */
public interface MultiCounters {

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


  /**
   * <p>Reads a resource in the given {@link StructuredFormat} which is made up of key value pairs where the key is the
   * item in the counter and the value is its count.</p>
   *
   * @param <K1>             the type parameter
   * @param <K2>             the type parameter
   * @param structuredFormat the format of the file being read
   * @param resource         the resource of the file being read
   * @param key1Type         the class of the item type
   * @param key2Type         the key 2 type
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <K1, K2> MultiCounter<K1, K2> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Class<K1> key1Type, @NonNull Class<K2> key2Type) throws IOException {
    return read(structuredFormat, resource, str -> Convert.convert(str, key1Type), str -> Convert.convert(str, key2Type), HashMapMultiCounter::new);
  }

  /**
   * <p>Reads a resource in the given {@link StructuredFormat} which is made up of key value pairs where the key is the
   * item in the counter and the value is its count.</p>
   *
   * @param <K1>             the type parameter
   * @param <K2>             the type parameter
   * @param structuredFormat the format of the file being read
   * @param resource         the resource of the file being read
   * @param k1Deserializer   Function to turn string representation of key into an item
   * @param k2Deserializer   the k 2 deserializer
   * @param supplier         the supplier to use for creating the initial counter
   * @return the counter
   * @throws IOException Something went wrong reading in the counter.
   */
  static <K1, K2> MultiCounter<K1, K2> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Function<String, K1> k1Deserializer, @NonNull Function<String, K2> k2Deserializer, @NonNull Supplier<MultiCounter<K1, K2>> supplier) throws IOException {
    MultiCounter<K1, K2> counter = supplier.get();
    try (StructuredReader reader = structuredFormat.createReader(resource)) {
      reader.beginDocument();
      while (reader.peek() != ElementType.END_DOCUMENT) {
        reader.beginObject();
        K1 k1 = null;
        K2 k2 = null;
        double value = 0;
        int i = 0;
        while (reader.peek() != ElementType.END_OBJECT) {
          Tuple2<String, Val> kv = reader.nextKeyValue();
          switch (kv.v1) {
            case "k1":
              k1 = k1Deserializer.apply(kv.v2.asString());
              break;
            case "k2":
              k2 = k2Deserializer.apply(kv.v2.asString());
              break;
            case "v":
              value = kv.v2.asDoubleValue();
              break;
            default:
              if (k1 == null) {
                k1 = k1Deserializer.apply(kv.v2.asString());
              } else if (k2 == null) {
                k2 = k2Deserializer.apply(kv.v2.asString());
              } else {
                value = kv.v2.asDoubleValue();
              }
          }
        }
        counter.set(k1, k2, value);
        reader.endObject();
      }
      reader.endDocument();
    }
    return counter;
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
  static <K1, K2> MultiCounter<K1, K2> readCSV(@NonNull Resource resource, @NonNull Class<K1> key1Class, @NonNull Class<K2> key2Class) throws IOException {
    return read(StructuredFormat.CSV, resource, key1Class, key2Class);
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
    return read(StructuredFormat.JSON, resource, key1Class, key2Class);
  }
}//END OF MultiCounters
