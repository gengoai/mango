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

package com.gengoai.mango.stream.accumulator;

import com.gengoai.mango.conversion.Cast;
import com.gengoai.mango.tuple.Tuple2;
import lombok.NonNull;

import java.util.Map;

/**
 * <p>An implementation of a {@link MMapAccumulator} for Spark streams</p>
 *
 * @param <K> the key type parameter of the map
 * @param <V> the value type parameter of the map
 * @author David B. Bracewell
 */
public class SparkMMapAccumulator<K, V> extends SparkMAccumulator<Tuple2<K, V>, Map<K, V>> implements MMapAccumulator<K, V> {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new SparkMMapAccumulator.
    *
    * @param name the name of the accumulator
    */
   public SparkMMapAccumulator(String name) {
      super(new LocalMMapAccumulator<>(name));
   }

   private LocalMMapAccumulator<K, V> getAccumulator() {
      return Cast.as(Cast.<AccumulatorV2Wrapper>as(accumulatorV2).accumulator);
   }

   @Override
   public void put(K key, V value) {
      getAccumulator().put(key, value);
   }

   @Override
   public void putAll(@NonNull Map<? extends K, ? extends V> other) {
      getAccumulator().putAll(other);
   }

}//END OF SparkMMapAccumulator
