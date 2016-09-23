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

import com.davidbracewell.collection.list.Lists;
import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.stream.accumulator.*;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.broadcast.Broadcast;
import org.apache.spark.util.CollectionAccumulator;
import org.apache.spark.util.DoubleAccumulator;
import org.apache.spark.util.LongAccumulator;
import scala.Option;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;


/**
 * The enum Spark streaming context.
 *
 * @author David B. Bracewell
 */
public enum SparkStreamingContext implements StreamingContext {
   /**
    * Instance spark streaming context.
    */
   INSTANCE;

   /**
    * The constant SPARK_MASTER.
    */
   public static final String SPARK_MASTER = "spark.master";
   /**
    * The constant SPARK_APPNAME.
    */
   public static final String SPARK_APPNAME = "spark.appName";

   private static volatile JavaSparkContext context;


   private static JavaSparkContext getSparkContext() {
      if (context == null) {
         synchronized (SparkStreamingContext.class) {
            if (context == null) {
               SparkConf conf = new SparkConf();
               if (Config.hasProperty(SPARK_MASTER)) {
                  conf.setMaster(Config.get(SPARK_MASTER).asString());
               }
               conf.setAppName(Config.get(SPARK_APPNAME).asString(StringUtils.randomHexString(20)));
               context = new JavaSparkContext(conf);
            }
         }
      }
      return context;
   }

   /**
    * Context of spark streaming context.
    *
    * @param stream the stream
    * @return the spark streaming context
    */
   public static SparkStreamingContext contextOf(@NonNull SparkStream<?> stream) {
      if (context == null) {
         synchronized (SparkStreamingContext.class) {
            if (context == null) {
               context = new JavaSparkContext(stream.asRDD().context());
            }
         }
      }
      return SparkStreamingContext.INSTANCE;
   }

   /**
    * Context of spark streaming context.
    *
    * @param stream the stream
    * @return the spark streaming context
    */
   public static SparkStreamingContext contextOf(@NonNull SparkDoubleStream stream) {
      if (context == null) {
         synchronized (SparkStreamingContext.class) {
            if (context == null) {
               context = new JavaSparkContext(stream.getRDD().context());
            }
         }
      }
      return SparkStreamingContext.INSTANCE;
   }

   /**
    * Context of spark streaming context.
    *
    * @param stream the stream
    * @return the spark streaming context
    */
   public static SparkStreamingContext contextOf(@NonNull SparkPairStream<?, ?> stream) {
      if (context == null) {
         synchronized (SparkStreamingContext.class) {
            if (context == null) {
               context = new JavaSparkContext(stream.getRDD().context());
            }
         }
      }
      return SparkStreamingContext.INSTANCE;
   }

   /**
    * Spark context java spark context.
    *
    * @return the java spark context
    */
   public JavaSparkContext sparkContext() {
      return getSparkContext();
   }

   @Override
   public MDoubleStream doubleStream(DoubleStream doubleStream) {
      if (doubleStream == null) {
         return empty().mapToDouble(o -> Double.NaN);
      }
      return new SparkDoubleStream(
                                     getSparkContext().parallelizeDoubles(
                                        doubleStream.boxed().collect(Collectors.toList()))
      );
   }

   @Override
   public MDoubleStream doubleStream(double... values) {
      if (values == null) {
         return empty().mapToDouble(o -> Double.NaN);
      }
      return new SparkDoubleStream(getSparkContext()
                                      .parallelizeDoubles(DoubleStream.of(values)
                                                                      .boxed()
                                                                      .collect(Collectors.toList()))
      );
   }

   @Override
   public <T> MStream<T> empty() {
      return new SparkStream<>(getSparkContext().parallelize(new ArrayList<>()));
   }

   @Override
   public void close() {
      context.close();
   }

   @Override
   public MStream<Integer> range(int startInclusive, int endExclusive) {
      return new SparkStream<>(
                                 IntStream.range(startInclusive, endExclusive).boxed().collect(Collectors.toList())
      );
   }

