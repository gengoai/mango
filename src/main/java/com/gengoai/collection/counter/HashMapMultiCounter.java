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

import java.util.HashMap;
import java.util.Map;

/**
 * Implementation of a MultiCounter using a HashMaps.
 *
 * @param <K> the first key type
 * @param <V> the second type
 * @author David B. Bracewell
 */
public class HashMapMultiCounter<K, V> extends BaseMultiCounter<K, V> {
   private static final long serialVersionUID = 1L;

   @Override
   protected Map<K, Counter<V>> createMap() {
      return new HashMap<>();
   }

   @Override
   protected Counter<V> createCounter() {
      return new HashMapCounter<>();
   }

   @Override
   protected MultiCounter<K, V> newInstance() {
      return new HashMapMultiCounter<>();
   }

}//END OF HashMapMultiCounter
