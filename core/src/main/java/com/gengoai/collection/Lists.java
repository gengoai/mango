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

import com.gengoai.function.SerializableFunction;
import com.gengoai.stream.Streams;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.gengoai.Validation.checkArgument;
import static com.gengoai.collection.Collect.createCollection;

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
   public static <T> ArrayList<T> arrayListOf(@NonNull T... elements) {
      return createCollection(ArrayList::new, elements);
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> ArrayList<T> asArrayList(@NonNull Stream<? extends T> stream) {
      return createCollection(ArrayList::new, stream);
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> ArrayList<T> asArrayList(@NonNull Iterator<? extends T> iterator) {
      return createCollection(ArrayList::new, Streams.asStream(iterator));
   }

   /**
    * Creates an array list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new array list containing the given elements
    */
   public static <T> ArrayList<T> asArrayList(@NonNull Iterable<? extends T> iterable) {
      return createCollection(ArrayList::new, Streams.asStream(iterable));
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(@NonNull Stream<? extends T> stream) {
      return createCollection(CopyOnWriteArrayList::new, stream);
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(@NonNull Iterator<? extends T> iterator) {
      return createCollection(CopyOnWriteArrayList::new, Streams.asStream(iterator));
   }

   /**
    * Creates a copy on write array list  of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new  copy on write array list  containing the given elements
    */
   public static <T> List<T> asConcurrentList(@NonNull Iterable<? extends T> iterable) {
      return createCollection(CopyOnWriteArrayList::new, Streams.asStream(iterable));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>    the component type of the set
    * @param stream the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> List<T> asLinkedList(Stream<? extends T> stream) {
      return createCollection(LinkedList::new, stream);
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterator the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> LinkedList<T> asLinkedList(@NonNull Iterator<? extends T> iterator) {
      return createCollection(LinkedList::new, Streams.asStream(iterator));
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param iterable the elements to add to the set
    * @return the new linked list containing the given elements
    */
   public static <T> LinkedList<T> asLinkedList(@NonNull Iterable<? extends T> iterable) {
      return createCollection(LinkedList::new, Streams.asStream(iterable));
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
   public static <T> List<T> concurrentListOf(@NonNull T... elements) {
      return createCollection(CopyOnWriteArrayList::new, elements);
   }

   /**
    * Creates a linked list of the supplied elements
    *
    * @param <T>      the component type of the set
    * @param elements the elements to add to the set
    * @return the new linked list containing the given elements
    */
   @SafeVarargs
   public static <T> LinkedList<T> linkedListOf(@NonNull T... elements) {
      return createCollection(LinkedList::new, elements);
   }

   /**
    * Sample with replacement
    *
    * @param <E>  the list type parameter
    * @param list the list of elements to sample
    * @param N    the number of elements to sample
    * @return the list of sampled elements
    */
   public static <E> List<E> sampleWithReplacement(@NonNull List<? extends E> list, int N) {
      checkArgument(N > 0, "Must have a sample size > 0");
      Random random = new Random();
      List<E> sample = new ArrayList<>();
      while (sample.size() < N) {
         sample.add(list.get(random.nextInt(list.size())));
      }
      return sample;
   }

   /**
    * Transforms the given list with the given function
    *
    * @param <I>       the input list type parameter
    * @param <O>       the transformed list type parameter
    * @param list      the input list
    * @param converter the function to convert the input elements to the output elements
    * @return the transformed list (lazy transformation)
    */
   public static <I, O> List<O> transform(@NonNull List<? extends I> list,
                                          @NonNull SerializableFunction<? super I, ? extends O> converter) {
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
   public static <E> List<E> union(@NonNull Collection<? extends E> collection1,
                                   @NonNull Collection<? extends E> collection2) {
      return Streams.union(collection1, collection2).collect(Collectors.toList());
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
