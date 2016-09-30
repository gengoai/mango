package com.davidbracewell.stream.accumulator;

import com.davidbracewell.collection.counter.Counter;
import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public class LocalMCounterAccumulator<IN> extends LocalMAccumulator<IN, Counter<IN>> implements MCounterAccumulator<IN> {
   private static final long serialVersionUID = 1L;

   /**
    * Instantiates a new Base local m accumulator.
    *
    * @param name the name
    */
   public LocalMCounterAccumulator(String name) {
      super(new CounterAccumulator<>(), name);
   }

   @Override
   public void increment(IN item, double amount) {
      this.<CounterAccumulator<IN>>getAccumulator().increment(item, amount);
   }

   @Override
   public void merge(@NonNull Counter<IN> counter) {
      this.<CounterAccumulator<IN>>getAccumulator().merge(counter);
   }

}// END OF LocalMCounterAccumulator
