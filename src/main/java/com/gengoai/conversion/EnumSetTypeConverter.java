package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.util.EnumSet;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class EnumSetTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      return EnumSet.copyOf(Converter.convert(object,Enum.class,parameters));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(EnumSet.class);
   }
}//END OF EnumSetTypeConverter
