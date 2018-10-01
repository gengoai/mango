package com.gengoai.conversion;

import java.util.Collection;
import java.util.concurrent.ConcurrentLinkedQueue;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
public class ConcurrentLinkedQueueTypeConverter extends CollectionTypeConverter {
   @Override
   public Class[] getConversionType() {
      return arrayOf(ConcurrentLinkedQueue.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new ConcurrentLinkedQueue<>();
   }
}//END OF ConcurrentLinkedQueueTypeConverter
