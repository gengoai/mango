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

import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import lombok.NonNull;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collector;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public class TransformedStream<L, T> implements MStream<T> {

  protected final MStream<L> source;
  protected final SerializableFunction<L, T> converter;

  public TransformedStream(@NonNull MStream<L> source, @NonNull SerializableFunction<L, T> converter) {
    this.source = source;
    this.converter = converter;
  }


  @Override
  public void close() throws Exception {
    source.close();
  }

  private MStream<T> of(MStream<L> m) {
    return new TransformedStream<>(m, converter);
  }

  @Override
  public MStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
    return of(source.filter(l -> predicate.test(converter.apply(l))));
  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
    return source.map(l -> function.apply(converter.apply(l)));
  }

  @Override
  public <R> MStream<R> flatMap(@NonNull SerializableFunction<? super T, ? extends Iterable<? extends R>> mapper) {
    return source.flatMap(l -> mapper.apply(converter.apply(l)));
  }

  @Override
  public <R, U> MPairStream<R, U> flatMapToPair(@NonNull SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function) {
    return source.map(converter::apply).flatMapToPair(function);
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
    return source.map(converter::apply).mapToPair(function);
  }

  @Override
  public <U> MPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
    return source.map(converter::apply).groupBy(function);
  }

  @Override
  public <R> R collect(@NonNull Collector<? super T, T, R> collector) {
    return source.map(converter::apply).collect(collector);
  }

  @Override
  public List<T> collect() {
    return source.map(converter::apply).collect();
  }

  @Override
  public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> reducer) {
    return source.map(converter::apply).reduce(reducer);
  }

  @Override
  public T fold(T zeroValue, SerializableBinaryOperator<T> operator) {
    return source.map(converter::apply).fold(zeroValue, operator);
  }

  @Override
  public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
    source.forEach(l -> consumer.accept(converter.apply(l)));
  }

  @Override
  public Iterator<T> iterator() {
    return source.map(converter::apply).iterator();
  }

  @Override
  public Optional<T> first() {
    return source.first().map(converter::apply);
  }

  @Override
  public MStream<T> sample(int number) {
    return of(source.sample(number));
  }

  @Override
  public long count() {
    return source.count();
  }

  @Override
  public boolean isEmpty() {
    return source.isEmpty();
  }

  @Override
  public Map<T, Long> countByValue() {
    return source.map(converter::apply).countByValue();
  }

  @Override
  public MStream<T> distinct() {
    return source.map(converter::apply).distinct();
  }

  @Override
  public MStream<T> limit(long number) {
    return of(source.limit(number));
  }

  @Override
  public List<T> take(int n) {
    return source.take(n).stream().map(converter::apply).collect(Collectors.toList());
  }

  @Override
  public MStream<T> skip(long n) {
    return of(source.skip(n));
  }

  @Override
  public void onClose(Runnable closeHandler) {
    source.onClose(closeHandler);
  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    return source.map(converter::apply).sorted(ascending);
  }

  @Override
  public Optional<T> max(Comparator<? super T> comparator) {
    return source.map(converter::apply).max(comparator);
  }

  @Override
  public Optional<T> min(Comparator<? super T> comparator) {
    return source.map(converter::apply).min(comparator);
  }

  @Override
  public <U> MPairStream<T, U> zip(MStream<U> other) {
    return source.map(converter::apply).zip(other);
  }

  @Override
  public MPairStream<T, Long> zipWithIndex() {
    return source.map(converter::apply).zipWithIndex();
  }

  @Override
  public MDoubleStream mapToDouble(ToDoubleFunction<? super T> function) {
    return source.mapToDouble(l -> function.applyAsDouble(converter.apply(l)));
  }

  @Override
  public MStream<T> cache() {
    return of(source.cache());
  }

  @Override
  public MStream<T> union(MStream<T> other) {
    return source.map(converter::apply).union(other);
  }


}//END OF TransformedStream
