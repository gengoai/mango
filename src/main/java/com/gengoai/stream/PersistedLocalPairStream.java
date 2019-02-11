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

import com.gengoai.conversion.Cast;
import com.gengoai.function.SerializableRunnable;

import java.util.Map;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class PersistedLocalPairStream<T, U> extends LazyLocalPairStream<T, U> {
   private final MStream<Map.Entry<T, U>> stream;
   private final CacheStrategy cacheStrategy;
   private SerializableRunnable onCloseHandler = null;

   public PersistedLocalPairStream(MStream<Map.Entry<T, U>> stream, CacheStrategy cacheStrategy) {
      this.stream = stream;
      this.cacheStrategy = cacheStrategy;
   }

   @Override
   public MPairStream<T, U> cache() {
      return Cast.as(cacheStrategy.cachePairStream(Cast.as(stream.javaStream())));
   }

   @Override
   public void close() throws Exception {
      if (onCloseHandler != null) {
         onCloseHandler.run();
      }
   }

   @Override
   protected MPairStream<T, U> getLocalStream() {
      return new LocalPairStream<>(this::javaStream, cacheStrategy);
   }

   @Override
   public Stream<Map.Entry<T, U>> javaStream() {
      Stream<Map.Entry<T, U>> javaStream = stream.javaStream();
      if (onCloseHandler != null) {
         javaStream = javaStream.onClose(onCloseHandler);
      }
      return javaStream;
   }

   @Override
   public MPairStream<T, U> onClose(SerializableRunnable closeHandler) {
      if (this.onCloseHandler == null) {
         this.onCloseHandler = closeHandler;
      } else {
         final SerializableRunnable old = this.onCloseHandler;
         this.onCloseHandler = () -> {
            old.run();
            closeHandler.run();
         };
      }
      return this;
   }

}//END OF PersistedLocalPairStream
