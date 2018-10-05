package com.gengoai.conversion;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.*;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class ResourceTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      if (source instanceof Resource) {
         return source;
      } else if (source instanceof File) {
         return Resources.fromFile((File) source);
      } else if (source instanceof Path) {
         return Resources.fromFile(Cast.<Path>as(source).toFile());
      } else if (source instanceof URL) {
         return Resources.fromUrl((URL) source);
      } else if (source instanceof URI) {
         try {
            return Resources.fromUrl(((URI) source).toURL());
         } catch (MalformedURLException e) {
            throw new TypeConversionException(source, Resource.class, e);
         }
      } else if (source instanceof Reader) {
         return new ReaderResource(Cast.as(source));
      } else if (source instanceof InputStream) {
         return new InputStreamResource(Cast.as(source));
      } else if (source instanceof OutputStream) {
         return new OutputStreamResource(Cast.as(source));
      } else if (source instanceof byte[]) {
         return new ByteArrayResource(Cast.as(source));
      } else if (source instanceof CharSequence) {
         return Resources.from(source.toString());
      }
      throw new TypeConversionException(source, Resource.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Resource.class);
   }
}//END OF ResourceTypeConverter
