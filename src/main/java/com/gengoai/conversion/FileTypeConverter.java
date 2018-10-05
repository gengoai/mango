package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.io.File;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class FileTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof File) {
         return source;
      } else if (source instanceof Path) {
         return Cast.as(source, Path.class).toFile();
      } else if (source instanceof URI) {
         return new File(Cast.as(source, URI.class));
      } else if (source instanceof URL) {
         try {
            return new File(Cast.as(source, URL.class).toURI());
         } catch (Exception e) {
            throw new TypeConversionException(source, File.class, e.getCause());
         }
      } else if (source instanceof CharSequence) {
         return new File(source.toString());
      }
      throw new TypeConversionException(source, File.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(File.class);
   }
}//END OF FileTypeConverter
