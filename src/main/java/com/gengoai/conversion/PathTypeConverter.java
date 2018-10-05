package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class PathTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof Path) {
         return source;
      } else if (source instanceof File) {
         return Paths.get(Cast.as(source, File.class).getPath());
      } else if (source instanceof URI) {
         return Paths.get(Cast.as(source, URI.class));
      } else if (source instanceof URL) {
         try {
            return Paths.get(Cast.as(source, URL.class).toURI());
         } catch (URISyntaxException e) {
            throw new TypeConversionException(source, Path.class, e);
         }
      } else if (source instanceof CharSequence) {
         return Paths.get(source.toString());
      }
      throw new TypeConversionException(source, Path.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Path.class);
   }
}//END OF PathTypeConverter
