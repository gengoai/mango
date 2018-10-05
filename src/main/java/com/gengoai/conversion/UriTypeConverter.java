package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class UriTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof URI) {
         return source;
      } else if (source instanceof Path) {
         return Cast.as(source, Path.class).toUri();
      } else if (source instanceof File) {
         return Cast.as(source, File.class).toURI();
      } else if (source instanceof URL) {
         try {
            return Cast.as(source, URL.class).toURI();
         } catch (URISyntaxException e) {
            throw new TypeConversionException(source, URI.class, e);
         }
      } else if (source instanceof CharSequence) {
         try {
            return new URI(source.toString());
         } catch (URISyntaxException e) {
            throw new TypeConversionException(source, URI.class, e);
         }
      }
      throw new TypeConversionException(source, URI.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(URI.class);
   }
}//END OF FileTypeConverter
