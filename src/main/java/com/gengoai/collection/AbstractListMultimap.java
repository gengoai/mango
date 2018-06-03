package com.gengoai.collection;

import com.gengoai.conversion.Cast;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public abstract class AbstractListMultimap<K, V> implements Multimap<K, V>, Serializable {
   private static final long serialVersionUID = 1L;
   private final Map<K, List<V>> map;

   public AbstractListMultimap() {
      this.map = createMap();
   }

   protected Map<K, List<V>> createMap() {
      return new HashMap<>();
   }

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
