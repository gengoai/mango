package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * The type List type converter.
 */
@MetaInfServices(value = TypeConverter.class)
public class ListTypeConverter extends CollectionTypeConverter {

   @Override
   public Class[] getConversionType() {
      return arrayOf(List.class, ArrayList.class, Iterable.class);
   }

   @Override
   protected Collection<?> newCollection() {
      return new ArrayList<>();
   }
}