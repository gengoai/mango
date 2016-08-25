package com.davidbracewell.stream.accumulator.v2;

import com.davidbracewell.conversion.Cast;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;
import scala.runtime.AbstractFunction0;

import java.util.Optional;

import static com.davidbracewell.Validations.validateArgument;

/**
 * @author David B. Bracewell
 */
public class BaseSparkAccumulator<IN, OUT> implements MAccumulator<IN, OUT> {
  private static final long serialVersionUID = 1L;
  protected final AccumulatorV2<IN, OUT> accumulatorV2;

  public BaseSparkAccumulator(AccumulatorV2<IN, OUT> accumulatorV2) {
    this.accumulatorV2 = accumulatorV2;
  }

  @Override
  public void add(IN in) {
    accumulatorV2.add(in);
  }

  @Override
  public OUT value() {
    return accumulatorV2.value();
  }

  @Override
  public void merge(@NonNull MAccumulator<IN, OUT> other) {
    validateArgument(other instanceof BaseSparkAccumulator);
    accumulatorV2.merge(Cast.as(other));
  }

  @Override
  public Optional<String> name() {
    return Optional.ofNullable(accumulatorV2.name().getOrElse(new AbstractFunction0<String>() {
      @Override
      public String apply() {
        return null;
      }
    }));
  }

  @Override
  public void reset() {
    accumulatorV2.reset();
  }

}// END OF BaseSparkAccumulator
