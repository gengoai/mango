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

package com.davidbracewell.collection.map;

import com.davidbracewell.conversion.Cast;

import java.util.*;

/**
 * @author David B. Bracewell
 */
public class ArrayListMultimap<K, V> extends BaseListMultimap<K, V> {
   private static final long serialVersionUID = 1L;

   public static <K, V> ArrayListMultimap<K, V> create() {
      return new ArrayListMultimap<>();
   }

   @Override
   protected Map<K, List<V>> createMap() {
      return new HashMap<>();
   }

   @Override
   protected List<V> createList() {
      return new ArrayList<>();
   }

   @Override
   public void trimToSize() {
      asMap().values().stream().map(Cast::<ArrayList<V>>as).forEach(ArrayList::trimToSize);
   }


   public static void main(String[] args) throws Exception {
      Multimap<String, Integer> map = ArrayListMultimap.create();
      map.put("A", 1);
      map.get("Z").add(2);
      Iterator<Integer> itr = map.get("A").iterator();
      itr.next();
      itr.remove();
      System.out.println(map.keySet());
   }


}//END OF ArrayListMultimap
