package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.LinkedHashSet;

/**
 * Multimap that stores values in a LinkedHashSet
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class LinkedHashSetMultimap<K, V> extends SetMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   public LinkedHashSetMultimap() {
      super(LinkedHashSet::new);
   }


}//END OF LinkedHashSetMultimap
