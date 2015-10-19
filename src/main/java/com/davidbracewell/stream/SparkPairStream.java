package com.davidbracewell.stream;

import com.davidbracewell.function.SerializableBiConsumer;
import lombok.NonNull;
import org.apache.spark.api.java.JavaPairRDD;

import java.util.function.BiConsumer;

/**
 * @author David B. Bracewell
 */
public class SparkPairStream<T, U> implements MPairStream<T, U> {

  private final JavaPairRDD<T, U> rdd;

  public SparkPairStream(JavaPairRDD<T, U> rdd) {
    this.rdd = rdd;
  }

  @Override
  public void forEach(@NonNull SerializableBiConsumer<? super T, ? super U> consumer) {
    rdd.foreach(tuple -> consumer.accept(tuple._1(), tuple._2()));
  }

  @Override
  public void close() throws Exception {

  }
}// END OF SparkPairStream
