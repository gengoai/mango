package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class IteratorTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      return Converter.<List<?>>convert(object, List.class, parameters).iterator();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Iterator.class);
   }
}//END OF IteratorTypeConverter