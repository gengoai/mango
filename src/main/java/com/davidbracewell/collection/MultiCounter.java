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

package com.davidbracewell.collection;

import com.davidbracewell.tuple.Tuple3;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Predicate;

/**
 * <p> Provides double-based counts for pairs of items. </p>
 *
 * @param <K>  Type of item.
 * @param <V>  the type parameter
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
   */
  void adjustValuesSelf(DoubleUnaryOperator function);

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
    return Collect.analyze(counts()).getAverage();
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
   */
  default void decrement(K item1, V item2) {
    decrement(item1, item2, 1);
  }

  /**
   * Decrement void.
   *
   * @param item1 the item 1
   * @param item2 the item 2
   * @param amount the amount
   */
  default void decrement(K item1, V item2, double amount) {
    increment(item1, item2, -amount);
  }

  /**
   * Decrement all.
   *
   * @param iterable the iterable
   */
  default void decrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
    if (iterable != null) {
      iterable.forEach(e -> decrement(e.getKey(), e.getValue()));
    }
  }

  /**
   * Decrement all.
   *
   * @param item the item
   * @param iterable the iterable
   */
  default void decrementAll(K item, Iterable<? extends V> iterable) {
    get(item).decrementAll(iterable);
  }

  /**
   * Divide by key sum.
   */
  default void divideByKeySum() {
    items().forEach(key -> get(key).divideBySum());
  }

  /**
   * Divides the values in the counter by the sum and sets the sum to 1.0
   */
  default void divideBySum() {
    adjustValuesSelf(d -> 1d / sum());
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
   */
  default void increment(K item1, V item2) {
    increment(item1, item2, 1);
  }

  /**
   * Increment void.
   *
   * @param item1 the item 1
   * @param item2 the item 2
   * @param amount the amount
   */
  default void increment(K item1, V item2, double amount) {
    get(item1).increment(item2, amount);
  }

  /**
   * Increment all.
   *
   * @param item the item
   * @param iterable the iterable
   */
  default void incrementAll(K item, Iterable<? extends V> iterable) {
    get(item).incrementAll(iterable);
  }

  /**
   * Increment all.
   *
   * @param iterable the iterable
   */
  default void incrementAll(Iterable<? extends Map.Entry<K, V>> iterable) {
    if (iterable != null) {
      iterable.forEach(e -> increment(e.getKey(), e.getValue()));
    }
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
    return Math.sqrt(sumOfSquares());
  }

  /**
   * Maximum count.
   *
   * @return The maximum count in the counter
   */
  default double maximumCount() {
    return Collect.analyze(counts()).getMax();
  }

  /**
   * Merges the counts in one counter with this one.
   *
   * @param other The other counter to merge.
   */
  void merge(MultiCounter<K, V> other);

  /**
   * Minimum count.
   *
   * @return The minimum count in the counter
   */
  default double minimumCount() {
    return Collect.analyze(counts()).getMin();
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
   */
  default void removeAll(Iterable<K> items) {
    if (items != null) {
      items.forEach(this::remove);
    }
  }

  /**
   * Set void.
   *
   * @param item1 the item 1
   * @param item2 the item 2
   * @param count the count
   */
  void set(K item1, V item2, double count);

  /**
   * Set void.
   *
   * @param item the item
   * @param counter the counter
   */
  void set(K item, Counter<V> counter);

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
    return Collect.analyze(counts()).getSampleStandardDeviation();
  }

  /**
   * Sum double.
   *
   * @return The sum of the counts in the counter
   */
  default double sum() {
    return Collect.analyze(counts()).getSum();
  }

  /**
   * Sum of squares.
   *
   * @return The sum of squares for the values
   */
  default double sumOfSquares() {
    return Collect.analyze(counts()).getSumOfSquares();
  }

}//END OF Counter
