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

import java.io.Serializable;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.TreeMap;

/**
 * @author David B. Bracewell
 */
class InMemoryNavigableKeyValueStore<K, V> extends TreeMap<K, V> implements NavigableKeyValueStore<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final String namespace;

   public InMemoryNavigableKeyValueStore(String namespace) {
      this.namespace = namespace;
   }

   @Override
   public void close() throws Exception {

   }

   @Override
   public void commit() {

   }

   @Override
   public String getNameSpace() {
      return namespace;
   }

   @Override
   public long sizeAsLong() {
      return size();
   }

   @Override
   public Iterator<K> keyIterator(K key) {
      return new Iterator<K>() {
         private K ck = ceilingKey(key);

         @Override
         public boolean hasNext() {
            return ck != null;
         }

         @Override
         public K next() {
            if (ck == null) {
               throw new NoSuchElementException();
            }
            K n = ck;
            ck = higherKey(n);
            return n;
         }
      };
   }

}//END OF InMemoryNavigableKeyValueStore
