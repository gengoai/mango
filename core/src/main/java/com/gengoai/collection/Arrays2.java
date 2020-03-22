package com.gengoai.collection;

import lombok.NonNull;

import java.lang.reflect.Array;

import static com.gengoai.Validation.checkArgument;

/**
 * <p>Convenience methods for creating object and primitive arrays.</p>
 *
 * @author David B. Bracewell
 */
public final class Arrays2 {

   private Arrays2() {
      throw new IllegalAccessError();
   }

   /**
    * Creates an array of Objects
    *
    * @param <T>     the object type parameter
    * @param objects the objects
    * @return the array
    */
   @SafeVarargs
   public static <T> T[] arrayOf(T... objects) {
      return objects;
   }

   /**
    * Creates an array of boolean values
    *
    * @param values the values
    * @return the boolean array
    */
   public static boolean[] arrayOfBoolean(boolean... values) {
      return values;
   }

   /**
    * Creates an array of byte values
    *
    * @param values the values
    * @return the byte array
    */
   public static byte[] arrayOfByte(int... values) {
      byte[] b = new byte[values.length];
      for(int i = 0; i < b.length; i++) {
         b[i] = (byte) values[i];
      }
      return b;
   }

   /**
    * Creates an array of byte values
    *
    * @param values the values
    * @return the byte array
    */
   public static byte[] arrayOfByte(byte... values) {
      return values;
   }

   /**
    * Creates an array of character values
    *
    * @param values the values
    * @return the character array
    */
   public static char[] arrayOfChar(char... values) {
      return values;
   }

   /**
    * Creates an array of double values
    *
    * @param values the values
    * @return the double array
    */
   public static double[] arrayOfDouble(double... values) {
      return values;
   }

   /**
    * Creates an array of float values
    *
    * @param values the values
    * @return the float array
    */
   public static float[] arrayOfFloat(float... values) {
      return values;
   }

   /**
    * Creates an array integer values
    *
    * @param values the values
    * @return the int array
    */
   public static int[] arrayOfInt(int... values) {
      return values;
   }

   /**
    * Creates an array of long values
    *
    * @param values the values
    * @return the long values
    */
   public static long[] arrayOfLong(long... values) {
      return values;
   }

   /**
    * Creates an array of short values
    *
    * @param values the values
    * @return the short values
    */
   public static short[] arrayOfShort(int... values) {
      short[] b = new short[values.length];
      for(int i = 0; i < b.length; i++) {
         b[i] = (short) values[i];
      }
      return b;
   }

   /**
    * Creates an array of short values
    *
    * @param values the values
    * @return the short values
    */
   public static short[] arrayOfShort(short... values) {
      return values;
   }

   /**
    * Concat byte [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the byte [ ]
    */
   public static byte[] concat(@NonNull byte[] a1, @NonNull byte[] a2) {
      return (byte[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat int [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the int [ ]
    */
   public static int[] concat(@NonNull int[] a1, @NonNull int[] a2) {
      return (int[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat float [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the float [ ]
    */
   public static float[] concat(@NonNull float[] a1, @NonNull float[] a2) {
      return (float[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat double [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the double [ ]
    */
   public static double[] concat(@NonNull double[] a1, @NonNull double[] a2) {
      return (double[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat short [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the short [ ]
    */
   public static short[] concat(@NonNull short[] a1, @NonNull short[] a2) {
      return (short[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat long [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the long [ ]
    */
   public static long[] concat(@NonNull long[] a1, @NonNull long[] a2) {
      return (long[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat boolean [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the boolean [ ]
    */
   public static boolean[] concat(@NonNull boolean[] a1, @NonNull boolean[] a2) {
      return (boolean[]) concat((Object) a1, (Object) a2);
   }

   /**
    * Concat char [ ].
    *
    * @param a1 the a 1
    * @param a2 the a 2
    * @return the char [ ]
    */
   public static char[] concat(@NonNull char[] a1, @NonNull char[] a2) {
      return (char[]) concat((Object) a1, (Object) a2);
   }

   public static Object[] concat(@NonNull Object[] a1, @NonNull Object[] a2) {
      return (Object[]) concat((Object) a1, (Object) a2);
   }

   private static Object concat(Object a1, Object a2) {
      checkArgument(a1.getClass().isArray());
      checkArgument(a2.getClass().isArray());
      checkArgument(a2.getClass().getComponentType().isAssignableFrom(a2.getClass().getComponentType()));
      int a1Length = Array.getLength(a1);
      int a2Length = Array.getLength(a2);
      Object out = Array.newInstance(a1.getClass().getComponentType(), a1Length + a2Length);
      if(a1Length > 0) {
         System.arraycopy(a1, 0, out, 0, a1Length);
      }
      if(a2Length > 0) {
         System.arraycopy(a2, 0, out, a1Length, a2Length);
      }
      return out;
   }

}//END OF Arrays2
