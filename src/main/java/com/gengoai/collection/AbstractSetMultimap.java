package com.gengoai.collection;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.*;

/**
 * @author David B. Bracewell
 */
public abstract class AbstractSetMultimap<K, V> implements Multimap<K, V>, Serializable {
   private final Map<K, Set<V>> map;

   @Override
   public String toString() {
      return map.toString();
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof AbstractSetMultimap)) return false;
      AbstractSetMultimap<?, ?> that = (AbstractSetMultimap<?, ?>) o;
      return Objects.equals(map, that.map);
   }

   @Override
   public int hashCode() {
      return Objects.hash(map);
   }

   public AbstractSetMultimap() {
      this.map = createMap();
   }

   protected Map<K, Set<V>> createMap() {
      return new HashMap<>();
   }

   protected abstract Set<V> createSet();

   @Override
   public Set<V> get(Object o) {
      map.putIfAbsent(Cast.as(o), createSet());
      return map.get(o);
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
   public Map<K, Collection<V>> asMap() {
      return Cast.cast(map);
   }


}//END OF AbstractSetMultimap
