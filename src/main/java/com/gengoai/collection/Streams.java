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
import com.gengoai.io.CharsetDetectingReader;
import com.gengoai.io.QuietIO;
import com.gengoai.tuple.Tuple2;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.Reader;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.gengoai.Validation.notNull;

/**
 * The interface Streams.
 *
 * @author David B. Bracewell
 */
public interface Streams {


   /**
    * As stream stream.
    *
    * @param stream the stream
    * @return the stream
    */
   static Stream<String> asStream(InputStream stream) {
      return asStream(new CharsetDetectingReader(notNull(stream)));
   }

   /**
    * As stream stream.
    *
    * @param reader the reader
    * @return the stream
    */
   static Stream<String> asStream(Reader reader) {
      notNull(reader);
      return ((reader instanceof BufferedReader) ? Cast.<BufferedReader>as(reader)
                                                 : new BufferedReader(reader))
                .lines()
                .onClose(() -> QuietIO.closeQuietly(reader));
   }


   /**
    * As parallel stream stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the stream
    */
   static <T> Stream<T> asParallelStream(Iterator<? extends T> iterator) {
      return asStream(iterator, true);
   }

   /**
    * As parallel stream stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @return the stream
    */
   static <T> Stream<T> asParallelStream(Iterable<? extends T> iterable) {
      return asStream(iterable, true);
   }

   /**
    * As stream stream.
    *
    * @param <T>    the type parameter
    * @param values the values
    * @return the stream
    */
   @SafeVarargs
   static <T> Stream<T> asStream(T... values) {
      if (values == null) {
         return Stream.empty();
      }
      return Stream.of(values);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterator<? extends T> iterator) {
      return asStream(iterator, false);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @param parallel the parallel
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterator<? extends T> iterator, boolean parallel) {
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(notNull(iterator), Spliterator.ORDERED),
                                  parallel);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterable<? extends T> iterable) {
      return asStream(notNull(iterable), false);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @param parallel the parallel
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterable<? extends T> iterable, boolean parallel) {
      return StreamSupport.stream(Cast.as(notNull(iterable).spliterator()), parallel);
   }

   /**
    * Zip stream.
    *
    * @param <T>     the type parameter
    * @param <U>     the type parameter
    * @param stream1 the stream 1
    * @param stream2 the stream 2
    * @return the stream
    */
   static <T, U> Stream<Map.Entry<T, U>> zip(final Stream<? extends T> stream1, final Stream<? extends U> stream2) {
      return asStream(Iterators.zip(notNull(stream1).iterator(), notNull(stream2).iterator()));
   }

   /**
    * Zip with index.
    *
    * @param <T>    the type parameter
    * @param stream the stream
    * @return the stream
    */
   static <T> Stream<Map.Entry<T, Integer>> zipWithIndex(Stream<T> stream) {
      final AtomicInteger integer = new AtomicInteger();
      return notNull(stream).map(t -> new Tuple2<>(t, integer.getAndIncrement()));
   }

   /**
    * Flatten stream.
    *
    * @param <T>       the type parameter
    * @param iterables the iterables
    * @return the stream
    */
   static <T> Stream<T> flatten(Iterable<? extends Iterable<? extends T>> iterables) {
      return asStream(notNull(iterables)).flatMap(Streams::asStream);
   }

   /**
    * Union stream.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the stream
    */
   static <T> Stream<T> union(Collection<? extends T> c1, Collection<? extends T> c2) {
      return Stream.concat(notNull(c1).stream(), notNull(c2).stream());
   }

   /**
    * Intersection stream.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the stream
    */
   static <T> Stream<T> intersection(Collection<? extends T> c1, Collection<? extends T> c2) {
      notNull(c2);
      return Cast.as(notNull(c1).stream().filter(c2::contains));
   }

   /**
    * Difference stream.
    *
    * @param <T> the type parameter
    * @param c1  the c 1
    * @param c2  the c 2
    * @return the stream
    */
   static <T> Stream<T> difference(Collection<? extends T> c1, Collection<? extends T> c2) {
      notNull(c2);
      return Cast.as(notNull(c1).stream().filter(v -> !c2.contains(v)));
   }

   /**
    * Transform stream.
    *
    * @param <IN>      the type parameter
    * @param <OUT>     the type parameter
    * @param iterable  the iterable
    * @param transform the transform
    * @return the stream
    */
   static <IN, OUT> Stream<OUT> transform(Iterable<? extends IN> iterable, Function<? super IN, ? extends OUT> transform) {
      return Cast.as(asStream(iterable).map(notNull(transform)));
   }

   /**
    * Filter stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @param filter   the filter
    * @return the stream
    */
   static <T> Stream<T> filter(Iterable<? extends T> iterable, Predicate<? super T> filter) {
      return Cast.as(asStream(iterable).filter(notNull(filter)));
   }


}//END OF Streams
