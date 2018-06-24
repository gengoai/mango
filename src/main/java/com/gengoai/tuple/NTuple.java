package com.gengoai.tuple;

import com.gengoai.conversion.Cast;
import com.gengoai.string.StringUtils;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * The type N tuple.
 *
 * @author David B. Bracewell
 */
public class NTuple extends Tuple {
   private static final long serialVersionUID = 1L;

   private final Object[] array;

   /**
    * Instantiates a new N tuple.
    *
    * @param other the other
    */
   public NTuple(Object[] other) {
      array = new Object[other.length];
      System.arraycopy(other, 0, array, 0, other.length);
   }

   /**
    * Of n tuple.
    *
    * @param <T>   the type parameter
    * @param items the items
    * @return the n tuple
    */
   @SafeVarargs
   public static <T> NTuple of(T... items) {
      return new NTuple(items);
   }

   /**
    * Of n tuple.
    *
    * @param <T>   the type parameter
    * @param items the items
    * @return the n tuple
    */
   public static <T> NTuple of(List<T> items) {
      return new NTuple(items.toArray());
   }

   @Override
   public NTuple copy() {
      return new NTuple(array);
   }

   @Override
   public <T> T get(int i) {
      return Cast.as(array[i]);
   }

   @Override
   public Iterator<Object> iterator() {
      return Arrays.asList(array).iterator();
   }

   @Override
   public int degree() {
      return array.length;
   }

   @Override
   public Object[] array() {
      Object[] copy = new Object[array.length];
      System.arraycopy(array, 0, copy, 0, array.length);
      return copy;
   }

   @Override
   public String toString() {
      return StringUtils.join(array(), ", ", "(", ")");
   }

}// END OF NTuple
