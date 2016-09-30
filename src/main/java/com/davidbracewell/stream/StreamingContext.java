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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.stream.accumulator.*;
import com.davidbracewell.tuple.Tuple2;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.DoubleStream;
import java.util.stream.Stream;

/**
 * <p>Provides methods for creating <code>MStreams</code> and <code>MAccumulators</code> within a given context, i.e.
 * local Java or distributed Spark</p>
 *
 * @author David B. Bracewell
 */
public interface StreamingContext extends AutoCloseable {

   /**
    * Gets the distributed streaming context. (requires Spark jars to be on classpath).
    *
    * @return the distributed streaming context
    */
   static SparkStreamingContext distributed() {
      return SparkStreamingContext.INSTANCE;
   }

   /**
    * Gets a streaming context.
    *
    * @param distributed Should the stream be distributed (True) or local (False)
    * @return the streaming context
    */
   static StreamingContext get(boolean distributed) {
      return distributed ? distributed() : local();
   }

   /**
    * Gets the default streaming context as defined. if the config property <code>streams.distributed</code> is set to
    * true, the default context will be distributed otherwise it will be local.
    *
    * @return the default streaming context
    */
   static StreamingContext get() {
      return get(Config.get("streams.distributed").asBooleanValue(false));
   }

   /**
    * Gets the Local streaming context.
    *
    * @return the local streaming context
    */
   static LocalStreamingContext local() {
      return LocalStreamingContext.INSTANCE;
   }

   /**
    * Creates a new Counter accumulator.
    *
    * @param <E> the component type of the counter
    * @return the counter accumulator
    */
   default <E> MCounterAccumulator<E> counterAccumulator() {
      return counterAccumulator(null);
   }

   /**
    * Creates a new Counter accumulator.
    *
    * @param <E>  the component type of the counter
    * @param name the name of the accumulator
    * @return the counter accumulator
    */
   <E> MCounterAccumulator<E> counterAccumulator(String name);

   /**
    * Creates a new double accumulator with initial value of 0.
    *
    * @return the double accumulator
    */
   default MDoubleAccumulator doubleAccumulator() {
      return doubleAccumulator(0d, null);
   }

   /**
    * Creates a new double accumulator with the given initial value.
    *
    * @param initialValue the initial value of the accumulator
    * @return the double accumulator
    */
   default MDoubleAccumulator doubleAccumulator(double initialValue) {
      return doubleAccumulator(initialValue, null);
   }

   /**
    * Creates a new double accumulator with the given initial value.
    *
    * @param initialValue the initial value of the accumulator
    * @param name         The name of the accumulator
    * @return the double accumulator
    */
   MDoubleAccumulator doubleAccumulator(double initialValue, String name);

   /**
    * Creates a MDoubleStream from a Java DoubleStream
    *
    * @param doubleStream the double stream to wrap / consume
    * @return the MDoubleStream
    */
   MDoubleStream doubleStream(DoubleStream doubleStream);

   /**
    * Creates a MDoubleStream from a variable list of doubles
    *
    * @param values the values making up the double stream
    * @return the MDoubleStream
    */
   default MDoubleStream doubleStream(double... values) {
      if (values == null) {
         return doubleStream(DoubleStream.empty());
      }
      return doubleStream(DoubleStream.of(values));
   }

   /**
    * Creates a new empty stream
    *
    * @param <T> the component type of the stream
    * @return the empty MStream
    */
   <T> MStream<T> empty();

   /**
    * Creates an empty MDoubleStream
    *
    * @return the empty double stream
    */
   default MDoubleStream emptyDouble() {
      return doubleStream(DoubleStream.empty());
   }

   /**
    * Creates an empty MPairStream
    *
    * @param <K> the key type parameter
    * @param <V> the value type parameter
    * @return the empty pair stream
    */
   default <K, V> MPairStream<K, V> emptyPair() {
      return empty().mapToPair(k -> null);
   }

   /**
    * Creates a list accumulator
    *
    * @param <E> the component type of the list
    * @return the list accumulator
    */
   default <E> MListAccumulator<E> listAccumulator() {
      return listAccumulator(null);
   }

   /**
    * Creates a list accumulator
    *
    * @param <E>  the component type of the list
    * @param name the name of the accumulator
    * @return the list accumulator
    */
   <E> MListAccumulator<E> listAccumulator(String name);

   /**
    * Creates a new long accumulator with the given initial value.
    *
    * @param initialValue the initial value of the accumulator
    * @return the long accumulator
    */
   default MLongAccumulator longAccumulator(long initialValue) {
      return longAccumulator(initialValue, null);
   }

   /**
    * Creates a new long accumulator with the initial value 0.
    *
    * @return the long accumulator
    */
   default MLongAccumulator longAccumulator() {
      return longAccumulator(0L, null);
   }

