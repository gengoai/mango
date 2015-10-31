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

import com.davidbracewell.collection.PrimitiveArrayList;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.function.*;
import org.apache.spark.api.java.JavaDoubleRDD;
import scala.Tuple2;

import java.util.Iterator;
import java.util.OptionalDouble;
import java.util.PrimitiveIterator;

/**
 * @author David B. Bracewell
 */
public class SparkDoubleStream implements MDoubleStream {

  private final JavaDoubleRDD doubleStream;

  public SparkDoubleStream(JavaDoubleRDD doubleStream) {
    this.doubleStream = doubleStream;
  }

  @Override
  public void close() throws Exception {

  }

  @Override
  public double sum() {
    return doubleStream.sum();
  }

  @Override
  public OptionalDouble first() {
    return OptionalDouble.of(doubleStream.first());
  }

  @Override
  public long count() {
    return doubleStream.count();
  }

  @Override
  public <T> MStream<T> mapToObj(SerializableDoubleFunction<? extends T> function) {
    return new SparkStream<T>(
      doubleStream.map(function::apply)
    );
  }

  @Override
  public MDoubleStream distinct() {
    return new SparkDoubleStream(doubleStream.distinct());
  }

  @Override
  public boolean allMatch(SerializableDoublePredicate predicate) {
    return doubleStream.filter(predicate::test).count() == count();
  }

  @Override
  public boolean anyMatch(SerializableDoublePredicate predicate) {
    return doubleStream.filter(predicate::test).count() != 0;
  }

  @Override
  public boolean noneMatch(SerializableDoublePredicate predicate) {
    return doubleStream.filter(predicate::test).count() == 0;
  }

  @Override
  public MDoubleStream filter(SerializableDoublePredicate predicate) {
    return new SparkDoubleStream(doubleStream.filter(predicate::test));
  }

  @Override
  public void forEach(SerializableDoubleConsumer consumer) {
    doubleStream.foreach(consumer::accept);
  }

  @Override
  public PrimitiveIterator.OfDouble iterator() {
    return new PrimitiveIterator.OfDouble() {

      Iterator<Double> itr = doubleStream.toLocalIterator();

      @Override
      public boolean hasNext() {
        return itr.hasNext();
      }

      @Override
      public double nextDouble() {
        return itr.next();
      }
    };
  }

  @Override
  public MDoubleStream limit(int n) {
    return new SparkDoubleStream(doubleStream.zipWithIndex().filter(p -> p._2() < n).mapToDouble(Tuple2::_1));
  }

  @Override
  public MDoubleStream skip(int n) {
    return new SparkDoubleStream(doubleStream.zipWithIndex().filter(p -> p._2() > n).mapToDouble(Tuple2::_1));
  }

  @Override
  public MDoubleStream map(SerializableDoubleUnaryOperator mapper) {
    return new SparkDoubleStream(doubleStream.mapToDouble(mapper::applyAsDouble));
  }

  @Override
  public OptionalDouble min() {
    return OptionalDouble.of(doubleStream.min());
  }

  @Override
  public OptionalDouble max() {
    return OptionalDouble.of(doubleStream.max());
  }

  @Override
  public double stddev() {
    return doubleStream.stdev();
  }

  @Override
  public double mean() {
    return doubleStream.mean();
  }

  @Override
  public OptionalDouble reduce(SerializableDoubleBinaryOperator operator) {
    return OptionalDouble.of(doubleStream.reduce(operator::applyAsDouble));
  }

  @Override
  public double reduce(double zeroValue, SerializableDoubleBinaryOperator operator) {
    return zeroValue + doubleStream.reduce(operator::applyAsDouble);
  }

  @Override
  public MDoubleStream sorted() {
    return new SparkDoubleStream(doubleStream.map(Double::valueOf).sortBy(d -> d, true, doubleStream.partitions().size()).mapToDouble(d -> d));
  }

  @Override
  public double[] toArray() {
    return Convert.convert(doubleStream.collect(), double[].class);
  }

  @Override
  public MDoubleStream flatMap(SerializableDoubleFunction<double[]> mapper) {
    return new SparkDoubleStream(doubleStream.flatMapToDouble(d -> new PrimitiveArrayList<>(mapper.apply(d), Double.class)));
  }
}//END OF SparkDoubleStream
