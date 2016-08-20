/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.davidbracewell;

import com.davidbracewell.conversion.Cast;
import lombok.NonNull;

import java.lang.reflect.Array;
import java.util.Collection;

/**
 * The type Primitives.
 * @author David B. Bracewell
 */
public final class Primitives {

  private Primitives() {
    throw new IllegalAccessError();
  }


  /**
   * To byte array.
   *
   * @param bytes the bytes
   * @return the byte [ ]
   */
  public static byte[] toByteArray(Number[] bytes) {
    return Cast.as(convert(bytes, byte.class));
  }

  /**
   * To short array.
   *
   * @param shorts the shorts
   * @return the short [ ]
   */
  public static short[] toShortArray(Number[] shorts) {
    return Cast.as(convert(shorts, short.class));
  }

  /**
   * To char array.
   *
   * @param chars the chars
   * @return the char [ ]
   */
  public static char[] toCharArray(Character[] chars) {
    return Cast.as(convert(chars, char.class));
  }

  /**
   * To int array.
   *
   * @param ints the ints
   * @return the int [ ]
   */
  public static int[] toIntArray(Number[] ints) {
    return Cast.as(convert(ints, int.class));
  }

  /**
   * To double array.
   *
   * @param doubles the doubles
   * @return the double [ ]
   */
  public static double[] toDoubleArray(Number[] doubles) {
    return Cast.as(convert(doubles, double.class));
  }

  /**
   * To float array.
   *
   * @param floats the floats
   * @return the float [ ]
   */
  public static float[] toFloatArray(Number[] floats) {
    return Cast.as(convert(floats, float.class));
  }

  /**
   * To long array.
   *
   * @param longs the longs
   * @return the long [ ]
   */
  public static long[] toLongArray(Number[] longs) {
    return Cast.as(convert(longs, long.class));
  }

  /**
   * To boolean array.
   *
   * @param booleans the booleans
   * @return the boolean [ ]
   */
  public static boolean[] toBooleanArray(Number[] booleans) {
    return Cast.as(convert(booleans, boolean.class));
  }


  /**
   * To byte array.
   *
   * @param bytes the bytes
   * @return the byte [ ]
   */
  public static byte[] toByteArray(Collection<? extends Number> bytes) {
    return Cast.as(convert(bytes, byte.class));
  }

  /**
   * To short array.
   *
   * @param shorts the shorts
   * @return the short [ ]
   */
  public static short[] toShortArray(Collection<? extends Number> shorts) {
    return Cast.as(convert(shorts, short.class));
  }

  /**
   * To char array.
   *
   * @param chars the chars
   * @return the char [ ]
   */
  public static char[] toCharArray(Collection<Character> chars) {
    return Cast.as(convert(chars, char.class));
  }

  /**
   * To int array.
   *
   * @param ints the ints
   * @return the int [ ]
   */
  public static int[] toIntArray(Collection<? extends Number> ints) {
    return Cast.as(convert(ints, int.class));
  }

  /**
   * To double array.
   *
   * @param doubles the doubles
   * @return the double [ ]
   */
  public static double[] toDoubleArray(Collection<? extends Number> doubles) {
    return Cast.as(convert(doubles, double.class));
  }

  /**
   * To float array.
   *
   * @param floats the floats
   * @return the float [ ]
   */
  public static float[] toFloatArray(Collection<? extends Number> floats) {
    return Cast.as(convert(floats, float.class));
  }

  /**
   * To boolean array.
   *
   * @param booleans the booleans
   * @return the boolean [ ]
   */
  public static boolean[] toBooleanArray(Collection<Boolean> booleans) {
    return Cast.as(convert(booleans, boolean.class));
  }


  /**
   * To long array.
   *
   * @param longs the longs
   * @return the long [ ]
   */
  public static long[] toLongArray(Collection<? extends Number> longs) {
    return Cast.as(convert(longs, long.class));
  }

  /**
   * To byte array.
   *
   * @param bytes the bytes
   * @return the byte [ ]
   */
  public static Byte[] toByteArray(byte[] bytes) {
    return Cast.as(convert(bytes, Byte.class));
  }

  /**
   * To short array.
   *
   * @param shorts the shorts
   * @return the short [ ]
   */
  public static Short[] toShortArray(short[] shorts) {
    return Cast.as(convert(shorts, Short.class));
  }

