/*
 * Take from Apache commons
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
package com.davidbracewell.collection.trie;

import java.util.SortedMap;

/**
 * Defines the interface for a prefix tree, an ordered tree data structure. For
 * more information, see <a href="http://en.wikipedia.org/wiki/Trie">Tries</a>.
 *
 * @version $Id: Trie.java 1543279 2013-11-19 00:54:31Z ggregory $
 * @since 4.0
 */
public interface Trie<K, V> extends SortedMap<K, V> {

  /**
   * Returns a view of this {@link Trie} of all elements that are prefixed
   * by the given key.
   * <p>
   * In a {@link Trie} with fixed size keys, this is essentially a
   * {@link #get(Object)} operation.
   * <p>
   * For example, if the {@link Trie} contains 'Anna', 'Anael',
   * 'Analu', 'Andreas', 'Andrea', 'Andres', and 'Anatole', then
   * a lookup of 'And' would return 'Andreas', 'Andrea', and 'Andres'.
   *
   * @param key the key used in the search
   * @return a {@link java.util.SortedMap} view of this {@link Trie} with all
   * elements whose
   * key is prefixed by the search key
   */
  SortedMap<K, V> prefixMap(K key);


  /**
   * Obtains an <code>OrderedMapIterator</code> over the map.
   * <p>
   * A ordered map iterator is an efficient way of iterating over maps
   * in both directions.
   *
   * @return a map iterator
   */
  OrderedMapIterator<K, V> mapIterator();

  /**
   * Gets the first key currently in this map.
   *
   * @return the first key currently in this map
   * @throws java.util.NoSuchElementException if this map is empty
   */
  K firstKey();

  /**
   * Gets the last key currently in this map.
   *
   * @return the last key currently in this map
   * @throws java.util.NoSuchElementException if this map is empty
   */
  K lastKey();

  /**
   * Gets the next key after the one specified.
   *
   * @param key the key to search for next from
   * @return the next key, null if no match or at end
   */
  K nextKey(K key);

  /**
   * Gets the previous key before the one specified.
   *
   * @param key the key to search for previous from
   * @return the previous key, null if no match or at start
   */
  K previousKey(K key);

}
