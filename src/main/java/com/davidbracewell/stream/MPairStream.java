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

import com.davidbracewell.collection.Sorting;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.*;
import lombok.NonNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

/**
 * The interface M pair stream.
 *
 * @param <T> the type parameter
 * @param <U> the type parameter
 * @author David B. Bracewell
 */
public interface MPairStream<T, U> extends AutoCloseable {

   /**
    * Gets context.
    *
    * @return the context
    */
   StreamingContext getContext();

   /**
    * Collect as list list.
    *
    * @return the list
    */
   List<Map.Entry<T, U>> collectAsList();

   /**
    * Collect as map map.
    *
    * @return the map
    */
   Map<T, U> collectAsMap();

   /**
    * Count long.
    *
    * @return the long
    */
   long count();

   /**
    * Filter m pair stream.
    *
    * @param predicate the predicate
    * @return the m pair stream
    */
   MPairStream<T, U> filter(SerializableBiPredicate<? super T, ? super U> predicate);

   /**
    * Filter by key m pair stream.
    *
    * @param predicate the predicate
    * @return the m pair stream
    */
   MPairStream<T, U> filterByKey(SerializablePredicate<T> predicate);

   /**
    * Filter by value m pair stream.
    *
    * @param predicate the predicate
    * @return the m pair stream
    */
   MPairStream<T, U> filterByValue(SerializablePredicate<U> predicate);

   /**
    * For each.
    *
    * @param consumer the consumer
    */
   void forEach(SerializableBiConsumer<? super T, ? super U> consumer);

   /**
    * For each local.
    *
    * @param consumer the consumer
    */
   void forEachLocal(SerializableBiConsumer<? super T, ? super U> consumer);

   /**
    * Group by key m pair stream.
    *
    * @return the m pair stream
    */
   MPairStream<T, Iterable<U>> groupByKey();

   /**
    * Join m pair stream.
    *
    * @param <V>    the type parameter
    * @param stream the stream
    * @return the m pair stream
    */
   <V> MPairStream<T, Map.Entry<U, V>> join(MPairStream<? extends T, ? extends V> stream);


   /**
    * Left outer join m pair stream.
    *
    * @param <V>    the type parameter
    * @param stream the stream
    * @return the m pair stream
    */
   <V> MPairStream<T, Map.Entry<U, V>> leftOuterJoin(MPairStream<? extends T, ? extends V> stream);

   /**
    * Right outer join m pair stream.
    *
    * @param <V>    the type parameter
    * @param stream the stream
    * @return the m pair stream
    */
   <V> MPairStream<T, Map.Entry<U, V>> rightOuterJoin(MPairStream<? extends T, ? extends V> stream);


   /**
    * Map to double m double stream.
    *
    * @param function the function
    * @return the m double stream
    */
   MDoubleStream mapToDouble(SerializableToDoubleBiFunction<? super T, ? super U> function);

   /**
    * Min by key optional.
    *
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> minByKey() {
      return minByKey((t1, t2) -> Sorting.natural().compare(Cast.as(t1), Cast.as(t2)));
   }

   /**
    * Min by key optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> minByKey(@NonNull SerializableComparator<? super T> comparator) {
      return min((t1, t2) -> comparator.compare(t1.getKey(), t2.getKey()));
   }

   /**
    * Min by value optional.
    *
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> minByValue() {
      return minByKey((t1, t2) -> Sorting.natural().compare(Cast.as(t1), Cast.as(t2)));
   }

   /**
    * Min by value optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> minByValue(@NonNull SerializableComparator<? super U> comparator) {
      return min((t1, t2) -> comparator.compare(t1.getValue(), t2.getValue()));
   }

   /**
    * Min optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   Optional<Map.Entry<T, U>> min(SerializableComparator<Map.Entry<T, U>> comparator);


   /**
    * Max by key optional.
    *
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> maxByKey() {
      return minByKey((t1, t2) -> Sorting.natural().reversed().compare(Cast.as(t1), Cast.as(t2)));
   }

   /**
    * Max by key optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> maxByKey(@NonNull SerializableComparator<? super T> comparator) {
      return min((t1, t2) -> comparator.compare(t1.getKey(), t2.getKey()));
   }

   /**
    * Max by value optional.
    *
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> maxByValue() {
      return minByKey((t1, t2) -> Sorting.natural().compare(Cast.as(t1), Cast.as(t2)));
   }

   /**
    * Max by value optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   default Optional<Map.Entry<T, U>> maxByValue(@NonNull SerializableComparator<? super U> comparator) {
      return min((t1, t2) -> comparator.compare(t1.getValue(), t2.getValue()));
   }

   /**
    * Max optional.
    *
    * @param comparator the comparator
    * @return the optional
    */
   Optional<Map.Entry<T, U>> max(SerializableComparator<Map.Entry<T, U>> comparator);

   /**
    * Is empty boolean.
    *
    * @return the boolean
    */
   boolean isEmpty();


   /**
    * Keys m stream.
    *
    * @return the m stream
    */
   MStream<T> keys();

   /**
    * Map m stream.
    *
    * @param <R>      the type parameter
    * @param function the function
    * @return the m stream
    */
   <R> MStream<R> map(SerializableBiFunction<? super T, ? super U, ? extends R> function);

   /**
    * Map to pair m pair stream.
    *
    * @param <R>      the type parameter
    * @param <V>      the type parameter
    * @param function the function
    * @return the m pair stream
    */
   <R, V> MPairStream<R, V> mapToPair(SerializableBiFunction<? super T, ? super U, ? extends Map.Entry<? extends R, ? extends V>> function);

   /**
    * Reduce by key m pair stream.
    *
    * @param operator the operator
    * @return the m pair stream
    */
   MPairStream<T, U> reduceByKey(SerializableBinaryOperator<U> operator);

   /**
    * Sort by key m pair stream.
    *
    * @param ascending the ascending
    * @return the m pair stream
    */
   default MPairStream<T, U> sortByKey(boolean ascending) {
      if (ascending) {
         return sortByKey((o1, o2) -> Sorting.natural().compare(Cast.as(o1), Cast.as(o2)));
      }
      return sortByKey((o1, o2) -> Sorting.natural().reversed().compare(Cast.as(o1), Cast.as(o2)));
   }

   /**
    * Sort by key m pair stream.
    *
    * @param comparator the comparator
    * @return the m pair stream
    */
   MPairStream<T, U> sortByKey(SerializableComparator<T> comparator);

   /**
    * Union m pair stream.
    *
    * @param other the other
    * @return the m pair stream
    */
   MPairStream<T, U> union(MPairStream<? extends T, ? extends U> other);

   /**
    * Values m stream.
    *
    * @return the m stream
    */
   MStream<U> values();

   /**
    * Parallel m pair stream.
    *
    * @return the m pair stream
    */
   MPairStream<T, U> parallel();

   /**
    * Shuffle m stream.
    *
    * @return the m stream
    */
   default MPairStream<T, U> shuffle() {
      return shuffle(new Random());
   }

   /**
    * Shuffle m pair stream.
    *
    * @param random the random
    * @return the m pair stream
    */
   MPairStream<T, U> shuffle(Random random);

   /**
    * Cache m pair stream.
    *
    * @return the m pair stream
    */
   MPairStream<T, U> cache();

   /**
    * Repartition m pair stream.
    *
    * @param partitions the partitions
    * @return the m pair stream
    */
   MPairStream<T, U> repartition(int partitions);

   /**
    * On close.
    *
    * @param closeHandler the close handler
    */
   void onClose(SerializableRunnable closeHandler);

}//END OF MPairStream
