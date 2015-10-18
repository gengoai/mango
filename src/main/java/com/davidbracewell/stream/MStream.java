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

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BinaryOperator;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collector;

/**
 * @author David B. Bracewell
 */
public interface MStream<T> extends AutoCloseable {


  MStream<T> filter(Predicate<? super T> predicate);

  <R> MStream<R> map(Function<? super T, ? extends R> function);

  <R> MStream<R> flatMap(Function<? super T, ? extends Iterable<? extends R>> mapper);

  <R, U> MPairStream<R, U> mapToPair(Function<? super T, ? extends Map.Entry<? extends R, ? extends U>> function);

  Optional<T> first();

  MStream<T> sample(int number);

  Optional<T> reduce(BinaryOperator<T> reducer);

  long size();

  MStream<T> distinct();

  void forEach(Consumer<? super T> consumer);

  <R> R collect(Collector<? super T, T, R> collector);

  MStream<T> limit(long number);

  List<T> take(int n);

  MStream<T> skip(long n);

  void onClose(Runnable closeHandler);

}//END OF MStream
