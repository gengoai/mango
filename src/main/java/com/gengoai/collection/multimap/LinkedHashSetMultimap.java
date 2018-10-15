package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Multimap that stores values in a LinkedHashSet
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class LinkedHashSetMultimap<K, V> extends SetMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   protected Set<V> createCollection() {
      return new LinkedHashSet<>();
   }

}//END OF LinkedHashSetMultimap
