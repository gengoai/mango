package com.gengoai.collection;

import java.io.Serializable;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * @author David B. Bracewell
 */
public class LinkedHashSetMultimap<K, V> extends AbstractSetMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   protected Set<V> createSet() {
      return new LinkedHashSet<>();
   }
}//END OF LinkedHashSetMultimap
