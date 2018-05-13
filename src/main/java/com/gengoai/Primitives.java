package com.gengoai;

import com.gengoai.conversion.Cast;
import lombok.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * The type Primitives.
 *
 * @author David B. Bracewell
 */
public class Primitives {

   private static final Map<Class<?>, Class<?>> primitiveToWrap = new HashMap<>(20);
   private static final Map<Class<?>, Class<?>> wrapToPrimitive = new HashMap<>(20);

   static {
      add(int.class, Integer.class);
      add(short.class, Short.class);
      add(long.class, Long.class);
      add(char.class, Character.class);
      add(boolean.class, Boolean.class);
      add(void.class, Void.class);
      add(byte.class, Byte.class);
      add(float.class, Float.class);
      add(double.class, Double.class);
   }

   private static void add(Class<?> primitive, Class<?> wrap) {
      primitiveToWrap.put(primitive, wrap);
      wrapToPrimitive.put(wrap, primitive);
   }

   /**
    * To byte array byte [ ].
    *
    * @param numbers the numbers
    * @return the byte [ ]
    */
   public static byte[] toByteArray(@NonNull Collection<? extends Number> numbers) {
      byte[] rval = new byte[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.byteValue();
         index++;
      }
      return rval;
   }

   /**
    * To int array int [ ].
    *
    * @param numbers the numbers
    * @return the int [ ]
    */
   public static int[] toIntArray(@NonNull Collection<? extends Number> numbers) {
      int[] rval = new int[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.intValue();
         index++;
      }
      return rval;
   }

   /**
    * To float array float [ ].
    *
    * @param numbers the numbers
    * @return the float [ ]
    */
   public static float[] toFloatArray(@NonNull Collection<? extends Number> numbers) {
      float[] rval = new float[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.floatValue();
         index++;
      }
      return rval;
   }

   /**
    * To double array double [ ].
    *
    * @param numbers the numbers
    * @return the double [ ]
    */
   public static double[] toDoubleArray(@NonNull Collection<? extends Number> numbers) {
      double[] rval = new double[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.doubleValue();
         index++;
      }
      return rval;
   }

   /**
    * To long array long [ ].
    *
    * @param numbers the numbers
    * @return the long [ ]
    */
   public static long[] toLongArray(@NonNull Collection<? extends Number> numbers) {
      long[] rval = new long[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.longValue();
         index++;
      }
      return rval;
   }

   /**
    * To short array short [ ].
    *
    * @param numbers the numbers
    * @return the short [ ]
    */
   public static short[] toShortArray(@NonNull Collection<? extends Number> numbers) {
      short[] rval = new short[numbers.size()];
      int index = 0;
      for (Number number : numbers) {
         rval[index] = number.shortValue();
         index++;
      }
      return rval;
   }

   /**
    * To char array char [ ].
    *
    * @param characters the characters
    * @return the char [ ]
    */
   public static char[] toCharArray(@NonNull Collection<Character> characters) {
      char[] rval = new char[characters.size()];
      int index = 0;
      for (Character character : characters) {
         rval[index] = character;
         index++;
      }
      return rval;
   }

   /**
    * Wrap class.
    *
    * @param <T>  the type parameter
    * @param type the type
    * @return the class
    */
   public static <T> Class<T> wrap(@NonNull Class<T> type) {
      return Cast.as(primitiveToWrap.getOrDefault(type, type));
   }

   /**
    * Unwrap class.
    *
    * @param <T>  the type parameter
    * @param type the type
    * @return the class
    */
   public static <T> Class<T> unwrap(@NonNull Class<T> type) {
      return Cast.as(wrapToPrimitive.getOrDefault(type, type));
   }

}//END OF Primitives
