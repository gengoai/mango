package com.gengoai.cache;

import com.gengoai.function.SerializableFunction;
import lombok.NonNull;

/**
 * Auto calculating LRU cache that calculates values for keys when retrieved.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class AutoCalculatingLRUCache<K, V> extends LRUCache<K, V> {
   private final SerializableFunction<K, V> valueCalculator;

   /**
    * Instantiates a new Auto calculating lru cache.
    *
    * @param maxSize         the max size
    * @param valueCalculator the value calculator
    */
   public AutoCalculatingLRUCache(int maxSize, @NonNull SerializableFunction<K, V> valueCalculator) {
      super(maxSize);
      this.valueCalculator = valueCalculator;
   }

   @Override
   public V get(K key) {
      return cache.computeIfAbsent(key, valueCalculator);
   }

}//END OF AutoCalculatingLRUCache
