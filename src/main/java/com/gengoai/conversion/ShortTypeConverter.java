package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ShortTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      return Convert.convert(object, Long.class).shortValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Short.class, short.class);
   }
}//END OF ShortTypeConverter
