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
import com.davidbracewell.function.*;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class JavaMPairStream<T, U> implements MPairStream<T, U>, Serializable {
  private static final long serialVersionUID = 1L;

  private final Stream<Map.Entry<T, U>> stream;


  public JavaMPairStream(Map<? extends T, ? extends U> map) {
    this(map.entrySet().stream());
  }

  public JavaMPairStream(Stream<? extends Map.Entry<? extends T, ? extends U>> stream) {
    this.stream = stream.map(Cast::as);
  }

  @Override
  public <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? extends T, ? extends V> other) {
    Map<T, Iterable<V>> map = Cast.as(other.groupByKey().collectAsMap());
    return new JavaMPairStream<>(stream.flatMap(e -> {
      List<Map.Entry<T, Map.Entry<U, V>>> list = new LinkedList<>();
      if (map.containsKey(e.getKey())) {
        map.get(e.getKey()).forEach(v -> list.add(Tuple2.of(e.getKey(), Tuple2.of(e.getValue(), v))));
      }
      return list.stream();
    }));
  }

  @Override
  public MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator) {
    return groupByKey().mapToPair((t, u) -> Tuple2.of(t, Collect.from(u).reduce(operator).orElse(null)));
  }


  @Override
  public void close() throws Exception {
    stream.close();
  }

  @Override
  public void forEach(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
    stream.forEach(e -> consumer.accept(e.getKey(), e.getValue()));
  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
    return new JavaMStream<>(stream.map(e -> function.apply(e.getKey(), e.getValue())));
  }

  @Override
  public MPairStream<T, Iterable<U>> groupByKey() {
    return new JavaMPairStream<>(
      stream.collect(Collectors.groupingBy(Map.Entry::getKey))
        .entrySet()
        .stream()
        .map(e -> Tuple2.of(e.getKey(), e.getValue().stream().map(Map.Entry::getValue).collect(Collectors.toList())))
    );
  }

  @Override
  public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
    return new JavaMPairStream<>(stream.map(entry -> Cast.as(function.apply(entry.getKey(), entry.getValue()))));
  }

  @Override
  public MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate) {
    return new JavaMPairStream<>(stream.filter(e -> predicate.test(e.getKey(), e.getValue())));
  }

  @Override
  public Map<T, U> collectAsMap() {
    return stream.collect(HashMap::new, (map, e) -> map.put(e.getKey(), e.getValue()), HashMap::putAll);
  }

  @Override
  public MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate) {
    return new JavaMPairStream<>(stream.filter(e -> predicate.test(e.getKey())));
  }

  @Override
  public MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate) {
    return new JavaMPairStream<>(stream.filter(e -> predicate.test(e.getValue())));
  }


  @Override
  public List<Map.Entry<T, U>> collectAsList() {
    return stream.map(Cast::<Map.Entry<T, U>>as).collect(Collectors.toList());
  }

  @Override
  public long count() {
    return stream.count();
  }

  @Override
  public MStream<T> keys() {
    return new JavaMStream<>(stream.map(Map.Entry::getKey));
  }

  @Override
  public MPairStream<T, U> sortByKey(SerializableComparator<T> comparator) {
    return new JavaMPairStream<>(stream.sorted((o1, o2) -> comparator.compare(o1.getKey(), o2.getKey())));
  }

  @Override
  public MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other) {
    if (other instanceof SparkPairStream) {
      return Cast.as(other.union(Cast.as(this)));
    }
    return new JavaMPairStream<>(Stream.concat(stream, Cast.<JavaMPairStream<T, U>>as(other).stream));
  }

  @Override
  public MStream<U> values() {
    return new JavaMStream<>(stream.map(Map.Entry::getValue));
  }

  @Override
  public MPairStream<T, U> parallel() {
    return new JavaMPairStream<>(stream.parallel());
  }

}//END OF JavaMPairStream
