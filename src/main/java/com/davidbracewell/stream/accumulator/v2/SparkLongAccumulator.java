package com.davidbracewell.stream.accumulator.v2;

import org.apache.spark.util.AccumulatorV2;

/**
 * @author David B. Bracewell
 */
public class SparkLongAccumulator extends BaseSparkAccumulator<Long, Long> implements MLongAccumulator {
  private static final long serialVersionUID = 1L;

  public SparkLongAccumulator(AccumulatorV2<Long, Long> accumulatorV2) {
    super(accumulatorV2);
  }

  @Override
  public void add(long value) {
    accumulatorV2.add(value);
  }

}// END OF SparkMLongAccumulator
