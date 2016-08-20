package com.davidbracewell.stream.accumulator;

import com.davidbracewell.EnhancedDoubleStatistics;

/**
 * @author David B. Bracewell
 */
public class StatisticsAccumulatable implements Accumulatable<EnhancedDoubleStatistics> {
  private static final long serialVersionUID = 1L;

  @Override
  public EnhancedDoubleStatistics addAccumulator(EnhancedDoubleStatistics t1, EnhancedDoubleStatistics t2) {
    EnhancedDoubleStatistics eds = new EnhancedDoubleStatistics();
    eds.combine(t1);
    eds.combine(t2);
    return eds;
  }

  @Override
  public EnhancedDoubleStatistics addInPlace(EnhancedDoubleStatistics t1, EnhancedDoubleStatistics t2) {
    t1.combine(t2);
    return t1;
  }

  @Override
  public EnhancedDoubleStatistics zero(EnhancedDoubleStatistics zeroValue) {
    if( zeroValue == null ){
      return new EnhancedDoubleStatistics();
    }
    return zeroValue;
  }
}// END OF StatisticsAccumulatable
