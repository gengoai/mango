package com.gengoai;

import com.gengoai.collection.Iterables;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Convert;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Methods for working with primitive values including wrapping, unwrapping to object types and converting
 * collections.</p>
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


   private static Object toArray(Iterable<? extends Number> numbers, Class<?> targetClass) {
      Object array = Array.newInstance(targetClass, Iterables.size(numbers));
      int index = 0;
      for (Number number : numbers) {
         Array.set(array, index, Convert.convert(number, targetClass));
         index++;
      }
      return array;
   }

   /**
    * Converts and iterable of numbers to an array of byte
    *
    * @param numbers the numbers to convert
    * @return the byte array
    */
   public static byte[] toByteArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, byte.class), byte[].class);
   }

   /**
    * Converts and iterable of numbers to an array of int
    *
    * @param numbers the numbers to convert
    * @return the int array
    */
   public static int[] toIntArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, int.class), int[].class);
   }

   /**
    * Converts and iterable of numbers to an array of float
    *
    * @param numbers the numbers to convert
    * @return the float array
    */
   public static float[] toFloatArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, float.class), float[].class);
   }

   /**
    * Converts and iterable of numbers to an array of double
    *
    * @param numbers the numbers to convert
    * @return the double array
    */
   public static double[] toDoubleArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, double.class), double[].class);
   }

   /**
    * Converts and iterable of numbers to an array of long
    *
    * @param numbers the numbers to convert
    * @return the long array
    */
   public static long[] toLongArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, long.class), long[].class);
   }

   /**
    * Converts and iterable of numbers to an array of short
    *
    * @param numbers the numbers to convert
    * @return the short array
    */
   public static short[] toShortArray(@NonNull Iterable<? extends Number> numbers) {
      return Cast.as(toArray(numbers, short.class), short[].class);
   }

   /**
    * Converts and iterable of Character to an array of char
    *
    * @param characters the characters to convert
    * @return the char array
    */
   public static char[] toCharArray(@NonNull Iterable<Character> characters) {
      char[] rval = new char[Iterables.size(characters)];
      int index = 0;
      for (Character character : characters) {
         rval[index] = character;
         index++;
      }
      return rval;
   }

   /**
    * Gets the object type class corresponding to a primitive class.
    *
    * @param <T>  the type parameter
    * @param type the primitive type
    * @return the wrapped type class
    */
   public static <T> Class<T> wrap(@NonNull Class<T> type) {
      return Cast.as(primitiveToWrap.getOrDefault(type, type));
   }

   /**
    * Gets the primitive type class corresponding to an boxed type.
    *
    * @param <T>  the type parameter
    * @param type the boxed type
    * @return the primitive class
    */
   public static <T> Class<T> unwrap(@NonNull Class<T> type) {
      return Cast.as(wrapToPrimitive.getOrDefault(type, type));
   }

}//END OF Primitives
