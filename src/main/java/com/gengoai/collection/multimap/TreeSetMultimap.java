package com.gengoai.collection.multimap;

import java.util.Set;
import java.util.TreeSet;

/**
 * @author David B. Bracewell
 */
public class TreeSetMultimap<K, V> extends SetMultimap<K, V> {
   private static final long serialVersionUID = 1L;

   @Override
   protected Set<V> createSet() {
      return new TreeSet<>();
   }
}//END OF TreeSetMultimap
