package com.gengoai.collection.multimap;

import com.gengoai.conversion.Cast;
import com.gengoai.function.SerializableSupplier;

import java.util.List;

/**
 * Multimap which stores values in a list
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public abstract class ListMultimap<K, V> extends BaseMultimap<K, V, List<V>> {
   private static final long serialVersionUID = 1L;
   private final SerializableSupplier<List<V>> listSupplier;

   protected ListMultimap(SerializableSupplier<List<V>> listSupplier) {
      this.listSupplier = listSupplier;
   }

   @Override
   public List<V> get(Object key) {
      return new ForwardingList<>(Cast.as(key), map, listSupplier);
   }

}//END OF ListMultimap
