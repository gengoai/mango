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
import lombok.NonNull;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author David B. Bracewell
 */
public final class MVStoreRegistry {
   private static final Counter<File> fileCounter = new ConcurrentHashMapCounter<>();
   private static final ReentrantLock lock = new ReentrantLock();
   private static final Map<File, MVStore> stores = new ConcurrentHashMap<>();

   private MVStoreRegistry() {
      throw new IllegalAccessError();
   }

   public static boolean close(@NonNull File file) {
      lock.lock();
      try {
         if (stores.containsKey(file)) {
            fileCounter.decrement(file);
            if (!fileCounter.contains(file)) {
               stores.get(file).close();
               stores.remove(file);
               return true;
            }
         }
      } finally {
         lock.unlock();
      }
      return false;
   }

   public static MVStore get(@NonNull File databaseFile, @NonNull boolean compressed) {
      final MVStore.Builder builder = new MVStore.Builder().fileName(databaseFile.getAbsolutePath());
      if (compressed) {
         builder.compress();
      }
      lock.lock();
      try {
         stores.computeIfAbsent(databaseFile, f -> builder.open());
         fileCounter.increment(databaseFile);
         return stores.get(databaseFile);
      } finally {
         lock.unlock();
      }
   }

}//END OF MVStoreRegistry