   /**
    * Creates a new long accumulator with the given initial value.
    *
    * @param initialValue the initial value of the accumulator
    * @param name         the name of the accumulator
    * @return the long accumulator
    */
   MLongAccumulator longAccumulator(long initialValue, String name);

   /**
    * Creates a new map accumulator
    *
    * @param <K> the key type parameter
    * @param <V> the value type parameter
    * @return the map accumulator
    */
   default <K, V> MMapAccumulator<K, V> mapAccumulator() {
      return mapAccumulator(null);
   }

   /**
    * Creates a new map accumulator
    *
    * @param <K>  the key type parameter
    * @param <V>  the value type parameter
    * @param name the name of the accumulator
    * @return the map accumulator
    */
   <K, V> MMapAccumulator<K, V> mapAccumulator(String name);

   /**
    * Creates a new MultiCounter accumulator
    *
    * @param <K1> the first key type parameter
    * @param <K2> the second key type parameter
    * @return the MultiCounter accumulator
    */
   default <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator() {
      return multiCounterAccumulator(null);
   }

   /**
    * Creates a new MultiCounter accumulator
    *
    * @param <K1> the first key type parameter
    * @param <K2> the second key type parameter
    * @param name the name of the accumulator
    * @return the MultiCounter accumulator
    */
   <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator(String name);

   /**
    * Creates a new pair stream from the given map.
    *
    * @param <K> the key type parameter
    * @param <V> the value type parameter
    * @param map the map to stream
    * @return the pair stream
    */
   <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map);

   /**
    * Creates a new pair stream from the given collection of entries.
    *
    * @param <K>    the key type parameter
    * @param <V>    the value type parameter
    * @param tuples the collection of entries to use to create the pair stream
    * @return the pair stream
    */
   <K, V> MPairStream<K, V> pairStream(Collection<Entry<? extends K, ? extends V>> tuples);

   /**
    * Creates a new pair stream from the given array of tuples.
    *
    * @param <K>    the key type parameter
    * @param <V>    the value type parameter
    * @param tuples the collection of entries to use to create the pair stream
    * @return the pair stream
    */
   @SuppressWarnings({"unchecked", "varargs"})
   default <K, V> MPairStream<K, V> pairStream(Tuple2<? extends K, ? extends V>... tuples) {
      if (tuples == null) {
         return emptyPair();
      }
      return pairStream(Arrays.asList(tuples));
   }

   /**
    * Creates a ranged based integer stream starting at <code>startInclusive</code> and ending before
    * <code>endExclusive</code>.
    *
    * @param startInclusive the starting number in the range (inclusive)
    * @param endExclusive   the ending number in the range (exclusive)
    * @return the integer stream
    */
   MStream<Integer> range(int startInclusive, int endExclusive);

   /**
    * Creates a new statistics accumulator
    *
    * @return the statistics accumulator
    */
   default MStatisticsAccumulator statisticsAccumulator() {
      return statisticsAccumulator(null);
   }

   /**
    * Creates a new statistics accumulator
    *
    * @param name the name of the accumulator
    * @return the statistics accumulator
    */
   MStatisticsAccumulator statisticsAccumulator(String name);

   /**
    * Creates a stream wrapping the given items.
    *
    * @param <T>   the component type parameter of the stream
    * @param items the items to stream
    * @return the stream
    */
   @SuppressWarnings({"unchecked", "varargs"})
   <T> MStream<T> stream(T... items);

   /**
    * Creates a new MStream from Java Stream
    *
    * @param <T>    the component type parameter of the stream
    * @param stream the Java stream to wrap / consume
    * @return the new MStream
    */
   <T> MStream<T> stream(Stream<T> stream);

   /**
    * Creates a new MStream from the given iterable
    *
    * @param <T>      the component type parameter of the stream
    * @param iterable the iterable to wrap / consume
    * @return the new MStream
    */
   <T> MStream<T> stream(Iterable<? extends T> iterable);

   /**
    * Creates a new MStream from the given iterator
    *
    * @param <T>      the component type parameter of the stream
    * @param iterator the iterator to wrap / consume
    * @return the new MStream
    */
   default <T> MStream<T> stream(Iterator<? extends T> iterator) {
      if (iterator == null) {
         return empty();
      }
      return stream(Cast.<Iterable<T>>as(Collect.asIterable(iterator)));
   }

   /**
    * Creates a new MStream where each element is a line in the resources (recursive) at the given location.
    *
    * @param location the location to read
    * @return the new MStream backed by the lines of the files in the given location.
    */
   MStream<String> textFile(String location);

   /**
    * Creates a new MStream where each element is a line in the resources (recursive) at the given location.
    *
    * @param location the location to read
    * @return the new MStream backed by the lines of the files in the given location.
    */
   MStream<String> textFile(Resource location);

   /**
    * Updates the config object used by this stream (important for distributed environments).
    */
   default void updateConfig() {

   }


}//END OF StreamingContext
