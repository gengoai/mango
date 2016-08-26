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

import com.davidbracewell.collection.Collect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public interface ListMultimap<K, V> extends Multimap<K, V> {

   @Override
   List<V> get(Object key);

   @Override
   default List<V> removeAll(Object key) {
      List<V> list = get(key);
      List<V> toReturn = new ArrayList<>(list);
      list.clear();
      return toReturn;
   }

   @Override
   default Collection<V> replaceAll(K key, Iterable<? extends V> values) {
      List<V> list = get(key);
      List<V> toReturn = new ArrayList<>(list);
      list.clear();
      Collect.addAll(list, values);
      return toReturn;
   }

   void trimToSize();

}//END OF ListMultimap
