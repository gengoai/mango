package com.davidbracewell.stream.accumulator.v2;

/**
 * @author David B. Bracewell
 */
public interface MLongAccumulator extends MAccumulator<Long, Long> {

  void add(long value);

  @Override
  default void add(Long aLong) {
    add(aLong == null ? 0 : aLong);
  }

}// END OF MLongAccumulator
