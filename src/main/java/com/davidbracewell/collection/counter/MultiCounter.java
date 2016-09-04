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

package com.davidbracewell.collection.counter;

import com.davidbracewell.Math2;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.tuple.Tuple3;
import lombok.NonNull;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <p> Provides double-based counts for pairs of items. </p>
 *
 * @param <K> Type of item.
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public interface MultiCounter<K, V> {

   /**
    * Entries set.
    *
    * @return the set
    */
   Set<Tuple3<K, V, Double>> entries();

   /**
    * Adjust values.
    *
    * @param function the function
    * @return the multi counter
    */
   MultiCounter<K, V> adjustValues(DoubleUnaryOperator function);

   /**
    * Adjust values.
    *
    * @param function the function
    * @return the multi counter
    */
   MultiCounter<K, V> adjustValuesSelf(DoubleUnaryOperator function);

   /**
    * As map.
    *
    * @return the map
    */
   Map<K, Counter<V>> asMap();

   /**
    * Average double.
    *
    * @return The average count in the counter
    */
   default double average() {
      return Math2.summaryStatistics(counts()).getAverage();
   }

   /**
    * Clears the counter
    */
   void clear();

   /**
    * Determines if the item is in the counter
    *
    * @param item item to check
    * @return True if item is in the counter, false otherwise
    */
   boolean contains(K item);

   /**
    * Contains boolean.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @return the boolean
    */
   boolean contains(K item1, V item2);

   /**
    * Counts collection.
    *
    * @return the collection
    */
   Collection<Double> counts();

   /**
    * Decrement void.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @return the multi counter
    */
   default MultiCounter<K, V> decrement(K item1, V item2) {
      return decrement(item1, item2, 1);
   }

   /**
    * Decrement void.
    *
    * @param item1  the item 1
    * @param item2  the item 2
    * @param amount the amount
    * @return the multi counter
    */
   default MultiCounter<K, V> decrement(K item1, V item2, double amount) {
      return increment(item1, item2, -amount);
   }

   /**
    * Decrement all.
    *
    * @param iterable the iterable
    * @return the multi counter
    */
   default MultiCounter<K, V> decrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
      if (iterable != null) {
         iterable.forEach(e -> decrement(e.getKey(), e.getValue()));
      }
      return this;
   }

   /**
    * Decrement all.
    *
    * @param item     the item
    * @param iterable the iterable
    * @return the multi counter
    */
   default MultiCounter<K, V> decrementAll(K item, Iterable<? extends V> iterable) {
      get(item).decrementAll(iterable);
      return this;
   }

   /**
    * Divide by key sum.
    *
    * @return the multi counter
    */
   default MultiCounter<K, V> divideByKeySum() {
      items().forEach(key -> get(key).divideBySum());
      return this;
   }

   /**
    * Divides the values in the counter by the sum and sets the sum to 1.0
    *
    * @return the multi counter
    */
   default MultiCounter<K, V> divideBySum() {
      adjustValuesSelf(d -> 1d / sum());
      return this;
   }

   /**
    * Filter by first key.
    *
    * @param predicate the predicate
    * @return the multi counter
    */
   MultiCounter<K, V> filterByFirstKey(Predicate<K> predicate);

   /**
    * Filter by second key.
    *
    * @param predicate the predicate
    * @return the multi counter
    */
   MultiCounter<K, V> filterBySecondKey(Predicate<V> predicate);

   /**
    * Filter by value.
    *
    * @param predicate the predicate
    * @return the multi counter
    */
   MultiCounter<K, V> filterByValue(DoublePredicate predicate);

   /**
    * Get double.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @return the double
    */
   default double get(K item1, V item2) {
      if (contains(item1)) {
         return get(item1).get(item2);
      }
      return 0d;
   }

   /**
    * Get counter.
    *
    * @param item the item
    * @return the counter
    */
   Counter<V> get(K item);

   /**
    * Increment void.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @return the multi counter
    */
   default MultiCounter<K, V> increment(K item1, V item2) {
      increment(item1, item2, 1);
      return this;
   }

   /**
    * Increment void.
    *
    * @param item1  the item 1
    * @param item2  the item 2
    * @param amount the amount
    * @return the multi counter
    */
   default MultiCounter<K, V> increment(K item1, V item2, double amount) {
      get(item1).increment(item2, amount);
      return this;
   }

   /**
    * Increment all.
    *
    * @param item     the item
    * @param iterable the iterable
    * @return the multi counter
    */
   default MultiCounter<K, V> incrementAll(K item, Iterable<? extends V> iterable) {
      get(item).incrementAll(iterable);
      return this;
   }

   /**
    * Increment all.
    *
    * @param iterable the iterable
    * @return the multi counter
    */
   default MultiCounter<K, V> incrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
      if (iterable != null) {
         iterable.forEach(e -> increment(e.getKey(), e.getValue()));
      }
      return this;
   }

   /**
    * Is empty.
    *
    * @return True if the counter is empty
    */
   boolean isEmpty();

   /**
    * Items set.
    *
    * @return The items in the counter
    */
   Set<K> items();

   /**
    * Returns the items as a sorted list by their counts.
    *
    * @param ascending True if the counts are sorted in ascending order, False if in descending order.
    * @return The sorted list of items.
    */
   List<Map.Entry<K, V>> itemsByCount(boolean ascending);

   /**
    * Calculates the magnitude (square root of sum of squares) of the items in the Counter.
    *
    * @return the magnitude
    */
   default double magnitude() {
      return Math.sqrt(Math2.summaryStatistics(counts()).getSumOfSquares());
   }

   /**
    * Maximum count.
    *
    * @return The maximum count in the counter
    */
   default double maximumCount() {
      return Math2.summaryStatistics(counts()).getMax();
   }

   /**
    * Merges the counts in one counter with this one.
    *
    * @param other The other counter to merge.
    * @return the multi counter
    */
   MultiCounter<K, V> merge(MultiCounter<K, V> other);

   /**
    * Minimum count.
    *
    * @return The minimum count in the counter
    */
   default double minimumCount() {
      return Math2.summaryStatistics(counts()).getMin();
   }

   /**
    * Removes an item from the counter
    *
    * @param item The item to remove
    * @return the count of the removed item
    */
   Counter<V> remove(K item);

   /**
    * Remove double.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @return the double
    */
   double remove(K item1, V item2);

   /**
    * Removes all the given items from the counter
    *
    * @param items The items to remove
    * @return the multi counter
    */
   default MultiCounter<K, V> removeAll(Iterable<K> items) {
      if (items != null) {
         items.forEach(this::remove);
      }
      return this;
   }

   /**
    * Set void.
    *
    * @param item1 the item 1
    * @param item2 the item 2
    * @param count the count
    * @return the multi counter
    */
   MultiCounter<K, V> set(K item1, V item2, double count);

   /**
    * Set void.
    *
    * @param item    the item
    * @param counter the counter
    * @return the multi counter
    */
   MultiCounter<K, V> set(K item, Counter<V> counter);

   /**
    * Size int.
    *
    * @return The number of items in the counter
    */
   int size();

   /**
    * Standard deviation.
    *
    * @return The standard deviation of the counts in the counter
    */
   default double standardDeviation() {
      return Math2.summaryStatistics(counts()).getSampleStandardDeviation();
   }

   /**
    * Sum double.
    *
    * @return The sum of the counts in the counter
    */
   default double sum() {
      return Math2.summaryStatistics(counts()).getSum();
   }

   /**
    * Write csv.
    *
    * @param output the output
    * @throws IOException the io exception
    */
   default void writeCSV(@NonNull Resource output) throws IOException {
      write(StructuredFormat.CSV, output);
   }

   default void writeJson(@NonNull Resource output) throws IOException {
      write(StructuredFormat.JSON, output);
   }

   /**
    * Write.
    *
    * @param structuredFormat the structured format
    * @param output           the output
    * @throws IOException the io exception
    */
   default void write(@NonNull StructuredFormat structuredFormat, @NonNull Resource output) throws IOException {
      write(structuredFormat,
            output,
            item -> Convert.convert(item, String.class),
            item -> Convert.convert(item, String.class)
           );
   }

   /**
    * Write.
    *
    * @param structuredFormat the structured format
    * @param output           the output
    * @param key1Serializer   the key 1 serializer
    * @param key2Serializer   the key 2 serializer
    * @throws IOException the io exception
    */
   default void write(@NonNull StructuredFormat structuredFormat, @NonNull Resource output, @NonNull Function<? super K, String> key1Serializer, @NonNull Function<? super V, String> key2Serializer) throws IOException {
      try (StructuredWriter writer = structuredFormat.createWriter(output)) {
         writer.beginDocument();
         for (Tuple3<K, V, Double> entry : entries()) {
            writer.beginObject("entry");
            writer.writeKeyValue("k1", key1Serializer.apply(entry.v1));
            writer.writeKeyValue("k2", key2Serializer.apply(entry.v2));
            writer.writeKeyValue("v", entry.v3);
            writer.endObject();
         }
         writer.endDocument();
      }
   }

}//END OF MultiCounter
