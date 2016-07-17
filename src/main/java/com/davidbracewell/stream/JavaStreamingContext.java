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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.stream.accumulator.Accumulatable;
import com.davidbracewell.stream.accumulator.DoubleAccumulatable;
import com.davidbracewell.stream.accumulator.IntAccumulatable;
import com.davidbracewell.stream.accumulator.JavaAccumulator;
import com.davidbracewell.stream.accumulator.MAccumulator;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Throwables;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public enum JavaStreamingContext implements StreamingContext, Serializable {
  INSTANCE;
  private static final long serialVersionUID = 1L;

  @Override
  public MAccumulator<Double> accumulator(double initialValue, String name) {
    return new JavaAccumulator<>(new DoubleAccumulatable(), initialValue, name);
  }

  @Override
  public MAccumulator<Integer> accumulator(int initialValue, String name) {
    return new JavaAccumulator<>(new IntAccumulatable(), initialValue, name);
  }

  @Override
  public <T> MAccumulator<T> accumulator(T initialValue, Accumulatable<T> accumulatable, String name) {
    return new JavaAccumulator<>(accumulatable, initialValue, name);
  }

  @Override
  public MDoubleStream doubleStream(DoubleStream doubleStream) {
    if (doubleStream == null) {
      return new LocalDoubleStream(DoubleStream.empty());
    }
    return new LocalDoubleStream(doubleStream);
  }

  @Override
  public MDoubleStream doubleStream(double... values) {
    if (values == null) {
      return new LocalDoubleStream(DoubleStream.empty());
    }
    return new LocalDoubleStream(DoubleStream.of(values));
  }

  @Override
  public <T> MStream<T> empty() {
    return new LocalStream<>();
  }

  @Override
  public MStream<Integer> range(int startInclusive, int endExclusive) {
    return new LocalStream<>(
      IntStream.range(startInclusive, endExclusive).boxed().parallel()
    );
  }

  @Override
  @SafeVarargs
  public final <T> MStream<T> stream(T... items) {
    if (items == null) {
      return empty();
    }
    return new ReusableLocalStream<>(Arrays.asList(items));
  }

  @Override
  public <T> MStream<T> stream(Stream<T> stream) {
    if (stream == null) {
      return empty();
    }
    return new LocalStream<>(stream);
  }

  @Override
  public <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map) {
    if (map == null) {
      return new LocalPairStream<>(Stream.empty());
    }
    return new LocalPairStream<>(map);
  }

  @Override
  public <K, V> MPairStream<K, V> pairStream(Collection<Map.Entry<K, V>> tuples) {
    return new LocalPairStream<>(tuples.stream());
  }

  @Override
  public <T> MStream<T> stream(Collection<? extends T> collection) {
    if (collection == null) {
      return empty();
    }
    return new ReusableLocalStream<>(Cast.<Collection<T>>as(collection));
  }

  @Override
  public <T> MStream<T> stream(Iterable<? extends T> iterable) {
    if (iterable == null) {
      return empty();
    } else if (iterable instanceof Collection) {
      return stream(Cast.<Collection<T>>as(iterable));
    }
    return new LocalStream<>(Cast.<Iterable<T>>as(iterable));
  }

  @Override
  public MStream<String> textFile(String location) {
    if (StringUtils.isNullOrBlank(location)) {
      return empty();
    }
    try {
      return new LocalStream<>(Files.lines(Paths.get(location)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }


}//END OF JavaStreamingContext
