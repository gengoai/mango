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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.function.SerializableComparator;
import lombok.NonNull;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * Methods for comparing and sorting.
 *
 * @author David B. Bracewell
 */
public final class Sorting {

   private Sorting() {
      throw new IllegalAccessError();
   }

   /**
    * Natural serializable comparator.
    *
    * @param <E> the type parameter
    * @return the serializable comparator
    */
   public static <E extends Comparable> SerializableComparator<E> natural() {
      return Sorting::compare;
   }

   /**
    * Compare int.
    *
    * @param o1 the o 1
    * @param o2 the o 2
    * @return the int
    */
   @SuppressWarnings("unchecked")
   public static int compare(Object o1, Object o2) {
      if (o1 == null && o2 == null) {
         return 0;
      } else if (o1 == null) {
         return 1;
      } else if (o2 == null) {
         return -1;
      }
      return Cast.<Comparable>as(o1).compareTo(o2);
   }

   /**
    * Creates a comparator by examining an item. If the item is of type Comparable (i.e. is not null and implements the
    * Comparable interface), a comparator is returned that uses the Comparable method. Otherwise a hashcode based
    * comparator is returned.
    *
    * @param <T>  The type of the comparator
    * @param item An example item to help and determine the best comparator
    * @return A Comparator
    */
   public static <T> SerializableComparator<T> comparator(T item) {
      if ((item != null && item instanceof Comparable)) {
         return Sorting::compare;
      }
      return Sorting.hashCodeComparator();
   }

   /**
    * Compares two objects based on their hashcode.
    *
    * @param <T> the type of object being compared
    * @return A simplistic comparator that compares hash code values
    */
   public static <T> SerializableComparator<T> hashCodeComparator() {
      return (o1, o2) -> {
         if (o1 == o2) {
            return 0;
         } else if (o1 == null) {
            return 1;
         } else if (o2 == null) {
            return -1;
         }
         return Integer.compare(o1.hashCode(), o2.hashCode());
      };
   }

   /**
    * Sorts a <code>Map</code> by value.
    *
    * @param <K>       the type parameter
    * @param <V>       the type parameter
    * @param map       The map to sort
    * @param ascending True if in ascending (natural) order, False if in descending order
    * @return A <code>List</code> of <code>Entry</code> containing the map entries in sorted order.
    */
   public static <K, V extends Comparable<V>> List<Entry<K, V>> sortMapEntriesByValue(@NonNull final Map<K, V> map,
                                                                                     final boolean ascending
                                                                                     ) {
      return sortMapEntries(map, new MapEntryComparator<>(false, ascending));
   }

   /**
    * Sorts a <code>Map</code> using a given <code>Comparator</code>
    *
    * @param <K>        the type parameter
    * @param <V>        the type parameter
    * @param map        The map to sort
    * @param comparator The compartor to use for sorting the map entries
    * @return A <code>List</code> of <code>Entry</code> containing the map entries in sorted order.
    */
   public static <K, V> List<Entry<K, V>> sortMapEntries(@NonNull final Map<K, V> map,
                                                         @NonNull final Comparator<Entry<K, V>> comparator
                                                        ) {
      return map.entrySet().stream().sorted(comparator).collect(Collectors.toList());
   }

   /**
    * Creates a comparator for Map entries. Requires that the element being sorted on implements
    * <code>Comparable</code>.
    *
    * @param <K>       the Key type
    * @param <V>       the value type
    * @param sortByKey True sort by key, False sort by value
    * @param ascending True sort in ascending order, False in descending order
    * @return A comparator for sorting map entries.
    */
   public static <K extends Comparable<? super K>, V> Comparator<Entry<K, V>> mapEntryComparator(boolean sortByKey,
                                                                                                 boolean ascending
                                                                                                ) {
      return new MapEntryComparator<>(sortByKey, ascending);
   }


   /**
    * The type Map entry comparator.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    */
   static class MapEntryComparator<K, V> implements Comparator<Entry<K, V>> {

      private final boolean ascending;
      private final boolean sortByKey;

      private MapEntryComparator(boolean sortByKey, boolean ascending) {
         this.sortByKey = sortByKey;
         this.ascending = ascending;
      }

      @Override
      public int compare(Entry<K, V> o1, Entry<K, V> o2) {
         if (sortByKey) {
            return (ascending ? 1 : -1) * Cast.<Comparable<K>>as(o1.getKey()).compareTo(o2.getKey());
         } else {
            return (ascending ? 1 : -1) * Cast.<Comparable<V>>as(o1.getValue()).compareTo(o2.getValue());
         }
      }
   }//END OF MapEntryComparator

}// END OF Sorting
