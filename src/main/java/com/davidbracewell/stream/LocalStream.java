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
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableComparator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.function.SerializableRunnable;
import com.davidbracewell.function.SerializableToDoubleFunction;
import com.davidbracewell.function.Unchecked;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Throwables;
import com.google.common.collect.Ordering;
import lombok.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The type Java m stream.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class LocalStream<T> implements MStream<T>, Serializable {
  private static final long serialVersionUID = 1L;

  private volatile Stream<T> stream;
  private SerializableRunnable onClose;

  public LocalStream(){
    this.stream = Stream.of();
  }

  /**
   * Instantiates a new Java m stream.
   *
   * @param items the items
   */
  @SafeVarargs
  public LocalStream(@NonNull final T... items) {
    this.stream = Stream.of(items);
  }

  /**
   * Instantiates a new Java m stream.
   *
   * @param stream the stream
   */
  public LocalStream(@NonNull final Stream<T> stream) {
    this.stream = stream;
  }

  /**
   * Instantiates a new Java m stream.
   *
   * @param collection the collection
   */
  public LocalStream(@NonNull final Collection<T> collection) {
    this.stream = collection.parallelStream();
  }

  /**
   * Instantiates a new Java m stream.
   *
   * @param iterable the iterable
   */
  public LocalStream(@NonNull final Iterable<T> iterable) {
    this.stream = Collect.stream(iterable);
  }


  @Override
  public void onClose(@NonNull SerializableRunnable closeHandler) {
    this.onClose = closeHandler;
    stream.onClose(closeHandler);
  }

  @Override
  public void close() throws IOException {
    stream.close();
  }

  @Override
  public MStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
    return new LocalStream<>(stream.filter(predicate));
  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
    return new LocalStream<>(stream.map(function));
  }

  @Override
  public <R> MStream<R> flatMap(@NonNull SerializableFunction<? super T, Iterable<? extends R>> mapper) {
    return new LocalStream<>(stream.flatMap(t -> Collect.stream(mapper.apply(t)).map(Cast::<R>as)));
  }

  @Override
  public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function) {
    return new LocalPairStream<>(
      stream.flatMap(t -> Collect.stream(Cast.<Iterable<Map.Entry<R, U>>>as(function.apply(t))))
    );
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
    return new LocalPairStream<>(stream.map(f -> Cast.<Map.Entry<R, U>>as(function.apply(f))));
  }

  @Override
  public Optional<T> first() {
    return stream.findFirst();
  }

  @Override
  public MStream<T> sample(int count) {
    if (count <= 0) {
      return new LocalStream<>(Stream.<T>empty());
    }
    Random random = new Random();
    List<T> sample = new ArrayList<>();
    AtomicInteger k = new AtomicInteger(count + 1);
    stream.sequential().forEach(document -> {
      if (sample.size() < count) {
        sample.add(document);
      } else {
        int rndIndex = random.nextInt(k.getAndIncrement());
        if (rndIndex < count) {
          sample.set(rndIndex, document);
        }
      }
    });
    return new LocalStream<>(sample.parallelStream());
  }

  @Override
  public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> accumulator) {
    return stream.reduce(accumulator);
  }

  @Override
  public long count() {
    List<T> objects = stream.collect(Collectors.toList());
    stream = objects.stream();
    return objects.size();
  }

  @Override
  public MStream<T> distinct() {
    return new LocalStream<>(stream.distinct());
  }

  @Override
  public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
    stream.forEachOrdered(consumer);
  }

  @Override
  public void forEachLocal(SerializableConsumer<? super T> consumer) {
    stream.forEachOrdered(consumer);
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
    return new LocalStream<>(stream.limit(number));
  }

  @Override
  public List<T> take(int n) {
    return stream.limit(n).collect(Collectors.toList());
  }

  @Override
  public MStream<T> skip(long n) {
    return new LocalStream<>(stream.skip(n));
  }

  /**
   * Stream stream.
   *
   * @return the stream
   */
  public Stream<T> stream() {
    return stream;
  }

  @Override
  public List<T> collect() {
    return stream.collect(Collectors.toList());
  }

  @Override
  public Map<T, Long> countByValue() {
    return stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
  }

  @Override
  public T fold(@NonNull T zeroValue, @NonNull SerializableBinaryOperator<T> operator) {
    return stream.reduce(zeroValue, operator);
  }

  @Override
  public <U> MPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
    return new LocalPairStream<>(
      stream.collect(Collectors.groupingBy(function)).entrySet().stream().map(e -> Tuple2.<U, Iterable<T>>of(e.getKey(), e.getValue()))
    );
  }

  @Override
  public boolean isEmpty() {
    return count() == 0;
  }

  @Override
  public Optional<T> max(@NonNull SerializableComparator<? super T> comparator) {
    return stream.max(comparator);
  }

  @Override
  public Optional<T> min(@NonNull SerializableComparator<? super T> comparator) {
    return stream.min(comparator);
  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    Comparator<T> comparator = Cast.as(ascending ? Ordering.natural() : Ordering.natural().reverse());
    return new LocalStream<>(stream.sorted(comparator));
  }

  @Override
  public <U> MPairStream<T, U> zip(@NonNull MStream<U> other) {
    return new LocalPairStream<>(Collect.zip(iterator(), other.iterator()));
  }

  @Override
  public MPairStream<T, Long> zipWithIndex() {
    final AtomicLong indexer = new AtomicLong();
    return new LocalPairStream<>(stream.map(t -> Cast.<Map.Entry<T, Long>>as(Tuple2.of(t, indexer.getAndIncrement()))));
  }

  @Override
  public MDoubleStream mapToDouble(@NonNull SerializableToDoubleFunction<? super T> function) {
    return new LocalDoubleStream(stream.mapToDouble(function));
  }

  @Override
  public MStream<T> cache() {
    return new ReusableLocalStream<>(collect());
  }

  @Override
  public MStream<T> union(MStream<T> other) {
    if (other instanceof LocalStream) {
      return new LocalStream<>(Stream.concat(stream, Cast.<LocalStream<T>>as(other).stream));
    }
    return new LocalStream<>(Stream.concat(stream, other.collect().stream()));
  }

  @Override
  public void saveAsTextFile(@NonNull Resource location) {
    try (BufferedWriter writer = new BufferedWriter(location.writer())) {
      stream.forEach(Unchecked.consumer(o -> {
          writer.write(o.toString());
          writer.newLine();
        }
      ));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }

  @Override
  public MStream<T> parallel() {
    return new LocalStream<>(stream.parallel());
  }

  @Override
  public MStream<T> shuffle(@NonNull Random random) {
    return new LocalStream<>(
      stream.map(t -> Tuple2.of(random.nextDouble(), t))
        .sorted(Map.Entry.comparingByKey())
        .map(Tuple2::getValue)
    );
  }

  @Override
  public MStream<T> repartition(int numPartitions) {
    return this;
  }

  @Override
  public StreamingContext getContext() {
    return JavaStreamingContext.INSTANCE;
  }

  @Override
  public SerializableRunnable getOnCloseHandler() {
    return onClose;
  }
}//END OF LocalStream
