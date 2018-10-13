package com.gengoai.conversion;

import com.gengoai.io.resource.Resource;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class FileTypeConverter implements TypeConverter {
   @Override
   public Object convert(Object source, Type... parameters) throws TypeConversionException {
      return Converter.convert(source, Resource.class).asFile()
                      .orElseThrow(() -> new TypeConversionException(source, File.class));
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(File.class);
   }
}//END OF FileTypeConverter
