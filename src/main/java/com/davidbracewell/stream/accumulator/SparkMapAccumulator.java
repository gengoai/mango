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
import org.apache.spark.util.AccumulatorV2;

import java.util.Map;

import static com.davidbracewell.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class SparkMapAccumulator<K, V> extends BaseSparkAccumulator<Tuple2<K, V>, Map<K, V>> implements MMapAccumulator<K, V> {
   private static final long serialVersionUID = 1L;

   public SparkMapAccumulator(AccumulatorV2<Tuple2<K, V>, Map<K, V>> accumulatorV2) {
      super(accumulatorV2);
   }

   @Override
   public void put(K key, V value) {
      accumulatorV2.add($(key, value));
   }

   @Override
   public void putAll(Map<? extends K, ? extends V> other) {
      other.forEach(this::put);
   }

}//END OF SparkMapAccumulator
