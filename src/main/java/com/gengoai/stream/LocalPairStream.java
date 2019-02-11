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

import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import com.gengoai.function.*;
import com.gengoai.tuple.Tuple2;
import com.gengoai.tuple.Tuples;

import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Java stream implementation of a MPairStream
 *
 * @param <T> the key type parameter
 * @param <U> the value type parameter
 * @author David B. Bracewell
 */
class LocalPairStream<T, U> extends BaseJavaPairStream<T, U> {
   private static final long serialVersionUID = 1L;
   private final CacheStrategy cacheStrategy;
   private SerializableSupplier<Stream<Map.Entry<T, U>>> streamSupplier;


   public LocalPairStream(Stream<Map.Entry<? extends T, ? extends U>> stream) {
      this(() -> stream, CacheStrategy.InMemory);
   }

   public LocalPairStream(SerializableSupplier<Stream<? extends Map.Entry<? extends T, ? extends U>>> streamSupplier,
                          CacheStrategy cacheStrategy
                         ) {
      this.cacheStrategy = cacheStrategy;
      this.streamSupplier = Cast.as(streamSupplier);
   }

   /**
    * Instantiates a new Local pair stream.
    *
    * @param map the map
    */
   public LocalPairStream(Map<? extends T, ? extends U> map) {
      this(() -> map.entrySet().stream(), CacheStrategy.InMemory);
   }

   @Override
   public MPairStream<T, U> cache() {
      return Cast.as(cacheStrategy.cachePairStream(Cast.as(javaStream())));
   }

   @Override
   public void close() throws Exception {
      streamSupplier.get().close();
   }

   @Override
   public MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate) {
      return new LocalPairStream<>(() -> streamSupplier.get().filter(e -> predicate.test(e.getKey(), e.getValue())),
                                   cacheStrategy);
   }

   @Override
   public MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate) {
      return new LocalPairStream<>(() -> streamSupplier.get().filter(e -> predicate.test(e.getKey())), cacheStrategy);
   }

   @Override
   public MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate) {
      return new LocalPairStream<>(() -> streamSupplier.get().filter(e -> predicate.test(e.getValue())), cacheStrategy);
   }

   @Override
   public <R, V> MPairStream<R, V> flatMapToPair(SerializableBiFunction<? super T, ? super U, Stream<Map.Entry<? extends R, ? extends V>>> function) {
      return new LocalPairStream<>(() -> streamSupplier.get().flatMap(e -> function.apply(e.getKey(), e.getValue())),
                                   cacheStrategy);
   }

   @Override
   public MPairStream<T, Iterable<U>> groupByKey() {
      return new LocalPairStream<>(() -> streamSupplier.get()
                                                       .collect(Collectors.groupingBy(Map.Entry::getKey))
                                                       .entrySet()
                                                       .stream()
                                                       .map(e -> Tuples.$(e.getKey(), e.getValue().stream()
                                                                                       .map(Map.Entry::getValue)
                                                                                       .collect(
                                                                                          Collectors.toList()))),
                                   cacheStrategy);

   }

   @Override
   public Stream<Map.Entry<T, U>> javaStream() {
      return streamSupplier.get();
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? extends T, ? extends V> other) {
      Map<T, Iterable<V>> rhs = Cast.as(other.groupByKey().collectAsMap());
      return flatMapToPair((k, v) -> {
         if (rhs.containsKey(k)) {
            return Streams.asStream(rhs.get(k)).map(rv -> Tuples.$(k, Tuples.$(v, rv)));
         }
         return Stream.empty();
      });
   }

   @Override
   public MStream<T> keys() {
      return new LocalStream<>(() -> javaStream().map(Map.Entry::getKey), cacheStrategy);
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> leftOuterJoin(MPairStream<? extends T, ? extends V> other) {
      Map<T, Iterable<V>> rhs = Cast.as(other.groupByKey().collectAsMap());
      return flatMapToPair((k, v) -> {
         if (rhs.containsKey(k)) {
            return Streams.asStream(rhs.get(k)).map(rv -> Tuples.$(k, Tuples.$(v, rv)));
         } else {
            return Stream.of(Tuples.$(k, Tuples.$(v, null)));
         }
      });
   }

   @Override
   public <R> MStream<R> map(SerializableBiFunction<? super T, ? super U, ? extends R> function) {
      return new LocalStream<>(() -> streamSupplier.get().map(e -> function.apply(e.getKey(), e.getValue())),
                               cacheStrategy);
   }

   @Override
   public MDoubleStream mapToDouble(SerializableToDoubleBiFunction<? super T, ? super U> function) {
      return new LocalDoubleStream(
         streamSupplier.get().mapToDouble(e -> function.applyAsDouble(e.getKey(), e.getValue())));
   }

   @Override
   public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
      return new LocalPairStream<>(() -> streamSupplier.get().map(e -> function.apply(e.getKey(), e.getValue())),
                                   cacheStrategy);
   }

   @Override
   public MPairStream<T, U> onClose(SerializableRunnable closeHandler) {
      return new LocalPairStream<>(() -> streamSupplier.get().onClose(closeHandler), cacheStrategy);
   }

   @Override
   public MPairStream<T, U> parallel() {
      return new LocalPairStream<>(() -> streamSupplier.get().parallel(), cacheStrategy);
   }

   @Override
   public MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator) {
      return groupByKey().mapToPair((t, u) -> Tuple2.of(t, Streams.asStream(u).reduce(operator).orElse(null)));
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> rightOuterJoin(MPairStream<? extends T, ? extends V> other) {
      Map<T, Iterable<U>> lhs = Cast.as(groupByKey().collectAsMap());
      return other.flatMapToPair((k, v) -> {
         if (lhs.containsKey(k)) {
            return Streams.asStream(lhs.get(k)).map(lv -> Tuples.$(k, Tuples.$(lv, v)));
         } else {
            return Stream.of(Tuples.$(k, Tuples.$(null, v)));
         }
      });
   }

   @Override
   public MPairStream<T, U> sample(boolean withReplacement, long number) {
      return new LocalPairStream<>(() -> new LocalStream<>(streamSupplier.get()).sample(withReplacement, (int) number)
                                                                                .javaStream(),
                                   cacheStrategy);
   }

   @Override
   public MPairStream<T, U> shuffle(Random random) {
      return new LocalPairStream<>(() -> new LocalStream<>(streamSupplier.get()).shuffle(random).javaStream(),
                                   cacheStrategy);
   }

   @Override
   public MPairStream<T, U> sortByKey(SerializableComparator<T> comparator) {
      return new LocalPairStream<>(
         () -> streamSupplier.get().sorted((o1, o2) -> comparator.compare(o1.getKey(), o2.getKey())), cacheStrategy);
   }

   @Override
   public MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other) {
      if (other.isDistributed()) {
         return Cast.as(other.union(Cast.as(this)));
      }
      return new LocalPairStream<>(() -> Stream.concat(streamSupplier.get(), other.javaStream()), cacheStrategy);
   }

   @Override
   public MStream<U> values() {
      return new LocalStream<>(() -> javaStream().map(Map.Entry::getValue), cacheStrategy);
   }

}//END OF LocalPairStream
