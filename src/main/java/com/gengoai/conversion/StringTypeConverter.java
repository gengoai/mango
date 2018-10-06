package com.gengoai.conversion;

import com.gengoai.Primitives;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;
import org.kohsuke.MetaInfServices;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.Type;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Blob;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Map;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class StringTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof JsonEntry) {
         JsonEntry entry = Cast.as(object);
         if (entry.isString()) {
            return entry.getAsString();
         }
         return entry.toString();
      }
      if (object instanceof CharSequence) {
         return object.toString();
      } else if (object instanceof char[]) {
         return new String(Cast.<char[]>as(object));
      } else if (object instanceof byte[]) {
         return new String(Cast.<byte[]>as(object));
      } else if (object instanceof Character[]) {
         return new String(Primitives.toCharArray(Arrays.asList(Cast.as(object))));
      } else if (object instanceof Byte[]) {
         return new String(Primitives.toByteArray(Arrays.asList(Cast.as(object))));
      } else if (object instanceof File || object instanceof Path || object instanceof URI || object instanceof URL || object instanceof InputStream || object instanceof Blob || object instanceof Reader) {
         byte[] bytes = Converter.convert(object, byte[].class);
         return new String(bytes);
      } else if (object.getClass().isArray()) {
         StringBuilder array = new StringBuilder("[");
         for (int i = 0; i < Array.getLength(object); i++) {
            if (i != 0) {
               array.append(", ");
            }
            array.append(Converter.convert(Array.get(object, i), String.class));
         }
         return array + "]";
      } else if (object instanceof Date) {
         return SimpleDateFormat.getDateTimeInstance().format(object);
      } else if (object instanceof Map) {
         return Json.dumps(object);
      }

      return object.toString();
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(String.class, CharSequence.class);
   }
}//END OF StringTypeConverter
