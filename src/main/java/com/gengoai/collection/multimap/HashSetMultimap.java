package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.HashSet;

/**
 * Multimap that stores values in a HashSet.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class HashSetMultimap<K, V> extends SetMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   public HashSetMultimap() {
      super(HashSet::new);
   }


}//END OF HashSetMultimap
