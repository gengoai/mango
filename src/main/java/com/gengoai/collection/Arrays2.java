package com.gengoai.collection;

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
      for (int i = 0; i < b.length; i++) {
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
    * Creates an array of short values
    *
    * @param values the values
    * @return the short values
    */
   public static short[] arrayOfShort(int... values) {
      short[] b = new short[values.length];
      for (int i = 0; i < b.length; i++) {
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
    * Creates an array of long values
    *
    * @param values the values
    * @return the long values
    */
   public static long[] arrayOfLong(long... values){
      return values;
   }

}//END OF Arrays2
