package com.gengoai.collection;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * List backed multimaps
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public abstract class AbstractListMultimap<K, V> implements Multimap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<K, List<V>> map;

   protected AbstractListMultimap() {
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
}//END OF AbstractListMultimap
