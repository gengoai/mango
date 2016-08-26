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

package com.davidbracewell.collection.set;

import com.davidbracewell.conversion.Cast;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public abstract class ForwardingSet<E> implements Set<E>, Serializable {
   private static final long serialVersionUID = 1L;

   protected abstract Set<E> delegate();

   @Override
   public int size() {
      return delegate() == null ? 0 : delegate().size();
   }

   @Override
   public boolean isEmpty() {
      return delegate() == null || delegate().isEmpty();
   }

   @Override
   public boolean contains(Object o) {
      return delegate() != null && delegate().contains(o);
   }

   @Override
   public Iterator<E> iterator() {
      return delegate() == null ? Collections.emptyIterator() : delegate().iterator();
   }

   @Override
   public Object[] toArray() {
      return delegate() == null ? new Object[0] : delegate().toArray();
   }

   @Override
   public <T> T[] toArray(T[] a) {
      return delegate() == null ? Cast.as(new Object[0]) : delegate().toArray(a);
   }

   @Override
   public boolean add(E e) {
      return delegate().add(e);
   }

   @Override
   public boolean remove(Object o) {
      return delegate() != null && delegate().remove(o);
   }

   @Override
   public boolean containsAll(Collection<?> c) {
      return delegate() != null && delegate().containsAll(c);
   }

   @Override
   public boolean addAll(Collection<? extends E> c) {
      return delegate().addAll(c);
   }

   @Override
   public boolean retainAll(Collection<?> c) {
      return delegate() != null && delegate().retainAll(c);
   }

   @Override
   public boolean removeAll(Collection<?> c) {
      return delegate() != null && delegate().removeAll(c);
   }

   @Override
   public void clear() {
      if (delegate() != null) {
         delegate().clear();
      }
   }

}//END OF ForwardingSet
