package com.gengoai.conversion;

import com.gengoai.reflection.Types;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ArrayTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Class<?> componentType = (parameters == null || parameters.length == 0) ? Object.class
                                                                              : Types.asClass(parameters[0]);
      Class<?> sourceClass = source.getClass();
      if (sourceClass.isArray() && sourceClass.getComponentType().equals(componentType)) {
         return source;
      }
      List<?> list = Converter.convert(source, List.class, parameters);
      Object array = Array.newInstance(componentType, list.size());
      for (int i = 0; i < list.size(); i++) {
         Array.set(array, i, list.get(i));
      }
      return array;
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Object[].class);
   }
}//END OF ArrayTypeConverter
