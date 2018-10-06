package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class LongTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      return number.longValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Long.class, long.class);
   }
}//END OF FloatTypeConverter
