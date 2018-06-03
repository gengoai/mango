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
import com.gengoai.function.SerializablePredicate;

import java.util.*;

import static com.gengoai.Validation.notNull;

/**
 * Static methods for working with collections and iterables.
 *
 * @author David B. Bracewell
 */
public final class Collect {


   private Collect() {
      throw new IllegalAccessError();
   }

   /**
    * <p>Creates a default instance of a collection type. If the passed in class is an implementation then that
    * implementation is created using the no-arg constructor. Interfaces (e.g. Set and List) have default
    * implementations assigned and returned. </p>
    *
    * @param <T>             the type parameter
    * @param collectionClass the collection class
    * @return t t
    */
   public static <T extends Collection> T create(Class<T> collectionClass) {
      if (collectionClass == null) {
         return null;
      }
      if (Set.class.equals(collectionClass)) {
         return Cast.as(new HashSet<>());
      } else if (List.class.equals(collectionClass)) {
         return Cast.as(new ArrayList<>());
      } else if (Queue.class.equals(collectionClass)) {
         return Cast.as(new LinkedList<>());
      } else if (Deque.class.equals(collectionClass)) {
         return Cast.as(new LinkedList<>());
      } else if (Stack.class.equals(collectionClass)) {
         return Cast.as(new Stack<>());
      } else if (NavigableSet.class.equals(collectionClass)) {
         return Cast.as(new TreeSet<>());
      }
      try {
         return collectionClass.newInstance();
      } catch (InstantiationException | IllegalAccessException e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Transform collection.
    *
    * @param <I>        the type parameter
    * @param <O>        the type parameter
    * @param collection the collection
    * @param transform  the transform
    * @return the collection
    */
   static <I, O> Collection<O> transform(final Collection<? extends I> collection,
                                         final SerializableFunction<? super I, ? extends O> transform
                                        ) {
      return new TransformedCollection<>(notNull(collection), notNull(transform));
   }

   /**
    * Filter collection.
    *
    * @param <T>        the type parameter
    * @param collection the collection
    * @param filter     the filter
    * @return the collection
    */
   static <T> Collection<T> filter(final Collection<? extends T> collection,
                                   final SerializablePredicate<? super T> filter
                                  ) {
      return new FilteredCollection<>(notNull(collection), notNull(filter));
   }


   private static class TransformedCollection<I, O> extends AbstractCollection<O> {
      /**
       * The Collection.
       */
      final Collection<? extends I> collection;
      /**
       * The Transform.
       */
      final SerializableFunction<? super I, ? extends O> transform;

      private TransformedCollection(Collection<? extends I> collection, SerializableFunction<? super I, ? extends O> transform) {
         this.collection = collection;
         this.transform = transform;
      }

      @Override
      public Iterator<O> iterator() {
         return Iterators.transform(collection.iterator(), transform);
      }

      @Override
      public int size() {
         return collection.size();
      }
   }


   private static class FilteredCollection<T> extends AbstractCollection<T> {
      /**
       * The Collection.
       */
      final Collection<? extends T> collection;
      /**
       * The Filter.
       */
      final SerializablePredicate<? super T> filter;

      private FilteredCollection(Collection<? extends T> collection, SerializablePredicate<? super T> filter) {
         this.collection = collection;
         this.filter = filter;
      }


      @Override
      public Iterator<T> iterator() {
         return Iterators.filter(collection.iterator(), filter);
      }

      @Override
      public int size() {
         return Iterators.size(iterator());
      }
   }


}// END OF CollectionUtils
