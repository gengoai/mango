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

package com.gengoai.mango.collection.list;

import com.gengoai.mango.collection.Streams;
import com.gengoai.mango.function.SerializableFunction;
import com.gengoai.mango.function.SerializablePredicate;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gengoai.mango.collection.Streams.asStream;

/**
 * <p>Convenience methods for creating lists and manipulating collections resulting in lists.</p>
 *
 * @author David B. Bracewell
 */
public interface Lists {

   /**
    * <p>Transforms a given collection using a supplied transform function returning the results as a list. </p>
    *
    * @param <I>        the component type of the collection being transformed
    * @param <O>        the component type of the resulting collection after transformation
    * @param collection the collection to be transformed
    * @param transform  the function used to transform elements of type E to R
    * @return A list containing the transformed items of the supplied collection
    */
   static <I, O> List<O> transform(@NonNull final Collection<? extends I> collection, @NonNull final SerializableFunction<? super I, ? extends O> transform) {
      return collection.stream().map(transform).collect(Collectors.toList());
   }

   /**
    * <p>Filters a given collection using a supplied predicate returning the results as a list. </p>
    *
    * @param <E>        the component type of the collection being filtered
    * @param collection the collection to be filtered
    * @param filter     the predicate to use for filtering (only items that result in true will be keep)
    * @return A list containing the filtered items of the supplied collection
    */
   static <E> List<E> filter(@NonNull final Collection<? extends E> collection, @NonNull final SerializablePredicate<? super E> filter) {
      return collection.stream().filter(filter).collect(Collectors.toList());
   }

   /**
    * <p>Ensures that the size of the given list is at least the supplied desired size. If the list size is smaller, it
    * will add the given default value to the end of the list until <code>list.size() >= desiredSize</code></p>
    *
    * @param <T>          the component type of the list
    * @param list         the list whose size is being checked
    * @param desiredSize  the desired size of the list
    * @param defaultValue the default value to add to the list to reach the desired size
    * @return the list passed in with size greater than or equal to the desired size
    */
   static <T> List<T> ensureSize(@NonNull List<T> list, int desiredSize, T defaultValue) {
      while (list.size() < desiredSize) {
         list.add(defaultValue);
      }
      return list;
   }

   /**
    * <p>Flattens an iterable of iterables into a single dimensional list.</p>
    *
    * @param <T>      the component type of the inner iterable
    * @param iterable the iterable to flatten
    * @return the flattened iterable as a list
    */
   static <T> List<T> flatten(@NonNull Iterable<? extends Iterable<? extends T>> iterable) {
      return asStream(iterable).flatMap(Streams::asStream).collect(Collectors.toList());
   }

   /**
    * <p>Retains all items in collection1 and collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list of the collection1 + collection2
    */
   static <E> List<E> union(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return Stream.concat(collection1.stream(), collection2.stream()).collect(Collectors.toList());
   }

   /**
    * <p>Retains all items that are in both collection1 and collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list containing the intersection of collection1 and collection2
    */
   static <E> List<E> intersection(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return collection1.stream().filter(collection2::contains).collect(Collectors.toList());
   }

   /**
    * <p>Retains all items in collection1 that are not in collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list of the collection1 - collection2
    */
   static <E> List<E> difference(@NonNull Collection<? extends E> collection1, @NonNull Collection<? extends E> collection2) {
      return collection1.stream().filter(v -> !collection2.contains(v)).collect(Collectors.toList());
   }


   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new array list containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> List<T> list(T... elements) {
      return createList(ArrayList::new, elements);
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new linked list containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> List<T> linkedList(T... elements) {
      return createList(LinkedList::new, elements);
   }

   /**
    * Creates a copy on write array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new copy on write array list  containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> List<T> concurrentList(T... elements) {
      return createList(CopyOnWriteArrayList::new, elements);
   }

   /**
    * Creates a new list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param supplier Supplies new set instances
    * @param elements the elements to add to the  set
    * @return the new list containing the given elements
    */
   @SafeVarargs
   @SuppressWarnings("varargs")
   static <T> List<T> createList(@NonNull Supplier<List<T>> supplier, T... elements) {
      if (elements == null || elements.length == 0) {
         return supplier.get();
      }
      return createList(supplier, asStream(elements));
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new array list containing the given elements
    */
   static <T> List<T> asArrayList(Stream<? extends T> stream) {
      return createList(ArrayList::new, stream);
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new linked list containing the given elements
    */
   static <T> List<T> asLinkedList(Stream<? extends T> stream) {
      return createList(LinkedList::new, stream);
   }


   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   static <T> List<T> asConcurrentList(Stream<? extends T> stream) {
      return createList(CopyOnWriteArrayList::new, stream);
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new array list containing the given elements
    */
   static <T> List<T> asArrayList(Iterator<? extends T> iterator) {
      return createList(ArrayList::new, asStream(iterator));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new linked list containing the given elements
    */
   static <T> List<T> asLinkedList(Iterator<? extends T> iterator) {
      return createList(LinkedList::new, asStream(iterator));
   }


   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   static <T> List<T> asConcurrentList(Iterator<? extends T> iterator) {
      return createList(CopyOnWriteArrayList::new, asStream(iterator));
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new array list containing the given elements
    */
   static <T> List<T> asArrayList(Iterable<? extends T> iterable) {
      return createList(ArrayList::new, asStream(iterable));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new linked list containing the given elements
    */
   static <T> List<T> asLinkedList(Iterable<? extends T> iterable) {
      return createList(LinkedList::new, asStream(iterable));
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   static <T> List<T> asConcurrentList(Iterable<? extends T> iterable) {
      return createList(CopyOnWriteArrayList::new, asStream(iterable));
   }

   /**
    * Creates a new list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param supplier Supplies new set instances
    * @param stream   the elements to add to the  set
    * @return the new list containing the given elements
    */
   static <T> List<T> createList(@NonNull Supplier<List<T>> supplier, Stream<? extends T> stream) {
      if (stream == null) {
         return supplier.get();
      }
      return stream.collect(Collectors.toCollection(supplier));
   }


}//END OF Lists
