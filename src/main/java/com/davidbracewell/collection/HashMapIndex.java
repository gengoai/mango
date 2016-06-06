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

package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public class HashMapIndex<TYPE> implements Index<TYPE>, Serializable {

  private static final long serialVersionUID = -288128807385573349L;
  private final Map<TYPE, Integer> map = new HashMap<>();
  private final List<TYPE> list = new ArrayList<>();

  @Override
  public int add(@NonNull TYPE item) {
    if (map.containsKey(item)) {
      return map.get(item);
    }
    if (list.add(item)) {
      map.put(item, list.size() - 1);
      return list.size() - 1;
    }
    return -1;
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
  public Index<TYPE> copy() {
    HashMapIndex<TYPE> copy = new HashMapIndex<>();
    copy.map.putAll(this.map);
    copy.list.addAll(this.list);
    return copy;
  }

  @Override
  public int indexOf(TYPE item) {
    if (map.containsKey(item)) {
      return map.get(item);
    }
    return -1;
  }

  @Override
  public TYPE get(int id) {
    if (id < 0 || id >= list.size()) {
      return null;
    }
    return list.get(id);
  }

  @Override
  public void clear() {
    map.clear();
    list.clear();
  }

  @Override
  public int size() {
    return list.size();
  }

  @Override
  public boolean isEmpty() {
    return list.isEmpty();
  }

  @Override
  public TYPE remove(int id) {
    if (id < 0 || id >= list.size()) {
      return null;
    }
    for (int i = id + 1; i < list.size(); i++) {
      map.put(list.get(i), i - 1);
    }
    map.remove(list.get(id));
    return list.remove(id);
  }

  @Override
  public int remove(TYPE item) {
    int index = indexOf(item);
    if (index >= 0) {
      remove(index);
    }
    return index;
  }

  @Override
  public boolean contains(TYPE item) {
    return map.containsKey(item);
  }

  @Override
  public List<TYPE> asList() {
    return Collections.unmodifiableList(list);
  }

  @Override
  public TYPE set(int index, @NonNull TYPE newValue) {
    if (index < 0 || index >= list.size()) {
      throw new IndexOutOfBoundsException();
    }
    if (map.containsKey(newValue)) {
      throw new IllegalArgumentException(newValue + " already exists in the index.");
    }
    TYPE oldValue = list.set(index, newValue);
    map.remove(oldValue);
    map.put(newValue, index);
    return oldValue;
  }

  @Override
  public Iterator<TYPE> iterator() {
    return new Iterator<TYPE>() {
      Iterator<TYPE> iterator = list.iterator();
      TYPE last = null;

      @Override
      public boolean hasNext() {
        return iterator.hasNext();
      }

      @Override
      public TYPE next() {
        if (!hasNext()) {
          throw new NoSuchElementException();
        }
        last = iterator.next();
        return last;
      }

      @Override
      public void remove() {
        if (last == null) {
          throw new IllegalStateException("Calling remove before next");
        }
        iterator.remove();
        map.remove(last);
      }
    };
  }

  @Override
  public int hashCode() {
    return list.hashCode();
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (o instanceof HashMapIndex) {
      return list.equals(Cast.<HashMapIndex>as(o).list);
    }
    return false;
  }

  @Override
  public String toString() {
    return list.toString();
  }


}//END OF AbstractMapListIndex
