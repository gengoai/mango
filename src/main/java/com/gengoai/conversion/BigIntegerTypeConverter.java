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
public class BigIntegerTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof BigInteger) {
         return Cast.as(object);
      } else if (object instanceof Boolean) {
         return new BigInteger(Integer.toString(Cast.<Boolean>as(object) ? 1 : 0));
      } else if (object instanceof Character) {
         return BigInteger.valueOf((long) Cast.<Character>as(object));
      }
      try {
         return new BigInteger(object.toString());
      } catch (Exception e) {
         //ignore
      }

      try {
         return BigInteger.valueOf(new Double(object.toString()).longValue());
      } catch (Exception e) {
         throw new TypeConversionException(object.getClass(), BigDecimal.class, e);
      }
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(BigInteger.class);
   }
}//END OF BigIntegerTypeConverter
