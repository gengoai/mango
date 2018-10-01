package com.gengoai.conversion;

import com.gengoai.EnumValue;
import com.gengoai.HierarchicalEnumValue;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
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
public class DynamicEnumTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof EnumValue) {
         return source;
      }

      Class<?> enumClass = null;
      if (source instanceof CharSequence) {
         String sourceStr = source.toString();
         int lastDot = sourceStr.lastIndexOf('.');
         if (lastDot > 0) {
            enumClass = ReflectionUtils.getClassForNameQuietly(sourceStr.substring(0, lastDot));
            source = sourceStr.substring(lastDot + 1);
         }
      }
      if (enumClass == null) {
         enumClass = (parameters == null || parameters.length < 1)
                     ? Object.class
                     : Types.asClass(parameters[0]);
      }
      if (enumClass == null || !EnumValue.class.isAssignableFrom(enumClass)) {
         throw new TypeConversionException("Invalid type parameter (" + enumClass + ")");
      }


      if (source instanceof CharSequence) {
         try {
            return Reflect.onClass(enumClass).invoke("create", source.toString());
         } catch (ReflectionException e) {
            throw new TypeConversionException(source.getClass(), parameterizedType(EnumValue.class, enumClass), e);
         }
      }


      throw new TypeConversionException(source.getClass(), parameterizedType(EnumValue.class, enumClass));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(EnumValue.class, HierarchicalEnumValue.class);
   }
}//END OF EnumTypeConverter
