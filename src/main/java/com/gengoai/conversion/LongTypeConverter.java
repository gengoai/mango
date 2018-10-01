package com.gengoai.conversion;

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
      if (object instanceof BigDecimal) {
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
         //return MathEvaluator.evaluate(object.toString());
      } catch (Exception e) {
         throw new TypeConversionException(object.getClass(), Number.class);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Long.class, long.class);
   }
}//END OF FloatTypeConverter
