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
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.collect.Ordering;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class JavaMStream<T> implements MStream<T> {

  private final Stream<T> stream;

  public JavaMStream(@NonNull final T... items) {
    this.stream = Stream.of(items);
  }

  public JavaMStream(@NonNull final Stream<T> stream) {
    this.stream = stream;
  }

  public JavaMStream(@NonNull final Collection<T> collection) {
    this.stream = collection.parallelStream();
  }

  public JavaMStream(@NonNull final Iterable<T> iterable) {
    this.stream = Streams.paralleFrom(iterable);
  }

  public JavaMStream(@NonNull final Iterator<T> iterator) {
    this.stream = Streams.paralleFrom(iterator);
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
  public MStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
    return new JavaMStream<>(stream.filter(predicate));
  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
    return new JavaMStream<>(stream.map(function));
  }

  @Override
  public <R> MStream<R> flatMap(@NonNull SerializableFunction<? super T, ? extends Iterable<? extends R>> mapper) {
    return new JavaMStream<>(stream.flatMap(t -> Streams.from(mapper.apply(t))));
  }

  @Override
  public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function) {
    return new JavaMPairStream<>(
      stream.flatMap(t -> Cast.as(function.apply(t)))
    );
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
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
  public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> accumulator) {
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
  public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
    stream.forEach(consumer);
  }

  @Override
  public Iterator<T> iterator() {
    return stream.iterator();
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

  public Stream<T> stream() {
    return stream;
  }

  @Override
  public List<T> collect() {
    return stream.collect(Collectors.toList());
  }

  @Override
  public Map<T, Long> countByValue() {
    return stream.collect(Collectors.groupingBy(Function.<T>identity(), Collectors.counting()));
  }

  @Override
  public T fold(@NonNull T zeroValue, @NonNull SerializableBinaryOperator<T> operator) {
    return stream.reduce(zeroValue, operator);
  }

  @Override
  public <U> MPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
    return new JavaMPairStream<>(
      stream.collect(Collectors.groupingBy(function)).entrySet().stream().map(e -> Tuple2.<U, Iterable<T>>of(e.getKey(), e.getValue()))
    );
  }

  @Override
  public boolean isEmpty() {
    return size() == 0;
  }

  @Override
  public Optional<T> max(@NonNull Comparator<? super T> comparator) {
    return stream.max(comparator);
  }

  @Override
  public Optional<T> min(@NonNull Comparator<? super T> comparator) {
    return stream.min(comparator);
  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    Comparator<T> comparator = Cast.as(ascending ? Ordering.natural() : Ordering.natural().reverse());
    return new JavaMStream<>(stream.sorted(comparator));
  }

  @Override
  public <U> MPairStream<T, U> zip(@NonNull MStream<U> other) {
    return new JavaMPairStream<>(Streams.zip(iterator(), other.iterator()));
  }

  @Override
  public MPairStream<T, Long> zipWithIndex() {
    final AtomicInteger integer = new AtomicInteger();
    return new JavaMPairStream<>(stream.map(t -> Cast.<Map.Entry<T, Long>>as(Tuple2.of(t, integer.getAndIncrement()))));
  }

  @Override
  public MDoubleStream mapToDouble(@NonNull ToDoubleFunction<? super T> function) {
    return new JavaDoubleStream(stream.mapToDouble(function));
  }

  @Override
  public MLongStream mapToLong(@NonNull ToLongFunction<? super T> function) {
    return new JavaLongStream(stream.mapToLong(function));
  }

  @Override
  public MStream<T> cache() {
    return this;
  }
}//END OF JavaMStream
