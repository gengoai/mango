package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.Collection;
import java.util.TreeSet;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * The type Tree set type converter.
 */
@MetaInfServices(value = TypeConverter.class)
public class TreeSetTypeConverter extends CollectionTypeConverter {

   @Override
   public Class[] getConversionType() {
      return arrayOf(TreeSet.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new TreeSet<>();
   }
}
