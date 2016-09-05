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
import com.davidbracewell.io.CharsetDetectingReader;
import com.davidbracewell.io.QuietIO;
import com.davidbracewell.tuple.Tuple2;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    * @throws IOException the io exception
    */
   static Stream<String> asStream(InputStream stream) throws IOException {
      if (stream == null) {
         return Stream.empty();
      }
      return asStream(new CharsetDetectingReader(stream));
   }

   /**
    * As stream stream.
    *
    * @param reader the reader
    * @return the stream
    * @throws IOException the io exception
    */
   static Stream<String> asStream(Reader reader) throws IOException {
      if (reader == null) {
         return Stream.empty();
      }
      Stream<String> result = ((reader instanceof BufferedReader) ?
                               Cast.<BufferedReader>as(reader) :
                               new BufferedReader(reader)
      ).lines();
      result.onClose(() -> QuietIO.closeQuietly(reader));
      return result;
   }


   /**
    * As parallel stream stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the stream
    */
   static <T> Stream<T> asParallelStream(Iterator<? extends T> iterator) {
      if (iterator == null) {
         return Stream.empty();
      }
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
      if (iterable == null) {
         return Stream.empty();
      }
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
   @SuppressWarnings("varargs")
   static <T> Stream<T> asStream(T... values) {
      if (values == null) {
         return Stream.empty();
      }
      return Stream.of(values);
   }

   /**
    * As stream stream.
    *
    * @param <T>   the type parameter
    * @param value the value
    * @return the stream
    */
   static <T> Stream<T> asStream(T value) {
      if (value == null) {
         return Stream.empty();
      }
      return Stream.of(value);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterator the iterator
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterator<? extends T> iterator) {
      if (iterator == null) {
         return Stream.empty();
      }
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
      if (iterator == null) {
         return Stream.empty();
      }
      return StreamSupport.stream(Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED), parallel);
   }

   /**
    * As stream stream.
    *
    * @param <T>      the type parameter
    * @param iterable the iterable
    * @return the stream
    */
   static <T> Stream<T> asStream(Iterable<? extends T> iterable) {
      if (iterable == null) {
         return Stream.empty();
      }
      return asStream(iterable, false);
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
      if (iterable == null) {
         return Stream.empty();
      }
      return StreamSupport.stream(Cast.as(iterable.spliterator()), parallel);
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
   static <T, U> Stream<Map.Entry<T, U>> zip(@NonNull final Stream<T> stream1, @NonNull final Stream<U> stream2) {
      if (stream1 == null || stream2 == null) {
         return Stream.empty();
      }
      return Collect.zip(stream1.iterator(), stream2.iterator());
   }

   /**
    * Zip with index.
    *
    * @param <T>    the type parameter
    * @param stream the stream
    * @return the stream
    */
   static <T> Stream<Map.Entry<T, Integer>> zipWithIndex(Stream<T> stream) {
      if (stream == null) {
         return Stream.empty();
      }
      final AtomicInteger integer = new AtomicInteger();
      return stream.map(t -> new Tuple2<>(t, integer.getAndIncrement()));
   }
}//END OF Streams
