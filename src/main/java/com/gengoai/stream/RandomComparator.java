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

package com.gengoai.stream;

import com.gengoai.function.SerializableComparator;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * A comparator that generates a random number that is used for comparison for each object
 *
 * @param <T> the type of object being compared
 * @author David B. Bracewell
 */
class RandomComparator<T> implements SerializableComparator<T> {
   private final Map<T, Double> map = new HashMap<>();
   private final Random random;

   /**
    * Instantiates a new Random comparator.
    *
    * @param random the random
    */
   public RandomComparator(Random random) {
      this.random = random;
   }

   @Override
   public int compare(T o1, T o2) {
      return Double.compare(map.computeIfAbsent(o1, o -> random.nextDouble()),
                            map.computeIfAbsent(o2, o -> random.nextDouble()));
   }
}
