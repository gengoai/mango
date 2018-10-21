package com.gengoai.collection.multimap;

import com.gengoai.collection.Sorting;
import com.gengoai.function.SerializableComparator;

import java.util.TreeSet;

/**
 * Multimap in which keys are mapped to values in a tree set.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class TreeSetMultimap<K, V> extends SetMultimap<K, V> {
   private static final long serialVersionUID = 1L;
   private final SerializableComparator<V> comparator;

   /**
    * Instantiates a new TreeSetMultimap.
    */
   public TreeSetMultimap() {
      this(Sorting.natural());
   }

   /**
    * Instantiates a new TreeSetMultimap
    *
    * @param comparator the comparator to use for comparing values
    */
   public TreeSetMultimap(SerializableComparator<V> comparator) {
      super(TreeSet::new);
      this.comparator = comparator;
   }


}//END OF TreeSetMultimap
