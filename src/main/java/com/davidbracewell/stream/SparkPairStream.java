package com.davidbracewell.stream;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @author David B. Bracewell
 */
public class SparkPairStream<T, U> implements MPairStream<T, U>, Serializable {
  private static final long serialVersionUID = 1L;

  private final JavaPairRDD<T, U> rdd;

  public SparkPairStream(JavaPairRDD<T, U> rdd) {
    this.rdd = rdd;
  }

  public SparkPairStream(Map<? extends T, ? extends U> map) {
    this(SparkStreamingContext.INSTANCE.sparkContext(), map);
  }

  public SparkPairStream(JavaSparkContext context, Map<? extends T, ? extends U> map) {
    List<scala.Tuple2<T, U>> tuples = new LinkedList<>();
    map.forEach((k, v) -> tuples.add(new scala.Tuple2<>(k, v)));
    this.rdd = context
      .parallelize(tuples)
      .mapToPair(t -> t);
  }

  static <K, V> Map.Entry<K, V> toMapEntry(scala.Tuple2<K, V> tuple2) {
    return Tuple2.of(tuple2._1(), tuple2._2());
  }

  @Override
  public <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? extends T, ? extends V> stream) {
    JavaPairRDD<T, Map.Entry<U, V>> nrdd = Cast.as(rdd
      .join(toPairRDD(stream))
      .mapToPair(t -> new scala.Tuple2<>(t._1(), toMapEntry(t._2()))));
    return new SparkPairStream<>(nrdd);
  }

  @Override
  public MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator) {
    return new SparkPairStream<>(rdd.reduceByKey(operator::apply));
  }

  @Override
  public void forEach(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
    rdd.foreach(tuple -> consumer.accept(tuple._1(), tuple._2()));
  }

  @Override
  public void forEachLocal(SerializableBiConsumer<? super T, ? super U> consumer) {
    rdd.toLocalIterator().forEachRemaining(e -> consumer.accept(e._1(), e._2()));
  }

  @Override
  public void close() throws Exception {

  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
    return new SparkStream<>(rdd.map(
      e -> function.apply(e._1(), e._2())));
  }

  @Override
  public MPairStream<T, Iterable<U>> groupByKey() {
    return new SparkPairStream<>(rdd.groupByKey());
  }

  @Override
  public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
    return new SparkPairStream<>(
      rdd.mapToPair((t) -> {
        Map.Entry<? extends R, ? extends V> e = function.apply(t._1(), t._2());
        return new scala.Tuple2<>(e.getKey(), e.getValue());
      }));
  }

  @Override
  public MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate) {
    return new SparkPairStream<>(rdd.filter(tuple -> predicate.test(tuple._1(), tuple._2())));
  }

  @Override
  public Map<T, U> collectAsMap() {
    return rdd.collectAsMap();
  }

  @Override
  public MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate) {
    return new SparkPairStream<>(rdd.filter(tuple -> predicate.test(tuple._1())));
  }

  @Override
  public MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate) {
    return new SparkPairStream<>(rdd.filter(tuple -> predicate.test(tuple._2())));
  }

  @Override
  public List<Map.Entry<T, U>> collectAsList() {
    return rdd.map(t -> Cast.<Map.Entry<T, U>>as(Tuple2.of(t._1(), t._2()))).collect();
  }

  @Override
  public long count() {
    return rdd.count();
  }


  @Override
  public MStream<T> keys() {
    return new SparkStream<>(rdd.keys());
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
  public MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other) {
    return new SparkPairStream<>(rdd.union(toPairRDD(other)));
  }

  @Override
  public MStream<U> values() {
    return new SparkStream<>(rdd.values());
  }

  @Override
  public MPairStream<T, U> sortByKey(SerializableComparator<T> comparator) {
    return new SparkPairStream<>(rdd.sortByKey(comparator));
  }

  @Override
  public MPairStream<T, U> parallel() {
    return this;
  }

  JavaPairRDD<T, U> getRDD() {
    return rdd;
  }

  @Override
  public StreamingContext getContext() {
    return SparkStreamingContext.contextOf(this);
  }

  @Override
  public MPairStream<T, U> shuffle(Random random) {
    return new SparkPairStream<T, U>(
      rdd.mapToPair(t -> new scala.Tuple2<>(random.nextDouble(), t))
        .sortByKey()
        .mapToPair(scala.Tuple2::_2)
    );
  }
}// END OF SparkPairStream
