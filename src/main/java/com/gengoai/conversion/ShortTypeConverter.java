package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ShortTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      return number.shortValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Short.class, short.class);
   }
}//END OF ShortTypeConverter