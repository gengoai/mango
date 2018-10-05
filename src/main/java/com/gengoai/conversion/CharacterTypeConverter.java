package com.gengoai.conversion;

import org.kohsuke.MetaInfServices;

import java.lang.reflect.Type;

import static com.gengoai.collection.Collect.arrayOf;

/**
 * @author David B. Bracewell
 */
@MetaInfServices(value = TypeConverter.class)
public class CharacterTypeConverter implements TypeConverter {

   @Override
   public Object convert(Object object, Type... parameters) throws TypeConversionException {
      if (object instanceof Character) {
         return Cast.as(object);
      } else if (object instanceof Number) {
         return (char) Cast.as(object, Number.class).intValue();
      } else if (object instanceof CharSequence) {
         CharSequence sequence = Cast.as(object);
         if (sequence.length() == 1) {
            return sequence.charAt(0);
         }
      }
      throw new TypeConversionException(object, Character.class);
   }

   @Override
   public Class[] getConversionType() {
      return arrayOf(Character.class, char.class);
   }
}//END OF CharacterTypeConverter
