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
import com.gengoai.function.SerializableBiConsumer;
import com.gengoai.function.SerializableComparator;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public abstract class BaseJavaPairStream<T, U> implements MPairStream<T, U>, Serializable {
   private static final long serialVersionUID = 1L;


   @Override
   public final List<Map.Entry<T, U>> collectAsList() {
      return javaStream().collect(Collectors.toList());
   }

   @Override
   public final Map<T, U> collectAsMap() {
      return javaStream().collect(HashMap::new,
                                  (map, e) -> map.put(e.getKey(), e.getValue()),
                                  HashMap::putAll);
   }

   @Override
   public long count() {
      return javaStream().count();
   }

   @Override
   public final void forEach(SerializableBiConsumer<? super T, ? super U> consumer) {
      javaStream().forEach(e -> {
         if (e == null) {
            consumer.accept(null, null);
         } else {
            consumer.accept(e.getKey(), e.getValue());
         }
      });
   }

   @Override
   public final void forEachLocal(SerializableBiConsumer<? super T, ? super U> consumer) {
      javaStream().sequential().forEach(e -> {
         if (e == null) {
            consumer.accept(null, null);
         } else {
            consumer.accept(e.getKey(), e.getValue());
         }
      });
   }

   @Override
   public final StreamingContext getContext() {
      return StreamingContext.local();
   }

   @Override
   public final boolean isEmpty() {
      return count() == 0;
   }

   @Override
   public Optional<Map.Entry<T, U>> max(SerializableComparator<Map.Entry<T, U>> comparator) {
      return javaStream().max(comparator);
   }

   @Override
   public Optional<Map.Entry<T, U>> min(SerializableComparator<Map.Entry<T, U>> comparator) {
      return javaStream().min(comparator);
   }

   @Override
   public MPairStream<T, U> persist(StorageLevel storageLevel) {
      switch (storageLevel) {
         case InMemory:
            return Cast.as(CacheStrategy.InMemory.cachePairStream(Cast.as(javaStream())));
         case OnDisk:
         case OffHeap:
            return Cast.as(CacheStrategy.OnDisk.cachePairStream(Cast.as(javaStream())));
      }
      throw new IllegalArgumentException();
   }

   @Override
   public MPairStream<T, U> repartition(int numPartitions) {
      return this;
   }


}//END OF BaseJavaPairStream
