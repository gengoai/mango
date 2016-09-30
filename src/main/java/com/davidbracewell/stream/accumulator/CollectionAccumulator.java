package com.davidbracewell.stream.accumulator;

import lombok.NonNull;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author David B. Bracewell
 */
public class CollectionAccumulator<E> implements Accumulator<E, List<E>> {
   private static final long serialVersionUID = 1L;

   private final CopyOnWriteArrayList<E> list = new CopyOnWriteArrayList<>();

   @Override
   public void add(E item) {
      list.add(item);
   }

   @Override
   public Accumulator<E, List<E>> copy() {
      CollectionAccumulator<E> copy = new CollectionAccumulator<>();
      copy.list.addAll(list);
      return copy;
   }

   @Override
   public boolean isZero() {
      return list.isEmpty();
   }

   @Override
   public void merge(@NonNull Accumulator<E, List<E>> accumulator) {
      list.addAll(accumulator.value());
   }

   @Override
   public void reset() {
      list.clear();
   }

   @Override
   public List<E> value() {
      return list;
   }
}// END OF CollectionAccumulator
