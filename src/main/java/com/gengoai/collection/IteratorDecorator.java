package com.gengoai.collection;

import java.util.Iterator;

/**
 * The type Iterator decorator.
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public abstract class IteratorDecorator<E> implements Iterator<E> {

   /**
    * Backing iterator iterator.
    *
    * @return the iterator
    */
   protected abstract Iterator<E> backingIterator();

   private E lastValue;

   @Override
   public void remove() {
      backingIterator().remove();
   }

   @Override
   public boolean hasNext() {
      return backingIterator().hasNext();
   }

   @Override
   public E next() {
      lastValue = backingIterator().next();
      return lastValue;
   }

   /**
    * Gets last value.
    *
    * @return the last value
    */
   protected E getLastValue() {
      return lastValue;
   }

}//END OF IteratorDecorator
