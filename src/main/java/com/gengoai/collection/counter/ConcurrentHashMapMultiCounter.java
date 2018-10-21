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

package com.gengoai.collection.counter;

import com.gengoai.json.JsonEntry;

import java.lang.reflect.Type;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementation of a MultiCounter using a ConcurrentHashMaps with {@link ConcurrentHashMapCounter} as the child
 * counters..
 *
 * @param <K> the first key type
 * @param <V> the second type
 * @author David B. Bracewell
 */
public class ConcurrentHashMapMultiCounter<K, V> extends BaseMultiCounter<K, V> {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new Concurrent hash map multi counter.
    */
   public ConcurrentHashMapMultiCounter() {
      super(new ConcurrentHashMap<>());
   }

   @Override
   protected Counter<V> createCounter() {
      return new ConcurrentHashMapCounter<>();
   }

   @Override
   protected MultiCounter<K, V> newInstance() {
      return new ConcurrentHashMapMultiCounter<>();
   }

   /**
    * Deserializes a <code>ConcurrentHashMapMultiCounter</code> from Json
    *
    * @param <K>   the key type parameter
    * @param <V>   the value type parameter
    * @param entry the json entry
    * @param types the key and value types
    * @return the multi counter
    */
   static <K, V> MultiCounter<K, V> fromJson(JsonEntry entry, Type... types) {
      return MultiCounter.fromJson(new ConcurrentHashMapMultiCounter<>(), entry, types);
   }

}//END OF HashMapMultiCounter
