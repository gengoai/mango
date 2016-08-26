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

import com.davidbracewell.collection.Iterators;
import com.davidbracewell.collection.list.ForwardingList;
import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public abstract class BaseListMultimap<K, V> implements ListMultimap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;

   private final Map<K, List<V>> map;

   public BaseListMultimap() {
      this.map = createMap();
   }

   protected abstract Map<K, List<V>> createMap();


   protected abstract List<V> createList();


   @Override
   public Map<K, Collection<V>> asMap() {
      return Cast.as(map);
   }

   @Override
   public List<V> get(Object key) {
      return new WrappedList(key);
   }

   @Override
   public boolean containsKey(Object key) {
      return map.containsKey(key);
   }

   @Override
   public boolean containsValue(Object value) {
      return map.values().stream().anyMatch(c -> c.contains(value));
   }

   @Override
   public void clear() {
      map.clear();
   }

   @Override
   public Collection<Map.Entry<K, V>> entrySet() {
      return null;
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public Set<K> keySet() {
      return map.keySet();
   }

   @Override
   public Collection<V> values() {
      return new AbstractCollection<V>() {

         @Override
         public Iterator<V> iterator() {
            return new Iterator<V>() {
               Iterator<List<V>> valueIter = map.values().iterator();
               Iterator<V> listIterator = null;

               private boolean advance() {
                  while (listIterator == null || !listIterator.hasNext()) {
                     if (!valueIter.hasNext()) {
                        return false;
                     }
                     listIterator = valueIter.next().iterator();
                  }
                  return listIterator.hasNext();
               }

               @Override
               public boolean hasNext() {
                  return advance();
               }

               @Override
               public V next() {
                  if (!advance()) {
                     throw new NoSuchElementException();
                  }
                  return listIterator.next();
               }
            };
         }

         @Override
         public int size() {
            return Iterators.size(iterator());
         }
      };
   }


   protected class WrappedList extends ForwardingList<V> {
      private static final long serialVersionUID = 1L;

      private final Object key;

      public WrappedList(Object key) {this.key = key;}

      @Override
      protected List<V> delegate() {
         return map.get(key);
      }


      private List<V> getOrCreate() {
         return map.computeIfAbsent(Cast.as(key), k -> createList());
      }

      private void removeIfEmpty() {
         if (isEmpty()) {
            map.remove(key);
         }
      }

      @Override
      public boolean add(V v) {
         return getOrCreate().add(v);
      }

      @Override
      public boolean removeAll(Collection<?> c) {
         return super.removeAll(c);
      }

      @Override
      public boolean retainAll(Collection<?> c) {
         boolean toReturn = super.retainAll(c);
         removeIfEmpty();
         return toReturn;
      }


      @Override
      public boolean remove(Object o) {
         boolean toReturn = super.remove(o);
         removeIfEmpty();
         return toReturn;
      }

      @Override
      public boolean addAll(@NonNull Collection<? extends V> c) {
         return !c.isEmpty() && getOrCreate().addAll(c);
      }

      @Override
      public void clear() {
         super.clear();
         map.remove(key);
      }

      @Override
      public V remove(int index) {
         V toReturn = super.remove(index);
         removeIfEmpty();
         return toReturn;
      }

      @Override
      public Iterator<V> iterator() {
         return new Iterator<V>() {
            Iterator<V> base = WrappedList.super.iterator();

            @Override
            public boolean hasNext() {
               return base.hasNext();
            }

            @Override
            public V next() {
               return base.next();
            }

            @Override
            public void remove() {
               base.remove();
               WrappedList.this.removeIfEmpty();
            }
         };
      }

      @Override
      public ListIterator<V> listIterator() {
         return new WrappedListIterator(super.listIterator());
      }

      @Override
      public ListIterator<V> listIterator(int index) {
         return new WrappedListIterator(super.listIterator(index));
      }

      private class WrappedListIterator implements ListIterator<V> {
         private final ListIterator<V> base;

         private WrappedListIterator(ListIterator<V> base) {this.base = base;}

         @Override
         public boolean hasNext() {
            return base.hasNext();
         }

         @Override
         public V next() {
            return base.next();
         }

         @Override
         public boolean hasPrevious() {
            return base.hasPrevious();
         }

         @Override
         public V previous() {
            return base.previous();
         }

         @Override
         public int nextIndex() {
            return base.nextIndex();
         }

         @Override
         public int previousIndex() {
            return base.previousIndex();
         }

         @Override
         public void remove() {
            base.remove();
            WrappedList.this.removeIfEmpty();
         }

         @Override
         public void set(V v) {
            base.set(v);
         }

         @Override
         public void add(V v) {
            WrappedList.this.getOrCreate();
            base.add(v);
         }
      }

   }

}//END OF BaseListMultimap
