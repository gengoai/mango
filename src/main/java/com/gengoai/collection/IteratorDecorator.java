package com.gengoai.collection;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public abstract class IteratorDecorator<E> implements Iterator<E> {

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

   protected E getLastValue() {
      return lastValue;
   }

}//END OF IteratorDecorator
