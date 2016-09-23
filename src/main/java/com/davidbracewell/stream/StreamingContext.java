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
 * The interface Streaming context.
 *
 * @author David B. Bracewell
 */
public interface StreamingContext extends AutoCloseable {

   /**
    * Get streaming context.
    *
    * @return the streaming context
    */
   static StreamingContext get() {
      return get(Config.get("streams.distributed").asBooleanValue(false));
   }

   /**
    * Get streaming context.
    *
    * @param distributed the distributed
    * @return the streaming context
    */
   static StreamingContext get(boolean distributed) {
      if (distributed) {
         return distributed();
      }
      return local();
   }

   /**
    * Local streaming context.
    *
    * @return the streaming context
    */
   static StreamingContext local() {
      return JavaStreamingContext.INSTANCE;
   }

   /**
    * Distributed streaming context.
    *
    * @return the streaming context
    */
   static StreamingContext distributed() {
      return SparkStreamingContext.INSTANCE;
   }


   /**
    * Double accumulator m double accumulator.
    *
    * @return the m double accumulator
    */
   default MDoubleAccumulator doubleAccumulator() {
      return doubleAccumulator(0d, null);
   }

   /**
    * Double accumulator m accumulator.
    *
    * @param initialValue the initial value
    * @return the m accumulator
    */
   default MDoubleAccumulator doubleAccumulator(double initialValue) {
      return doubleAccumulator(initialValue, null);
   }

   /**
    * Double accumulator m accumulator.
    *
    * @param initialValue the initial value
    * @param name         the name
    * @return the m accumulator
    */
   MDoubleAccumulator doubleAccumulator(double initialValue, String name);

   /**
    * Int accumulator m accumulator.
    *
    * @param initialValue the initial value
    * @return the m accumulator
    */
   default MLongAccumulator longAccumulator(long initialValue) {
      return longAccumulator(initialValue, null);
   }

   /**
    * Long accumulator m long accumulator.
    *
    * @return the m long accumulator
    */
   default MLongAccumulator longAccumulator() {
      return longAccumulator(0L, null);
   }

   /**
    * Int accumulator m accumulator.
    *
    * @param initialValue the initial value
    * @param name         the name
    * @return the m accumulator
    */
   MLongAccumulator longAccumulator(long initialValue, String name);


   /**
    * Counter accumulator m accumulator.
    *
    * @param <E> the type parameter
    * @return the m accumulator
    */
   default <E> MCounterAccumulator<E> counterAccumulator() {
      return counterAccumulator(null);
   }


   /**
    * Counter accumulator m accumulator.
    *
    * @param <E>  the type parameter
    * @param name the name
    * @return the m accumulator
    */
   <E> MCounterAccumulator<E> counterAccumulator(String name);


   /**
    * Multi counter accumulator m multi counter accumulator.
    *
    * @param <K1> the type parameter
    * @param <K2> the type parameter
    * @return the m multi counter accumulator
    */
   default <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator() {
      return multiCounterAccumulator(null);
   }

   /**
    * Multi counter accumulator m multi counter accumulator.
    *
    * @param <K1> the type parameter
    * @param <K2> the type parameter
    * @param name the name
    * @return the m multi counter accumulator
    */
   <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator(String name);


   /**
    * List accumulator m list accumulator.
    *
    * @param <E> the type parameter
    * @return the m list accumulator
    */
   default <E> MListAccumulator<E> listAccumulator() {
      return listAccumulator(null);
   }


   /**
    * List accumulator m list accumulator.
    *
    * @param <E>  the type parameter
    * @param name the name
    * @return the m list accumulator
    */
   <E> MListAccumulator<E> listAccumulator(String name);

   /**
    * Map accumulator m map accumulator.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    * @return the m map accumulator
    */
   default <K, V> MMapAccumulator<K, V> mapAccumulator() {
      return mapAccumulator(null);
   }


   /**
    * Map accumulator m map accumulator.
    *
    * @param <K>  the type parameter
    * @param <V>  the type parameter
    * @param name the name
    * @return the m map accumulator
    */
   <K, V> MMapAccumulator<K, V> mapAccumulator(String name);


   /**
    * Statistics accumulator m statistics accumulator.
    *
    * @return the m statistics accumulator
    */
   default MStatisticsAccumulator statisticsAccumulator() {
      return statisticsAccumulator(null);
   }

   /**
    * Statistics accumulator m statistics accumulator.
    *
    * @param name the name
    * @return the m statistics accumulator
    */
   MStatisticsAccumulator statisticsAccumulator(String name);

   /**
    * Of m stream.
    *
    * @param <T>   the type parameter
    * @param items the items
    * @return the m stream
    */
   <T> MStream<T> stream(T... items);

   /**
    * Of m stream.
    *
    * @param <T>    the type parameter
    * @param stream the stream
    * @return the m stream
    */
   <T> MStream<T> stream(Stream<T> stream);


   /**
    * Stream m pair stream.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    * @param map the map
    * @return the m pair stream
    */
   <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map);

   /**
    * Pair stream m pair stream.
    *
    * @param <K>    the type parameter
    * @param <V>    the type parameter
    * @param tuples the tuples
    * @return the m pair stream
    */
   <K, V> MPairStream<K, V> pairStream(Collection<Entry<K, V>> tuples);

   /**
    * Pair stream m pair stream.
    *
    * @param <K>    the type parameter
    * @param <V>    the type parameter
    * @param tuples the tuples
    * @return the m pair stream
    */
   default <K, V> MPairStream<K, V> pairStream(Tuple2... tuples) {
      if (tuples == null) {
         return emptyPair();
      }
      Collection<Tuple2> collection = Arrays.asList(tuples);
      return pairStream(Cast.cast(collection));
   }

   /**
    * Stream m stream.
    *
    * @param <T>        the type parameter
    * @param collection the collection
    * @return the m stream
    */
   <T> MStream<T> stream(Collection<? extends T> collection);


   /**
    * Stream m stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @return the m stream
    */
   <T> MStream<T> stream(Iterable<? extends T> iterable);

   /**
    * Stream m stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the m stream
    */
   default <T> MStream<T> stream(Iterator<? extends T> iterator) {
      if (iterator == null) {
         return empty();
      }
      return stream(Cast.<Iterable<T>>as(Collect.asIterable(iterator)));
   }

   /**
    * Double stream m double stream.
    *
    * @param doubleStream the double stream
    * @return the m double stream
    */
   MDoubleStream doubleStream(DoubleStream doubleStream);

   /**
    * Double stream m double stream.
    *
    * @param values the values
    * @return the m double stream
    */
   MDoubleStream doubleStream(double... values);

   /**
    * Text file m stream.
    *
    * @param location the location
    * @return the m stream
    */
   MStream<String> textFile(String location);

   /**
    * Text file m stream.
    *
    * @param location the location
    * @return the m stream
    */
   MStream<String> textFile(Resource location);

   /**
    * Range m stream.
    *
    * @param startInclusive the start inclusive
    * @param endExclusive   the end exclusive
    * @return the m stream
    */
   MStream<Integer> range(int startInclusive, int endExclusive);

   /**
    * Empty m stream.
    *
    * @param <T> the type parameter
    * @return the m stream
    */
   <T> MStream<T> empty();

   /**
    * Empty pair m pair stream.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    * @return the m pair stream
    */
   default <K, V> MPairStream<K, V> emptyPair() {
      return empty().mapToPair(k -> null);
   }

   /**
    * Empty double m double stream.
    *
    * @return the m double stream
    */
   default MDoubleStream emptyDouble() {
      return empty().mapToDouble(u -> Double.NaN);
   }


}//END OF StreamingContext
