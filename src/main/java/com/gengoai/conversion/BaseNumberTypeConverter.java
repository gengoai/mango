package com.gengoai.conversion;

import com.gengoai.json.JsonEntry;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;

/**
 * @author David B. Bracewell
 */
public abstract class BaseNumberTypeConverter implements TypeConverter {

   protected abstract Object convertNumber(Number number);

   @Override
   public final Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof JsonEntry) {
         JsonEntry entry = Cast.as(object);
         if (entry.isNumber()) {
            return convertNumber(entry.getAsNumber());
         } else if (entry.isBoolean()) {
            return convertNumber(entry.getAsBoolean() ? 1L : 0L);
         } else if (entry.isString()) {
            return convert(entry.getAsString());
         }
         throw new TypeConversionException(object, Number.class);
      } else if (object instanceof Number) {
         return convertNumber(Cast.as(object));
      } else if (object instanceof Boolean) {
         return convertNumber(Cast.as(object, Boolean.class) ? 1L : 0L);
      } else if (object instanceof Character) {
         return convertNumber(((long) Cast.as(object, Character.class)));
      }

      try {
         return convertNumber(Long.parseLong(object.toString()));
      } catch (Exception e) {
         //ignore this and try a double parse
      }

      try {
         return convertNumber(Double.parseDouble(object.toString()));
      } catch (Exception e) {
         //ignore this and try biginteger
      }

      try {
         return convertNumber(new BigInteger(object.toString()));
      } catch (Exception e) {
         //try BigDecimal
      }

      try {
         return convertNumber(new BigDecimal(object.toString()));
      } catch (Exception e) {
         throw new TypeConversionException(object, Number.class, e);
      }

   }
}//END OF BaseNumberTypeConverter
