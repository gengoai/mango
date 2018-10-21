package com.gengoai.collection.multimap;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Multimap that stores values in an ArrayList
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class ArrayListMultimap<K, V> extends ListMultimap<K, V> implements Serializable {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new Array list multimap.
    */
   public ArrayListMultimap() {
      super(ArrayList::new);
   }

}//END OF ArrayListMultimap
