package com.gengoai.collection;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public class SimpleIteratorDecorator<E> extends IteratorDecorator<E> {
   private final Iterator<E> iterator;

   public SimpleIteratorDecorator(Iterator<E> iterator) {
      this.iterator = iterator;
   }

   @Override
   protected Iterator<E> backingIterator() {
      return iterator;
   }

}//END OF SimpleIteratorDecorator
