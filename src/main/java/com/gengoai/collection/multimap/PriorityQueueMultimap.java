package com.gengoai.collection.multimap;

import com.gengoai.collection.Sorting;
import com.gengoai.function.SerializableComparator;

import java.util.PriorityQueue;

/**
 * Multimap in which keys are mapped to value stored in a priority queue.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public class PriorityQueueMultimap<K, V> extends BaseMultimap<K, V, PriorityQueue<V>> {
   private static final long serialVersionUID = 1L;
   private final SerializableComparator<V> comparator;

   /**
    * Instantiates a new Priority queue multimap.
    */
   public PriorityQueueMultimap() {
      this(Sorting.natural());
   }

   /**
    * Instantiates a new Priority queue multimap.
    *
    * @param comparator the comparator to use for comparing values
    */
   public PriorityQueueMultimap(SerializableComparator<V> comparator) {
      this.comparator = comparator;
   }

   @Override
   protected PriorityQueue<V> createCollection() {
      return new PriorityQueue<>(comparator);
   }

}//END OF PriorityQueueMultimap
