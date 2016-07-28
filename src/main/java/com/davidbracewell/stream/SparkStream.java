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
import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.io.resource.Resource;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collector;

/**
 * The type Spark stream.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class SparkStream<T> implements MStream<T>, Serializable {
  private static final long serialVersionUID = 1L;
  private final JavaRDD<T> rdd;
  private SerializableRunnable onClose;

  public SparkStream(@NonNull MStream<T> mStream) {
    if (mStream instanceof SparkStream) {
      this.rdd = Cast.<SparkStream<T>>as(mStream).asRDD();
    } else {
      List<T> collection = mStream.collect();
      int slices = Math.max(1, collection.size() / Config.get("spark.partitions").asIntegerValue(100));
      this.rdd = SparkStreamingContext.INSTANCE.sparkContext().parallelize(collection, slices);
    }
    this.onClose = mStream.getOnCloseHandler();
  }

  /**
   * Instantiates a new Spark stream.
   *
   * @param rdd the rdd
   */
  public SparkStream(JavaRDD<T> rdd) {
    this.rdd = rdd;
  }

  /**
   * Instantiates a new Spark stream.
   *
   * @param collection the collection
   */
  public SparkStream(List<T> collection) {
    int slices = Math.max(1, collection.size() / Config.get("spark.partitions").asIntegerValue(100));
    this.rdd = SparkStreamingContext.INSTANCE.sparkContext().parallelize(collection, slices);
  }

  @Override
  public SerializableRunnable getOnCloseHandler() {
    return onClose;
  }

  @Override
  public JavaRDD<T> asRDD() {
    return rdd;
  }

  @Override
  public StreamingContext getContext() {
    return SparkStreamingContext.contextOf(this);
  }

  @Override
  public void close() throws IOException {
    if (onClose != null) {
      onClose.run();
    }
  }

  @Override
  public MStream<T> filter(SerializablePredicate<? super T> predicate) {
    return new SparkStream<>(rdd.filter(predicate::test));
  }

  @Override
  public <R> MStream<R> map(SerializableFunction<? super T, ? extends R> function) {
    return new SparkStream<>(rdd.map(function::apply));
  }

  @Override
  public <R> MStream<R> flatMap(SerializableFunction<? super T, Iterable<? extends R>> mapper) {
    return new SparkStream<>(rdd.flatMap(t -> Cast.as(mapper.apply(t).iterator())));
  }

  @Override
  public <R, U> MPairStream<R, U> flatMapToPair(SerializableFunction<? super T, ? extends Iterable<? extends Map.Entry<? extends R, ? extends U>>> function) {
    return new SparkPairStream<>(rdd.flatMapToPair(t -> {
      List<Tuple2<R, U>> list = new LinkedList<>();
      function.apply(t).forEach(e -> list.add(new Tuple2<>(e.getKey(), e.getValue())));
      return list.iterator();
    }));
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
      rdd.groupBy(function::apply)
    );
  }

  @Override
  public <R> R collect(Collector<? super T, T, R> collector) {
    return Collect.stream(rdd.toLocalIterator()).collect(collector);
  }

  @Override
  public List<T> collect() {
    return rdd.collect();
  }

  @Override
  public Optional<T> reduce(SerializableBinaryOperator<T> reducer) {
    return Optional.of(rdd.reduce(reducer::apply));
  }

  @Override
  public T fold(T zeroValue, SerializableBinaryOperator<T> operator) {
    return rdd.fold(zeroValue, operator::apply);
  }

  @Override
  public void forEach(SerializableConsumer<? super T> consumer) {
    rdd.foreach(consumer::accept);
  }

  @Override
  public void forEachLocal(SerializableConsumer<? super T> consumer) {
    rdd.toLocalIterator().forEachRemaining(consumer);
  }

  @Override
  public Iterator<T> iterator() {
    return rdd.toLocalIterator();
  }

  @Override
  public Optional<T> first() {
    if (rdd.isEmpty()) {
      return Optional.empty();
    }
    return Optional.ofNullable(rdd.first());
  }

  @Override
  public MStream<T> sample(int number) {
    if (number <= 0) {
      return getContext().empty();
    }
    double count = count();
    if (count <= number) {
      return this;
    }
    return shuffle().limit(number);
  }

  @Override
  public long count() {
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
    Preconditions.checkArgument(number >= 0);
    return new SparkStream<>(rdd.zipWithIndex().filter(p -> p._2() < number).map(Tuple2::_1));
  }

  @Override
  public List<T> take(int n) {
    Preconditions.checkArgument(n >= 0);
    return rdd.take(n);
  }

  @Override
  public MStream<T> skip(long n) {
    if (n > count()) {
      return getContext().empty();
    } else if (n <= 0) {
      return this;
    }
    return new SparkStream<>(rdd.zipWithIndex().filter(p -> p._2() > n - 1).map(Tuple2::_1));
  }

  @Override
  public void onClose(SerializableRunnable closeHandler) {
    this.onClose = closeHandler;
  }

  @Override
  public MStream<T> sorted(boolean ascending) {
    return new SparkStream<>(rdd.sortBy(t -> t, ascending, rdd.partitions().size()));
  }

  @Override
  public Optional<T> max(SerializableComparator<? super T> comparator) {
    return Optional.ofNullable(rdd.max(Cast.as(comparator)));
  }

  @Override
  public Optional<T> min(SerializableComparator<? super T> comparator) {
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
  public MDoubleStream mapToDouble(SerializableToDoubleFunction<? super T> function) {
    return new SparkDoubleStream(rdd.mapToDouble(function::applyAsDouble));
  }

  @Override
  public MStream<T> cache() {
    return new SparkStream<>(rdd.cache());
  }

  @Override
  public MStream<T> union(MStream<T> other) {
    if (isEmpty()) {
      return new SparkStream<>(other);
    } else if (other instanceof SparkStream) {
      return new SparkStream<>(rdd.union(Cast.<SparkStream<T>>as(other).rdd));
    }
    JavaSparkContext sc = new JavaSparkContext(rdd.context());
    return new SparkStream<>(rdd.union(sc.parallelize(other.collect())));
  }

  @Override
  public void saveAsTextFile(@NonNull Resource location) {
    rdd.saveAsTextFile(location.descriptor());
  }

  @Override
  public void saveAsTextFile(@NonNull String location) {
    rdd.saveAsTextFile(location);
  }


  @Override
  public MStream<T> parallel() {
    return this;
  }

  @Override
  public MStream<T> shuffle(@NonNull Random random) {
    return new SparkStream<>(
      rdd.mapToPair(t -> new Tuple2<>(random.nextDouble(), t))
        .sortByKey()
        .map(Tuple2::_2)
    );
  }

  @Override
  public MStream<T> repartition(int numPartitions) {
    return new SparkStream<>(rdd.repartition(numPartitions));
  }

}//END OF SparkStream
