package com.davidbracewell.stream.accumulator.v2;

/**
 * @author David B. Bracewell
 */
public interface MDoubleAccumulator extends MAccumulator<Double, Double> {

  void add(double value);

  @Override
  default void add(Double aDouble) {
    add(aDouble == null ? 0 : aDouble);
  }

}// END OF MDoubleAccumulator
