package com.gengoai.collection;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public abstract class IteratorDecorator<E> implements Iterator<E> {

   protected abstract Iterator<E> backingIterator();

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
      return backingIterator().next();
   }
}//END OF IteratorDecorator
