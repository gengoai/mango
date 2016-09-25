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

import com.davidbracewell.collection.Streams;
import com.davidbracewell.config.Config;
import com.davidbracewell.config.Configurator;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.io.resource.Resource;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.hadoop.io.compress.GzipCodec;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import scala.Tuple2;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collector;
import java.util.stream.Stream;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * A MStream wrapper around a Spark RDD.
 *
 * @param <T> the component type of the stream
 * @author David B. Bracewell
 */
public class SparkStream<T> implements MStream<T>, Serializable {
   private static final long serialVersionUID = 1L;
   private final JavaRDD<T> rdd;
   private SerializableRunnable onClose;

   /**
    * Instantiates a new Spark stream.
    *
    * @param mStream the m stream
    */
   public SparkStream(@NonNull MStream<T> mStream) {
      if (mStream instanceof SparkStream) {
         this.rdd = Cast.<SparkStream<T>>as(mStream).getRDD();
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
   public SparkStream(@NonNull JavaRDD<T> rdd) {
      this.rdd = rdd;
   }

   /**
    * Instantiates a new Spark stream.
    *
    * @param collection the collection
    */
   SparkStream(List<T> collection) {
      int slices = Math.max(1, collection.size() / Config.get("spark.partitions").asIntegerValue(100));
      this.rdd = SparkStreamingContext.INSTANCE.sparkContext().parallelize(collection, slices);
   }

   @Override
   public SerializableRunnable getOnCloseHandler() {
      return onClose;
   }

   /**
    * Gets the wrapped rdd.
    *
    * @return the rdd
    */
   public JavaRDD<T> getRDD() {
      return rdd;
   }

   @Override
   public SparkStreamingContext getContext() {
      return SparkStreamingContext.contextOf(this);
   }

   @Override
   public void close() throws IOException {
      if (onClose != null) {
         onClose.run();
      }
   }

   @Override
   public SparkStream<T> filter(@NonNull SerializablePredicate<? super T> predicate) {
      return new SparkStream<>(rdd.filter(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return predicate.test(t);
      }));
   }

   @Override
   public <R> SparkStream<R> map(@NonNull SerializableFunction<? super T, ? extends R> function) {
      return new SparkStream<>(rdd.map(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return function.apply(t);
      }));
   }

