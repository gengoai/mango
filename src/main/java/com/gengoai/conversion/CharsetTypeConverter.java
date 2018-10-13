package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.nio.charset.Charset;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class CharsetTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof Charset) {
         return source;
      }
      try {
         return Charset.forName(Converter.convert(source, String.class));
      } catch (Exception e) {
         throw new TypeConversionException(source, Charset.class);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Charset.class);
   }
}//END OF CharsetTypeConverter
