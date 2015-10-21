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

import com.davidbracewell.collection.EnhancedDoubleStatistics;
import com.davidbracewell.function.*;

import java.util.OptionalLong;
import java.util.PrimitiveIterator;
import java.util.stream.LongStream;

/**
 * @author David B. Bracewell
 */
public class JavaLongStream implements MLongStream {

  private final LongStream stream;

  public JavaLongStream(LongStream stream) {
    this.stream = stream;
  }


  @Override
  public void close() throws Exception {
    stream.close();
  }

  @Override
  public long sum() {
    return stream.sum();
  }

  @Override
  public OptionalLong first() {
    return stream.findFirst();
  }

  @Override
  public long count() {
    return stream.count();
  }

  @Override
  public <T> MStream<T> mapToObj(SerializableLongFunction<? extends T> function) {
    return new JavaMStream<>(stream.mapToObj(function));
  }

  @Override
  public MLongStream distinct() {
    return new JavaLongStream(stream.distinct());
  }

  @Override
  public boolean allMatch(SerializableLongPredicate predicate) {
    return stream.allMatch(predicate);
  }

  @Override
  public boolean anyMatch(SerializableLongPredicate predicate) {
    return stream.anyMatch(predicate);
  }

  @Override
  public boolean noneMatch(SerializableLongPredicate predicate) {
    return stream.noneMatch(predicate);
  }

  @Override
  public MLongStream filter(SerializableLongPredicate predicate) {
    return new JavaLongStream(stream.filter(predicate));
  }

  @Override
  public void forEach(SerializableLongConsumer consumer) {
    stream.forEach(consumer);
  }

  @Override
  public PrimitiveIterator.OfLong iterator() {
    return stream.iterator();
  }

  @Override
  public MLongStream limit(int n) {
    return new JavaLongStream(stream.limit(n));
  }

  @Override
  public MLongStream skip(int n) {
    return new JavaLongStream(stream.skip(n));
  }

  @Override
  public MDoubleStream mapToDouble(SerializableLongToDoubleFunction function) {
    return new JavaDoubleStream(stream.mapToDouble(function));
  }

  @Override
  public MLongStream map(SerializableLongUnaryOperator mapper) {
    return new JavaLongStream(stream.map(mapper));
  }

  @Override
  public OptionalLong min() {
    return stream.min();
  }

  @Override
  public OptionalLong max() {
    return stream.max();
  }

  @Override
  public double stddev() {
    return mapToDouble(l -> (double)l).stddev();
  }

  @Override
  public double mean() {
    return mapToDouble(l -> (double)l).mean();
  }

  @Override
  public EnhancedDoubleStatistics statistics() {
    return mapToDouble(l -> (double)l).statistics();
  }

  @Override
  public OptionalLong reduce(SerializableLongBinaryOperator operator) {
    return stream.reduce(operator);
  }

  @Override
  public long reduce(long zeroValue, SerializableLongBinaryOperator operator) {
    return stream.reduce(zeroValue,operator);
  }

  @Override
  public MLongStream sorted() {
    return new JavaLongStream(stream.sorted());
  }

  @Override
  public long[] toArray() {
    return stream.toArray();
  }

  @Override
  public MLongStream peek(SerializableLongConsumer action) {
    return new JavaLongStream(stream.peek(action));
  }

  @Override
  public MLongStream flatMap(SerializableLongFunction<long[]> mapper) {
    return new JavaLongStream(stream.flatMap(t -> LongStream.of(mapper.apply(t)).parallel()));
  }
}//END OF JavaLongStream
