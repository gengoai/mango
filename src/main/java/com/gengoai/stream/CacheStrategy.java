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

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public enum CacheStrategy {
   InMemory {
      @Override
      @SuppressWarnings("unchecked")
      public MStream cacheStream(Stream<?> stream) {
         return new InMemoryPersistedLocalStream(stream.collect(Collectors.toList()));
      }

      @Override
      @SuppressWarnings("unchecked")
      public MPairStream cachePairStream(Stream<Map.Entry> pairStream) {
         return new PersistedLocalPairStream(
            new InMemoryPersistedLocalStream<>(pairStream.collect(Collectors.toList())), this);
      }
   },
   OnDisk {
      @Override
      @SuppressWarnings("unchecked")
      public MStream cacheStream(Stream<?> stream) {
         return new OnDiskPersistedLocalStream(stream);
      }

      @Override
      @SuppressWarnings("unchecked")
      public MPairStream cachePairStream(Stream<Map.Entry> pairStream) {
         return new PersistedLocalPairStream(new OnDiskPersistedLocalStream<>(pairStream), this);
      }
   };


   public abstract MStream cacheStream(Stream<?> stream);

   public abstract MPairStream cachePairStream(Stream<Map.Entry> pairStream);
}//END OF CacheFunctions
