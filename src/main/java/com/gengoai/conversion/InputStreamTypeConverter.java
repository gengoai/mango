package com.gengoai.conversion;

import com.gengoai.Primitives;
import com.gengoai.io.Resources;
import org.kohsuke.MetaInfServices;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.util.Arrays;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class InputStreamTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof InputStream) {
         return source;
      }

      try {
         if (source instanceof File) {
            return Resources.fromFile(Cast.<File>as(source)).inputStream();
         } else if (source instanceof Path) {
            return Resources.fromFile(Cast.<Path>as(source).toFile()).inputStream();
         } else if (source instanceof URL) {
            return Resources.fromUrl(Cast.as(source)).inputStream();
         } else if (source instanceof URI) {
            return Resources.fromURI(Cast.as(source)).inputStream();
         }
      } catch (IOException e) {
         throw new TypeConversionException(source, InputStream.class, e);
      }


      byte[] bytes = null;
      if (source instanceof CharSequence) {
         bytes = source.toString().getBytes();
      } else if (source instanceof byte[]) {
         bytes = Cast.as(source);
      } else if (source instanceof Byte[]) {
         Byte[] b = Cast.as(source);
         bytes = Primitives.toByteArray(Arrays.asList(b));
      } else {
         bytes = Converter.convert(source, byte[].class);
      }

      if (bytes != null) {
         return new ByteArrayInputStream(bytes);
      }

      throw new TypeConversionException(source, InputStream.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(InputStream.class);
   }
}//END OF FileTypeConverter
