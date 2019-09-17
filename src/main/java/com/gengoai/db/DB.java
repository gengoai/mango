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

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;

/**
 * @author David B. Bracewell
 */
public final class DB {
   private DB() {
      throw new IllegalAccessError();
   }

   public static <K, V> KeyValueStore<K, V> inMemoryKeyValueStore(String namespace) {
      return new InMemoryKeyValueStore<>(namespace);
   }

   public static <K, V> NavigableKeyValueStore<K, V> inMemoryNavigableKeyValueStore(String namespace) {
      return new InMemoryNavigableKeyValueStore<>(namespace);
   }

   public static <K, V> KeyValueStore<K, V> keyValueStore(Resource location, String namespace) {
      return navigableKeyValueStore(location, namespace);
   }

   public static <K, V> KeyValueStore<K, V> keyValueStore(Resource location, String namespace, boolean compressed) {
      return navigableKeyValueStore(location, namespace, compressed);
   }

   public static <K, V> KeyValueStore<K, V> keyValueStore(String namespace) {
      return navigableKeyValueStore(namespace);
   }

   public static <K, V> NavigableKeyValueStore<K, V> navigableKeyValueStore(Resource location, String namespace, boolean compressed) {
      return new MVKeyValueStore<>(
         location.asFile().orElseThrow(() -> new IllegalArgumentException("Resource must be file-based")),
         namespace,
         compressed);
   }

   public static <K, V> NavigableKeyValueStore<K, V> navigableKeyValueStore(String namespace) {
      return navigableKeyValueStore(Resources.temporaryFile().deleteOnExit(), namespace);
   }

   public static <K, V> NavigableKeyValueStore<K, V> navigableKeyValueStore(Resource location, String namespace) {
      return navigableKeyValueStore(location, namespace, false);
   }

}//END OF DB
