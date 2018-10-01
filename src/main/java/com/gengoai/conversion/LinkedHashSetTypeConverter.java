package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashSet;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * The type Linked hash set type converter.
 */
@MetaInfServices(value = TypeConverter.class)
public class LinkedHashSetTypeConverter extends CollectionTypeConverter {

   @Override
   public Class[] getConversionType() {
      return arrayOf(LinkedHashSet.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new HashSet<>();
   }
}
