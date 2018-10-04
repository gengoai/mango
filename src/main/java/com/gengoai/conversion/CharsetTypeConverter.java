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
      } else if (source instanceof CharSequence) {
         try {
            return Charset.forName(source.toString());
         } catch (Exception e) {
            throw new TypeConversionException(source.getClass(), Charset.class, e.getCause());
         }
      }
      throw new TypeConversionException(source.getClass(), Charset.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Charset.class);
   }
}//END OF CharsetTypeConverter
