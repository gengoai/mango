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

package com.gengoai.collection.map;

import com.gengoai.tuple.Tuple2;

import java.io.Serializable;
import java.text.CollationKey;
import java.text.Collator;
import java.util.*;
import java.util.function.Supplier;

/**
 * A string map that uses a <code>Collator</code> to normalize strings. All Strings are lower-cased when put into the
 * map. Once an item has been put in the map the
 * original case can not be restored. Does not support null key values.
 *
 * @param <V> The value type
 * @author David B. Bracewell
 */
public class NormalizedStringMap<V> implements Map<String, V>, Serializable {

   private static final long serialVersionUID = 1930175668438751641L;
   private final Collator collator;
   private final Map<CollationKey, V> map;

   /**
    * Default Constructor uses the default locale for collation
    */
   public NormalizedStringMap() {
      this(Locale.getDefault());
   }

   /**
    * Locale initializing constructor. Uses <code>Collator.PRIMARY</code> as the strength
    *
    * @param locale The local to use for collation
    */
   public NormalizedStringMap(Locale locale) {
      this(locale, Collator.PRIMARY);
   }


   /**
    * Locale and strength initializing constructor
    *
    * @param locale   The local to use for collation
    * @param strength the collation strength(e.g. <code>Collator.PRIMARY</code> )
    */
   public NormalizedStringMap(Locale locale, int strength) {
      this(HashMap::new, locale, strength);
   }

   /**
    * Instantiates a new Normalized string map.
    *
    * @param supplier the supplier
    * @param locale   the locale
    * @param strength the strength
    */
   public NormalizedStringMap(Supplier<Map<CollationKey, V>> supplier, Locale locale, int strength) {
      this.map = supplier.get();
      this.collator = locale == null ? Collator.getInstance() : Collator.getInstance(locale);
      this.collator.setStrength(strength);
      this.collator.setDecomposition(Collator.CANONICAL_DECOMPOSITION);
   }

   protected boolean canEqual(Object other) {
      return other instanceof NormalizedStringMap;
   }

   @Override
   public void clear() {
      map.clear();
   }

   private CollationKey collate(Object o) {
      if (o == null || !(o instanceof String)) {
         return null;
      }
      return collator.getCollationKey(o.toString());
   }

   @Override
   public boolean containsKey(Object key) {
      return map.containsKey(collate(key));
   }

   @Override
   public boolean containsValue(Object value) {
      return map.containsValue(value);
   }

   @Override
   public Set<Entry<String, V>> entrySet() {
      return new AbstractSet<Entry<String, V>>() {
         @Override
         public Iterator<Entry<String, V>> iterator() {
            return map.entrySet().stream()
                      .map(
                         entry -> (Map.Entry<String, V>) Tuple2.of(entry.getKey().getSourceString(), entry.getValue()))
                      .iterator();
         }

         @Override
         public int size() {
            return map.size();
         }
      };
   }

   public boolean equals(Object o) {
      if (o == this) return true;
      if (!(o instanceof NormalizedStringMap)) return false;
      final NormalizedStringMap other = (NormalizedStringMap) o;
      if (!other.canEqual((Object) this)) return false;
      final Object this$collator = this.collator;
      final Object other$collator = other.collator;
      if (this$collator == null ? other$collator != null : !this$collator.equals(other$collator)) return false;
      final Object this$map = this.map;
      final Object other$map = other.map;
      if (this$map == null ? other$map != null : !this$map.equals(other$map)) return false;
      return true;
   }

   @Override
   public V get(Object arg0) {
      return map.get(collate(arg0));
   }

   public int hashCode() {
      final int PRIME = 59;
      int result = 1;
      final Object $collator = this.collator;
      result = result * PRIME + ($collator == null ? 43 : $collator.hashCode());
      final Object $map = this.map;
      result = result * PRIME + ($map == null ? 43 : $map.hashCode());
      return result;
   }

   @Override
   public boolean isEmpty() {
      return map.isEmpty();
   }

   @Override
   public Set<String> keySet() {
      return new AbstractSet<String>() {
         @Override
         public Iterator<String> iterator() {
            return map.keySet().stream().map(CollationKey::getSourceString).iterator();
         }

         @Override
         public int size() {
            return map.size();
         }
      };
   }

   @Override
   public V put(String arg0, V arg1) {
      return map.put(collator.getCollationKey(arg0), arg1);
   }

   @Override
   public void putAll(Map<? extends String, ? extends V> m) {
      m.entrySet().forEach(entry -> put(entry.getKey(), entry.getValue()));
   }

   @Override
   public V remove(Object key) {
      return map.remove(collate(key));
   }

   @Override
   public int size() {
      return map.size();
   }

   @Override
   public String toString() {
      return map.toString();
   }

   @Override
   public Collection<V> values() {
      return map.values();
   }

}// END OF NormalizedStringMap
