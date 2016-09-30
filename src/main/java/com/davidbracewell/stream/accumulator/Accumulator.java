package com.davidbracewell.stream.accumulator;

import com.davidbracewell.Copyable;

import java.io.Serializable;

/**
 * <p>Interface for local accumulators</p>
 *
 * @param <IN>  the type parameter of what is being accumulated
 * @param <OUT> the type parameter of the result of the accumulation
 * @author David B. Bracewell
 */
public interface Accumulator<IN, OUT> extends Serializable, Copyable<Accumulator<IN, OUT>> {

   /**
    * Adds an item to the accumulator
    *
    * @param item the item to add
    */
   void add(IN item);

   /**
    * Determines if the accumulator is a zero value
    *
    * @return True if the accumulator is in a zero state
    */
   boolean isZero();

   /**
    * Merges another accumulator with this one
    *
    * @param accumulator the other accumulator to merge
    * @throws NullPointerException if the other accumulator is null
    */
   void merge(Accumulator<IN, OUT> accumulator);

   /**
    * Resets the accumulator to its zero-value.
    */
   void reset();

   /**
    * The value of the accumulator.
    *
    * @return the result of the accumulator
    */
   OUT value();


}// END OF Accumulator
