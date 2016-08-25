package com.davidbracewell.stream.accumulator.v2;

import org.apache.spark.util.CollectionAccumulator;

import java.util.List;

/**
 * @author David B. Bracewell
 */
public class SparkListAccumulator<T> extends BaseSparkAccumulator<T, List<T>> implements MListAccumulator<T> {
  private static final long serialVersionUID = 1L;

  public SparkListAccumulator(CollectionAccumulator<T> accumulatorV2) {
    super(accumulatorV2);
  }
}// END OF SparkMListAccumulator
