package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * The type Linked list multimap.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class LinkedListMultimap<K, V> extends ListMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   protected List<V> createCollection() {
      return new LinkedList<>();
   }

}//END OF LinkedListMultimap
