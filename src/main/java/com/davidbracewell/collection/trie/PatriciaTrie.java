/*
 * Take from Apache commons with modifications
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

import com.davidbracewell.collection.trie.analyzer.StringKeyAnalyzer;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.CharMatcher;
import com.google.common.base.Function;
import com.google.common.base.Functions;
import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Implementation of a PATRICIA Trie (Practical Algorithm to Retrieve Information
 * Coded in Alphanumeric).
 * <p>
 * A PATRICIA {@link Trie} is a compressed {@link Trie}. Instead of storing
 * all data at the edges of the {@link Trie} (and having empty internal nodes),
 * PATRICIA stores data in every node. This allows for very efficient traversal,
 * insert, delete, predecessor, successor, prefix, range, and {@link #select(Object)}
 * operations. All operations are performed at worst in O(K) time, where K
 * is the number of bits in the largest item in the tree. In practice,
 * operations actually take O(A(K)) time, where A(K) is the average number of
 * bits of all items in the tree.
 * <p>
 * Most importantly, PATRICIA requires very few comparisons to keys while
 * doing any operation. While performing a lookup, each comparison (at most
 * K of them, described above) will perform a single bit comparison against
 * the given key, instead of comparing the entire key to another key.
 * <p>
 * The {@link Trie} can return operations in lexicographical order using the
 * 'prefixMap', 'submap', or 'iterator' methods. The {@link Trie} can also
 * scan for items that are 'bitwise' (using an XOR metric) by the 'select' method.
 * Bitwise closeness is determined by the {@link KeyAnalyzer} returning true or
 * false for a bit being set or not in a given key.
 * <p>
 * This PATRICIA {@link Trie} supports both variable length & fixed length
 * keys. Some methods, such as {@link #prefixMap(Object)} are suited only
 * to variable length keys.
 *
 * @version $Id: PatriciaTrie.java 1543928 2013-11-20 20:15:35Z tn $
 * @see <a href="http://en.wikipedia.org/wiki/Radix_tree">Radix Tree</a>
 * @see <a href="http://www.csse.monash.edu.au/~lloyd/tildeAlgDS/Tree/PATRICIA">PATRICIA</a>
 * @see <a href="http://www.imperialviolet.org/binary/critbit.pdf">Crit-Bit Tree</a>
 * @since 4.0
 */
public class PatriciaTrie<E> extends AbstractPatriciaTrie<String, E> {

  private static final long serialVersionUID = 4446367780901817838L;

  public PatriciaTrie() {
    super(new StringKeyAnalyzer());
  }

  public PatriciaTrie(final Map<? extends String, ? extends E> m) {
    super(new StringKeyAnalyzer(), m);
  }

  protected PatriciaTrie(final Map<? extends String, ? extends E> m, boolean suffix) {
    super(new StringKeyAnalyzer(), m);
  }


  /**----------------------------------------------------------

   Illuminating knowledge contribution

   ----------------------------------------------------------**/


  /**
   * <p>Constructs a trie from a csv file where the first column is the string and the second column is the value.</p>
   *
   * @param resource  The csv resource
   * @param valueType Class information for the value
   * @return A <code>ByteTrie</code> from the csv
   * @throws java.io.IOException Something went wrong loading the csv file
   */
  public static <V> PatriciaTrie<V> loadCSV(Resource resource, Class<V> valueType) throws IOException {
    return loadCSV(resource, valueType, Functions.<String>identity());
  }

