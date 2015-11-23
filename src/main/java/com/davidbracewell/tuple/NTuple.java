package com.davidbracewell.tuple;

import lombok.NonNull;

/**
 * @author David B. Bracewell
 */
public class NTuple implements Tuple {

  private final Object[] array;


  public NTuple(@NonNull Object object1, Object... others) {
    int size = 1 + (others == null ? 0 : others.length);
    array = new Object[size];
    array[0] = object1;
    System.arraycopy(others, 0, array, 1, others.length);
  }

  public NTuple(@NonNull Object[] other) {
    array = new Object[other.length];
    System.arraycopy(other, 0, array, 0, other.length);
  }

  public static NTuple of(@NonNull Object[] array) {
    return new NTuple(array);
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

}// END OF NTuple
