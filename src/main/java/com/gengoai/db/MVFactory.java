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

package com.gengoai.db;

import com.gengoai.collection.counter.ConcurrentHashMapCounter;
import com.gengoai.collection.counter.Counter;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

class MVFactory {

   private static final Counter<File> fileCounter = new ConcurrentHashMapCounter<>();
   private static final ReentrantLock lock = new ReentrantLock();
   private static final Map<File, MVStore> openStores = new ConcurrentHashMap<>();

   public static void close(File file) {
      lock.lock();
      try {
         if (openStores.containsKey(file)) {
            fileCounter.decrement(file);
            if (!fileCounter.contains(file)) {
               openStores.get(file).close();
            }
         }
      } finally {
         lock.unlock();
      }
   }

   public static <K, V> MVMap<K, V> getMap(File file, String namespace, boolean compressed) {
      lock.lock();
      try {
         if (compressed) {
            openStores.computeIfAbsent(file,
                                       f -> new MVStore.Builder().compress().fileName(f.getAbsolutePath()).open());
         } else {
            openStores.computeIfAbsent(file, f -> MVStore.open(f.getAbsolutePath()));
         }
         fileCounter.increment(file);
         return openStores.get(file).openMap(namespace);
      } finally {
         lock.unlock();
      }
   }

}//END OF MVFactory
