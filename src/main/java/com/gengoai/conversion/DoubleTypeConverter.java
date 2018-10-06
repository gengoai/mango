package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class DoubleTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      return number.doubleValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Number.class, Double.class, double.class);
   }
}//END OF DoubleTypeConverter
