package com.gengoai.conversion;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedDeque;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
public class ConcurrentLinkedDequeTypeConverter extends CollectionTypeConverter {
   @Override
   public Class[] getConversionType() {
      return arrayOf(ConcurrentLinkedDeque.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new ConcurrentLinkedDeque<>();
   }
}//END OF ConcurrentLinkedDequeTypeConverter
