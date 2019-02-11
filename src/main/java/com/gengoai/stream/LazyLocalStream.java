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
 *
 */

package com.gengoai.stream;

import com.gengoai.function.SerializableFunction;
import com.gengoai.function.SerializablePredicate;
import com.gengoai.function.SerializableToDoubleFunction;

import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * The type Lazy local stream.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
abstract class LazyLocalStream<T> extends BaseJavaStream<T> {


   @Override
   public MStream<T> distinct() {
      return getLocalStream().distinct();
   }

   @Override
   public MStream<T> filter(SerializablePredicate<? super T> predicate) {
      return getLocalStream().filter(predicate);
   }

   @Override
   public <R> MStream<R> flatMap(SerializableFunction<? super T, Stream<? extends R>> mapper) {
      return getLocalStream().flatMap(mapper);
   }

   @Override
   public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, Stream<? extends Map.Entry<? extends R, ? extends U>>> function) {
      return getLocalStream().flatMapToPair(function);
   }

   @Override
   public StreamingContext getContext() {
      return StreamingContext.local();
   }

   /**
    * Gets local stream.
    *
    * @return the local stream
    */
   protected abstract MStream<T> getLocalStream();

   @Override
   public <U> MPairStream<U, Iterable<T>> groupBy(SerializableFunction<? super T, ? extends U> function) {
      return getLocalStream().groupBy(function);
   }

   @Override
   public MStream<T> intersection(MStream<T> other) {
      return getLocalStream().intersection(other);
   }

   @Override
   public MStream<T> limit(long number) {
      return getLocalStream().limit(number);
   }

   @Override
   public <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function) {
      return getLocalStream().map(function);
   }

   @Override
   public MDoubleStream mapToDouble(SerializableToDoubleFunction<? super T> function) {
      return getLocalStream().mapToDouble(function);
   }

   @Override
   public <R, U> MPairStream<R, U> mapToPair(SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
      return getLocalStream().mapToPair(function);
   }

   @Override
   public MStream<Stream<T>> partition(long partitionSize) {
      return getLocalStream().partition(partitionSize);
   }

   @Override
   public MStream<T> repartition(int numPartitions) {
      return getLocalStream().repartition(numPartitions);
   }


   @Override
   public MStream<T> shuffle(Random random) {
      return getLocalStream().shuffle(random);
   }

   @Override
   public MStream<T> skip(long n) {
      return getLocalStream().skip(n);
   }

   @Override
   public <R extends Comparable<R>> MStream<T> sortBy(boolean ascending, SerializableFunction<? super T, ? extends R> keyFunction) {
      return getLocalStream().sortBy(ascending, keyFunction);
   }

   @Override
   public MStream<T> union(MStream<T> other) {
      return getLocalStream().union(other);
   }

   @Override
   public <U> MPairStream<T, U> zip(MStream<U> other) {
      return getLocalStream().zip(other);
   }

   @Override
   public MPairStream<T, Long> zipWithIndex() {
      return getLocalStream().zipWithIndex();
   }

}//END OF LazyLocalStream
