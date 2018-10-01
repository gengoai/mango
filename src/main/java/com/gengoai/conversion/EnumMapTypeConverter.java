package com.gengoai.conversion;

import com.gengoai.reflection.Types;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.util.EnumMap;
import java.util.Map;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class EnumMapTypeConverter implements TypeConverter {

   @Override
   @SuppressWarnings("unchecked")
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      Class<?> enumClass = (parameters == null || parameters.length < 1)
                           ? Object.class
                           : Types.asClass(parameters[0]);
      Type valueType = (parameters == null || parameters.length < 2)
                       ? Object.class
                       : parameters[1];
      if (enumClass == null || !Enum.class.isAssignableFrom(enumClass)) {
         throw new TypeConversionException("Invalid type parameter (" + enumClass + ")");
      }
      return new EnumMap<>(Converter.<Map<Enum, Object>>convert(object, Map.class, enumClass, valueType));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(EnumMap.class);
   }
}//END OF EnumSetTypeConverter
