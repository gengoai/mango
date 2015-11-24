package com.davidbracewell.tuple;

import com.google.common.base.Joiner;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.util.Arrays;
import java.util.Iterator;

/**
 * @author David B. Bracewell
 */
@EqualsAndHashCode
public class NTuple implements Tuple {

  private final Object[] array;

  public NTuple(@NonNull Object[] other) {
    array = new Object[other.length];
    System.arraycopy(other, 0, array, 0, other.length);
  }

  @SafeVarargs
  public static <T> NTuple of(@NonNull T... items) {
    return new NTuple(items);
  }

  @Override
  public Object get(int index) {
    return array[index];
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
    return "(" + Joiner.on(',').join(array) + ")";
  }

}// END OF NTuple
