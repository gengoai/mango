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

import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.function.SerializablePredicate;
import lombok.NonNull;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;


/**
 * <p>Convenience methods for creating sets and manipulating collections resulting in sets.</p>
 *
 * @author David B. Bracewell
 */
public interface Sets {


   /**
    * Creates a concurrent hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the  set
    * @return the new concurrent hash set containing the given elements
    */
   static <T> Set<T> asConcurrentHashSet(Iterator<? extends T> iterator) {
      return createSet(com.google.common.collect.Sets::newConcurrentHashSet, Streams.asStream(iterator));
   }

   /**
    * Creates a concurrent hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the  set
    * @return the new concurrent hash set containing the given elements
    */
   static <T> Set<T> asConcurrentHashSet(Iterable<? extends T> iterable) {
      return createSet(com.google.common.collect.Sets::newConcurrentHashSet, Streams.asStream(iterable));
   }

   /**
    * Creates a concurrent hash set of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the  set
    * @return the new concurrent hash set containing the given elements
    */
   static <T> Set<T> asConcurrentHashSet(Stream<? extends T> stream) {
      return createSet(com.google.common.collect.Sets::newConcurrentHashSet, stream);
   }

   /**
    * Creates a linked hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the  set
    * @return the new linked hash set containing the given elements
    */
   static <T> Set<T> asLinkedHashSet(Iterator<? extends T> iterator) {
      return createSet(LinkedHashSet::new, Streams.asStream(iterator));
   }

   /**
    * Creates a linked hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the  set
    * @return the new linked hash set containing the given elements
    */
   static <T> Set<T> asLinkedHashSet(Iterable<? extends T> iterable) {
      return createSet(LinkedHashSet::new, Streams.asStream(iterable));
   }

   /**
    * Creates a linked hash set of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the  set
    * @return the new linked hash set containing the given elements
    */
   static <T> Set<T> asLinkedHashSet(Stream<? extends T> stream) {
      return createSet(LinkedHashSet::new, stream);
   }

   /**
    * Creates a hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new hash set containing the given elements
    */
   static <T> Set<T> asSet(Iterator<? extends T> iterator) {
      return createSet(HashSet::new, Streams.asStream(iterator));
   }

   /**
    * Creates a hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new hash set containing the given elements
    */
   static <T> Set<T> asSet(Iterable<? extends T> iterable) {
      return createSet(HashSet::new, Streams.asStream(iterable));
   }

   /**
    * Creates a hash set of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new hash set containing the given elements
    */
   static <T> Set<T> asSet(Stream<? extends T> stream) {
      return createSet(HashSet::new, stream);
   }

   /**
    * Creates a tree set of the supplied elements
    *
    * @param <T>      the component type of the  set
    * @param iterator the elements to add to the  set
    * @return the new tree set containing the given elements
    */
   static <T> Set<T> asTreeSet(Iterator<? extends T> iterator) {
      return createSet(TreeSet::new, Streams.asStream(iterator));
   }

   /**
    * Creates a tree hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the  set
    * @return the new tree hash set containing the given elements
    */
   static <T> Set<T> asTreeSet(Iterable<? extends T> iterable) {
      return createSet(TreeSet::new, Streams.asStream(iterable));
   }

   /**
    * Creates a tree hash set of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the  set
    * @return the new tree hash set containing the given elements
    */
   static <T> Set<T> asTreeSet(Stream<? extends T> stream) {
      return createSet(TreeSet::new, stream);
   }

   /**
    * Creates a concurrent hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the  set
    * @return the new concurrent hash set containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> Set<T> concurrentSet(T... elements) {
      return createSet(com.google.common.collect.Sets::newConcurrentHashSet, elements);
   }

   /**
    * Creates a new set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param supplier Supplies new set instances
    * @param elements the elements to add to the  set
    * @return the new set containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, T... elements) {
      if (elements == null) {
         return supplier.get();
      }
      return createSet(supplier, Stream.of(elements));
   }

   /**
    * Creates a new set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param supplier Supplies new set instances
    * @param stream   the elements to add to the  set
    * @return the new set containing the given elements
    */
   static <T> Set<T> createSet(@NonNull Supplier<Set<T>> supplier, Stream<? extends T> stream) {
      if (stream == null) {
         return supplier.get();
      }
      return stream.collect(Collectors.toCollection(supplier));
   }

   /**
    * <p>Retains all items in collection1 that are not in collection2 and returns them as a set.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A set of the collection1 - collection2
    */
   static <E> Set<E> difference(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return collection1.stream().filter(v -> !collection2.contains(v)).collect(Collectors.toSet());
   }

   /**
    * <p>Filters a given collection using a supplied predicate returning the results as a set. </p>
    *
    * @param <E>        the component type of the collection being filtered
    * @param collection the collection to be filtered
    * @param filter     the predicate to use for filtering (only items that result in true will be keep)
    * @return A set containing the filtered items of the supplied collection
    */
   static <E> Set<E> filter(@NonNull final Collection<? extends E> collection, @NonNull final SerializablePredicate<? super E> filter) {
      return collection.stream().filter(filter).collect(Collectors.toSet());
   }

   /**
    * <p>Retains all items that are in both collection1 and collection2 and returns them as a set.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A set containing the intersection of collection1 and collection2
    */
   static <E> Set<E> intersection(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return collection1.stream().filter(collection2::contains).collect(Collectors.toSet());
   }

   /**
    * Creates a linked hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the  set
    * @return the new linked hash set containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> Set<T> linkedHashSet(T... elements) {
      return createSet(LinkedHashSet::new, elements);
   }

   /**
    * Creates a hash set of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new hash set containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> Set<T> set(T... elements) {
      return createSet(HashSet::new, elements);
   }

   /**
    * <p>Transforms a given collection using a supplied transform function returning the results as a set. </p>
    *
    * @param <E>        the component type of the collection being transformed
    * @param <R>        the component type of the resulting collection after transformation
    * @param collection the collection to be transformed
    * @param transform  the function used to transform elements of type E to R
    * @return A set containing the transformed items of the supplied collection
    */
   static <E, R> Set<R> transform(@NonNull final Collection<? extends E> collection, @NonNull final SerializableFunction<? super E, R> transform) {
      return collection.stream().map(transform).collect(Collectors.toSet());
   }

   /**
    * Creates a tree set of the supplied elements
    *
    * @param <T>      the component type of the  set
    * @param elements the elements to add to the  set
    * @return the new tree set containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> Set<T> treeSet(T... elements) {
      return createSet(TreeSet::new, elements);
   }

   /**
    * <p>Retains all items in collection1 and collection2 and returns them as a set.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A set of the collection1 + collection2
    */
   static <E> Set<E> union(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return Stream.concat(collection1.stream(), collection2.stream()).collect(Collectors.toSet());
   }

}//END OF Sets
