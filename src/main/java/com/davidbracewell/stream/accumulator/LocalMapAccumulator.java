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

package com.davidbracewell.stream.accumulator;

import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author David B. Bracewell
 */
public class LocalMapAccumulator<K, V> implements MMapAccumulator<K, V> {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final Map<K, V> map = new ConcurrentHashMap<>();

   public LocalMapAccumulator() { this(null);}

   public LocalMapAccumulator(String name) {this.name = name;}

   @Override
   public void add(@NonNull Tuple2<K, V> objects) {
      map.put(objects.v1, objects.v2);
   }

   @Override
   public Map<K, V> value() {
      return map;
   }

   @Override
   public void merge(@NonNull MAccumulator<Tuple2<K, V>, Map<K, V>> other) {
      Preconditions.checkArgument(LocalMapAccumulator.class == other.getClass(),
                                  "Only other " + this.getClass().getSimpleName() + " can be merged");
      map.putAll(other.value());
   }

   @Override
   public Optional<String> name() {
      return Optional.ofNullable(name);
   }

   @Override
   public void reset() {
      map.clear();
   }

   @Override
   public void put(K key, V value) {
      map.put(key, value);
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> other) {
      map.putAll(other);
   }

}//END OF LocalMapAccumulator
