package com.gengoai.collection.multimap;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.*;

/**
 * List backed multimaps
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public abstract class ListMultimap<K, V> implements Multimap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<K, List<V>> map;

   protected ListMultimap() {
      this.map = createMap();
   }

   /**
    * Creates a new map to store the entries.
    *
    * @return the map
    */
   protected Map<K, List<V>> createMap() {
      return new HashMap<>();
   }

   /**
    * Creates a new list for a key.
    *
    * @return the list
    */
   protected abstract List<V> createList();

   @Override
   public List<V> get(Object o) {
      map.putIfAbsent(Cast.as(o), createList());
      return map.get(o);
   }

   @Override
   public List<V> removeAll(Object key) {
      return map.remove(key);
   }

   @Override
   public void replace(K key, Iterable<? extends V> values) {
      List<V> list = get(key);
      list.clear();
      values.forEach(list::add);
   }

   @Override
   public Map<K, Collection<V>> asMap() {
      return Cast.as(map);
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ListMultimap)) return false;
      ListMultimap<?, ?> that = (ListMultimap<?, ?>) o;
      return Objects.equals(map, that.map);
   }

   @Override
   public int hashCode() {
      return Objects.hash(map);
   }

   @Override
   public String toString() {
      return map.toString();
   }
}//END OF ListMultimap
