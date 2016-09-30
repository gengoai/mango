package com.davidbracewell.stream.accumulator;

import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.util.Optional;

/**
 * The type Base local m accumulator.
 *
 * @param <IN>  the type parameter
 * @param <OUT> the type parameter
 * @author David B. Bracewell
 */
public class LocalMAccumulator<IN, OUT> implements MAccumulator<IN, OUT> {
   private static final long serialVersionUID = 1L;
   private final Accumulator<IN, OUT> accumulator;
   private final String name;

   /**
    * Instantiates a new Base local m accumulator.
    *
    * @param accumulator the accumulator
    * @param name        the name
    */
   public LocalMAccumulator(@NonNull Accumulator<IN, OUT> accumulator, String name) {
      this.accumulator = accumulator;
      this.name = name;
   }

   @Override
   public void add(IN in) {
      accumulator.add(in);
   }

   protected <T extends Accumulator<IN, OUT>> T getAccumulator() {
      return Cast.as(accumulator);
   }

   @Override
   public boolean isZero() {
      return accumulator.isZero();
   }

   @Override
   public void merge(@NonNull MAccumulator<IN, OUT> other) {
      if (other instanceof LocalMAccumulator) {
         accumulator.merge(Cast.<LocalMAccumulator<IN, OUT>>as(other).accumulator);
      }
      throw new IllegalArgumentException(getClass().getSimpleName() + " cannot merge with " + other.getClass()
                                                                                                   .getSimpleName());
   }

   @Override
   public Optional<String> name() {
      return Optional.ofNullable(name);
   }

   @Override
   public void reset() {
      accumulator.reset();
   }

   @Override
   public OUT value() {
      return accumulator.value();
   }

}// END OF LocalMAccumulator
