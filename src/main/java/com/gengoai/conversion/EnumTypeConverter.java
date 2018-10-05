package com.gengoai.conversion;

import com.gengoai.reflection.ReflectionUtils;
import com.gengoai.reflection.Types;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.reflection.Types.parameterizedType;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class EnumTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Class<?> enumClass = null;
      if (source instanceof CharSequence) {
         String sourceStr = source.toString();
         int lastDot = sourceStr.lastIndexOf('.');
         int lastDollar = sourceStr.lastIndexOf('$');
         if (lastDollar > 0) {
            enumClass = ReflectionUtils.getClassForNameQuietly(sourceStr.substring(0, lastDollar));
            try {
               source = enumClass.getFields()[Integer.parseInt(sourceStr.substring(lastDollar + 1)) - 1].get(null);
            } catch (IllegalAccessException e) {
               throw new TypeConversionException(source, parameterizedType(Enum.class, enumClass));
            }
         } else if (lastDot > 0) {
            enumClass = ReflectionUtils.getClassForNameQuietly(sourceStr.substring(0, lastDot));
            source = sourceStr.substring(lastDot + 1);
         }
      }
      if (enumClass == null) {
         enumClass = (parameters == null || parameters.length < 1)
                     ? Object.class
                     : Types.asClass(parameters[0]);
      }
      if (enumClass == null || !Enum.class.isAssignableFrom(enumClass)) {
         throw new TypeConversionException("Invalid type parameter (" + enumClass + ")");
      }

      if (source instanceof Enum) {
         return source;
      }
      if (source instanceof CharSequence) {
         return Enum.valueOf(Cast.as(enumClass), source.toString());
      }
      if (source instanceof Class) {
         return convert(Cast.<Class>as(source).getName(), enumClass);
      }
      throw new TypeConversionException(source, parameterizedType(Enum.class, enumClass));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Enum.class);
   }
}//END OF EnumTypeConverter
