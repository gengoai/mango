package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ByteTypeConverter extends BaseNumberTypeConverter {

   @Override
   protected Object convertNumber(Number number) {
      return number.byteValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(byte.class, Byte.class);
   }
}//END OF ByteTypeConverter
