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

package com.davidbracewell.stream;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * The interface Streams.
 *
 * @author David B. Bracewell
 */
public interface Streams {

  /**
   * The constant DISTRIBUTED.
   */
  String DISTRIBUTED = "streams.distributed";

  static MStream<Integer> range(int startInclusive, int endExclusive) {
    return new JavaMStream<>(
      IntStream.range(startInclusive, endExclusive).boxed().parallel()
    );
  }

  /**
   * Text file m stream.
   *
   * @param location the location
   * @return the m stream
   */
  static MStream<String> textFile(String location) {
    return textFile(location, Config.get(DISTRIBUTED).asBooleanValue(false));
  }

  /**
   * Text file m stream.
   *
   * @param location    the location
   * @param distributed the distributed
   * @return the m stream
   */
  static MStream<String> textFile(String location, boolean distributed) {
    if (distributed) {
      return new SparkStream<>(Spark.context().textFile(location));
    }
    try {
      return new JavaMStream<>(Files.lines(Paths.get(location)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * Of m pair stream.
   *
   * @param <K> the type parameter
   * @param <V> the type parameter
   * @param map the map
   * @return the m pair stream
   */
  static <K, V> MPairStream<K, V> of(Map<? extends K, ? extends V> map) {
    return of(map, Config.get(DISTRIBUTED).asBooleanValue(false));
  }

  /**
   * Of m pair stream.
   *
   * @param <K>         the type parameter
   * @param <V>         the type parameter
   * @param map         the map
   * @param distributed the distributed
   * @return the m pair stream
   */
  static <K, V> MPairStream<K, V> of(Map<? extends K, ? extends V> map, boolean distributed) {
    if (distributed) {
      return new SparkPairStream<>(map);
    }
    return new JavaMPairStream<>(map);
  }

  /**
   * Of m stream.
   *
   * @param <T>        the type parameter
   * @param collection the collection
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Collection<? extends T> collection) {
    return of(collection, Config.get(DISTRIBUTED).asBoolean(false));
  }

  /**
   * Of m stream.
   *
   * @param <T>         the type parameter
   * @param collection  the collection
   * @param distributed the distributed
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Collection<? extends T> collection, boolean distributed) {
    if (distributed) {
      List<T> list;
      if (collection instanceof List) {
        list = Cast.as(collection);
      } else {
        list = new LinkedList<>(collection);
      }
      return new SparkStream<>(list);
    }
    return new JavaMStream<>(Cast.cast(collection));
  }

  /**
   * Of m stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Iterable<? extends T> iterable) {
    return of(iterable, Config.get(DISTRIBUTED).asBoolean(false));
  }

  /**
   * Of m stream.
   *
   * @param <T>         the type parameter
   * @param iterable    the iterable
   * @param distributed the distributed
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Iterable<? extends T> iterable, boolean distributed) {
    if (distributed) {
      List<T> list;
      if (iterable instanceof List) {
        list = Cast.as(iterable);
      } else {
        list = Lists.newLinkedList(iterable);
      }
      return new SparkStream<>(list);
    }
    return new JavaMStream<>(Cast.cast(iterable));
  }

  /**
   * Of m stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Iterator<? extends T> iterator) {
    return of(iterator, Config.get(DISTRIBUTED).asBoolean(false));
  }

  /**
   * Of m stream.
   *
   * @param <T>         the type parameter
   * @param iterator    the iterator
   * @param distributed the distributed
   * @return the m stream
   */
  static <T> MStream<T> of(@NonNull Iterator<? extends T> iterator, boolean distributed) {
    if (distributed) {
      return new SparkStream<>(Lists.newLinkedList(Collect.asIterable(iterator)));
    }
    return new JavaMStream<>(iterator);
  }

  /**
   * Of m stream.
   *
   * @param <T>         the type parameter
   * @param distributed the distributed
   * @param items       the items
   * @return the m stream
   */
  @SafeVarargs
  static <T> MStream<T> of(boolean distributed, @NonNull T... items) {
    return of(Arrays.asList(items), distributed);
  }

  /**
   * Of m stream.
   *
   * @param <T>   the type parameter
   * @param items the items
   * @return the m stream
   */
  @SafeVarargs
  @SuppressWarnings("unchecked")
  static <T> MStream<T> of(@NonNull T... items) {
    return of(Config.get(DISTRIBUTED).asBoolean(false), items);
  }

  static <T> MStream<T> of(@NonNull Stream<T> stream) {
    return new JavaMStream<>(stream);
  }

  static MDoubleStream doubleStream(@NonNull DoubleStream doubleStream) {
    return new JavaDoubleStream(doubleStream);
  }

}//END OF Streams
