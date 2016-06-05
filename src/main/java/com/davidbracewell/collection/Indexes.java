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


import lombok.NonNull;

import java.util.*;

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
   * New synchronized index.
   *
   * @param <TYPE> the type parameter
   * @return the index
   */
  public static <TYPE> Index<TYPE> synchronizedIndex(@NonNull Index<TYPE> index) {
    return new SynchronizedIndex<>(index);
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


}//END OF Indexes
