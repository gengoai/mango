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

package com.davidbracewell.collection.index;

import com.google.common.collect.Iterators;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Synchronized;
import org.apache.mahout.math.map.OpenObjectIntHashMap;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * <p>An Index implementation that uses a combination of a HashMap and List.</p>
 *
 * @param <TYPE> the type being indexed.
 * @author David B. Bracewell
 */
@EqualsAndHashCode(exclude = "map", callSuper = false)
public class HashMapIndex<TYPE> implements Index<TYPE>, Serializable {
   private static final long serialVersionUID = -288128807385573349L;
   private final OpenObjectIntHashMap<TYPE> map = new OpenObjectIntHashMap<>();
   private final List<TYPE> list = new ArrayList<>();

   @Override
   @Synchronized
   public int add(@NonNull TYPE item) {
      if (!map.containsKey(item)) {
         list.add(item);
         map.put(item, list.size() - 1);
      }
      return map.get(item);
   }

   @Override
   public void addAll(Iterable<TYPE> items) {
      if (items != null) {
         for (TYPE item : items) {
            add(item);
         }
      }
   }

   @Override
   public List<TYPE> asList() {
      return Collections.unmodifiableList(list);
   }

   @Override
   public void clear() {
      map.clear();
      list.clear();
   }

   @Override
   public boolean contains(TYPE item) {
      return map.containsKey(item);
   }

   @Override
   public Index<TYPE> copy() {
      HashMapIndex<TYPE> copy = new HashMapIndex<>();
      this.map.forEachPair(copy.map::put);
      copy.list.addAll(this.list);
      return copy;
   }

   @Override
   public TYPE get(int id) {
      if (id < 0 || id >= list.size()) {
         return null;
      }
      return list.get(id);
   }

   @Override
   public int getId(TYPE item) {
      if (map.containsKey(item)) {
         return map.get(item);
      }
      return -1;
   }

   @Override
   public boolean isEmpty() {
      return list.isEmpty();
   }

   @Override
   public Iterator<TYPE> iterator() {
      return Iterators.unmodifiableIterator(list.iterator());
   }

   @Override
   public TYPE remove(int id) {
      if (id < 0 || id >= list.size()) {
         return null;
      }
      for (int i = id + 1; i < list.size(); i++) {
         map.put(list.get(i), i - 1);
      }
      map.removeKey(list.get(id));
      return list.remove(id);
   }

   @Override
   public int remove(TYPE item) {
      int index = getId(item);
      if (index >= 0) {
         remove(index);
      }
      return index;
   }

   @Override
   public int size() {
      return list.size();
   }

   @Override
   public String toString() {
      return list.toString();
   }
}//END OF AbstractMapListIndex
