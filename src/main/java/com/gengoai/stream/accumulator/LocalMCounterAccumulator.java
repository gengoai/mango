package com.gengoai.stream.accumulator;

import com.gengoai.collection.counter.Counter;
import com.gengoai.collection.counter.Counters;

/**
 * <p>An implementation of a {@link MCounterAccumulator} for local streams</p>
 *
 * @param <IN> the component type parameter of the counter
 * @author David B. Bracewell
 */
public class LocalMCounterAccumulator<IN> extends LocalMAccumulator<IN, Counter<IN>> implements MCounterAccumulator<IN> {
   private static final long serialVersionUID = 1L;
   private final Counter<IN> counter = Counters.newConcurrentCounter();

   /**
    * Instantiates a new LocalMCounterAccumulator.
    *
    * @param name the name of the accumulator
    */
   public LocalMCounterAccumulator(String name) {
      super(name);
   }

   @Override
   public void add(IN in) {
      counter.increment(in);
   }

   @Override
   public void merge(MAccumulator<IN, Counter<IN>> other) {
      if (other instanceof LocalMAccumulator) {
         this.counter.merge(other.value());
      } else {
         throw new IllegalArgumentException(getClass().getName() + " cannot merge with " + other.getClass().getName());
      }
   }

   @Override
   public void reset() {
      counter.clear();
   }

   @Override
   public Counter<IN> value() {
      return counter;
   }

   @Override
   public boolean isZero() {
      return counter.isEmpty();
   }

   @Override
   public LocalMAccumulator<IN, Counter<IN>> copy() {
      LocalMCounterAccumulator<IN> copy = new LocalMCounterAccumulator<>(name().orElse(null));
      copy.counter.merge(counter);
      return copy;
   }

   @Override
   public void increment(IN item, double amount) {
      counter.increment(item, amount);
   }

   @Override
   public void merge(Counter<? extends IN> counter) {
      this.counter.merge(counter);
   }

}// END OF LocalMCounterAccumulator
