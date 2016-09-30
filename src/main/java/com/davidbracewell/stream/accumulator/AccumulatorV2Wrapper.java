package com.davidbracewell.stream.accumulator;

import com.davidbracewell.conversion.Cast;
import lombok.NonNull;
import org.apache.spark.util.AccumulatorV2;

import java.io.Serializable;

/**
 * The type Accumulator v 2 wrapper.
 *
 * @param <IN>  the type parameter
 * @param <OUT> the type parameter
 * @author David B. Bracewell
 */
public class AccumulatorV2Wrapper<IN, OUT> extends AccumulatorV2<IN, OUT> implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Accumulator<IN, OUT> accumulator;

   /**
    * Instantiates a new Accumulator v 2 wrapper.
    *
    * @param accumulator the accumulator
    */
   public AccumulatorV2Wrapper(@NonNull Accumulator<IN, OUT> accumulator) {
      this.accumulator = accumulator;
   }

   @Override
   public void add(IN v) {
      accumulator.add(v);
   }

   @Override
   public AccumulatorV2<IN, OUT> copy() {
      return new AccumulatorV2Wrapper<>(accumulator.copy());
   }

   @Override
   public boolean isZero() {
      return accumulator.isZero();
   }

   @Override
   public void merge(@NonNull AccumulatorV2<IN, OUT> other) {
      if (other instanceof AccumulatorV2Wrapper) {
         accumulator.merge(Cast.<AccumulatorV2Wrapper<IN, OUT>>as(other).accumulator);
      }
      throw new IllegalArgumentException(getClass().getSimpleName() + " cannot merge with " + other.getClass()
                                                                                                   .getSimpleName());
   }

   @Override
   public void reset() {
      accumulator.reset();
   }

   @Override
   public OUT value() {
      return null;
   }
}// END OF AccumulatorV2Wrapper
