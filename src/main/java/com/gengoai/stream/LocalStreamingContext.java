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

package com.gengoai.stream;

import com.gengoai.collection.Streams;
import com.gengoai.conversion.Cast;
import com.gengoai.function.Unchecked;
import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import com.gengoai.stream.accumulator.*;
import com.gengoai.string.Strings;

import java.io.IOException;
import java.util.*;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Represents a local streaming context using Java's built-in streams
 *
 * @author David B. Bracewell
 */
public final class LocalStreamingContext extends StreamingContext {
   /**
    * The singleton instance of the context
    */
   public static final LocalStreamingContext INSTANCE = new LocalStreamingContext();
   private static final long serialVersionUID = 1L;

   @Override
   public void close() {

   }

   @Override
   public <E> MCounterAccumulator<E> counterAccumulator(String name) {
      return new LocalMCounterAccumulator<>(name);
   }

   @Override
   public MDoubleAccumulator doubleAccumulator(double initialValue, String name) {
      return new LocalMDoubleAccumulator(initialValue, name);
   }


   @Override
   public MDoubleStream doubleStream(DoubleStream doubleStream) {
      if (doubleStream == null) {
         return new LocalDoubleStream(DoubleStream.empty());
      }
      return new LocalDoubleStream(doubleStream);
   }

   @Override
   public MDoubleStream doubleStream(double... values) {
      if (values == null || values.length == 0) {
         return emptyDouble();
      }
      return new ReusableDoubleStream(values);
   }


   @Override
   public <T> MStream<T> empty() {
      return new InMemoryPersistedLocalStream<>(new ArrayList<>());
   }

   @Override
   public <E> MAccumulator<E, List<E>> listAccumulator(String name) {
      return new LocalMListAccumulator<>(name);
   }

   @Override
   public MLongAccumulator longAccumulator(long initialValue, String name) {
      return new LocalMLongAccumulator(initialValue, name);
   }

   @Override
   public <K, V> MMapAccumulator<K, V> mapAccumulator(String name) {
      return new LocalMMapAccumulator<>(name);
   }

   @Override
   public <K1, K2> MMultiCounterAccumulator<K1, K2> multiCounterAccumulator(String name) {
      return new LocalMMultiCounterAccumulator<>(name);
   }

   @Override
   public <K, V> MPairStream<K, V> pairStream(Map<? extends K, ? extends V> map) {
      if (map == null) {
         return new LocalPairStream<>(Stream.empty());
      }
      return new LocalPairStream<>(map);
   }

   @Override
   public <K, V> MPairStream<K, V> pairStream(Collection<Map.Entry<? extends K, ? extends V>> tuples) {
      return new LocalPairStream<>(tuples.stream());
   }

   @Override
   public MStream<Integer> range(int startInclusive, int endExclusive) {
      return new LocalStream<>(() -> IntStream.range(startInclusive, endExclusive)
                                              .boxed()
                                              .parallel(), CacheStrategy.InMemory);
   }

   @Override
   public <E> MAccumulator<E, Set<E>> setAccumulator(String name) {
      return new LocalMSetAccumulator<>(name);
   }

   @Override
   public MStatisticsAccumulator statisticsAccumulator(String name) {
      return new LocalMStatisticsAccumulator(name);
   }

   @Override
   public <T> MStream<T> stream(Stream<T> stream) {
      if (stream == null) {
         return empty();
      }
      return new LocalStream<>(stream);
   }

   @Override
   public <T> MStream<T> stream(Iterable<? extends T> iterable) {
      if (iterable == null) {
         return empty();
      } else if (iterable instanceof Collection) {
         return new InMemoryPersistedLocalStream<>(Cast.<Collection<T>>as(iterable));
      }
      return new LocalStream<>(Streams.asStream(iterable));
   }


   @Override
   public MStream<String> textFile(String location) {
      if (Strings.isNullOrBlank(location)) {
         return empty();
      }
      return textFile(Resources.from(location));

   }

   @Override
   public MStream<String> textFile(Resource resource) {
      if (resource == null) {
         return empty();
      }
      if (resource.isDirectory()) {
         return new LocalStream<>(resource.getChildren(true).stream()
                                          .filter(r -> !r.isDirectory())
                                          .flatMap(Unchecked.function(r -> r.lines().javaStream())));
      }
      try {
         return resource.lines();
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   @Override
   public MStream<String> textFile(Resource location, boolean wholeFile) {
      if (!wholeFile) {
         return textFile(location);
      }
      if (location == null) {
         return empty();
      }
      if (location.isDirectory()) {
         return new LocalStream<>(location.getChildren(true).stream()
                                          .filter(r -> !r.isDirectory())
                                          .map(Unchecked.function(Resource::readToString)));
      }
      try {
         return new InMemoryPersistedLocalStream<>(Collections.singleton(location.readToString()));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }


}//END OF LocalStreamingContext
