package com.gengoai.conversion;

import com.gengoai.reflection.ReflectionUtils;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.reflection.Types.asClass;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ClassTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof Type) {
         try {
            return asClass(Cast.as(object));
         } catch (IllegalArgumentException e) {
            throw new TypeConversionException(object, Class.class, e);
         }
      } else if (object instanceof CharSequence) {
         Class<?> clazz = ReflectionUtils.getClassForNameQuietly(object.toString());
         if (clazz != null) {
            return clazz;
         }
      }
      return object.getClass();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Class.class);
   }
}//END OF ClassTypeConverter
