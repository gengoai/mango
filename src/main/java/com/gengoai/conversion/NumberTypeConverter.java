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
public class NumberTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof BigDecimal) {
         return Cast.as(object, BigDecimal.class).doubleValue();
      } else if (object instanceof BigInteger) {
         return Cast.as(object, BigInteger.class).doubleValue();
      } else if (object instanceof Number) {
         return Cast.as(object, Number.class).doubleValue();
      } else if (object instanceof Boolean) {
         return Cast.as(object, Boolean.class) ? 1d : 0d;
      } else if (object instanceof Character) {
         return (double) ((int) Cast.as(object, Character.class));
      }
      try {
         return Double.parseDouble(object.toString());
         //return MathEvaluator.evaluate(object.toString());
      } catch (Exception e) {
         throw new TypeConversionException(object.getClass(), Number.class);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Number.class, Double.class, double.class);
   }
}//END OF NumberTypeConverter
