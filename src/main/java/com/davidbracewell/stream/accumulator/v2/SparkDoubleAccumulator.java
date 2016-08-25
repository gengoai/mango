package com.davidbracewell.stream.accumulator.v2;

import org.apache.spark.util.DoubleAccumulator;

/**
 * @author David B. Bracewell
 */
public class SparkDoubleAccumulator extends BaseSparkAccumulator<Double, Double> implements MDoubleAccumulator {
  private static final long serialVersionUID = 1L;

  public SparkDoubleAccumulator(DoubleAccumulator accumulatorV2) {
    super(accumulatorV2);
  }

  @Override
  public void add(double value) {
    accumulatorV2.add(value);
  }

}// END OF SparkMDoubleAccumulator
