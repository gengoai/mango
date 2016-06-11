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

package com.davidbracewell.collection;

import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.io.structured.csv.CSVWriter;
import com.davidbracewell.tuple.Tuple3;
import com.google.common.primitives.Doubles;
import lombok.NonNull;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * The interface Multi counters.
 */
public interface MultiCounters {

  /**
   * New hash map multi counter.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @return the multi counter
   */
  static <K, V> MultiCounter<K, V> newHashMapMultiCounter() {
    return new HashMapMultiCounter<>();
  }

  /**
   * New hash map multi counter.
   *
   * @param <K>     the type parameter
   * @param <V>     the type parameter
   * @param triples the triples
   * @return the multi counter
   */
  @SafeVarargs
  static <K, V> MultiCounter<K, V> newHashMapMultiCounter(Tuple3<K, V, ? extends Number>... triples) {
    return new HashMapMultiCounter<>(triples);
  }

  static <K, V> Resource toCSV(@NonNull MultiCounter<K, V> counter, @NonNull Resource resource, @NonNull CSV csv) throws IOException {
    try (CSVWriter writer = csv.writer(resource)) {
      Set<String> columns = new TreeSet<>(
        counter.entries().stream()
          .map(Tuple3::getV2)
          .map(o -> Convert.convert(o, String.class))
          .collect(Collectors.toSet())
      );

      List<String> header = new LinkedList<>();
      header.add("Item");
      header.addAll(columns);
      writer.write(header);

      for (K key : counter.items()) {
        List<String> row = new LinkedList<>();
        row.add(Convert.convert(key, String.class));

        Counter<String> ctr = new HashMapCounter<>();
        counter.get(key).asMap().entrySet().forEach(e ->
          ctr.set(Convert.convert(e.getKey(), String.class), e.getValue())
        );
        row.addAll(columns.stream().map(c -> Double.toString(ctr.get(c))).collect(Collectors.toList()));

        writer.write(row);
      }

    }
    return resource;
  }

  /**
   * From csv.
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param csv        the csv
   * @param keyClass   the key class
   * @param valueClass the value class
   * @param supplier   the supplier
   * @return the multi counter
   * @throws IOException the iO exception
   */
  static <K, V> MultiCounter<K, V> fromCsv(@NonNull Resource csv, @NonNull Class<K> keyClass, @NonNull Class<V> valueClass, @NonNull Supplier<MultiCounter<K, V>> supplier) throws IOException {
    MultiCounter<K, V> mc = supplier.get();
    try (CSVReader reader = CSV.builder().reader(csv.reader())) {
      List<V> header = reader.nextRow().stream().skip(1).map(v -> Convert.convert(v, valueClass)).collect(Collectors.toList());
      List<String> row;
      int rowN = 1;
      while ((row = reader.nextRow()) != null) {
        K k = Convert.convert(row.get(0), keyClass);
        for (int i = 1; i < row.size() && (i - 1) < header.size(); i++) {
          Double d = Doubles.tryParse(row.get(i));
          if (d != null) {
            mc.set(k, header.get(i - 1), d);
          } else {
            throw new IllegalStateException("Could not parse " + row.get(i) + " in row " + rowN + " col " + i);
          }
        }
        rowN++;
      }
    }
    return mc;
  }

}//END OF MultiCounters
