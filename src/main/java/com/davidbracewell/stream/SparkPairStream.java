package com.davidbracewell.stream;

import com.davidbracewell.config.Config;
import com.davidbracewell.config.Configurator;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Stream;

/**
 * A MPairStream implementation backed by a JavaPairRDD.
 *
 * @param <T> the key type parameter
 * @param <U> the value type parameter
 * @author David B. Bracewell
 */
public class SparkPairStream<T, U> implements MPairStream<T, U>, Serializable {
   private static final long serialVersionUID = 1L;
   private final JavaPairRDD<T, U> rdd;
   private SerializableRunnable onClose;
   private volatile Broadcast<Config> configBroadcast;

   /**
    * Instantiates a new Spark pair stream.
    *
    * @param rdd the rdd
    */
   public SparkPairStream(JavaPairRDD<T, U> rdd) {
      this.configBroadcast = SparkStreamingContext.INSTANCE.getConfigBroadcast();
      this.rdd = rdd;
   }

   /**
    * Instantiates a new Spark pair stream.
    *
    * @param map the map
    */
   public SparkPairStream(Map<? extends T, ? extends U> map) {
      this(SparkStreamingContext.INSTANCE.sparkContext(), map);
   }

   /**
    * Instantiates a new Spark pair stream.
    *
    * @param context the context
    * @param map     the map
    */
   SparkPairStream(JavaSparkContext context, Map<? extends T, ? extends U> map) {
      this.configBroadcast = SparkStreamingContext.INSTANCE.getConfigBroadcast();
      List<scala.Tuple2<T, U>> tuples = new ArrayList<>();
      map.forEach((k, v) -> tuples.add(new scala.Tuple2<>(k, v)));
      this.rdd = context.parallelize(tuples).mapToPair(t -> Cast.as(t));
   }

   /**
    * To map entry map . entry.
    *
    * @param <K>    the type parameter
    * @param <V>    the type parameter
    * @param tuple2 the tuple 2
    * @return the map . entry
    */
   static <K, V> Map.Entry<K, V> toMapEntry(scala.Tuple2<K, V> tuple2) {
      return Tuple2.of(tuple2._1(), tuple2._2());
   }

   @Override
   public MPairStream<T, U> cache() {
      return new SparkPairStream<>(rdd.cache());
   }

   @Override
   public void close() throws Exception {
      this.rdd.unpersist();
      if (onClose != null) {
         onClose.run();
      }
   }

   @Override
   public List<Map.Entry<T, U>> collectAsList() {
      return rdd.map(t -> Cast.<Map.Entry<T, U>>as(Tuple2.of(t._1(), t._2()))).collect();
   }

   @Override
   public Map<T, U> collectAsMap() {
      return rdd.collectAsMap();
   }

   @Override
   public long count() {
      return rdd.count();
   }