   @Override
   public <R> SparkStream<R> flatMap(@NonNull SerializableFunction<? super T, Stream<? extends R>> mapper) {
      return new SparkStream<>(rdd.flatMap(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return Cast.as(mapper.apply(t).iterator());
      }));
   }

   @Override
   public <R, U> SparkPairStream<R, U> flatMapToPair(@NonNull SerializableFunction<? super T, Stream<? extends Map.Entry<? extends R, ? extends U>>> function) {
      return new SparkPairStream<>(rdd.flatMapToPair(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return Cast.as(function.apply(t).map(e -> new Tuple2<>(e.getKey(), e.getValue())).iterator());
      }));
   }

   @Override
   public <R, U> SparkPairStream<R, U> mapToPair(@NonNull SerializableFunction<? super T, ? extends Map.Entry<? extends R, ? extends U>> function) {
      return new SparkPairStream<>(rdd.mapToPair(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         Map.Entry<R, U> entry = Cast.as(function.apply(t));
         return Cast.as(new Tuple2<>(entry.getKey(), entry.getValue()));
      }));
   }

   @Override
   public <U> SparkPairStream<U, Iterable<T>> groupBy(@NonNull SerializableFunction<? super T, ? extends U> function) {
      return new SparkPairStream<>(rdd.groupBy(e -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return function.apply(e);
      }));
   }

   @Override
   public <R> R collect(@NonNull Collector<? super T, T, R> collector) {
      return Streams.asStream(rdd.toLocalIterator()).collect(collector);
   }

   @Override
   public List<T> collect() {
      return rdd.collect();
   }

   @Override
   public Optional<T> reduce(@NonNull SerializableBinaryOperator<T> reducer) {
      return Optional.of(rdd.reduce((t, u) -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return reducer.apply(t, u);
      }));
   }

   @Override
   public T fold(T zeroValue, @NonNull SerializableBinaryOperator<T> operator) {
      return rdd.fold(zeroValue, (t, u) -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return operator.apply(t, u);
      });
   }

   @Override
   public void forEach(@NonNull SerializableConsumer<? super T> consumer) {
      rdd.foreach(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         consumer.accept(t);
      });
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
   public SparkStream<T> sample(boolean withReplacement, int number) {
      if (number <= 0) {
         return getContext().empty();
      }
      if (withReplacement) {
         SparkStream<T> sample = new SparkStream<>(rdd.sample(true, 0.5));
         while (sample.count() < number) {
            sample = sample.union(new SparkStream<>(rdd.sample(true, 0.5)));
         }
         if (sample.count() > number) {
            sample = sample.limit(number);
         }
         return sample;
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
   public SparkStream<T> distinct() {
      return new SparkStream<>(rdd.distinct());
   }

   @Override
   public SparkStream<T> limit(long number) {
      if (number <= 0) {
         return SparkStreamingContext.INSTANCE.empty();
      }
      return new SparkStream<>(rdd.zipWithIndex().filter(p -> p._2() < number).map(Tuple2::_1));
   }

   @Override
   public List<T> take(int n) {
      if (n <= 0) {
         return Collections.emptyList();
      }
      return rdd.take(n);
   }

   @Override
   public SparkStream<T> skip(long n) {
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


   /**
    * Maps the objects in the stream by block using the given function
    *
    * @param <R>      the component type of the returning stream
    * @param function the function to use to map objects
    * @return the new stream
    */
   public <R> SparkStream<R> mapPartitions(@NonNull SerializableFunction<Iterator<? super T>, ? extends R> function) {
      return new SparkStream<>(rdd.mapPartitions(iterator -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return Cast.as(function.apply(iterator));
      }));
   }

   @Override
   public <R extends Comparable<R>> MStream<T> sorted(boolean ascending, @NonNull SerializableFunction<? super T, ? extends R> keyFunction) {
      return new SparkStream<>(rdd.sortBy(keyFunction::apply, ascending, rdd.partitions().size()));
   }

   @Override
   public Optional<T> max(@NonNull SerializableComparator<? super T> comparator) {
      return Optional.ofNullable(rdd.max(Cast.as(comparator)));
   }

   @Override
   public Optional<T> min(@NonNull SerializableComparator<? super T> comparator) {
      return Optional.ofNullable(rdd.min(Cast.as(comparator)));
   }

   @Override
   public <U> SparkPairStream<T, U> zip(@NonNull MStream<U> other) {
      if (other instanceof SparkStream) {
         return new SparkPairStream<>(rdd.zip(Cast.<SparkStream<U>>as(other).rdd));
      }
      JavaSparkContext jsc = new JavaSparkContext(rdd.context());
      return new SparkPairStream<>(rdd.zip(jsc.parallelize(other.collect(), rdd.partitions().size())));
   }

   @Override
   public SparkPairStream<T, Long> zipWithIndex() {
      return new SparkPairStream<>(rdd.zipWithIndex());
   }


   @Override
   public SparkDoubleStream mapToDouble(@NonNull SerializableToDoubleFunction<? super T> function) {
      return new SparkDoubleStream(rdd.mapToDouble(t -> {
         Configurator.INSTANCE.configure(SparkStreamingContext.INSTANCE.getConfigBroadcast().value());
         return function.applyAsDouble(t);
      }));
   }

   @Override
   public SparkStream<T> cache() {
      return new SparkStream<>(rdd.cache());
   }

   @Override
   public SparkStream<T> union(@NonNull MStream<T> other) {
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
      if (location.isCompressed()) {
         rdd.saveAsTextFile(location.descriptor(), GzipCodec.class);
      } else {
         rdd.saveAsTextFile(location.descriptor());
      }
   }

   @Override
   public void saveAsTextFile(@NonNull String location) {
      rdd.saveAsTextFile(location);
   }


   @Override
   public SparkStream<T> parallel() {
      return this;
   }

   @Override
   public SparkStream<T> shuffle() {
      return shuffle(new Random());
   }

   @Override
   public SparkStream<T> shuffle(@NonNull Random random) {
      return new SparkStream<>(rdd.sortBy(t -> random.nextDouble(),
                                          true,
                                          rdd.getNumPartitions()
                                         ));
   }

   @Override
   public SparkStream<T> repartition(int numPartitions) {
      return new SparkStream<>(rdd.repartition(numPartitions));
   }

   @Override
   public boolean isReusable() {
      return true;
   }

   @Override
   public MStream<Iterable<T>> partition(long partitionSize) {
      Preconditions.checkArgument(partitionSize > 0, "Number of partitions must be greater than zero.");
      return zipWithIndex().mapToPair((k, v) -> $(pindex(v, partitionSize, Long.MAX_VALUE), k))
                           .groupByKey()
                           .sortByKey(true)
                           .values();
   }

   @Override
   public MStream<Iterable<T>> split(int n) {
      Preconditions.checkArgument(n > 0, "N must be greater than zero.");
      final long pSize = count() / n;
      return zipWithIndex().mapToPair((k, v) -> $(pindex(v, pSize, n), k))
                           .groupByKey()
                           .sortByKey(true)
                           .values();
   }

   private long pindex(double rawIndex, long partitionSize, long numPartitions) {
      return Math.min(numPartitions - 1, (long) Math.floor(rawIndex / partitionSize));
   }


}//END OF SparkStream
