package com.gengoai.conversion;

import com.gengoai.io.Resources;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * OutputStream Converter
 *
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class OutputStreamTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof OutputStream) {
         return source;
      }

      try {
         if (source instanceof File) {
            return Resources.fromFile(Cast.<File>as(source)).outputStream();
         } else if (source instanceof Path) {
            return Resources.fromFile(Cast.<Path>as(source).toFile()).outputStream();
         } else if (source instanceof URL) {
            return Resources.fromUrl(Cast.as(source)).outputStream();
         } else if (source instanceof URI) {
            return Resources.fromURI(Cast.as(source)).outputStream();
         }
      } catch (IOException e) {
         throw new TypeConversionException(source, InputStream.class, e);
      }

      throw new TypeConversionException(source, OutputStream.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(OutputStream.class);
   }
}//END OF OutputStreamTypeConverter
