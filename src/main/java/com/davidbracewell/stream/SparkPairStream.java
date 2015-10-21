package com.davidbracewell.stream;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.List;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class SparkPairStream<T, U> implements MPairStream<T, U> {

  private final JavaPairRDD<T, U> rdd;

  public SparkPairStream(JavaPairRDD<T, U> rdd) {
    this.rdd = rdd;
  }

  static <K, V> Map.Entry<K, V> toMapEntry(scala.Tuple2<K, V> tuple2) {
    return Tuple2.of(tuple2._1(), tuple2._2());
  }

  @Override
  public <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? super T, ? super V> stream) {
    JavaPairRDD<T, V> oRDD;
    if (stream instanceof SparkPairStream) {
      oRDD = Cast.<SparkPairStream<T, V>>as(stream).rdd;
    } else {
      oRDD = new JavaSparkContext(rdd.context())
        .parallelize(stream.collectAsList())
        .mapToPair(e -> new scala.Tuple2<>(Cast.<T>as(e.getKey()), Cast.<V>as(e.getValue())));
    }
    return new SparkPairStream<>(rdd.join(oRDD).mapToPair(t -> new scala.Tuple2<>(t._1(), toMapEntry(t._2()))));
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
  public void close() throws Exception {

  }

  @Override
  public <R> MStream<R> map(@NonNull SerializableBiFunction<? super T, ? super U, ? extends R> function) {
    return new SparkStream<>(rdd.map(e -> function.apply(e._1(), e._2())));
  }

  @Override
  public MPairStream<T, Iterable<U>> groupByKey() {
    return new SparkPairStream<>(rdd.groupByKey());
  }

  @Override
  public <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function) {
    return null;
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
}// END OF SparkPairStream
