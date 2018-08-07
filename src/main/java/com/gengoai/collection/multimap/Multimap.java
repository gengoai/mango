package com.gengoai.collection.multimap;

import com.gengoai.Validation;
import com.gengoai.collection.Collect;
import com.gengoai.collection.Iterables;

import java.util.*;

import static com.gengoai.tuple.Tuples.$;

/**
 * The interface Multimap.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public interface Multimap<K, V> {

   /**
    * Contains boolean.
    *
    * @param key   the key
    * @param value the value
    * @return the boolean
    */
   default boolean contains(Object key, Object value) {
      return asMap().containsKey(key) && asMap().get(key).contains(value);
   }

   /**
    * Entry set set.
    *
    * @return the set
    */
   default Set<Map.Entry<K, V>> entries() {
      return new AbstractSet<Map.Entry<K, V>>() {
         @Override
         public Iterator<Map.Entry<K, V>> iterator() {
            return new EntrySetIterator<>(asMap());
         }

         @Override
         public int size() {
            return Multimap.this.size();
         }
      };
   }

   /**
    * Get v.
    *
    * @param o the o
    * @return the v
    */
   Collection<V> get(Object o);

   /**
    * Is empty boolean.
    *
    * @return the boolean
    */
   default boolean isEmpty() {
      return asMap().isEmpty();
   }

   /**
    * Key set set.
    *
    * @return the set
    */
   default Set<K> keySet() {
      return asMap().keySet();
   }

   /**
    * Put boolean.
    *
    * @param key   the key
    * @param value the value
    * @return the boolean
    */
   default boolean put(K key, V value) {
      return get(key).add(value);
   }

   /**
    * Put all boolean.
    *
    * @param map the map
    */
   default void putAll(Map<? extends K, ? extends Collection<? extends V>> map) {
      map.forEach(this::putAll);
   }

   /**
    * Put all boolean.
    *
    * @param multimap the multimap
    */
   default void putAll(Multimap<? extends K, ? extends V> multimap) {
      putAll(multimap.asMap());
   }

   /**
    * Put all boolean.
    *
    * @param key    the key
    * @param values the values
    */
   default void putAll(K key, Iterable<? extends V> values) {
      values.forEach(v -> put(key, v));
   }

   /**
    * Remove boolean.
    *
    * @param key   the key
    * @param value the value
    * @return the boolean
    */
   default boolean remove(Object key, Object value) {
      return asMap().containsKey(key) && asMap().get(key).remove(value);
   }

   /**
    * Remove all collection.
    *
    * @param key the key
    * @return the collection
    */
   Collection<V> removeAll(Object key);

   /**
    * Replace boolean.
    *
    * @param key    the key
    * @param values the values
    * @return the boolean
    */
   void replace(K key, Iterable<? extends V> values);

   /**
    * Size int.
    *
    * @return the int
    */
   default int size() {
      return asMap().values().stream().mapToInt(Collection::size).sum();
   }

   /**
    * Values collection.
    *
    * @return the collection
    */
   default Collection<V> values() {
      return Collect.asCollection(Iterables.flatten(asMap().values()));
   }

   /**
    * Clear.
    */
   default void clear() {
      asMap().clear();
   }

   /**
    * As map map.
    *
    * @return the map
    */
   Map<K, Collection<V>> asMap();

   /**
    * Contains key boolean.
    *
    * @param key the key
    * @return the boolean
    */
   default boolean containsKey(Object key) {
      return asMap().containsKey(key);
   }

   /**
    * Contains value boolean.
    *
    * @param value the value
    * @return the boolean
    */
   default boolean containsValue(Object value) {
      return values().contains(value);
   }

   /**
    * Trim.
    */
   default void trim() {
      asMap().keySet().removeIf(key -> get(key).isEmpty());
   }

   /**
    * The type Entry set iterator.
    *
    * @param <K> the type parameter
    * @param <V> the type parameter
    */
   class EntrySetIterator<K, V> implements Iterator<Map.Entry<K, V>> {
      private final Map<K, Collection<V>> map;
      private Iterator<K> keyIterator = null;
      private Iterator<V> currentCollectionIter = null;
      private K currentKey = null;
      private V currentValue = null;

      /**
       * Instantiates a new Entry set iterator.
       *
       * @param map the map
       */
      public EntrySetIterator(Map<K, Collection<V>> map) {
         this.map = map;
         this.keyIterator = map.keySet().iterator();
      }


      private boolean advance() {
         while (currentCollectionIter == null || !currentCollectionIter.hasNext()) {
            if (keyIterator.hasNext()) {
               currentKey = keyIterator.next();
               currentCollectionIter = map.get(currentKey).iterator();
            } else {
               return false;
            }
         }
         return true;
      }

      @Override
      public void remove() {
         map.getOrDefault(currentKey, Collections.emptyList()).remove(currentValue);
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public Map.Entry<K, V> next() {
         Validation.checkState(advance(), "No such element");
         currentValue = currentCollectionIter.next();
         return $(currentKey, currentValue);
      }
   }


}//END OF Multimap
