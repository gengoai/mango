package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Collection;
import java.util.Stack;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * The type Set type converter.
 */
@MetaInfServices(value = TypeConverter.class)
public class StackTypeConverter extends CollectionTypeConverter {

   @Override
   public Class[] getConversionType() {
      return arrayOf(Stack.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new Stack<>();
   }
}
