package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class BooleanTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof Boolean) {
         return Cast.as(object);
      } else if (object instanceof Number) {
         return Cast.as(object, Number.class).intValue() == 1;
      } else if (object instanceof CharSequence) {
         return Boolean.parseBoolean(object.toString());
      }
      throw new TypeConversionException(object.getClass(), Boolean.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Boolean.class, boolean.class);
   }
}//END OF BooleanTypeConverter
