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
public class BigDecimalTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof BigDecimal) {
         return Cast.as(object);
      } else if (object instanceof BigInteger) {
         return new BigDecimal(Cast.as(object, BigInteger.class));
      } else if (object instanceof Number) {
         return new BigDecimal(Cast.as(object, Number.class).doubleValue());
      } else if (object instanceof Boolean) {
         return new BigDecimal(Cast.<Boolean>as(object) ? 1d : 0d);
      } else if (object instanceof Character) {
         return new BigDecimal((int) Cast.<Character>as(object));
      }
      try {
         return new BigDecimal(object.toString());
      } catch (Exception e) {
         throw new TypeConversionException(object.getClass(), BigDecimal.class, e);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(BigDecimal.class);
   }
}//END OF BigDecimalTypeConverter
