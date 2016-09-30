package com.davidbracewell.stream.accumulator;

import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.collection.counter.Counters;
import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public class CounterAccumulator<E> implements Accumulator<E, Counter<E>> {
   private static final long serialVersionUID = 1L;
   private final Counter<E> counter = Counters.synchronizedCounter();

   @Override
   public void add(E item) {
      counter.increment(item);
   }

   @Override
   public Accumulator<E, Counter<E>> copy() {
      CounterAccumulator<E> copy = new CounterAccumulator<>();
      copy.counter.merge(counter);
      return copy;
   }

   public void increment(E item, double amount) {
      counter.increment(item, amount);
   }

   @Override
   public boolean isZero() {
      return copy().isZero();
   }

   @Override
   public void merge(@NonNull Accumulator<E, Counter<E>> accumulator) {
      counter.merge(accumulator.value());
   }

   public void merge(@NonNull Counter<E> other) {
      counter.merge(other);
   }

   @Override
   public void reset() {
      counter.clear();
   }

   @Override
   public Counter<E> value() {
      return counter;
   }

}// END OF CounterAccumulator
