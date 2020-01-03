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
 *
 */

package com.gengoai.stream;

import com.gengoai.Validation;
import com.gengoai.collection.Maps;
import com.gengoai.collection.Streams;
import com.gengoai.collection.disk.NavigableDiskMap;
import com.gengoai.function.SerializableRunnable;
import com.gengoai.io.Resources;
import lombok.NonNull;

import java.io.File;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.LongStream;
import java.util.stream.Stream;

/**
 * <p>Implementation of an {@link MStream} that stores its data on disk. The data stored in the stream is immutable
 * with all operations performed lazily converting the stream into a {@link LocalStream}. The lazy operations will
 * create a new disk persisted stream on calls to {@link #cache()}.</p>
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class OnDiskPersistedLocalStream<T> extends LazyLocalStream<T> {
   /**
    * The constant DATA_MAP_NAME.
    */
   public static final String DATA_MAP_NAME = "data";
   private boolean isParallel = false;
   private NavigableDiskMap<Long, T> map;
   private SerializableRunnable onClose;


   /**
    * Instantiates a new On disk persisted local stream.
    *
    * @param db the db
    */
   public OnDiskPersistedLocalStream(File db) {
      this.map = NavigableDiskMap.<Long, T>builder()
         .namespace(DATA_MAP_NAME)
         .file(db)
         .compressed(true)
         .build();
   }


   /**
    * Instantiates a new On disk persisted local stream.
    *
    * @param source the source
    */
   public OnDiskPersistedLocalStream(@NonNull Stream<T> source) {
      this(createTemporaryStream(source));
      onClose = () -> map.getHandle().delete();
   }

   /**
    * Instantiates a new On disk persisted local stream.
    *
    * @param source the source
    */
   public OnDiskPersistedLocalStream(@NonNull MStream<T> source) {
      this(source.javaStream());
   }

   private static <E> File createTemporaryStream(Stream<E> stream) {
      File tempFile = Resources.temporaryFile().asFile().get();
      NavigableDiskMap<Long, E> map = NavigableDiskMap.<Long, E>builder()
         .namespace(DATA_MAP_NAME)
         .file(tempFile)
         .compressed(true)
         .build();
      AtomicLong indexer = new AtomicLong();
      stream.forEach(data -> map.put(indexer.getAndIncrement(), data));
      map.commit();
      try {
         map.close();
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
      return tempFile;
   }

   @Override
   public MStream<T> cache() {
      return this;
   }

   @Override
   public void close() throws Exception {
      if (onClose != null) {
         onClose.run();
      }
   }

   @Override
   public long count() {
      return map.size();
   }

   @Override
   protected MStream<T> getLocalStream() {
      return new LocalStream<>(this::javaStream, CacheStrategy.OnDisk);
   }

   @Override
   public Stream<T> javaStream() {
      return javaStream(0L);
   }

   private Stream<T> javaStream(long start) {
      Stream<T> stream = Streams.asStream(Maps.tailKeyIterator(map, start)).map(map::get).onClose(onClose);
      return isParallel ? stream.parallel() : stream;
   }

   /**
    * Last id long.
    *
    * @return the long
    */
   public long lastId() {
      return map.lastKey();
   }

   @Override
   public MStream<T> onClose(SerializableRunnable closeHandler) {
      if (onClose == null) {
         onClose = closeHandler;
      } else if (closeHandler != null) {
         onClose = SerializableRunnable.chain(onClose, closeHandler);
      }
      return this;
   }

   @Override
   public MStream<T> parallel() {
      this.isParallel = true;
      return this;
   }

   @Override
   public MStream<T> sample(boolean withReplacement, int number) {
      Validation.checkArgument(number >= 0, "Sample size must be non-negative.");
      if (number == 0) {
         return StreamingContext.local().empty();
      }
      if (withReplacement) {
         final long size = map.size();
         return new OnDiskPersistedLocalStream<>(
            LongStream.generate(() -> (long) (Math.random() * size)).mapToObj(map::get));
      }
      return getLocalStream().sample(false, number);
   }

   @Override
   public MStream<T> skip(long n) {
      return new LocalStream<>(() -> javaStream(n), CacheStrategy.OnDisk);
   }

}//END OF OnDiskPersistedLocalStream
