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

package com.gengoai.persistence;

import com.gengoai.stream.MStream;

import java.io.File;

/**
 * The type Mv persistent collection.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public class MVPersistentCollection<E> implements PersistentCollection<E> {
   private final MVObjectStore<Long, E> objectStore;

   /**
    * Instantiates a new Mv persistent collection.
    *
    * @param dbFile the db file
    */
   public MVPersistentCollection(File dbFile) {
      this.objectStore = new MVObjectStore<>(dbFile);
   }

   @Override
   public void add(E element) {
      Long i = objectStore.lastKey() == null ? 0L : objectStore.lastKey() + 1;
      objectStore.put(i, element);
   }

   @Override
   public void close() throws Exception {
      objectStore.close();
   }

   @Override
   public void commit() {
      objectStore.commit();
   }

   @Override
   public boolean contains(E element) {
      return objectStore.containsValue(element);
   }

   @Override
   public E get(long index) {
      return objectStore.get(index);
   }

   @Override
   public E remove(long index) {
      if (objectStore.containsKey(index)) {
         E toReturn = objectStore.delete(index);
         long last = objectStore.lastKey();
         for (long i = index + 1; i <= last; i++) {
            objectStore.put(index - 1, objectStore.get(index));
         }
         objectStore.delete(last);
         return toReturn;
      } else {
         throw new IndexOutOfBoundsException();
      }
   }

   @Override
   public boolean remove(E element) {
      long index = objectStore.entries()
                              .filter((l, v) -> v.equals(element))
                              .keys()
                              .first()
                              .orElse(-1L);
      if (index >= 0) {
         remove(index);
         return true;
      }
      return false;
   }

   @Override
   public long size() {
      return objectStore.size();
   }

   @Override
   public MStream<E> stream() {
      return objectStore.values();
   }

   @Override
   public void update(long index, E element) {
      if (objectStore.containsKey(index)) {
         objectStore.put(index, element);
      } else {
         throw new IndexOutOfBoundsException();
      }
   }
}//END OF MVPersistentCollection
