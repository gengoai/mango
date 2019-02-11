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

import com.gengoai.function.*;

import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public abstract class LazyLocalPairStream<T, U> extends BaseJavaPairStream<T, U> {

   @Override
   public MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate) {
      return getLocalStream().filter(predicate);
   }

   @Override
   public MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate) {
      return getLocalStream().filterByKey(predicate);
   }

   @Override
   public MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate) {
      return getLocalStream().filterByValue(predicate);
   }

   @Override
   public <R, V> MPairStream<R, V> flatMapToPair(SerializableBiFunction<? super T, ? super U, Stream<Map.Entry<? extends R, ? extends V>>> function) {
      return getLocalStream().flatMapToPair(function);
   }

   protected abstract MPairStream<T, U> getLocalStream();

   @Override
   public MPairStream<T, Iterable<U>> groupByKey() {
      return getLocalStream().groupByKey();
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? extends T, ? extends V> stream) {
      return getLocalStream().join(stream);
   }

   @Override
   public MStream<T> keys() {
      return getLocalStream().keys();
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> leftOuterJoin(MPairStream<? extends T, ? extends V> stream) {
      return getLocalStream().leftOuterJoin(stream);
   }

   @Override
   public <R> MStream<R> map(SerializableBiFunction<? super T, ? super U, ? extends R> function) {
      return getLocalStream().map(function);
   }

   @Override
   public MDoubleStream mapToDouble(SerializableToDoubleBiFunction<? super T, ? super U> function) {
      return getLocalStream().mapToDouble(function);
   }

   @Override
   public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
      return getLocalStream().mapToPair(function);
   }

   @Override
   public MPairStream<T, U> parallel() {
      return getLocalStream().parallel();
   }

   @Override
   public MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator) {
      return getLocalStream().reduceByKey(operator);
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> rightOuterJoin(MPairStream<? extends T, ? extends V> stream) {
      return getLocalStream().rightOuterJoin(stream);
   }

   @Override
   public MPairStream<T, U> sample(boolean withReplacement, long number) {
      return getLocalStream().sample(withReplacement, number);
   }

   @Override
   public MPairStream<T, U> shuffle(Random random) {
      return getLocalStream().shuffle();
   }

   @Override
   public MPairStream<T, U> sortByKey(SerializableComparator<T> comparator) {
      return getLocalStream().sortByKey(comparator);
   }

   @Override
   public MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other) {
      return getLocalStream().union(other);
   }

   @Override
   public MStream<U> values() {
      return getLocalStream().values();
   }
}//END OF LazyLocalPairStream
