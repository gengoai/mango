package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.math.BigInteger;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class BigIntegerTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      if (number instanceof BigInteger) {
         return number;
      }
      return BigInteger.valueOf(number.longValue());
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(BigInteger.class);
   }
}//END OF BigIntegerTypeConverter