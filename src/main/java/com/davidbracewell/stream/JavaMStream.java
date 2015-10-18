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

import com.davidbracewell.collection.Streams;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class JavaMStream<T> implements MStream<T> {

  private final Stream<T> stream;

  public JavaMStream(@NonNull Stream<T> stream) {
    this.stream = stream;
  }

  @Override
  public void onClose(@NonNull Runnable closeHandler) {
    stream.onClose(closeHandler);
  }

  @Override
  public void close() throws Exception {
    stream.close();
  }

  @Override
  public MStream<T> filter(@NonNull Predicate<? super T> predicate) {
    return new JavaMStream<>(stream.filter(predicate));
  }

  @Override
  public <R> MStream<R> map(@NonNull Function<? super T, ? extends R> function) {
    return new JavaMStream<>(stream.map(function));
  }

  @Override
  public <R> MStream<R> flatMap(@NonNull Function<? super T, ? extends Iterable<? extends R>> mapper) {
    return new JavaMStream<>(stream.flatMap(t -> Streams.from(mapper.apply(t))));
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(Function<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
    return new JavaMPairStream<>(stream.map(f -> Cast.<Map.Entry<R, U>>as(function.apply(f))));
  }

  @Override
  public Optional<T> first() {
    return stream.findFirst();
  }

  @Override
  public MStream<T> sample(int count) {
    if (count <= 0) {
      return new JavaMStream<>(Stream.<T>empty());
    }
    Random random = new Random();
    List<T> sample = stream.limit(count).collect(Collectors.toList());
    AtomicInteger k = new AtomicInteger(count + 1);
    stream.skip(count).forEach(document -> {
      int rndIndex = random.nextInt(k.getAndIncrement());
      if (rndIndex < count) {
        sample.set(rndIndex, document);
      }
    });
    return new JavaMStream<>(sample.parallelStream());
  }

  @Override
  public Optional<T> reduce(@NonNull BinaryOperator<T> accumulator) {
    return stream.reduce(accumulator);
  }

  @Override
  public long size() {
    return stream.count();
  }

  @Override
  public MStream<T> distinct() {
    return new JavaMStream<>(stream.distinct());
  }

  @Override
  public void forEach(@NonNull Consumer<? super T> consumer) {
    stream.forEach(consumer);
  }

  @Override
  public <R> R collect(@NonNull Collector<? super T, T, R> collector) {
    return stream.collect(collector);
  }

  @Override
  public MStream<T> limit(long number) {
    return new JavaMStream<>(stream.limit(number));
  }

  @Override
  public List<T> take(int n) {
    return stream.limit(n).collect(Collectors.toList());
  }

  @Override
  public MStream<T> skip(long n) {
    return new JavaMStream<>(stream.skip(n));
  }

}//END OF JavaMStream
