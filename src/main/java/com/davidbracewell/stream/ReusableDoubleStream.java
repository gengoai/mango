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

import com.davidbracewell.Math2;
import com.davidbracewell.function.*;
import lombok.NonNull;

import java.util.Arrays;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;
import java.util.stream.DoubleStream;

/**
 * @author David B. Bracewell
 */
public class ReusableDoubleStream implements MDoubleStream {
   private final double[] array;
   private SerializableRunnable onCloseHandler;
   private boolean parallel = false;

   public ReusableDoubleStream(@NonNull double[] array) {
      this.array = array;
   }

   private MDoubleStream getStream() {
      MDoubleStream stream = new LocalDoubleStream(DoubleStream.of(array));
      if (parallel) {
         stream = stream.parallel();
      }
      return stream;
   }

   @Override
   public boolean allMatch(@NonNull SerializableDoublePredicate predicate) {
      return getStream().allMatch(predicate);
   }

   @Override
   public boolean anyMatch(@NonNull SerializableDoublePredicate predicate) {
      return getStream().anyMatch(predicate);
   }

   @Override
   public MDoubleStream cache() {
      return this;
   }

   @Override
   public void close() throws Exception {
      if (onCloseHandler != null) {
         onCloseHandler.run();
      }
   }

   @Override
   public long count() {
      return array.length;
   }

   @Override
   public MDoubleStream distinct() {
      return getStream().distinct();
   }

   @Override
   public MDoubleStream filter(@NonNull SerializableDoublePredicate predicate) {
      return getStream().filter(predicate);
   }

   @Override
   public OptionalDouble first() {
      return array.length > 0 ? OptionalDouble.of(array[0]) : OptionalDouble.empty();
   }

   @Override
   public MDoubleStream flatMap(@NonNull SerializableDoubleFunction<double[]> mapper) {
      return getStream().flatMap(mapper);
   }

   @Override
   public void forEach(@NonNull SerializableDoubleConsumer consumer) {
      for (double v : array) {
         consumer.accept(v);
      }
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
      return array.length == 0;
   }

   @Override
   public PrimitiveIterator.OfDouble iterator() {
      return getStream().iterator();
   }

   @Override
   public MDoubleStream limit(int n) {
      return getStream().limit(n);
   }

   @Override
   public MDoubleStream map(@NonNull SerializableDoubleUnaryOperator mapper) {
      return getStream().map(mapper);
   }

   @Override
   public <T> MStream<T> mapToObj(@NonNull SerializableDoubleFunction<? extends T> function) {
      return getStream().mapToObj(function);
   }

   @Override
   public OptionalDouble max() {
      return getStream().max();
   }

   @Override
   public double mean() {
      return Math2.summaryStatistics(array).getAverage();
   }

   @Override
   public OptionalDouble min() {
      return getStream().min();
   }

   @Override
   public boolean noneMatch(@NonNull SerializableDoublePredicate predicate) {
      return getStream().noneMatch(predicate);
   }

   @Override
   public void onClose(SerializableRunnable onCloseHandler) {
      this.onCloseHandler = onCloseHandler;
   }

   @Override
   public MDoubleStream parallel() {
      this.parallel = true;
      return this;
   }

   @Override
   public OptionalDouble reduce(@NonNull SerializableDoubleBinaryOperator operator) {
      return getStream().reduce(operator);
   }

   @Override
   public double reduce(double zeroValue, @NonNull SerializableDoubleBinaryOperator operator) {
      return getStream().reduce(zeroValue, operator);
   }

   @Override
   public MDoubleStream repartition(int numberOfPartition) {
      return this;
   }

   @Override
   public MDoubleStream skip(int n) {
      return getStream().skip(n);
   }

   @Override
   public MDoubleStream sorted(boolean ascending) {
      return getStream().sorted(ascending);
   }

   @Override
   public double stddev() {
      return Math2.summaryStatistics(array).getSampleStandardDeviation();
   }

   @Override
   public double sum() {
      return Math2.sum(array);
   }

   @Override
   public double[] toArray() {
      return Arrays.copyOf(array, array.length);
   }

   @Override
   public MDoubleStream union(@NonNull MDoubleStream other) {
      return getStream().union(other);
   }

}//END OF ReusableDoubleStream
