package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class UrlTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof URL) {
         return source;
      }
      try {
         return Converter.convert(source, URI.class).toURL();
      } catch (MalformedURLException | TypeConversionException e) {
         throw new TypeConversionException(source, URL.class, e);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(URL.class);
   }
}//END OF FileTypeConverter
