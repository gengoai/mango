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
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableDoubleBinaryOperator;
import com.davidbracewell.function.SerializableDoubleConsumer;
import com.davidbracewell.function.SerializableDoubleFunction;
import com.davidbracewell.function.SerializableDoublePredicate;
import com.davidbracewell.function.SerializableDoubleUnaryOperator;
import com.davidbracewell.function.SerializableRunnable;
import lombok.NonNull;

import java.io.Serializable;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;

/**
 * The type Java double stream.
 *
 * @author David B. Bracewell
 */
public class JavaDoubleStream implements MDoubleStream, Serializable {
  private static final long serialVersionUID = 1L;

  private final DoubleStream stream;

  /**
   * Instantiates a new Java double stream.
   *
   * @param stream the stream
   */
  public JavaDoubleStream(DoubleStream stream) {
    this.stream = stream;
  }

  @Override
  public void close() throws Exception {
    stream.close();
  }

  @Override
  public StreamingContext getContext() {
    return JavaStreamingContext.INSTANCE;
  }

  @Override
  public MDoubleStream cache() {
    return this;
  }

  @Override
  public MDoubleStream repartition(int numberOfPartition) {
    return this;
  }

  @Override
  public void onClose(@NonNull SerializableRunnable onCloseHandler) {
    stream.onClose(onCloseHandler);
  }

  @Override
  public double sum() {
    return stream.sum();
  }

  @Override
  public OptionalDouble min() {
    return stream.min();
  }

  @Override
  public OptionalDouble max() {
    return stream.max();
  }


  @Override
  public double stddev() {
    return stream.collect(EnhancedDoubleStatistics::new, EnhancedDoubleStatistics::accept, EnhancedDoubleStatistics::combine).getSampleStandardDeviation();
  }

  @Override
  public double mean() {
    return stream.collect(EnhancedDoubleStatistics::new, EnhancedDoubleStatistics::accept, EnhancedDoubleStatistics::combine).getAverage();
  }

  @Override
  public OptionalDouble first() {
    return stream.findFirst();
  }

  @Override
  public long count() {
    return stream.count();
  }

  @Override
  public <T> MStream<T> mapToObj(@NonNull SerializableDoubleFunction<? extends T> function) {
    return new JavaMStream<>(stream.mapToObj(function));
  }


  @Override
  public MDoubleStream distinct() {
    return new JavaDoubleStream(stream.distinct());
  }

  @Override
  public boolean allMatch(@NonNull SerializableDoublePredicate predicate) {
    return stream.allMatch(predicate);
  }

  @Override
  public boolean anyMatch(@NonNull SerializableDoublePredicate predicate) {
    return stream.anyMatch(predicate);
  }

  @Override
  public boolean noneMatch(@NonNull SerializableDoublePredicate predicate) {
    return stream.noneMatch(predicate);
  }

  @Override
  public MDoubleStream filter(@NonNull SerializableDoublePredicate predicate) {
    return new JavaDoubleStream(stream.filter(predicate));
  }

  @Override
  public void forEach(@NonNull SerializableDoubleConsumer consumer) {
    stream.forEach(consumer);
  }

  @Override
  public PrimitiveIterator.OfDouble iterator() {
    return stream.iterator();
  }

  @Override
  public MDoubleStream limit(int n) {
    return new JavaDoubleStream(stream.limit(n));
  }

  @Override
  public MDoubleStream skip(int n) {
    return new JavaDoubleStream(stream.skip(n));
  }

  @Override
  public MDoubleStream map(@NonNull SerializableDoubleUnaryOperator mapper) {
    return new JavaDoubleStream(stream.map(mapper));
  }

  @Override
  public OptionalDouble reduce(@NonNull SerializableDoubleBinaryOperator operator) {
    return stream.reduce(operator);
  }

  @Override
  public double reduce(double zeroValue, @NonNull SerializableDoubleBinaryOperator operator) {
    return stream.reduce(zeroValue, operator);
  }

  @Override
  public MDoubleStream sorted() {
    return new JavaDoubleStream(stream.sorted());
  }

  @Override
  public double[] toArray() {
    return stream.toArray();
  }

  @Override
  public MDoubleStream flatMap(@NonNull SerializableDoubleFunction<double[]> mapper) {
    return new JavaDoubleStream(
      stream.flatMap(d -> DoubleStream.of(mapper.apply(d)).parallel())
    );
  }

  @Override
  public MDoubleStream parallel() {
    return new JavaDoubleStream(stream.parallel());
  }

  @Override
  public MDoubleStream union(MDoubleStream other) {
    if (other == null) {
      return this;
    } else if (other instanceof JavaDoubleStream) {
      return new JavaDoubleStream(DoubleStream.concat(stream, Cast.<JavaDoubleStream>as(other).stream));
    }
    return new JavaDoubleStream(DoubleStream.concat(stream, DoubleStream.of(other.toArray())));
  }

}//END OF JavaDoubleStream
