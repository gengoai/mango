package com.gengoai.cache;

import com.gengoai.Validation;
import com.gengoai.collection.map.LRUMap;
import com.gengoai.function.SerializableSupplier;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public class LRUCache<K, V> implements Cache<K, V> {
   private transient final Map<K, V> cache;

   public LRUCache(int maxSize) {
      Validation.checkArgument(maxSize > 0, "Cache must have size of greater than zero.");
      this.cache = Collections.synchronizedMap(new LRUMap<>(maxSize));
   }

   @Override
   public boolean containsKey(K key) {
      return cache.containsKey(key);
   }

   @Override
   public V get(K key) {
      return cache.get(key);
   }

   @Override
   public void invalidate(K key) {
      cache.remove(key);
   }

   @Override
   public void invalidateAll(@NonNull Iterable<? extends K> keys) {
      keys.forEach(cache::remove);
   }

   @Override
   public void invalidateAll() {
      cache.clear();
   }

   @Override
   public void put(K key, V value) {
      cache.put(key, value);
   }

   @Override
   public V get(K key, @NonNull SerializableSupplier<? extends V> supplier) {
      if (!containsKey(key)) {
         cache.put(key, supplier.get());
      }
      return cache.get(key);
   }

   @Override
   public long size() {
      return cache.size();
   }
}//END OF LRUCache
