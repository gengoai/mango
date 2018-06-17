package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public class HashSetMultimap<K, V> extends SetMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   protected Set<V> createSet() {
      return new HashSet<>();
   }
}//END OF HashSetMultimap
