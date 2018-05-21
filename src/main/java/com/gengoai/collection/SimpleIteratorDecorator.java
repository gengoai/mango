package com.gengoai.collection;

import com.gengoai.conversion.Cast;

import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
public class SimpleIteratorDecorator<E> extends IteratorDecorator<E> {
   private final Iterator<? extends E> iterator;

   public SimpleIteratorDecorator(Iterator<? extends E> iterator) {
      this.iterator = iterator;
   }

   @Override
   protected Iterator<E> backingIterator() {
      return Cast.as(iterator);
   }

}//END OF SimpleIteratorDecorator
