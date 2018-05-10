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

package com.gengoai.collection.index;


import com.gengoai.collection.Iterators;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
final class UnmodifiableIndex<TYPE> extends ForwardingIndex<TYPE> {

   private static final long serialVersionUID = -3044104679509123935L;
   private final Index<TYPE> backing;

   UnmodifiableIndex(Index<TYPE> backing) {
      this.backing = backing;
   }

   @Override
   public int add(TYPE item) {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
   }

   @Override
   public void addAll(Iterable<TYPE> items) {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
   }

   @Override
   public void clear() {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
   }

   @Override
   protected Index<TYPE> delegate() {
      return backing;
   }

   @Override
   public Iterator<TYPE> iterator() {
      return Iterators.unmodifiableIterator(super.iterator());
   }

   @Override
   public int remove(TYPE item) {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
   }

   @Override
   public TYPE remove(int id) {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
   }

}
//END OF UnmodifiableIndex
