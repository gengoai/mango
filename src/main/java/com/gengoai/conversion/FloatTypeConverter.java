package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class FloatTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      return Convert.convert(object, Number.class).floatValue();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Float.class, float.class);
   }
}//END OF FloatTypeConverter
