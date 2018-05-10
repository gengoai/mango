package com.gengoai.cache;

import com.gengoai.function.SerializableFunction;
import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public class AutoCalculatingLRUCache<K, V> extends LRUCache<K, V> {
   private final SerializableFunction<K, V> valueCalculator;

   public AutoCalculatingLRUCache(int maxSize, @NonNull SerializableFunction<K, V> valueCalculator) {
      super(maxSize);
      this.valueCalculator = valueCalculator;
   }

   @Override
   public V get(K key) {
      if (!containsKey(key)) {
         put(key, valueCalculator.apply(key));
      }
      return super.get(key);
   }
}//END OF AutoCalculatingLRUCache
