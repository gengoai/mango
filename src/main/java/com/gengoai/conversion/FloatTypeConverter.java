package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class FloatTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      return number.floatValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Float.class, float.class);
   }
}//END OF FloatTypeConverter
