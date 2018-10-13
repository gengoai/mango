package com.gengoai.conversion;

import com.gengoai.reflection.Types;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;
import static com.gengoai.reflection.Types.getOrObject;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ArrayTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      Type componentType = getOrObject(0, parameters);
      //Convert the source to a list and create an array from it.
      List<?> list = Converter.convert(source, List.class, parameters);
      Object array = Array.newInstance(Types.asClass(componentType), list.size());
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
