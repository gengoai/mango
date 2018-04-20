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
import lombok.NonNull;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p>A reusable non-distributed pair stream backed by a map.</p>
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
class ReusableLocalPairStream<K, V> implements MPairStream<K, V> {
   private final Map<K, V> backingMap;
   private SerializableRunnable onClose;
   private boolean parallel = false;

   /**
    * Instantiates a new Reusable local pair stream.
    *
    * @param backingMap the backing map
    */
   public ReusableLocalPairStream(@NonNull Map<? extends K, ? extends V> backingMap) {
      this.backingMap = Cast.as(backingMap);
   }

   @Override
   public MPairStream<K, V> cache() {
      return this;
   }

   @Override
   public void close() throws Exception {
      if (onClose != null) {
         onClose.run();
      }
   }

   @Override
   public List<Map.Entry<K, V>> collectAsList() {
      return backingMap.entrySet().stream().collect(Collectors.toList());
   }

   @Override
   public Map<K, V> collectAsMap() {
      return new HashMap<>(backingMap);
   }

   @Override
   public long count() {
      return backingMap.size();
   }

   @Override
   public MPairStream<K, V> filter(@NonNull SerializableBiPredicate<? super K, ? super V> predicate) {
      return toStream().filter(predicate);
   }

   @Override
   public MPairStream<K, V> filterByKey(@NonNull SerializablePredicate<K> predicate) {
      return toStream().filterByKey(predicate);
   }

   @Override
   public MPairStream<K, V> filterByValue(@NonNull SerializablePredicate<V> predicate) {
      return toStream().filterByValue(predicate);
   }

   @Override
   public <R, V1> MPairStream<R, V1> flatMapToPair(@NonNull SerializableBiFunction<? super K, ? super V, Stream<Map.Entry<? extends R, ? extends V1>>> function) {
      return toStream().flatMapToPair(function);
   }

   @Override
   public void forEach(@NonNull SerializableBiConsumer<? super K, ? super V> consumer) {
      backingMap.forEach(consumer);
   }

   @Override
   public void forEachLocal(SerializableBiConsumer<? super K, ? super V> consumer) {
      backingMap.forEach(consumer);
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
   public MPairStream<K, Iterable<V>> groupByKey() {
      return toStream().groupByKey();
   }

   @Override
   public boolean isEmpty() {
      return backingMap.isEmpty();
   }

   @Override
   public boolean isReusable() {
      return true;
   }

   @Override
   public <V1> MPairStream<K, Map.Entry<V, V1>> join(MPairStream<? extends K, ? extends V1> stream) {
      return toStream().join(stream);
   }

   @Override
   public MStream<K> keys() {
      MStream<K> stream = new ReusableLocalStream<>(backingMap.keySet());
      if (parallel) {
         stream = stream.parallel();
      }
      return stream;
   }

   @Override
   public <V1> MPairStream<K, Map.Entry<V, V1>> leftOuterJoin(MPairStream<? extends K, ? extends V1> stream) {
      return toStream().leftOuterJoin(stream);
   }

   @Override
   public <R> MStream<R> map(@NonNull SerializableBiFunction<? super K, ? super V, ? extends R> function) {
      return toStream().map(function);
   }

   @Override
   public MDoubleStream mapToDouble(@NonNull SerializableToDoubleBiFunction<? super K, ? super V> function) {
      return new LocalDoubleStream(backingMap.entrySet()
                                             .stream()
                                             .mapToDouble(e -> function.applyAsDouble(e.getKey(), e.getValue())));
   }

   @Override
   public <R, V1> MPairStream<R, V1> mapToPair(@NonNull SerializableBiFunction<? super K, ? super V, ? extends Map.Entry<? extends R, ? extends V1>> function) {
      return toStream().mapToPair(function);
   }

   @Override
   public Optional<Map.Entry<K, V>> max(@NonNull SerializableComparator<Map.Entry<K, V>> comparator) {
      return toStream().max(comparator);
   }

   @Override
   public Optional<Map.Entry<K, V>> min(@NonNull SerializableComparator<Map.Entry<K, V>> comparator) {
      return toStream().min(comparator);
   }

   @Override
   public void onClose(SerializableRunnable closeHandler) {
      this.onClose = closeHandler;
   }

   @Override
   public MPairStream<K, V> parallel() {
      this.parallel = true;
      return this;
   }

   @Override
   public MPairStream<K, V> reduceByKey(@NonNull SerializableBinaryOperator<V> operator) {
      return groupByKey().mapToPair((t, u) -> Tuple2.of(t, Streams.asStream(u).reduce(operator).orElse(null)));
   }

   @Override
   public MPairStream<K, V> repartition(int partitions) {
      return this;
   }

   @Override
   public <V1> MPairStream<K, Map.Entry<V, V1>> rightOuterJoin(MPairStream<? extends K, ? extends V1> stream) {
      return toStream().rightOuterJoin(stream);
   }

   @Override
   public MPairStream<K, V> shuffle(Random random) {
      return toStream().shuffle();
   }

   @Override
   public MPairStream<K, V> sortByKey(@NonNull SerializableComparator<K> comparator) {
      return toStream().sortByKey(comparator);
   }

   private MPairStream<K, V> toStream() {
      MPairStream<K, V> stream = new LocalPairStream<>(backingMap);
      if (parallel) {
         stream = stream.parallel();
      }
      return stream;
   }

   @Override
   public MPairStream<K, V> union(MPairStream<? extends K, ? extends V> other) {
      return toStream().union(other);
   }

   @Override
   public MStream<V> values() {
      return new ReusableLocalStream<>(backingMap.values());
   }

}//END OF ReusableLocalPairStream
