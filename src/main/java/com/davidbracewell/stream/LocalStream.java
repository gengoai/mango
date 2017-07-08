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
import com.davidbracewell.collection.Sorting;
import com.davidbracewell.collection.Streams;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.function.*;
import com.davidbracewell.io.resource.Resource;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterators;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * A wrapper around Java's <code>Stream</code> class.
 *
 * @param <T> the component type of the stream
 * @author David B. Bracewell
 */
public class LocalStream<T> implements MStream<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Stream<T> stream;
   private SerializableRunnable onClose;


   /**
    * Instantiates a new local stream from a Java stream
    *
    * @param stream the Java stream to wrap
    */
   public LocalStream(@NonNull final Stream<T> stream) {
      this.stream = stream;
   }

   @Override
   public MStream<T> cache() {
      return new ReusableLocalStream<>(collect());
   }

   @Override
   public void close() throws IOException {
      try {
         stream.close();
      } catch (UnsupportedOperationException uoe) {
         //noopt
      }
   }

   @Override
   public <R> R collect(@NonNull Collector<? super T, T, R> collector) {
      return stream.collect(collector);
   }

   @Override
   public List<T> collect() {
      ArrayList<T> list = new ArrayList<>();
      stream.sequential().forEach(list::add);
      return list;
   }

   @Override
   public long count() {
      return stream.count();
   }

   @Override
   public Map<T, Long> countByValue() {
      return stream.collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
   }

   @Override
   public MStream<T> distinct() {
      return new LocalStream<>(stream.distinct());
   }

   @Override
   public MStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
      return new LocalStream<>(stream.filter(predicate));
   }

   @Override
   public Optional<T> first() {
      return stream.findFirst();
   }

   @Override
   public <R> MStream<R> flatMap(@NonNull SerializableFunction<? super T, Stream<? extends R>> mapper) {
      return new LocalStream<>(stream.flatMap(mapper::apply));
   }

   @Override
   public <R, U> MPairStream<R, U> flatMapToPair(@NonNull SerializableFunction<? super T, Stream<? extends Map.Entry<? extends R, ? extends U>>> function) {
      return new LocalPairStream<>(stream.flatMap(function));
   }

   @Override
   public T fold(@NonNull T zeroValue, @NonNull SerializableBinaryOperator<T> operator) {
      return stream.reduce(zeroValue, operator);
   }

   @Override
   public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
      stream.forEach(consumer);
   }

   @Override
   public void forEachLocal(@NonNull SerializableConsumer<? super T> consumer) {
      stream.forEachOrdered(consumer);
   }

   @Override
   public StreamingContext getContext() {
      return LocalStreamingContext.INSTANCE;
   }

   @Override
   public SerializableRunnable getOnCloseHandler() {
      return onClose;
   }

   @Override
   public <U> MPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
      return new ReusableLocalPairStream<>(stream.collect(Collectors.groupingBy(function)));
   }

   @Override
   public boolean isEmpty() {
      return count() == 0;
   }

   @Override
   public Iterator<T> iterator() {
      return stream.iterator();
   }

   @Override
   public Stream<T> javaStream() {
      return stream;
   }

   @Override
   public MStream<T> limit(long number) {
      Preconditions.checkArgument(number >= 0, "Limit number must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      return new LocalStream<>(stream.limit(number));
   }

   @Override
   public <R> MStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
      return new LocalStream<>(stream.map(function));
   }

   @Override
   public MDoubleStream mapToDouble(@NonNull SerializableToDoubleFunction<? super T> function) {
      return new LocalDoubleStream(stream.mapToDouble(function));
   }

   @Override
   public <R, U> MPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
      return new LocalPairStream<>(stream.map(f -> Cast.<Map.Entry<R, U>>as(function.apply(f))));
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
   public void onClose(@NonNull SerializableRunnable closeHandler) {
      this.onClose = closeHandler;
      stream.onClose(closeHandler);
   }

   @Override
   public MStream<T> parallel() {
      if (stream.isParallel()) {
         return this;
      }
      return new LocalStream<>(stream.parallel());
   }

   @Override
   public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> accumulator) {
      return stream.reduce(accumulator);
   }

   @Override
   public MStream<T> repartition(int numPartitions) {
      return this;
   }

   @Override
   public MStream<T> sample(boolean withReplacement, int number) {
      Preconditions.checkArgument(number >= 0, "Sample size must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      Random random = new Random();
      if (withReplacement) {
         List<T> all = collect();
         List<T> sample = new ArrayList<>();
         while (sample.size() < number) {
            sample.add(all.get(random.nextInt(all.size())));
         }
         return new ReusableLocalStream<>(sample);
      } else {
         List<T> sample = new ArrayList<>();
         AtomicInteger k = new AtomicInteger(number + 1);
         stream.sequential().forEach(document -> {
            if (sample.size() < number) {
               sample.add(document);
            } else {
               int rndIndex = random.nextInt(k.getAndIncrement());
               if (rndIndex < number) {
                  sample.set(rndIndex, document);
               }
            }
         });
         return new ReusableLocalStream<>(sample);
      }
   }

   @Override
   @SneakyThrows
   public void saveAsTextFile(@NonNull Resource location) {
      try (BufferedWriter writer = new BufferedWriter(location.writer())) {
         stream.forEach(Unchecked.consumer(o -> {
                                              writer.write(Convert.convert(o, String.class));
                                              writer.newLine();
                                           }
                                          ));
      }
   }

   @Override
   public MStream<T> shuffle(@NonNull Random random) {
      return new LocalStream<>(stream.map(t -> $(random.nextInt(), t))
                                     .sorted(Comparator.comparing(e -> e.v1))
                                     .map(Map.Entry::getValue));
   }

   @Override
   public MStream<T> skip(long n) {
      return new LocalStream<>(stream.skip(n));
   }

   @Override
   public MStream<T> sorted(boolean ascending) {
      Comparator<T> comparator = Cast.as(ascending ? Sorting.natural() : Sorting.natural().reversed());
      return new LocalStream<>(stream.sorted(comparator));
   }

   @Override
   public <R extends Comparable<R>> MStream<T> sorted(boolean ascending, @NonNull SerializableFunction<? super T, ? extends R> keyFunction) {
      return new LocalStream<>(stream.sorted((t1, t2) -> keyFunction.apply(t1).compareTo(keyFunction.apply(t2))));
   }

   @Override
   public List<T> take(int n) {
      Preconditions.checkArgument(n >= 0, "N must be non-negative.");
      if (n == 0) {
         return Collections.emptyList();
      }
      return stream.limit(n).collect(Collectors.toList());
   }

   @Override
   public MStream<T> union(@NonNull MStream<T> other) {
      if (other.isReusable() && other.isEmpty()) {
         return this;
      }
      return new LocalStream<>(Stream.concat(stream, other.javaStream()));
   }

   @Override
   public <U> MPairStream<T, U> zip(@NonNull MStream<U> other) {
      return new LocalPairStream<>(Collect.zip(iterator(), other.iterator()));
   }

   @Override
   public MPairStream<T, Long> zipWithIndex() {
      final AtomicLong indexer = new AtomicLong();
      return new LocalPairStream<>(stream.map(t -> $(t, indexer.getAndIncrement())));
   }

   @Override
   public MStream<Iterable<T>> split(int n) {
      return cache().split(n);
   }

   @Override
   public MStream<Iterable<T>> partition(long partitionSize) {
      return new LocalStream<>(Streams.asStream(Iterators.partition(iterator(), (int) partitionSize)));
   }


}//END OF LocalStream
