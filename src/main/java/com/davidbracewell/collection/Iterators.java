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
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.util.*;
import java.util.stream.Stream;

import static com.davidbracewell.collection.Streams.asStream;
import static com.google.common.base.Preconditions.checkState;

/**
 * The interface Iterators.
 *
 * @author David B. Bracewell
 */
public interface Iterators {

   /**
    * Filtered iterator iterator.
    *
    * @param <E>       the type parameter
    * @param backing   the backing
    * @param predicate the predicate
    * @return the iterator
    */
   static <E> Iterator<E> filter(@NonNull final Iterator<E> backing, @NonNull SerializablePredicate<? super E> predicate) {
      return new Iterator<E>() {
         List<E> buffer;

         @Override
         public boolean hasNext() {
            while (buffer == null && backing.hasNext()) {
               E next = backing.next();
               if (predicate.test(next)) {
                  buffer = Collections.singletonList(next);
               }
            }
            return buffer != null && !buffer.isEmpty();
         }

         @Override
         public E next() {
            checkState(hasNext(), "No such element");
            E rval = buffer.get(0);
            buffer = null;
            return rval;
         }
      };
   }

   /**
    * First optional.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the optional
    */
   static <T> Optional<T> getFirst(@NonNull Iterator<T> iterator) {
      return asStream(iterator).findFirst();
   }

   static <T> Optional<T> getLast(@NonNull Iterator<T> iterator) {
      T object = null;
      while (iterator.hasNext()) {
         object = iterator.next();
      }
      return Optional.ofNullable(object);
   }

   /**
    * Size int.
    *
    * @param iterator the iterator
    * @return the int
    */
   static int size(Iterator<?> iterator) {
      return (int) asStream(iterator).count();
   }

   /**
    * Transformed iterator iterator.
    *
    * @param <E>     the type parameter
    * @param <R>     the type parameter
    * @param backing the backing
    * @param mapper  the mapper
    * @return the iterator
    */
   static <E, R> Iterator<R> transform(@NonNull final Iterator<E> backing, @NonNull SerializableFunction<? super E, ? extends R> mapper) {
      return new Iterator<R>() {
         @Override
         public boolean hasNext() {
            return backing.hasNext();
         }

         @Override
         public R next() {
            return mapper.apply(backing.next());
         }

         @Override
         public void remove() {
            backing.remove();
         }
      };
   }

   /**
    * Unmodifiable iterator iterator.
    *
    * @param <E>     the type parameter
    * @param backing the backing
    * @return the iterator
    */
   static <E> Iterator<E> unmodifiable(@NonNull final Iterator<E> backing) {
      return new Iterator<E>() {
         @Override
         public boolean hasNext() {
            return backing.hasNext();
         }

         @Override
         public E next() {
            return backing.next();
         }
      };
   }

   static <E> Iterator<E> concat(@NonNull Iterator<? extends E> iterator1, @NonNull Iterator<? extends E> iterator2) {
      return Stream.concat(asStream(iterator1), asStream(iterator2)).iterator();
   }


   /**
    * Zip stream.
    *
    * @param <T>       the type parameter
    * @param <U>       the type parameter
    * @param iterator1 the iterator 1
    * @param iterator2 the iterator 2
    * @return the stream
    */
   static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Iterator<T> iterator1, @NonNull final Iterator<U> iterator2) {
      return asStream(new Iterator<Map.Entry<T, U>>() {
         @Override
         public boolean hasNext() {
            return iterator1.hasNext() && iterator2.hasNext();
         }

         @Override
         public Map.Entry<T, U> next() {
            if (!iterator1.hasNext() || !iterator2.hasNext()) {
               throw new NoSuchElementException();
            }
            return new Tuple2<>(iterator1.next(), iterator2.next());
         }
      });
   }
}//END OF Iterators
