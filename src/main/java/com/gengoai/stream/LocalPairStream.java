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
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A Java stream implementation of a MPairStream
 *
 * @param <T> the key type parameter
 * @param <U> the value type parameter
 * @author David B. Bracewell
 */
class LocalPairStream<T, U> implements MPairStream<T, U>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Stream<Entry<T, U>> stream;
   private SerializableRunnable onClose;

   /**
    * Instantiates a new Local pair stream.
    *
    * @param map the map
    */
   public LocalPairStream(Map<? extends T, ? extends U> map) {
      this(map.entrySet().stream().map(e -> Tuples.$(e.getKey(), e.getValue())).collect(Collectors.toList()).stream());
   }

   /**
    * Instantiates a new Local pair stream.
    *
    * @param stream the stream
    */
   public LocalPairStream(Stream<? extends Entry<? extends T, ? extends U>> stream) {
      this.stream = stream.map(Cast::as);
   }

   @Override
   public MPairStream<T, U> cache() {
      return new ReusableLocalPairStream<>(collectAsMap());
   }

   @Override
   public void close() throws Exception {
      stream.close();
   }

   @Override
   public List<Entry<T, U>> collectAsList() {
      return stream.collect(Collectors.toList());
   }

   @Override
   public Map<T, U> collectAsMap() {
      return stream.collect(HashMap::new, (map, e) -> map.put(e.getKey(), e.getValue()), HashMap::putAll);
   }

   @Override
   public long count() {
      return stream.count();
   }

   @Override
   public MPairStream<T, U> filter(@NonNull SerializableBiPredicate<? super T, ? super U> predicate) {
      return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getKey(), e.getValue())));
   }

   @Override
   public MPairStream<T, U> filterByKey(@NonNull SerializablePredicate<T> predicate) {
      return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getKey())));
   }

   @Override
   public MPairStream<T, U> filterByValue(@NonNull SerializablePredicate<U> predicate) {
      return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getValue())));
   }

   @Override
   public void forEach(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
      stream.forEach(e -> {
         if (e == null) {
            consumer.accept(null, null);
         } else {
            consumer.accept(e.getKey(), e.getValue());
         }
      });
   }

   @Override
   public void forEachLocal(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
      stream.sequential().forEach(e -> {
         if (e == null) {
            consumer.accept(null, null);
         } else {
            consumer.accept(e.getKey(), e.getValue());
         }
      });
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
   public MPairStream<T, Iterable<U>> groupByKey() {
      return new LocalPairStream<>(stream.collect(Collectors.groupingBy(Entry::getKey)).entrySet()
                                         .stream()
                                         .map(e -> Tuples.$(e.getKey(), e.getValue().stream()
                                                                         .map(Entry::getValue)
                                                                         .collect(
                                                                     Collectors.toList()))));

   }

   @Override
   public boolean isEmpty() {
      return stream.count() == 0;
   }

   @Override
   public <V> MPairStream<T, Entry<U, V>> join(@NonNull MPairStream<? extends T, ? extends V> other) {
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
      return new LocalStream<>(stream.map(Entry::getKey));
   }

   @Override
   public <V> MPairStream<T, Entry<U, V>> leftOuterJoin(@NonNull MPairStream<? extends T, ? extends V> other) {
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
   public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
      return new LocalStream<>(stream.map(e -> function.apply(e.getKey(), e.getValue())));
   }

   @Override
   public MDoubleStream mapToDouble(@NonNull SerializableToDoubleBiFunction<? super T, ? super U> function) {
      return new LocalDoubleStream(stream.mapToDouble(e -> function.applyAsDouble(e.getKey(), e.getValue())));
   }

   @Override
   public <R, V> MPairStream<R, V> mapToPair(@NonNull SerializableBiFunction<? super T, ? super U, ? extends Entry<? extends R, ? extends V>> function) {
      return new LocalPairStream<>(stream.map(entry -> Cast.as(function.apply(entry.getKey(), entry.getValue()))));
   }

   @Override
   public Optional<Entry<T, U>> max(@NonNull SerializableComparator<Entry<T, U>> comparator) {
      return stream.max(comparator);
   }

   @Override
   public Optional<Entry<T, U>> min(@NonNull SerializableComparator<Entry<T, U>> comparator) {
      return stream.min(comparator);
   }

   @Override
   public void onClose(SerializableRunnable closeHandler) {
      this.onClose = closeHandler;
      stream.onClose(closeHandler);
   }

   @Override
   public MPairStream<T, U> parallel() {
      return new LocalPairStream<>(stream.parallel());
   }

   @Override
   public MPairStream<T, U> reduceByKey(@NonNull SerializableBinaryOperator<U> operator) {
      return groupByKey().mapToPair((t, u) -> Tuple2.of(t, Streams.asStream(u).reduce(operator).orElse(null)));
   }

   @Override
   public MPairStream<T, U> repartition(int partitions) {
      return this;
   }

   @Override
   public <R, V> MPairStream<R, V> flatMapToPair(@NonNull SerializableBiFunction<? super T, ? super U, Stream<Entry<? extends R, ? extends V>>> function) {
      return new LocalPairStream<>(stream.flatMap(e -> function.apply(e.getKey(), e.getValue())));
   }

   @Override
   public <V> MPairStream<T, Entry<U, V>> rightOuterJoin(@NonNull MPairStream<? extends T, ? extends V> other) {
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
   public MPairStream<T, U> shuffle(@NonNull Random random) {
      return new LocalPairStream<>(stream.sorted(new RandomComparator<>(random)));
   }

   @Override
   public MPairStream<T, U> sortByKey(@NonNull SerializableComparator<T> comparator) {
      return new LocalPairStream<>(stream.sorted((o1, o2) -> comparator.compare(o1.getKey(), o2.getKey())));
   }

   @Override
   public MPairStream<T, U> union(@NonNull MPairStream<? extends T, ? extends U> other) {
      if (other.isReusable() && other.isEmpty()) {
         return this;
      }
      Stream<Entry<T, U>> oStream = Cast.as(other.collectAsList().stream());
      return new LocalPairStream<>(Stream.concat(stream, oStream));
   }

   @Override
   public MStream<U> values() {
      return new LocalStream<>(stream.map(Entry::getValue));
   }

}//END OF LocalPairStream
