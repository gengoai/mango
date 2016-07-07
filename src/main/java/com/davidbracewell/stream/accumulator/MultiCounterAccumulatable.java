package com.davidbracewell.stream.accumulator;

import com.davidbracewell.collection.HashMapMultiCounter;
import com.davidbracewell.collection.MultiCounter;

/**
 * @author David B. Bracewell
 */
public class MultiCounterAccumulatable<K, V> implements Accumulatable<MultiCounter<K, V>> {
  @Override
  public MultiCounter<K, V> addAccumulator(MultiCounter<K, V> t1, MultiCounter<K, V> t2) {
    MultiCounter<K, V> mc = new HashMapMultiCounter<>(t1);
    mc.merge(t2);
    return mc;
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
