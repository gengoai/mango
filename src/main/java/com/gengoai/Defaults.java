package com.gengoai;

import com.gengoai.conversion.Cast;

/**
 * The type Defaults.
 *
 * @author David B. Bracewell
 */
public final class Defaults {

   private static final Double DEFAULT_DOUBLE = 0d;
   private static final Integer DEFAULT_INT = 0;
   private static final Short DEFAULT_SHORT = 0;
   private static final Long DEFAULT_LONG = 0L;
   private static final Float DEFAULT_FLOAT = 0f;
   private static final Boolean DEFAULT_BOOLEAN = false;
   private static final Character DEFAULT_CHARACTER = 0;
   private static final Byte DEFAULT_BYTE = 0;

   private Defaults() {
      throw new IllegalAccessError();
   }

   /**
    * Default value t.
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return the t
    */
   public static <T> T defaultValue(Class<T> clazz) {
      Validation.notNull(clazz);

      if (clazz == boolean.class || clazz == Boolean.class) {
         return Cast.as(DEFAULT_BOOLEAN);
      } else if (clazz == byte.class || clazz == Byte.class) {
         return Cast.as(DEFAULT_BYTE);
      } else if (clazz == char.class || clazz == Character.class) {
         return Cast.as(DEFAULT_CHARACTER);
      } else if (clazz == int.class || clazz == Integer.class) {
         return Cast.as(DEFAULT_INT);
      } else if (clazz == short.class || clazz == Short.class) {
         return Cast.as(DEFAULT_SHORT);
      } else if (clazz == long.class || clazz == Long.class) {
         return Cast.as(DEFAULT_LONG);
      } else if (clazz == float.class || clazz == Float.class) {
         return Cast.as(DEFAULT_FLOAT);
      } else if (clazz == double.class || clazz == Double.class) {
         return Cast.as(DEFAULT_DOUBLE);
      }

      return null;
   }

}//END OF Defaults
