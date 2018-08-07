package com.gengoai.collection.multimap;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public abstract class SetMultimap<K, V> implements Multimap<K, V>, Serializable {
   private final Map<K, Set<V>> map;

   public SetMultimap() {
      this.map = createMap();
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return Cast.cast(map);
   }

   protected Map<K, Set<V>> createMap() {
      return new HashMap<>();
   }

   protected abstract Set<V> createSet();

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof SetMultimap)) return false;
      SetMultimap<?, ?> that = (SetMultimap<?, ?>) o;
      return Objects.equals(map, that.map);
   }

   @Override
   public Set<V> get(Object o) {
      map.putIfAbsent(Cast.as(o), createSet());
      return map.get(o);
   }

   @Override
   public int hashCode() {
      return Objects.hash(map);
   }

   @Override
   public Set<V> removeAll(Object key) {
      return map.remove(key);
   }

   @Override
   public void replace(K key, Iterable<? extends V> values) {
      Set<V> valueSet = get(key);
      valueSet.clear();
      values.forEach(valueSet::add);
   }

   @Override
   public String toString() {
      return map.toString();
   }



}//END OF SetMultimap
