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

import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableBinaryOperator;
import com.davidbracewell.function.SerializableConsumer;
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import com.davidbracewell.string.StringUtils;
import com.google.common.collect.Ordering;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.util.*;
import java.util.function.ToDoubleFunction;
import java.util.function.ToLongFunction;
import java.util.stream.Collector;

/**
 * @author David B. Bracewell
 */
public class SparkStream<T> implements MStream<T> {

  private final JavaRDD<T> rdd;

  public SparkStream(JavaRDD<T> rdd) {
    this.rdd = rdd;
  }

  public static void main(String[] args) throws Exception {

    Config.initialize("");
    Config.setProperty("spark.master", "local[*]");

    try (MStream<String> stream = Streams.textFile("/home/david/build.py", false)) {
      stream.flatMap(str -> Arrays.asList(str.toLowerCase().split("[\\W\\s]+")))
        .filter(str -> !StringUtils.isNullOrBlank(str))
        .countByValue()
        .entrySet()
        .stream()
        .sorted(Map.Entry.comparingByValue(Ordering.natural().reversed()))
        .forEach(e -> System.out.println(e.getKey() + "\t" + e.getValue()));
    }

  }

  @Override
  public void close() throws Exception {
  }

  @Override
  public MStream<T> filter(SerializablePredicate<? super T> predicate) {
    return new SparkStream<>(rdd.filter(t -> predicate.test(t)));
  }

  @Override
  public <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function) {
    return new SparkStream<>(rdd.map(function::apply));
  }

  @Override
  public <R> MStream<R> flatMap(SerializableFunction<? super T, ? extends Iterable<? extends R>> mapper) {
    return new SparkStream<>(rdd.flatMap(t -> Cast.as(mapper.apply(t))));
  }

  @Override
  public <R, U> MPairStream<R, U> mapToPair(SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
    return new SparkPairStream<>(
      rdd.mapToPair(t -> {
        Map.Entry<R, U> entry = Cast.as(function.apply(t));
        return new Tuple2<>(entry.getKey(), entry.getValue());
      })
    );
  }

  @Override
  public <U> MPairStream<U, Iterable<T>> groupBy(SerializableFunction<? super T, ? extends U> function) {
    return new SparkPairStream<>(
      rdd.groupBy(t -> function.apply(t))
    );
  }

  @Override
  public <R> R collect(Collector<? super T, T, R> collector) {
    return collect().stream().collect(collector);
  }

  @Override
  public List<T> collect() {
    return rdd.collect();
  }

  @Override
  public Optional<T> reduce(SerializableBinaryOperator<T> reducer) {
    return Optional.of(rdd.reduce((x, y) -> reducer.apply(x, y)));
  }

  @Override
  public T fold(T zeroValue, SerializableBinaryOperator<T> operator) {
    return rdd.fold(zeroValue, (x, y) -> operator.apply(x, y));
  }

  @Override
  public void forEach(SerializableConsumer<? super T> consumer) {
    rdd.foreach(consumer::accept);
  }

  @Override
  public Iterator<T> iterator() {
    return rdd.toLocalIterator();
  }

  @Override
  public Optional<T> first() {
    return Optional.ofNullable(rdd.first());
  }

  @Override
  public MStream<T> sample(int number) {
    return new SparkStream<>(rdd.sample(false, number / (double) size()));
  }

  @Override
  public long size() {
    return rdd.count();
  }

  @Override
  public boolean isEmpty() {
    return rdd.isEmpty();
  }

  @Override
  public Map<T, Long> countByValue() {
    return rdd.countByValue();
  }

  @Override
  public MStream<T> distinct() {
    return new SparkStream<>(rdd.distinct());
  }

  @Override
  public MStream<T> limit(long number) {
    return new SparkStream<>(rdd.zipWithIndex().filter(p -> p._2() < number).map(Tuple2::_1));
  }

  @Override
  public List<T> take(int n) {
    return rdd.take(n);
  }

  @Override
  public MStream<T> skip(long n) {
    return new SparkStream<>(rdd.zipWithIndex().filter(p -> p._2() > n).map(Tuple2::_1));
  }

  @Override
  public void onClose(Runnable closeHandler) {

  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    return new SparkStream<>(rdd.sortBy(t -> t, ascending, rdd.partitions().size()));
  }

  @Override
  public Optional<T> max(Comparator<? super T> comparator) {
    return Optional.ofNullable(rdd.max(Cast.as(comparator)));
  }

  @Override
  public Optional<T> min(Comparator<? super T> comparator) {
    return Optional.ofNullable(rdd.min(Cast.as(comparator)));
  }

  @Override
  public <U> MPairStream<T, U> zip(MStream<U> other) {
    if (other instanceof SparkStream) {
      return new SparkPairStream<>(rdd.zip(Cast.<SparkStream<U>>as(other).rdd));
    }
    JavaSparkContext jsc = new JavaSparkContext(rdd.context());
    return new SparkPairStream<>(rdd.zip(jsc.parallelize(other.collect(), rdd.partitions().size())));
  }

  @Override
  public MPairStream<T, Long> zipWithIndex() {
    return new SparkPairStream<>(rdd.zipWithIndex());
  }

  @Override
  public MLongStream mapToLong(ToLongFunction<? super T> function) {
    return null;
  }

  @Override
  public MDoubleStream mapToDouble(ToDoubleFunction<? super T> function) {
    return null;
  }

}//END OF SparkStream
