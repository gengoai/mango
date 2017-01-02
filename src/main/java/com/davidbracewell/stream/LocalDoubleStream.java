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

import com.davidbracewell.EnhancedDoubleStatistics;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.Serializable;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;

/**
 * <p>Implementation of a <code>MDoubleStream</code> that wraps a Java <code>DoubleStream</code>.</p>
 *
 * @author David B. Bracewell
 */
class LocalDoubleStream implements MDoubleStream, Serializable {
   private static final long serialVersionUID = 1L;

   private final DoubleStream stream;
   private SerializableRunnable onCloseHandler;

   /**
    * Instantiates a new Local double stream.
    *
    * @param stream the double stream to wrap
    */
   LocalDoubleStream(@NonNull DoubleStream stream) {
      this.stream = stream;
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
   public MDoubleStream cache() {
      return new ReusableDoubleStream(stream.toArray());
   }

   @Override
   public void close() throws Exception {
      stream.close();
   }

   @Override
   public long count() {
      return stream.count();
   }

   @Override
   public MDoubleStream distinct() {
      return new LocalDoubleStream(stream.distinct());
   }

   @Override
   public MDoubleStream filter(@NonNull SerializableDoublePredicate predicate) {
      return new LocalDoubleStream(stream.filter(predicate));
   }

   @Override
   public OptionalDouble first() {
      return stream.findFirst();
   }

   @Override
   public MDoubleStream flatMap(@NonNull SerializableDoubleFunction<double[]> mapper) {
      return new LocalDoubleStream(stream.flatMap(d -> DoubleStream.of(mapper.apply(d))));
   }

   @Override
   public void forEach(@NonNull SerializableDoubleConsumer consumer) {
      stream.forEach(consumer);
   }

   @Override
   public StreamingContext getContext() {
      return LocalStreamingContext.INSTANCE;
   }

   @Override
   public SerializableRunnable getOnCloseHandler() {
      return onCloseHandler;
   }

   @Override
   public boolean isEmpty() {
      return count() == 0;
   }

   @Override
   public PrimitiveIterator.OfDouble iterator() {
      return stream.iterator();
   }

   @Override
   public MDoubleStream limit(int n) {
      Preconditions.checkArgument(n >= 0, "Limit number must be non-negative.");
      return new LocalDoubleStream(stream.limit(n));
   }

   @Override
   public MDoubleStream map(@NonNull SerializableDoubleUnaryOperator mapper) {
      return new LocalDoubleStream(stream.map(mapper));
   }

   @Override
   public <T> MStream<T> mapToObj(@NonNull SerializableDoubleFunction<? extends T> function) {
      return new LocalStream<>(stream.mapToObj(function));
   }

   @Override
   public OptionalDouble max() {
      return stream.max();
   }

   @Override
   public double mean() {
      return statistics().getAverage();
   }

   @Override
   public OptionalDouble min() {
      return stream.min();
   }

   @Override
   public boolean noneMatch(@NonNull SerializableDoublePredicate predicate) {
      return stream.noneMatch(predicate);
   }

   @Override
   public void onClose(@NonNull SerializableRunnable onCloseHandler) {
      this.onCloseHandler = onCloseHandler;
      stream.onClose(onCloseHandler);
   }

   @Override
   public MDoubleStream parallel() {
      return new LocalDoubleStream(stream.parallel());
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
   public MDoubleStream repartition(int numberOfPartition) {
      return this;
   }

   @Override
   public MDoubleStream skip(int n) {
      return new LocalDoubleStream(stream.skip(n));
   }

   @Override
   public MDoubleStream sorted(boolean ascending) {
      if (ascending) {
         return new LocalDoubleStream(stream.sorted());
      }
      return new LocalDoubleStream(stream.mapToObj(Double::valueOf)
                                         .sorted((d1, d2) -> -Double.compare(d1, d2))
                                         .mapToDouble(d -> d));
   }

   @Override
   public EnhancedDoubleStatistics statistics() {
      return stream.collect(EnhancedDoubleStatistics::new, EnhancedDoubleStatistics::accept,
                            EnhancedDoubleStatistics::combine);
   }

   @Override
   public double stddev() {
      return statistics().getSampleStandardDeviation();
   }

   @Override
   public double sum() {
      return stream.sum();
   }

   @Override
   public double[] toArray() {
      return stream.toArray();
   }

   @Override
   public MDoubleStream union(@NonNull MDoubleStream other) {
      if (other.isReusable() && other.isEmpty()) {
         return this;
      } else if (other instanceof LocalDoubleStream) {
         return new LocalDoubleStream(DoubleStream.concat(stream, Cast.<LocalDoubleStream>as(other).stream));
      }
      return new LocalDoubleStream(DoubleStream.concat(stream, DoubleStream.of(other.toArray())));
   }
}//END OF LocalDoubleStream