   @Override
   @SafeVarargs
   public final <T> MStream<T> stream(T... items) {
      if (items == null) {
         return empty();
      }
      return new SparkStream<>(Arrays.asList(items));
   }

   @Override
   public <T> MStream<T> stream(@NonNull Stream<T> stream) {
      if (stream == null) {
         return empty();
      }
      return new SparkStream<>(stream.collect(Collectors.toList()));
   }

   @Override
   public <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map) {
      if (map == null) {
         return new SparkPairStream<>(new HashMap<K, V>());
      }
      return new SparkPairStream<>(map);
   }

   @Override
   public <K, V> MPairStream<K, V> pairStream(Collection<Map.Entry<K, V>> tuples) {
      return stream(tuples).mapToPair(t -> t);
   }

   @Override
   public <T> MStream<T> stream(Collection<? extends T> collection) {
      JavaRDD<T> rdd;
      if (collection == null) {
         return empty();
      } else if (collection instanceof List) {
         rdd = getSparkContext().parallelize(Cast.<List<T>>as(collection));
      } else {
         rdd = getSparkContext().parallelize(new ArrayList<T>(collection));
      }
      return new SparkStream<>(rdd);
   }

   @Override
   public <T> MStream<T> stream(Iterable<? extends T> iterable) {
      JavaRDD<T> rdd;
      if (iterable == null) {
         return empty();
      } else if (iterable instanceof List) {
         rdd = getSparkContext().parallelize(Cast.<List<T>>as(iterable));
      } else {
         rdd = getSparkContext().parallelize(Lists.asArrayList(iterable));
      }
      return new SparkStream<>(rdd);
   }


   @Override
   public MStream<String> textFile(String location) {
      if (StringUtils.isNullOrBlank(location)) {
         return empty();
      }
      return new SparkStream<>(getSparkContext().textFile(location));
   }

   @Override
   public MStream<String> textFile(Resource location) {
      if (location == null) {
         return empty();
      }
      return textFile(location.path());
   }

   /**
    * Broadcast broadcast.
    *
    * @param <T>    the type parameter
    * @param object the object
    * @return the broadcast
    */
   public <T> Broadcast<T> broadcast(T object) {
      return getSparkContext().broadcast(object);
   }


   @Override
   public MDoubleAccumulator doubleAccumulator(double initialValue, String name) {
      DoubleAccumulator accumulator = new DoubleAccumulator();
      accumulator.setValue(initialValue);
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkDoubleAccumulator(accumulator);
   }

   @Override
   public MLongAccumulator longAccumulator(long initialValue, String name) {
      LongAccumulator accumulator = new LongAccumulator();
      accumulator.setValue(initialValue);
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkLongAccumulator(accumulator);
   }

   @Override
   public <E> MCounterAccumulator<E> counterAccumulator(String name) {
      CounterAccumulatorV2<E> accumulator = new CounterAccumulatorV2<>();
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkCounterAccumulator<>(accumulator);
   }

   @Override
   public <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator(String name) {
      MultiCounterAccumulatorV2<K1, K2> accumulator = new MultiCounterAccumulatorV2<>();
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkMultiCounterAccumulator<>(accumulator);
   }

   @Override
   public <E> MListAccumulator<E> listAccumulator(String name) {
      CollectionAccumulator<E> accumulator = new CollectionAccumulator<>();
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkListAccumulator<>(accumulator);
   }

   @Override
   public <K, V> MMapAccumulator<K, V> mapAccumulator(String name) {
      MapAccumulatorV2<K, V> accumulator = new MapAccumulatorV2<>();
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkMapAccumulator<>(accumulator);
   }

   @Override
   public MStatisticsAccumulator statisticsAccumulator(String name) {
      StatisticsAccumulatorV2 accumulator = new StatisticsAccumulatorV2();
      accumulator.register(sparkContext().sc(), Option.apply(name), false);
      return new SparkStatisticsAccumulator(accumulator);
   }


}//END OF SparkStreamingContext
