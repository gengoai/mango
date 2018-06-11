package com.gengoai.cache;

import com.gengoai.Validation;
import com.gengoai.collection.LRUMap;
import com.gengoai.function.SerializableSupplier;
import lombok.NonNull;

import java.util.Collections;
import java.util.Map;

/**
 * LRU Cache
 *
 * @param <K> the key type parameter
 * @param <V> the value type parameter
 * @author David B. Bracewell
 */
public class LRUCache<K, V> implements Cache<K, V> {
   protected transient final Map<K, V> cache;

   /**
    * Instantiates a new Lru cache.
    *
    * @param maxSize the max size
    */
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
      return cache.computeIfAbsent(key, k -> supplier.get());
   }

   @Override
   public long size() {
      return cache.size();
   }
}//END OF LRUCache
