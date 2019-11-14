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

package com.gengoai.collection;

import com.gengoai.conversion.Cast;
import com.gengoai.function.SerializableFunction;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.Validation.notNull;

/**
 * <p>Convenience methods for creating lists and manipulating collections resulting in lists.</p>
 *
 * @author David B. Bracewell
 */
public final class Lists {

   private Lists() {
      throw new IllegalAccessError();
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
   public static <T> List<T> arrayListOf(T... elements) {
      return createList(ArrayList::new, elements);
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> List<T> asArrayList(Stream<? extends T> stream) {
      return createList(ArrayList::new, stream);
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> List<T> asArrayList(Iterator<? extends T> iterator) {
      return createList(ArrayList::new, Streams.asStream(iterator));
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> List<T> asArrayList(Iterable<? extends T> iterable) {
      return createList(ArrayList::new, Streams.asStream(iterable));
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(Stream<? extends T> stream) {
      return createList(CopyOnWriteArrayList::new, stream);
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(Iterator<? extends T> iterator) {
      return createList(CopyOnWriteArrayList::new, Streams.asStream(iterator));
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(Iterable<? extends T> iterable) {
      return createList(CopyOnWriteArrayList::new, Streams.asStream(iterable));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> List<T> asLinkedList(Stream<? extends T> stream) {
      return createList(LinkedList::new, stream);
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> LinkedList<T> asLinkedList(Iterator<? extends T> iterator) {
      return Cast.as(createList(LinkedList::new, Streams.asStream(iterator)));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> LinkedList<T> asLinkedList(Iterable<? extends T> iterable) {
      return Cast.as(createList(LinkedList::new, Streams.asStream(iterable)));
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
   public static <T> List<T> concurrentListOf(T... elements) {
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
   public static <T> List<T> createList(Supplier<List<T>> supplier, T... elements) {
      if (elements == null || elements.length == 0) {
         return supplier.get();
      }
      return createList(supplier, Streams.asStream(elements));
   }

   /**
    * Creates a new list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param supplier Supplies new set instances
    * @param stream   the elements to add to the  set
    * @return the new list containing the given elements
    */
   public static <T> List<T> createList(Supplier<List<T>> supplier, Stream<? extends T> stream) {
      notNull(supplier);
      if (stream == null) {
         return supplier.get();
      }
      return stream.collect(Collectors.toCollection(supplier));
   }

   /**
    * <p>Retains all items in collection1 that are not in collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list of the collection1 - collection2
    */
   public static <E> List<E> difference(Collection<? extends E> collection1, Collection<? extends E> collection2) {
      return Streams.difference(collection1, collection2).collect(Collectors.toList());
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
   public static <T> List<T> ensureSize(List<T> list, int desiredSize, T defaultValue) {
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
   public static <T> List<T> flatten(Iterable<? extends Iterable<? extends T>> iterable) {
      return Streams.flatten(iterable)
                    .collect(Collectors.toList());
   }

   /**
    * <p>Retains all items that are in both collection1 and collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list containing the intersection of collection1 and collection2
    */
   public static <E> List<E> intersection(Collection<? extends E> collection1, Collection<? extends E> collection2) {
      return Streams.intersection(collection1, collection2).collect(Collectors.toList());
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new linked list containing the given elements
    */
   @SafeVarargs
   public static <T> LinkedList<T> linkedListOf(T... elements) {
      return Cast.as(createList(LinkedList::new, elements));
   }

   /**
    * Partitions a list into multiple lists of partition size (last list may have a size less than partition size). This
    * method uses <code>subList</code> which means that each partition is a view into the underlying list.
    *
    * @param <T>           the list component type
    * @param list          the list to partition
    * @param partitionSize the partition size
    * @return A list of partitioned lists
    */
   public static <T> List<List<T>> partition(List<T> list, int partitionSize) {
      notNull(list);
      checkArgument(partitionSize > 0, "Partition size must be >= 0");
      List<List<T>> partitions = new ArrayList<>();
      for (int i = 0; i < list.size(); i += partitionSize) {
         partitions.add(list.subList(i, Math.min(list.size(), i + partitionSize)));
      }
      return partitions;
   }

   /**
    * Wraps a primitive array as  a list
    *
    * @param <E>      the output type parameter
    * @param array    the array to wrap as a list
    * @param outclass the class type of the output element
    * @return the list
    */
   public static <E> List<E> primitiveList(Object array, Class<E> outclass) {
      return new PrimitiveArrayList<>(array, outclass);
   }

   /**
    * Transform list.
    *
    * @param <I>       the type parameter
    * @param <O>       the type parameter
    * @param list      the list
    * @param converter the converter
    * @return the list
    */
   public static <I, O> List<O> transform(List<? extends I> list,
                                          SerializableFunction<? super I, ? extends O> converter) {
      return new TransformedList<>(list, converter);
   }

   /**
    * <p>Retains all items in collection1 and collection2 and returns them as a list.</p>
    *
    * @param <E>         the component type of the collections
    * @param collection1 the first collection of items
    * @param collection2 the second collection of items
    * @return A list of the collection1 + collection2
    */
   public static <E> List<E> union(Collection<? extends E> collection1, Collection<? extends E> collection2) {
      return Streams.union(collection1, collection2).collect(Collectors.toList());
   }


   public static <E> List<E> sampleWithReplacement(List<? extends E> list, int N) {
      Random random = new Random();
      List<E> sample = new ArrayList<>();
      while (sample.size() < N) {
         sample.add(list.get(random.nextInt(list.size())));
      }
      return sample;
   }

   private static class TransformedList<I, O> extends AbstractList<O> {
      private final List<I> backing;
      private final SerializableFunction<? super I, ? extends O> converter;

      private TransformedList(List<I> backing, SerializableFunction<? super I, ? extends O> converter) {
         this.backing = backing;
         this.converter = converter;
      }

      @Override
      public O get(int index) {
         return converter.apply(backing.get(index));
      }

      @Override
      public O remove(int index) {
         return converter.apply(backing.remove(index));
      }

      @Override
      public int size() {
         return backing.size();
      }
   }

}//END OF Lists
