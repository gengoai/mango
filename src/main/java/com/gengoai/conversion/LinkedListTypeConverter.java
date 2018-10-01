package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Queue;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * The type Linked list type converter.
 */
@MetaInfServices(value = TypeConverter.class)
public class LinkedListTypeConverter extends CollectionTypeConverter {

   @Override
   public Class[] getConversionType() {
      return arrayOf(LinkedList.class, Deque.class, Queue.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new LinkedList<>();
   }
}
