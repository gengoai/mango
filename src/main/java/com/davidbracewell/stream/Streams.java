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

import com.davidbracewell.config.Config;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Throwables;
import lombok.NonNull;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * The interface Streams.
 *
 * @author David B. Bracewell
 */
public interface Streams {

  static MStream<String> textFile(String location, boolean distributed) {
    if (distributed) {
      SparkConf conf = new SparkConf();
      if( Config.hasProperty("spark.master")) {
        conf.setMaster(Config.get("spark.master").asString());
      }
      conf.setAppName(StringUtils.randomHexString(20));
      JavaSparkContext sc = new JavaSparkContext(conf);
      return new SparkStream<>(sc.textFile(location));
    }
    try {
      return new JavaMStream<>(Files.lines(Paths.get(location)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  /**
   * From stream.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> from(Iterator<T> iterator) {
    if (iterator == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), false);
  }

  /**
   * From stream.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> from(Iterable<T> iterable) {
    if (iterable == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(iterable.spliterator(), false);
  }

  /**
   * Paralle from.
   *
   * @param <T>      the type parameter
   * @param iterator the iterator
   * @return the stream
   */
  static <T> Stream<T> paralleFrom(Iterator<T> iterator) {
    if (iterator == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), true);
  }

  /**
   * Paralle from.
   *
   * @param <T>      the type parameter
   * @param iterable the iterable
   * @return the stream
   */
  static <T> Stream<T> paralleFrom(Iterable<T> iterable) {
    if (iterable == null) {
      return Collections.<T>emptyList().stream();
    }
    return StreamSupport.stream(iterable.spliterator(), true);
  }


  /**
   * Zip with index.
   *
   * @param <T>    the type parameter
   * @param stream the stream
   * @return the stream
   */
  static <T> Stream<Map.Entry<T, Integer>> zipWithIndex(Stream<T> stream) {
    if (stream == null) {
      return Stream.empty();
    }
    final AtomicInteger integer = new AtomicInteger();
    return stream.map(t -> new Tuple2<>(t, integer.getAndIncrement()));
  }

  /**
   * Zip stream.
   *
   * @param <T>     the type parameter
   * @param <U>     the type parameter
   * @param stream1 the stream 1
   * @param stream2 the stream 2
   * @return the stream
   */
  static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Stream<T> stream1, @NonNull final Stream<U> stream2) {
    if (stream1 == null || stream2 == null) {
      return Stream.empty();
    }
    return zip(stream1.iterator(), stream2.iterator());
  }


  static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterator<T> iterator1, @NonNull final Iterator<U> iterator2) {
    return from(new Iterator<Map.Entry<T, U>>() {
      @Override
      public boolean hasNext() {
        return iterator1.hasNext() && iterator2.hasNext();
      }

      @Override
      public Map.Entry<T, U> next() {
        if (!iterator1.hasNext() || !iterator2.hasNext()) {
          throw new NoSuchElementException();
        }
        return new Tuple2<>(iterator1.next(), iterator2.next());
      }
    });
  }


}//END OF Streams