  /**
   * <p>Constructs a trie from a csv file where the first column is the string and the second column is the value.</p>
   *
   * @param resource     The csv resource
   * @param valueType    Class information for the value
   * @param keyTransform function to transform the keys in some fashion, e.g. lower case
   * @return A <code>ByteTrie</code> from the csv
   * @throws java.io.IOException Something went wrong loading the csv file
   */
  @SuppressWarnings("unchecked")
  public static <V> PatriciaTrie<V> loadCSV(Resource resource, Class<V> valueType, Function<String, String> keyTransform) throws IOException {
    Preconditions.checkNotNull(resource, "Resource cannot be null");
    Preconditions.checkNotNull(valueType, "valueType cannot be null");
    Preconditions.checkNotNull(keyTransform, "keyTransform cannot be null");
    PatriciaTrie<V> trie = new PatriciaTrie<>();
    try (CSVReader csv = CSV.builder().reader(resource)) {
      List<String> row;
      while ((row = csv.nextRow()) != null) {
        if (row.size() >= 2) {
          String key = keyTransform.apply(row.get(0));
          V value = Convert.convert(row.get(1), valueType);
          trie.put(key, value);
        } else if (row.size() == 1 && valueType == String.class) {
          String key = keyTransform.apply(row.get(0));
          trie.put(key, (V) key);
        }
      }
    }
    return trie;
  }

  /**
   * Finds all occurrences of keys in the ByteTrie in the input text. Uses a default character matcher that
   * matches anything t hat is not alphanumeric. Does not allow prefix matches.
   *
   * @param text The text to search in
   * @return A list of Tuple3s indicating <code>[start, end)</code> and the value associated with the match.
   */
  public List<TrieMatch<E>> findOccurrencesIn(String text) {
    return findOccurrencesIn(text, false, StringUtils.NOT_LETTER_OR_DIGIT);
  }

  /**
   * Finds all occurrences of keys in the ByteTrie in the input text. Uses a default character matcher that
   * matches anything t hat is not alphanumeric.
   *
   * @param text        The text to search in
   * @param prefixMatch True if allow prefix matches
   * @return A list of Tuple3s indicating <code>[start, end)</code> and the value associated with the match.
   */
  public List<TrieMatch<E>> findOccurrencesIn(String text, boolean prefixMatch) {
    return findOccurrencesIn(text, prefixMatch, StringUtils.NOT_LETTER_OR_DIGIT);
  }

  /**
   * Finds all occurrences of keys in the ByteTrie in the input text.
   *
   * @param text        The text to search in
   * @param prefixMatch True if allow prefix matches
   * @param matcher     The character matcher to use to mark end of word
   * @return A list of Tuple3s indicating <code>[start, end)</code> and the value associated with the match.
   */
  public List<TrieMatch<E>> findOccurrencesIn(String text, boolean prefixMatch, CharMatcher matcher) {
    List<TrieMatch<E>> rval = Lists.newArrayList();

    int len = text.length();
    StringBuilder key = new StringBuilder();
    int start = 0;
    int lastMatch = -1;

    for (int i = 0; i < len; i++) {
      key.append(text.charAt(i));

      //We have a key match
      if (containsKey(key.toString())) {
        int nextI = i + 1;
        lastMatch = i + 1;

        //There is something longer!
        if (nextI < len && !prefixMap(key.toString() + text.charAt(i + 1)).isEmpty()) {
          continue;
        }

        lastMatch = -1;

        //check if we accept
        if (nextI >= len || prefixMatch || matcher.matches(text.charAt(nextI))) {
          E value = get(key.toString());
          if (prefixMatch) {
            while ((i + 1) < text.length() && !matcher.matches(text.charAt(i + 1))) {
              i++;
              key.append(text.charAt(i));
            }
          }
          rval.add(new TrieMatch<>(start, i + 1, value));
          start = i + 1;
          continue;
        }

      }

      if (prefixMap(key.toString()).isEmpty()) {

        if (lastMatch != -1) {
          int nextI = lastMatch;
          if (nextI >= len || prefixMatch || matcher.matches(text.charAt(nextI))) {
            key = new StringBuilder(text.substring(start, nextI));
            E value = get(key.toString());
            rval.add(new TrieMatch<>(start, nextI, value));
            i = lastMatch;
            lastMatch = -1;
          }

        }

        start = i;
        if (key.length() > 1) {
          key.setLength(1);
          key.setCharAt(0, text.charAt(i));
        } else {
          key.setLength(0);
        }

      }


    }
    return rval;
  }

}
