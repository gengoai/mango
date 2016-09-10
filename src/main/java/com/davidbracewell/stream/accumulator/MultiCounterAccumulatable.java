package com.davidbracewell.stream.accumulator;

import com.davidbracewell.collection.counter.HashMapMultiCounter;
import com.davidbracewell.collection.counter.MultiCounter;
import com.davidbracewell.collection.counter.MultiCounters;

/**
 * @author David B. Bracewell
 */
public class MultiCounterAccumulatable<K, V> implements Accumulatable<MultiCounter<K, V>> {
   private static final long serialVersionUID = 1L;

   @Override
   public MultiCounter<K, V> addAccumulator(MultiCounter<K, V> t1, MultiCounter<K, V> t2) {
      MultiCounter<K, V> mc = MultiCounters.newMultiCounter(t1);
      return mc.merge(t2);
   }

   @Override
   public MultiCounter<K, V> addInPlace(MultiCounter<K, V> t1, MultiCounter<K, V> t2) {
      return t1.merge(t2);
   }

   @Override
   public MultiCounter<K, V> zero(MultiCounter<K, V> zeroValue) {
      if (zeroValue == null) {
         return new HashMapMultiCounter<>();
      }
      return zeroValue;
   }

}// END OF MultiCounterAccumulatable
