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
public interface Collect {

   /**
    * <p>Creates a default instance of a collection type. If the passed in class is an implementation then that
    * implementation is created using the no-arg constructor. Interfaces (e.g. Set and List) have default
    * implementations assigned and returned. </p>
    *
    * @param collectionClass the collection class
    * @return t
    */
   static <T extends Collection> T create(Class<T> collectionClass) {
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

   static <I, O> Collection<O> transform(final Collection<? extends I> collection,
                                         final SerializableFunction<? super I, ? extends O> transform
                                        ) {
      return new TransformedCollection<>(notNull(collection), notNull(transform));
   }

   static <T> Collection<T> filter(final Collection<? extends T> collection,
                                   final SerializablePredicate<? super T> filter
                                  ) {
      return new FilteredCollection<>(notNull(collection), notNull(filter));
   }

   static <T> Optional<T> getFirst(final Collection<? extends T> collection) {
      return Iterators.next(notNull(collection).iterator());
   }

   static <T> T getFirst(final Collection<? extends T> collection, T defaultValue) {
      return Iterators.next(notNull(collection).iterator(), defaultValue);
   }

   static class TransformedCollection<I, O> extends AbstractCollection<O> {
      final Collection<? extends I> collection;
      final SerializableFunction<? super I, ? extends O> transform;

      public TransformedCollection(Collection<? extends I> collection, SerializableFunction<? super I, ? extends O> transform) {
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


   static class FilteredCollection<T> extends AbstractCollection<T> {
      final Collection<? extends T> collection;
      final SerializablePredicate<? super T> filter;

      public FilteredCollection(Collection<? extends T> collection, SerializablePredicate<? super T> filter) {
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
