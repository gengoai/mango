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


import java.util.*;
import java.util.function.DoublePredicate;
import java.util.function.DoubleUnaryOperator;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * <p> Provides double-based counts for items. </p>
 *
 * @param <T> Type of item.
 * @author David B. Bracewell
 */
public interface Counter<T> {

  /**
   * Constructs a new counter made up of counts that are adjusted using a <code>Function</code>.
   *
   * @param function The function to use to adjust the counts
   * @return The new counter with adjusted counts.
   */
  Counter<T> adjustValues(DoubleUnaryOperator function);

  /**
   * Adjust values self.
   *
   * @param function the function
   */
  Counter<T> adjustValuesSelf(DoubleUnaryOperator function);

  /**
   * As map.
   *
   * @return The counter as a <code>Map</code>
   */
  Map<T, Double> asMap();

  /**
   * Average double.
   *
   * @return The average count in the counter
   */
  default double average() {
    return Collect.analyze(counts()).getAverage();
  }

  /**
   * Bottom n.
   *
   * @param n the n
   * @return the counter
   */
  Counter<T> bottomN(int n);

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
  boolean contains(T item);

  /**
   * Counts collection.
   *
   * @return The counts of the items in the counter.
   */
  Collection<Double> counts();

  /**
   * Decrements the count of the item by one.
   *
   * @param item The item to increment
   */
  default Counter<T> decrement(T item) {
    return decrement(item, 1);
  }

  /**
   * Decrements the count of the item by a given amount
   *
   * @param item   The item to increment
   * @param amount The amount to decrement
   */
  default Counter<T> decrement(T item, double amount) {
    return increment(item, -amount);
  }

  /**
   * Decrements all items in a given iterable by 1
   *
   * @param iterable The iterable of items to decrement
   */
  default Counter<T> decrementAll(Iterable<? extends T> iterable) {
    if (iterable != null) {
      iterable.forEach(this::decrement);
    }
    return this;
  }

  /**
   * Decrements all items in a given iterable by a given amount
   *
   * @param iterable The iterable of items to decrement
   * @param amount   The amount to decrement
   */
  default Counter<T> decrementAll(Iterable<? extends T> iterable, double amount) {
    if (iterable != null) {
      iterable.forEach(i -> decrement(i, amount));
    }
    return this;
  }

  /**
   * Divides the values in the counter by the sum and sets the sum to 1.0
   */
  Counter<T> divideBySum();

  /**
   * Entries set.
   *
   * @return the set
   */
  Set<Map.Entry<T, Double>> entries();

  /**
   * Filter by key.
   *
   * @param predicate the predicate
   * @return the counter
   */
  Counter<T> filterByKey(Predicate<T> predicate);

  /**
   * Filter by value.
   *
   * @param doublePredicate the double predicate
   * @return the counter
   */
  Counter<T> filterByValue(DoublePredicate doublePredicate);

  /**
   * Returns the count for the given item
   *
   * @param item The item we want the count for
   * @return The count of the item or 0 if it is not in the counter.
   */
  double get(T item);

  /**
   * Increments the count of the item by one.
   *
   * @param item The item to increment
   */
  default Counter<T> increment(T item) {
    return increment(item, 1);
  }

  /**
   * Increments the count of the item by a given amount
   *
   * @param item   The item to increment
   * @param amount The amount to increment
   */
  Counter<T> increment(T item, double amount);

  /**
   * Increments all items in a given iterable by 1
   *
   * @param iterable The iterable of items to increment
   */
  default Counter<T> incrementAll(Iterable<? extends T> iterable) {
    if (iterable != null) {
      iterable.forEach(this::increment);
    }
    return this;
  }

  /**
   * Increments all items in a given iterable by a given amount
   *
   * @param iterable The iterable of items to increment
   * @param amount   The amount to increment
   */
  default Counter<T> incrementAll(Iterable<? extends T> iterable, double amount) {
    if (iterable != null) {
      iterable.forEach(i -> increment(i, amount));
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
  Set<T> items();

  /**
   * Returns the items as a sorted list by their counts.
   *
   * @param ascending True if the counts are sorted in ascending order, False if in descending order.
   * @return The sorted list of items.
   */
  default List<T> itemsByCount(boolean ascending) {
    return Sorting.sortMapEntriesByValue(asMap(), ascending).stream().map(Map.Entry::getKey).collect(Collectors.toList());
  }

  /**
   * Calculates the magnitude (square root of sum of squares) of the items in the Counter.
   *
   * @return the magnitude
   */
  default double magnitude() {
    return Math.sqrt(sumOfSquares());
  }

  /**
   * Map keys counter.
   *
   * @param <R>      the type parameter
   * @param function the function
   * @return the counter
   */
  <R> Counter<R> mapKeys(Function<T,R> function);

  /**
   * Max t.
   *
   * @return The item with max count
   */
  default T max() {
    Optional<Map.Entry<T, Double>> max = Collect.argMax(asMap().entrySet());
    return max.map(Map.Entry::getKey).orElse(null);
  }

  /**
   * Maximum count.
   *
   * @return The maximum count in the counter
   */
  default double maximumCount() {
    if (isEmpty()) {
      return 0d;
    }
    return Collections.max(counts());
  }

  /**
   * Merges the counts in one counter with this one.
   *
   * @param other The other counter to merge.
   * @return the counter
   */
  Counter<T> merge(Counter<? extends T> other);

  /**
   * Merges the counts in a map with this counter.
   *
   * @param other The other counter to merge.
   */
  Counter<T> merge(Map<? extends T, ? extends Number> other);

  /**
   * Min t.
   *
   * @return The item with min count
   */
  default T min() {
    Optional<Map.Entry<T, Double>> min = Collect.argMin(asMap().entrySet());
    return min.map(Map.Entry::getKey).orElse(null);
  }

  /**
   * Minimum count.
   *
   * @return The minimum count in the counter
   */
  default double minimumCount() {
    if (isEmpty()) {
      return 0d;
    }
    return Collections.min(counts());
  }

  /**
   * Removes an item from the counter
   *
   * @param item The item to remove
   * @return the count of the removed item
   */
  double remove(T item);

  /**
   * Removes all the given items from the counter
   *
   * @param items The items to remove
   */
  default Counter<T> removeAll(Iterable<T> items) {
    if (items != null) {
      items.forEach(this::remove);
    }
    return this;
  }

  /**
   * removes all items whose count is zero
   */
  Counter<T> removeZeroCounts();

  /**
   * Sample an item based on its count.
   *
   * @return the sampled item
   */
  default T sample() {
    Random rnd = new Random();
    double i = rnd.nextDouble() * sum();
    double sum = 0;
    T last = null;
    for (T item : items()) {
      sum += get(item);
      if (i <= sum) {
        return item;
      }
      last = item;
    }
    return last;
  }

  /**
   * Sets the value of an item in the counter
   *
   * @param item  The item
   * @param count The count
   */
  Counter<T> set(T item, double count);

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
    return Collect.sum(counts());
  }

  /**
   * Sum of squares.
   *
   * @return The sum of squares for the values
   */
  default double sumOfSquares() {
    return Collect.analyze(counts()).getSumOfSquares();
  }

  /**
   * Top n.
   *
   * @param n the n
   * @return the counter
   */
  Counter<T> topN(int n);

}//END OF Counter