  /**
   * To char array.
   *
   * @param chars the chars
   * @return the character [ ]
   */
  public static Character[] toCharArray(char[] chars) {
    return Cast.as(convert(chars, Character.class));
  }

  /**
   * To int array.
   *
   * @param ints the ints
   * @return the integer [ ]
   */
  public static Integer[] toIntArray(int[] ints) {
    return Cast.as(convert(ints, Integer.class));
  }

  /**
   * To double array.
   *
   * @param doubles the doubles
   * @return the double [ ]
   */
  public static Double[] toDoubleArray(double[] doubles) {
    return Cast.as(convert(doubles, Double.class));
  }

  /**
   * To float array.
   *
   * @param floats the floats
   * @return the float [ ]
   */
  public static Float[] toFloatArray(float[] floats) {
    return Cast.as(convert(floats, Float.class));
  }

  /**
   * To long array.
   *
   * @param longs the longs
   * @return the long [ ]
   */
  public static Long[] toLongArray(long[] longs) {
    return Cast.as(convert(longs, Long.class));
  }

  /**
   * To boolean array.
   *
   * @param booleans the booleans
   * @return the boolean [ ]
   */
  public static Boolean[] toBooleanArray(boolean[] booleans) {
    return Cast.as(convert(booleans, Boolean.class));
  }


  private static <T> Object convert(Collection<?> collection, Class<T> to) {
    if (collection == null) {
      return null;
    }
    Object array = Array.newInstance(to, collection.size());
    int index = 0;
    for (Object val : collection) {
      Array.set(array, index, getValue(val, to));
      index++;
    }
    return array;
  }

  private static Object convert(Object from, Class<?> to) {
    if (from == null) {
      return null;
    }
    Object array = Array.newInstance(to, Array.getLength(from));
    for (int i = 0; i < Array.getLength(from); i++) {
      Array.set(array, i, getValue(Array.get(from, i), to));
    }
    return array;
  }


  private static <T> Object getValue(Object o, Class<T> primitive) {
    if (o == null) {
      return Defaults.defaultValue(primitive);
    }
    if (o instanceof Number) {
      Number number = Cast.as(o);
      if (primitive == int.class) {
        return number.intValue();
      } else if (primitive == double.class) {
        return number.doubleValue();
      } else if (primitive == long.class) {
        return number.longValue();
      } else if (primitive == short.class) {
        return number.shortValue();
      } else if (primitive == float.class) {
        return number.floatValue();
      } else if (primitive == byte.class) {
        return number.byteValue();
      }
    }
    return o;
  }

  /**
   * Wrap class.
   *
   * @param <T>  the type parameter
   * @param primitive the primitive
   * @return the class
   */
  public static <T> Class<T> wrap(@NonNull Class<T> primitive) {
    if (primitive == int.class) {
      return Cast.as(Integer.class);
    } else if (primitive == double.class) {
      return Cast.as(Double.class);
    } else if (primitive == long.class) {
      return Cast.as(Long.class);
    } else if (primitive == short.class) {
      return Cast.as(Short.class);
    } else if (primitive == float.class) {
      return Cast.as(Float.class);
    } else if (primitive == boolean.class) {
      return Cast.as(Boolean.class);
    } else if (primitive == char.class) {
      return Cast.as(Character.class);
    } else if (primitive == byte.class) {
      return Cast.as(Byte.class);
    }
    return primitive;
  }

  /**
   * Unwrap class.
   *
   * @param <T>  the type parameter
   * @param object the object
   * @return the class
   */
  public static <T> Class<T> unwrap(@NonNull Class<T> object) {
    if (object == Integer.class) {
      return Cast.as(int.class);
    } else if (object == Double.class) {
      return Cast.as(double.class);
    } else if (object == Long.class) {
      return Cast.as(long.class);
    } else if (object == Short.class) {
      return Cast.as(short.class);
    } else if (object == Float.class) {
      return Cast.as(float.class);
    } else if (object == Boolean.class) {
      return Cast.as(boolean.class);
    } else if (object == Character.class) {
      return Cast.as(char.class);
    } else if (object == Byte.class) {
      return Cast.as(byte.class);
    }
    return object;
  }

}//END OF Primitives
