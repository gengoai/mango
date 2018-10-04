package com.gengoai.conversion;

import com.gengoai.json.JsonEntry;
import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class LongTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof JsonEntry) {
         JsonEntry entry = Cast.as(object);
         if (entry.isNumber()) {
            return entry.getAsNumber().longValue();
         } else if (entry.isBoolean()) {
            return entry.getAsBoolean() ? 1L : 0L;
         } else if (entry.isString()) {
            return convert(entry.getAsString());
         }
         throw new TypeConversionException(object, Long.class);
      } else if (object instanceof BigDecimal) {
         return Cast.as(object, BigDecimal.class).longValue();
      } else if (object instanceof BigInteger) {
         return Cast.as(object, BigInteger.class).longValue();
      } else if (object instanceof Number) {
         return Cast.as(object, Number.class).longValue();
      } else if (object instanceof Boolean) {
         return Cast.as(object, Boolean.class) ? 1L : 0L;
      } else if (object instanceof Character) {
         return ((long) Cast.as(object, Character.class));
      }

      try {
         return Long.parseLong(object.toString());
      } catch (Exception e) {
         //ignore this and try a double parse
      }

      try {
         return (long) Double.parseDouble(object.toString());
      } catch (Exception e) {
         throw new TypeConversionException(object, Number.class, e);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Long.class, long.class);
   }
}//END OF FloatTypeConverter
