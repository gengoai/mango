package com.gengoai.collection;

import java.util.AbstractSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * The type Iterator set.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public class IteratorSet<E> extends AbstractSet<E> {

   private final Supplier<Iterator<E>> iteratorSupplier;

   /**
    * Instantiates a new Iterator set.
    *
    * @param iteratorSupplier the iterator supplier
    */
   public IteratorSet(Supplier<Iterator<E>> iteratorSupplier) {
      this.iteratorSupplier = iteratorSupplier;
   }

   public Iterator<E> iterator() {
      return new RemovableIterator<>(Streams.asStream(iteratorSupplier.get()).distinct().iterator());
   }

   private class RemovableIterator<E> extends IteratorDecorator<E> {
      private final Iterator<E> backingIterator;

      private RemovableIterator(Iterator<E> backingIterator) {
         this.backingIterator = backingIterator;
      }

      @Override
      protected Iterator<E> backingIterator() {
         return backingIterator;
      }

      @Override
      public void remove() {
         IteratorSet.this.remove(getLastValue());
      }
   }


   @Override
   public boolean removeAll(Collection<?> c) {
      return removeIf(c::contains);
   }

   @Override
   public boolean removeIf(Predicate<? super E> filter) {
      boolean removed = false;
      for (Iterator<E> itr = iteratorSupplier.get(); itr.hasNext(); ) {
         E next = itr.next();
         if (filter.test(next)) {
            itr.remove();
            removed = true;
         }
      }
      return removed;
   }

   @Override
   public boolean remove(Object o) {
      boolean removed = false;
      for (Iterator<E> itr = iteratorSupplier.get(); itr.hasNext(); ) {
         E next = itr.next();
         if (next.equals(o)) {
            itr.remove();
            removed = true;
         }
      }
      return removed;
   }

   @Override
   public String toString() {
      StringBuilder builder = new StringBuilder("[");
      for (E e : this) {
         builder.append(e).append(", ");
      }
      if (builder.length() > 1) {
         builder.setLength(builder.length() - 2);
      }
      builder.append("]");
      return builder.toString();
   }

   public int size() {
      return (int) Streams.asStream(iterator()).count();
   }

   @Override
   public void clear() {
      for (Iterator<E> itr = iteratorSupplier.get(); itr.hasNext(); ) {
         itr.next();
         itr.remove();
      }
   }
}//END OF IteratorSet
