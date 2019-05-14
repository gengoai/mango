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
 *
 */

package com.gengoai.persistence.mv;

import com.gengoai.Validation;
import com.gengoai.collection.Sorting;
import com.gengoai.json.JsonEntry;
import com.gengoai.persistence.DBDocument;
import com.gengoai.persistence.Index;
import com.gengoai.persistence.IndexType;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.stream.LongStream;


/**
 * @author David B. Bracewell
 */
class MVIndex extends Index {
   private static final long serialVersionUID = 1L;
   private final MVMap<Object, Set<Long>> index;

   public MVIndex(String fieldName, IndexType indexType, MVStore store) {
      super(fieldName, "index_" + fieldName, indexType);
      this.index = store.openMap(getIndexName());
   }


   private void add(Object value, long docId) {
      Set<Long> ids = index.computeIfAbsent(value, v -> new HashSet<>());
      if (getIndexType() == IndexType.Unique) {
         Validation.checkArgument(ids.isEmpty() || (ids.size() == 1 && ids.contains(docId)),
                                  () -> "Unique Field Violation: " + getFieldName() + " = " + value);
      }
      ids.add(docId);
      index.put(value, ids);
   }

   public void add(DBDocument document) {
      if (document.contains(getFieldName())) {
         add(document.get(getFieldName()), document.getId());
      }
   }

   public void add(JsonEntry value, long docId) {
      if (value.isArray()) {
         value.elementIterator().forEachRemaining(e -> add(e, docId));
      } else if (value.isNumber()) {
         add(value.getAsNumber(), docId);
      } else if (value.isBoolean()) {
         add(value.getAsBoolean(), docId);
      } else {
         add(value.getAsString(), docId);
      }
   }

   public LongStream lookup(Object value) {
      return index.getOrDefault(value, Collections.emptySet())
                  .stream()
                  .mapToLong(l -> l);
   }

   public Set<Long> range(Object lower, Object upper) {
      Set<Long> toReturn = new HashSet<>();
      for (Iterator<Object> itr = index.keyIterator(lower); itr.hasNext(); ) {
         Object next = itr.next();
         if (Sorting.compare(next, upper) >= 0) {
            System.out.println(next);
            break;
         }
         toReturn.addAll(index.get(next));
      }
      return toReturn;
   }

   private void remove(Object value, long docId) {
      Set<Long> ids = index.computeIfAbsent(value, v -> new HashSet<>());
      ids.remove(docId);
      index.put(value, ids);
   }

   public void remove(DBDocument document) {
      if (document.contains(getFieldName())) {
         remove(document.get(getFieldName()), document.getId());
      }
   }

   protected void remove(JsonEntry value, long docId) {
      if (value.isArray()) {
         value.elementIterator().forEachRemaining(e -> remove(e, docId));
      } else if (value.isNumber()) {
         remove(value.getAsNumber(), docId);
      } else if (value.isBoolean()) {
         remove(value.getAsBoolean(), docId);
      } else {
         remove(value.getAsString(), docId);
      }
   }

}//END OF MVIndex
