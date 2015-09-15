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


import com.google.common.collect.Iterators;
import lombok.NonNull;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The type Indexes.
 *
 * @author David B. Bracewell
 */
public final class Indexes {

  private Indexes() {
    throw new IllegalAccessError();
  }

  /**
   * New concurrent index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  @SafeVarargs
  public static <TYPE> Index<TYPE> newConcurrentIndex(TYPE... items) {
    Index<TYPE> index = new ConcurrentMapIndex<>();
    if (items != null) {
      index.addAll(Arrays.asList(items));
    }
    return index;
  }

  /**
   * New concurrent index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  public static <TYPE> Index<TYPE> newConcurrentIndex(@NonNull Iterable<TYPE> items) {
    Index<TYPE> index = new ConcurrentMapIndex<>();
    index.addAll(items);
    return index;
  }

  /**
   * New index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  @SafeVarargs
  public static <TYPE> Index<TYPE> newIndex(TYPE... items) {
    Index<TYPE> index = new HashMapIndex<>();
    if (items != null) {
      index.addAll(Arrays.asList(items));
    }
    return index;
  }

  /**
   * New index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  public static <TYPE> Index<TYPE> newIndex(@NonNull Iterable<TYPE> items) {
    Index<TYPE> index = new HashMapIndex<>();
    index.addAll(items);
    return index;
  }

  /**
   * Unmodifiable index.
   *
   * @param <TYPE> the type parameter
   * @param index  the index
   * @return the index
   */
  public static <TYPE> Index<TYPE> unmodifiableIndex(@NonNull final Index<TYPE> index) {
    return new UnmodifiableIndex<>(index);
  }

  private static abstract class MapIndex<TYPE> extends AbstractMapListIndex<TYPE> {
    private static final long serialVersionUID = 7244518534324307338L;
    private final List<TYPE> list;
    private final Map<TYPE, Integer> map;

    /**
     * Instantiates a new Map index.
     *
     * @param map  the map
     * @param list the list
     */
    protected MapIndex(Map<TYPE, Integer> map, List<TYPE> list) {
      this.map = map;
      this.list = list;
    }

    @Override
    protected List<TYPE> list() {
      return list;
    }

    @Override
    protected Map<TYPE, Integer> map() {
      return map;
    }


  }

  private static class HashMapIndex<TYPE> extends MapIndex<TYPE> {

    private static final long serialVersionUID = -323657672209561540L;

    private HashMapIndex() {
      super(new HashMap<>(), new ArrayList<>());
    }

  }

  private static class ConcurrentMapIndex<TYPE> extends MapIndex<TYPE> {

    private static final long serialVersionUID = -323657672209561540L;

    private ConcurrentMapIndex() {
      super(new ConcurrentHashMap<>(), new CopyOnWriteArrayList<>());
    }

  }

  private static class UnmodifiableIndex<TYPE> extends ForwardingIndex<TYPE> {

    private static final long serialVersionUID = -3044104679509123935L;
    private final Index<TYPE> backing;

    private UnmodifiableIndex(Index<TYPE> backing) {
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

    @Override
    public TYPE set(int index, TYPE newValue) {
      throw new UnsupportedOperationException("Cannot modify a read only index.");
    }
  }


}//END OF Indexes
