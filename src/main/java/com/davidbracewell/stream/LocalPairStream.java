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
import com.davidbracewell.function.SerializableBiConsumer;
import com.davidbracewell.function.SerializableBiFunction;
import com.davidbracewell.function.SerializableBiPredicate;
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableComparator;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.function.SerializableRunnable;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class LocalPairStream<T, U> implements MPairStream<T, U>, Serializable {
  private static final long serialVersionUID = 1L;

  private volatile Stream<Entry<T, U>> stream;


  public LocalPairStream(Map<? extends T, ? extends U> map) {
    this(map.entrySet().stream());
  }

  public LocalPairStream(Stream<? extends Entry<? extends T, ? extends U>> stream) {
    this.stream = stream.map(Cast::as);
  }

  @Override
  public <V> MPairStream<T, Entry<U, V>> join(MPairStream<? extends T, ? extends V> other) {
    if (other == null) {
      return getContext().emptyPair();
    }
    Map<T, Iterable<V>> map = Cast.as(other.groupByKey().collectAsMap());
    return new LocalPairStream<>(stream.flatMap(e -> {
      List<Entry<T, Entry<U, V>>> list = new LinkedList<>();
      if (map.containsKey(e.getKey())) {
        map.get(e.getKey()).forEach(v -> list.add(Tuple2.of(e.getKey(), Tuple2.of(e.getValue(), v))));
      }
      return list.stream();
    }));
  }

  @Override
  public <V> MPairStream<T, Entry<U, V>> leftOuterJoin(MPairStream<? extends T, ? extends V> other) {
    if (other == null) {
      return Cast.as(this);
    }
    Map<T, Iterable<V>> map = Cast.as(other.groupByKey().collectAsMap());
    return new LocalPairStream<>(stream.flatMap(e -> {
      List<Entry<T, Entry<U, V>>> list = new LinkedList<>();
      if (map.containsKey(e.getKey())) {
        map.get(e.getKey()).forEach(v -> list.add(Tuple2.of(e.getKey(), Tuple2.of(e.getValue(), v))));
      } else {
        list.add(Tuple2.of(e.getKey(), Tuple2.of(e.getValue(), null)));
      }
      return list.stream();
    }));
  }

  @Override
  public <V> MPairStream<T, Entry<U, V>> rightOuterJoin(MPairStream<? extends T, ? extends V> other) {
    if (other == null) {
      return getContext().emptyPair();
    }

    Map<T, Iterable<U>> lhs = Cast.as(groupByKey().collectAsMap());
    List<Entry<T, V>> rhs = Cast.as(other.collectAsList());
    List<Entry<T, Entry<U, V>>> result = new ArrayList<>();
    rhs.forEach(e -> {
      if (lhs.containsKey(e.getKey())) {
        lhs.get(e.getKey()).forEach(u -> result.add(
          Tuple2.of(
            e.getKey(),
            Tuple2.of(u, e.getValue()
            )
          )));
      } else {
        result.add(Tuple2.of(e.getKey(), Tuple2.of(null, e.getValue())));
      }
    });
    return new LocalPairStream<>(result.stream());
  }

  @Override
  public MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator) {
    return groupByKey().mapToPair((t, u) -> Tuple2.of(t, Collect.stream(u).reduce(operator).orElse(null)));
  }


  @Override
  public void close() throws Exception {
    stream.close();
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
  public void forEachLocal(SerializableBiConsumer<? super T, ? super U> consumer) {
    stream.sequential().forEach(e -> {
      if (e == null) {
        consumer.accept(null, null);
      } else {
        consumer.accept(e.getKey(), e.getValue());
      }
    });
  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
    return new LocalStream<>(stream.map(e -> function.apply(e.getKey(), e.getValue())));
  }

  @Override
  public MPairStream<T, Iterable<U>> groupByKey() {
    return new LocalPairStream<>(
      stream.collect(Collectors.groupingBy(Entry::getKey))
        .entrySet()
        .stream()
        .map(e -> Tuple2.of(e.getKey(), e.getValue().stream().map(Entry::getValue).collect(Collectors.toList())))
    );
  }

  @Override
  public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Entry<? extends R, ? extends V>> function) {
    return new LocalPairStream<>(stream.map(entry -> Cast.as(function.apply(entry.getKey(), entry.getValue()))));
  }

  @Override
  public MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate) {
    return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getKey(), e.getValue())));
  }

  @Override
  public Map<T, U> collectAsMap() {
    return stream.collect(HashMap::new, (map, e) -> map.put(e.getKey(), e.getValue()), HashMap::putAll);
  }

  @Override
  public MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate) {
    return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getKey())));
  }

  @Override
  public MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate) {
    return new LocalPairStream<>(stream.filter(e -> predicate.test(e.getValue())));
  }


  @Override
  public StreamingContext getContext() {
    return JavaStreamingContext.INSTANCE;
  }

  @Override
  public List<Entry<T, U>> collectAsList() {
    return stream.map(Cast::<Entry<T, U>>as).collect(Collectors.toList());
  }

  @Override
  public long count() {
    List<Entry<T, U>> list = stream.collect(Collectors.toList());
    stream = list.stream();
    return list.size();
  }

  @Override
  public MStream<T> keys() {
    return new LocalStream<>(stream.map(Entry::getKey));
  }

  @Override
  public MPairStream<T, U> sortByKey(@NonNull SerializableComparator<T> comparator) {
    return new LocalPairStream<>(stream.sorted((o1, o2) -> comparator.compare(o1.getKey(), o2.getKey())));
  }

  @Override
  public MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other) {
    if (other == null) {
      return this;
    }
    Stream<Entry<T, U>> oStream = Cast.as(other.collectAsList().stream());
    return new LocalPairStream<>(Stream.concat(stream, oStream));
  }

  @Override
  public MStream<U> values() {
    return new LocalStream<>(stream.map(Entry::getValue));
  }

  @Override
  public MPairStream<T, U> parallel() {
    return new LocalPairStream<>(stream.parallel());
  }

  @Override
  public MPairStream<T, U> shuffle(Random random) {
    return new LocalPairStream<>(
      stream.map(t -> Tuple2.of(random.nextDouble(), t))
        .sorted(Entry.comparingByKey())
        .map(Tuple2::getValue)
    );
  }

  @Override
  public MPairStream<T, U> cache() {
    return this;
  }

  @Override
  public MPairStream<T, U> repartition(int partitions) {
    return this;
  }

  @Override
  public void onClose(@NonNull SerializableRunnable closeHandler) {
    stream.onClose(closeHandler);
  }
}//END OF LocalPairStream
