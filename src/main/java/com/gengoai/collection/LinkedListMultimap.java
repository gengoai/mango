package com.gengoai.collection;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

/**
 * @author David B. Bracewell
 */
public class LinkedListMultimap<K, V> extends AbstractListMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   @Override
   protected List<V> createList() {
      return new LinkedList<>();
   }
}//END OF LinkedListMultimap
