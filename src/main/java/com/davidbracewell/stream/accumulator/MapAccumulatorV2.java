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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class MapAccumulatorV2<K, V> extends AccumulatorV2<Tuple2<K, V>, Map<K, V>> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<K, V> map = new HashMap<>();

   @Override
   public void add(@NonNull Tuple2<K, V> v) {
      map.put(v.v1, v.v2);
   }

   @Override
   public AccumulatorV2<Tuple2<K, V>, Map<K, V>> copy() {
      MapAccumulatorV2<K, V> copy = new MapAccumulatorV2<>();
      copy.map.putAll(map);
      return copy;
   }

   @Override
   public boolean isZero() {
      return map.isEmpty();
   }

   @Override
   public void merge(@NonNull AccumulatorV2<Tuple2<K, V>, Map<K, V>> other) {
      Preconditions.checkArgument(other instanceof MapAccumulatorV2);
      map.putAll(Cast.<MapAccumulatorV2<K, V>>as(other).map);
   }

   @Override
   public void reset() {
      map.clear();
   }

   @Override
   public Map<K, V> value() {
      return map;
   }

}//END OF MapAccumulatorV2
