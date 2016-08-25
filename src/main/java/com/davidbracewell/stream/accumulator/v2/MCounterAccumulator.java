package com.davidbracewell.stream.accumulator.v2;

import com.davidbracewell.collection.counter.Counter;

/**
 * @author David B. Bracewell
 */
public interface MCounterAccumulator<T> extends MAccumulator<T, Counter<T>> {
}// END OF MCounterAccumulator
