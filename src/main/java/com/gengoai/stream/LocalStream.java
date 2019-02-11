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

package com.gengoai.stream;

import com.gengoai.Validation;
import com.gengoai.collection.Iterators;
import com.gengoai.collection.Sorting;
import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import com.gengoai.function.*;
import com.gengoai.tuple.Tuples;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A wrapper around Java's <code>Stream</code> class.
 *
 * @param <T> the component type of the stream
 * @author David B. Bracewell
 */
public class LocalStream<T> extends BaseJavaStream<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final CacheStrategy cacheStrategy;
   private final SerializableSupplier<Stream<T>> streamSupplier;

   /**
    * Instantiates a new local stream from a Java stream
    *
    * @param stream the Java stream to wrap
    */
   public LocalStream(final Stream<T> stream) {
      this(() -> stream, CacheStrategy.InMemory);
   }

   public LocalStream(final SerializableSupplier<Stream<T>> streamSerializableSupplier, final CacheStrategy cacheStrategy) {
      this.streamSupplier = streamSerializableSupplier;
      this.cacheStrategy = cacheStrategy;
   }

   @Override
   public MStream<T> cache() {
      return Cast.as(cacheStrategy.cacheStream(streamSupplier.get()));
   }

   @Override
   public void close() throws IOException {
      try {
         streamSupplier.get().close();
      } catch (UnsupportedOperationException uoe) {
         //noopt
      }
   }

   @Override
   public MStream<T> distinct() {
      return new LocalStream<>(() -> streamSupplier.get().distinct(), cacheStrategy);
   }

   @Override
   public MStream<T> filter(SerializablePredicate<? super T> predicate) {
      return new LocalStream<>(() -> streamSupplier.get().filter(predicate), cacheStrategy);
   }

   @Override
   public <R> MStream<R> flatMap(SerializableFunction<? super T, Stream<? extends R>> mapper) {
      return new LocalStream<>(() -> streamSupplier.get().flatMap(mapper), Cast.as(cacheStrategy));
   }

   @Override
   public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, Stream<? extends Map.Entry<? extends R, ? extends U>>> function) {
      return new LocalPairStream<>(() -> streamSupplier.get().flatMap(function), cacheStrategy);
   }


   @Override
   public StreamingContext getContext() {
      return LocalStreamingContext.INSTANCE;
   }


   @Override
   public <U> MPairStream<U, Iterable<T>> groupBy(SerializableFunction<? super T, ? extends U> function) {
      return new LocalPairStream<>(() -> streamSupplier.get()
                                                       .collect(Collectors.groupingBy(function))
                                                       .entrySet()
                                                       .stream(), cacheStrategy);
   }

   @Override
   public MStream<T> intersection(MStream<T> other) {
      if (other.isDistributed()) {
         return other.intersection(this);
      }
      final Set<T> set = other.collect(Collectors.toSet());
      return filter(set::contains);
   }

   @Override
   public Iterator<T> iterator() {
      return streamSupplier.get().iterator();
   }

   @Override
   public Stream<T> javaStream() {
      return streamSupplier.get();
   }

   @Override
   public MStream<T> limit(long number) {
      Validation.checkArgument(number >= 0, "Limit number must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      return new LocalStream<>(() -> streamSupplier.get().limit(number), cacheStrategy);
   }

   @Override
   public <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function) {
      return new LocalStream<>(() -> streamSupplier.get().map(function), cacheStrategy);
   }

   @Override
   public MDoubleStream mapToDouble(SerializableToDoubleFunction<? super T> function) {
      return new LocalDoubleStream(streamSupplier.get().mapToDouble(function));
   }

   @Override
   public <R, U> MPairStream<R, U> mapToPair(SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
      return new LocalPairStream<>(() -> streamSupplier.get().map(f -> Cast.<Map.Entry<R, U>>as(function.apply(f))),
                                   cacheStrategy);
   }

   @Override
   public MStream<T> onClose(SerializableRunnable closeHandler) {
      return new LocalStream<>(() -> streamSupplier.get().onClose(closeHandler), cacheStrategy);
   }

   @Override
   public MStream<T> parallel() {
      if (streamSupplier.get().isParallel()) {
         return this;
      }
      return new LocalStream<>(() -> streamSupplier.get().parallel(), cacheStrategy);
   }

   @Override
   public MStream<Stream<T>> partition(long partitionSize) {
      return new LocalStream<>(() -> Cast.as(Streams.partition(streamSupplier.get(), partitionSize)),
                               cacheStrategy);
   }

   //Further test to see when / if this randomOrder is faster than current approach
   private Comparator<T> randomOrder() {
      ThreadLocalRandom tlr = ThreadLocalRandom.current();
      long m1 = tlr.nextLong();
      return Comparator.comparingDouble(l -> {
         Random r = new Random(l.hashCode() + m1);
         return r.nextDouble();
      });
   }

   @Override
   public MStream<T> repartition(int numPartitions) {
      return this;
   }

   @Override
   public MStream<T> sample(boolean withReplacement, int number) {
      Validation.checkArgument(number >= 0, "Sample size must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      if (withReplacement) {
         return cache().sample(true, number);
      } else {
         return shuffle(new Random()).limit(number);
      }
   }

   @Override
   public MStream<T> shuffle(Random random) {
      return new LocalStream<>(() -> streamSupplier.get() // .sorted(randomOrder()));
                                                   .map(t -> Tuples.$(random.nextInt(), t))
                                                   .sorted(Comparator.comparing(e -> e.v1))
                                                   .map(Map.Entry::getValue), cacheStrategy);
   }

   @Override
   public MStream<T> skip(long n) {
      return new LocalStream<>(() -> streamSupplier.get().skip(n), cacheStrategy);
   }

   @Override
   public MStream<T> sorted(boolean ascending) {
      Comparator<T> comparator = Cast.as(ascending ? Sorting.natural() : Sorting.natural().reversed());
      return new LocalStream<>(() -> streamSupplier.get().sorted(comparator), cacheStrategy);
   }

   @Override
   public <R extends Comparable<R>> MStream<T> sortBy(boolean ascending, SerializableFunction<? super T, ? extends R> keyFunction) {
      final Comparator<T> comparator = ascending ? Comparator.comparing(keyFunction)
                                                 : Cast.as(Comparator.comparing(keyFunction).reversed());
      return new LocalStream<>(() -> streamSupplier.get().sorted(comparator), cacheStrategy);
   }

   @Override
   public MStream<T> union(MStream<T> other) {
      return new LocalStream<>(() -> Stream.concat(streamSupplier.get(), other.javaStream()), cacheStrategy);
   }

   @Override
   public <U> MPairStream<T, U> zip(MStream<U> other) {
      return new LocalPairStream<>(() -> Streams.asStream(Iterators.zip(iterator(), other.iterator())), cacheStrategy);
   }

   @Override
   public MPairStream<T, Long> zipWithIndex() {
      final AtomicLong indexer = new AtomicLong();
      return new LocalPairStream<>(() -> streamSupplier.get().map(t -> Tuples.$(t, indexer.getAndIncrement())),
                                   cacheStrategy);
   }

}//END OF LocalStream