   @Override
   public MPairStream<T, U> filter(@NonNull SerializableBiPredicate<? super T, ? super U> predicate) {
      return new SparkPairStream<>(rdd.filter(tuple -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return predicate.test(tuple._1(), tuple._2());
      }));
   }

   @Override
   public MPairStream<T, U> filterByKey(@NonNull SerializablePredicate<T> predicate) {
      return new SparkPairStream<>(rdd.filter(tuple -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return predicate.test(tuple._1());
      }));
   }

   @Override
   public MPairStream<T, U> filterByValue(@NonNull SerializablePredicate<U> predicate) {
      return new SparkPairStream<>(rdd.filter(tuple -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return predicate.test(tuple._2());
      }));
   }

   @Override
   public <R, V> SparkPairStream<R, V> flatMapToPair(@NonNull SerializableBiFunction<? super T, ? super U, Stream<Map.Entry<? extends R, ? extends V>>> function) {
      return new SparkPairStream<>(rdd.flatMapToPair(t -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return Cast.cast(function.apply(t._1(), t._2())
                                  .map(e -> new scala.Tuple2<>(e.getKey(), e.getValue()))
                                  .iterator());
      }));
   }

   @Override
   public void forEach(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
      rdd.foreach(tuple -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         consumer.accept(tuple._1(), tuple._2());
      });
   }

   @Override
   public void forEachLocal(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
      rdd.toLocalIterator().forEachRemaining(e -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         consumer.accept(e._1(), e._2());
      });
   }

   @Override
   public StreamingContext getContext() {
      return SparkStreamingContext.contextOf(this);
   }

   @Override
   public SerializableRunnable getOnCloseHandler() {
      return onClose;
   }

   /**
    * Gets rdd.
    *
    * @return the rdd
    */
   JavaPairRDD<T, U> getRDD() {
      return rdd;
   }

   @Override
   public MPairStream<T, Iterable<U>> groupByKey() {
      return new SparkPairStream<>(rdd.groupByKey());
   }

   @Override
   public boolean isEmpty() {
      return rdd.isEmpty();
   }

   @Override
   public boolean isReusable() {
      return true;
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> join(@NonNull MPairStream<? extends T, ? extends V> stream) {
      return new SparkPairStream<>(rdd.join(toPairRDD(stream))
                                      .mapToPair(
                                         t -> Cast.as(new scala.Tuple2<>(t._1(), toMapEntry(t._2()))))
      );
   }

   @Override
   public MStream<T> keys() {
      return new SparkStream<>(rdd.keys());
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> leftOuterJoin(@NonNull MPairStream<? extends T, ? extends V> stream) {
      return new SparkPairStream<>(rdd.leftOuterJoin(toPairRDD(stream))
                                      .mapToPair(t -> Cast.as(
                                         new scala.Tuple2<>(t._1(), Tuple2.of(t._2()._1(), t._2()._2().or(null))))));
   }

   @Override
   public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
      return new SparkStream<>(rdd.map(e -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return function.apply(e._1(), e._2());
      }));
   }

   @Override
   public MDoubleStream mapToDouble(@NonNull SerializableToDoubleBiFunction<? super T, ? super U> function) {
      return new SparkDoubleStream(rdd.mapToDouble(e -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return function.applyAsDouble(e._1(), e._2());
      }));
   }

   @Override
   public <R, V> MPairStream<R, V> mapToPair(@NonNull SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
      return new SparkPairStream<>(rdd.mapToPair((t) -> {
         Configurator.INSTANCE.configure(
            configBroadcast.value());
         Map.Entry<? extends R, ? extends V> e = function.apply(t._1(), t._2());
         return Cast.as(new scala.Tuple2<>(e.getKey(), e.getValue()));
      }));
   }

   @Override
   public Optional<Map.Entry<T, U>> max(@NonNull SerializableComparator<Map.Entry<T, U>> comparator) {
      if (isEmpty()) {
         return Optional.empty();
      }
      return Optional.of(toMapEntry(rdd.max((t1, t2) -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return comparator.compare(toMapEntry(t1), toMapEntry(t2));
      })));
   }

   @Override
   public Optional<Map.Entry<T, U>> min(@NonNull SerializableComparator<Map.Entry<T, U>> comparator) {
      if (isEmpty()) {
         return Optional.empty();
      }
      return Optional.of(toMapEntry(rdd.min((t1, t2) -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return comparator.compare(toMapEntry(t1), toMapEntry(t2));
      })));
   }

   @Override
   public void onClose(SerializableRunnable closeHandler) {
      this.onClose = closeHandler;
   }

   @Override
   public MPairStream<T, U> parallel() {
      return this;
   }

   @Override
   public MPairStream<T, U> reduceByKey(@NonNull SerializableBinaryOperator<U> operator) {
      return new SparkPairStream<>(rdd.reduceByKey((t, u) -> {
         Configurator.INSTANCE.configure(configBroadcast.value());
         return operator.apply(t, u);
      }));
   }

   @Override
   public MPairStream<T, U> repartition(int partitions) {
      return new SparkPairStream<>(rdd.repartition(partitions));
   }

   @Override
   public <V> MPairStream<T, Map.Entry<U, V>> rightOuterJoin(@NonNull MPairStream<? extends T, ? extends V> stream) {
      return new SparkPairStream<>(rdd.rightOuterJoin(toPairRDD(stream))
                                      .mapToPair(t -> Cast.as(
                                         new scala.Tuple2<>(t._1(), Tuple2.of(t._2()._1().or(null), t._2()._2())))));
   }

   @Override
   public MPairStream<T, U> shuffle(@NonNull Random random) {
      return new SparkPairStream<>(rdd.sortByKey(
         (SerializableComparator<T>) (t1, t2) -> random.nextDouble() >= 0.5 ? 1 : -1));
   }

   @Override
   public MPairStream<T, U> sortByKey(@NonNull SerializableComparator<T> comparator) {
      return new SparkPairStream<>(rdd.sortByKey(comparator));
   }

   private <K, V> JavaPairRDD<K, V> toPairRDD(MPairStream<? extends K, ? extends V> other) {
      JavaPairRDD<K, V> oRDD;
      if (other instanceof SparkPairStream) {
         oRDD = Cast.<SparkPairStream<K, V>>as(other).rdd;
      } else {
         JavaSparkContext jsc = SparkStreamingContext.contextOf(this).sparkContext();
         oRDD = Cast.as(new SparkPairStream<>(jsc, other.collectAsMap()).rdd);
      }
      return oRDD;
   }

   @Override
   public MPairStream<T, U> union(@NonNull MPairStream<? extends T, ? extends U> other) {
      return new SparkPairStream<>(rdd.union(toPairRDD(other)));
   }

   @Override
   public MStream<U> values() {
      return new SparkStream<>(rdd.values());
   }


   @Override
   public void updateConfig() {
      SparkStreamingContext.INSTANCE.updateConfig();
      this.configBroadcast = SparkStreamingContext.INSTANCE.getConfigBroadcast();
   }

}// END OF SparkPairStream
