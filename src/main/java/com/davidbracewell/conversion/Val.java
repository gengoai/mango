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

package com.davidbracewell.conversion;

import com.davidbracewell.io.resource.Resource;
import lombok.NonNull;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Wraps an object allowing conversion into other formats.
 *
 * @author David B. Bracewell
 */
public class Val implements Serializable {

  /**
   * False value
   */
  public static final Val FALSE = Val.of(false);
  /**
   * Null value
   */
  public static final Val NULL = Val.of(null);
  /**
   * True value
   */
  public static final Val TRUE = Val.of(true);
  private static final long serialVersionUID = 1303236922605307700L;
  private final Object toConvert;

  /**
   * Default Constructor
   *
   * @param toConvert The object to convert
   */
  public Val(Object toConvert) {
    this.toConvert = toConvert;
  }

  /**
   * Convenience method for creating a Convertible Object
   *
   * @param o The object to convert
   * @return The ConvertibleObject wrapping the given object
   */
  public static Val of(Object o) {
    if (o != null && o instanceof Val) {
      return Cast.as(o);
    }
    return new Val(o);
  }

  /**
   * As object.
   *
   * @param clazz the clazz
   * @return the object
   */
  public Object asObject(@NonNull Class<?> clazz) {
    if (Map.class.isAssignableFrom(clazz)) {
      return asMap(clazz, String.class, String.class);
    } else if (Collection.class.isAssignableFrom(clazz)) {
      return asCollection(clazz, String.class);
    }
    return Object.class.cast(as(clazz));
  }

  /**
   * Is null.
   *
   * @return True if the object to convert is null
   */
  public boolean isNull() {
    return toConvert == null;
  }

  /**
   * Converts the object to a collection
   *
   * @param <E>             the type parameter
   * @param <T>             the type parameter
   * @param collectionClass The collection to convert to
   * @param genericClass    The class of the item in the collection
   * @return The object as a collection
   */
  public <E, T extends Collection<E>> T asCollection(@NonNull Class<?> collectionClass, @NonNull Class<E> genericClass) {
    return Convert.convert(toConvert, collectionClass, genericClass);
  }

  /**
   * Converts the object to a map
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param mapClass   The map class
   * @param keyClass   The key class
   * @param valueClass The value class
   * @return the object as a map
   */
  public <K, V> Map<K, V> asMap(@NonNull Class<?> mapClass, @NonNull Class<K> keyClass, @NonNull Class<V> valueClass) {
    return Convert.convert(toConvert, mapClass, keyClass, valueClass);
  }

  /**
   * Converts the object to a map
   *
   * @param <K>        the type parameter
   * @param <V>        the type parameter
   * @param keyClass   The key class
   * @param valueClass The value class
   * @return the object as a map
   */
  public <K, V> Map<K, V> asMap(Class<K> keyClass, Class<V> valueClass) {
    return asMap(HashMap.class, keyClass, valueClass);
  }

  /**
   * As string.
   *
   * @return the object as a string
   */
  public String asString() {
    return as(String.class);
  }

  /**
   * As string.
   *
   * @param defaultValue The default value
   * @return the object as a string
   */
  public String asString(String defaultValue) {
    return as(String.class, defaultValue);
  }

  /**
   * As string array.
   *
   * @return the object as a string array
   */
  public String[] asStringArray() {
    return as(String[].class);
  }

  /**
   * Casts the object
   *
   * @param <T> the type of the class
   * @return the object cast as the class type
   */
  @SuppressWarnings("unchecked")
  public <T> T cast() {
    return Cast.as(toConvert);
  }

  /**
   * Get wrapped class.
   *
   * @return the class
   */
  public Class<?> getWrappedClass() {
    return toConvert == null ? null : toConvert.getClass();
  }

  /**
   * Get object.
   *
   * @return The wrapped object
   */
  public Object get() {
    return toConvert;
  }

  /**
   * Converts the underlying object to the given class type.
   *
   * @param <T>   the type parameter
   * @param clazz The class to convert to
   * @return This object as the given type or null if the wrapped object is null
   */
  public <T> T as(Class<T> clazz) {
    return Convert.convert(toConvert, clazz);
  }

  /**
   * Converts the underlying object to the given class type.
   *
   * @param <T>          the type parameter
   * @param clazz        The class to convert to
   * @param defaultValue The value to return if the wrapped value is null or cannot be converted
   * @return This object as the given type or null if the wrapped object is null
   */
  public <T> T as(Class<T> clazz, T defaultValue) {
    if (isNull()) {
      return defaultValue;
    }
    T value = as(clazz);
    if (value == null) {
      return defaultValue;
    }
    return value;
  }

  /**
   * Converts the underlying object to the given class type.
   *
   * @param <T>      the type parameter
   * @param clazz    The class to convert to
   * @param supplier The supplier to use to generate a default value
   * @return This object as the given type or null if the wrapped object is null
   */
  public <T> T asOrElse(Class<T> clazz, @NonNull Supplier<T> supplier) {
    if (isNull()) {
      return supplier.get();
    }
    T value = as(clazz);
    if (value == null) {
      return supplier.get();
    }
    return value;
  }


  /**
   * As boolean.
   *
   * @return the object as a Boolean.
   */
  public Boolean asBoolean() {
    return as(Boolean.class);
  }

  /**
   * As boolean.
   *
   * @param defaultValue the default value
   * @return the object as a Boolean.
   */
  public Boolean asBoolean(Boolean defaultValue) {
    return as(Boolean.class, defaultValue);
  }

  /**
   * As boolean array.
   *
   * @return the object as a Boolean array.
   */
  public Boolean[] asBooleanArray() {
    return as(Boolean[].class);
  }

  /**
   * As boolean value.
   *
   * @return the object as a boolean.
   */
  public boolean asBooleanValue() {
    return as(boolean.class);
  }

  /**
   * As boolean value.
   *
   * @param defaultValue the default value
   * @return the object as a boolean.
   */
  public boolean asBooleanValue(boolean defaultValue) {
    return as(boolean.class, defaultValue);
  }

  /**
   * As boolean value array.
   *
   * @return the object as a boolean array.
   */
  public boolean[] asBooleanValueArray() {
    return as(boolean[].class);
  }

  /**
   * As byte.
   *
   * @return the object as a Byte.
   */
  public Byte asByte() {
    return as(Byte.class);
  }

  /**
   * As byte.
   *
   * @param defaultValue the default value
   * @return the object as a Byte.
   */
  public Byte asByte(Byte defaultValue) {
    return as(Byte.class, defaultValue);
  }

  /**
   * As byte value.
   *
   * @return the object as a byte.
   */
  public byte asByteValue() {
    return as(byte.class);
  }

  /**
   * As byte array.
   *
   * @return the object as a Byte array.
   */
  public Byte[] asByteArray() {
    return as(Byte[].class);
  }

  /**
   * As byte value array.
   *
   * @return the object as a byte array.
   */
  public byte[] asByteValueArray() {
    return as(byte[].class);
  }

  /**
   * As byte value.
   *
   * @param defaultValue the default value
   * @return the object as a byte.
   */
  public byte asByteValue(byte defaultValue) {
    return as(byte.class, defaultValue);
  }

  /**
   * As class.
   *
   * @return the object as a class
   */
  public Class<?> asClass() {
    return as(Class.class);
  }

  /**
   * As class.
   *
   * @param <T>          the type parameter
   * @param defaultValue the default value
   * @return The object as a class
   */
  @SuppressWarnings("unchecked")
  public <T> Class<T> asClass(Class<T> defaultValue) {
    return as(Class.class, defaultValue);
  }

  /**
   * As double.
   *
   * @return The object as a Double
   */
  public Double asDouble() {
    return as(Double.class);
  }

  /**
   * As double.
   *
   * @param defaultValue The default value
   * @return The object as a double
   */
  public Double asDouble(Double defaultValue) {
    return as(Double.class, defaultValue);
  }

  /**
   * As double array.
   *
   * @return The object as a Double array
   */
  public Double[] asDoubleArray() {
    return as(Double[].class);
  }

  /**
   * As double value.
   *
   * @return The object as a double
   */
  public double asDoubleValue() {
    return as(double.class);
  }

  /**
   * As double value.
   *
   * @param defaultValue The default value
   * @return The object as a double
   */
  public double asDoubleValue(double defaultValue) {
    return as(double.class, defaultValue);
  }

  /**
   * As double value array.
   *
   * @return The object as a double array
   */
  public double[] asDoubleValueArray() {
    return as(double[].class);
  }

  /**
   * As float.
   *
   * @return The object as a Float
   */
  public Float asFloat() {
    return as(Float.class);
  }

  /**
   * As float.
   *
   * @param defaultValue The default value
   * @return The object as a float
   */
  public Float asFloat(Float defaultValue) {
    return as(Float.class, defaultValue);
  }

  /**
   * As float array.
   *
   * @return The object as a Float array
   */
  public Float[] asFloatArray() {
    return as(Float[].class);
  }

  /**
   * As float value.
   *
   * @return The object as a float
   */
  public float asFloatValue() {
    return as(float.class);
  }

  /**
   * As float value.
   *
   * @param defaultValue The default value
   * @return The object as a float
   */
  public float asFloatValue(float defaultValue) {
    return as(float.class, defaultValue);
  }

  /**
   * As float value array.
   *
   * @return The object as a float array
   */
  public float[] asFloatValueArray() {
    return as(float[].class);
  }

  /**
   * As integer.
   *
   * @return The object as a Integer
   */
  public Integer asInteger() {
    return as(Integer.class);
  }

  /**
   * As integer.
   *
   * @param defaultValue The default value
   * @return The object as a int
   */
  public Integer asInteger(Integer defaultValue) {
    return as(Integer.class, defaultValue);
  }

  /**
   * As integer array.
   *
   * @return The object as a Integer array
   */
  public Integer[] asIntegerArray() {
    return as(Integer[].class);
  }

  /**
   * As integer value.
   *
   * @return The object as a int
   */
  public int asIntegerValue() {
    return as(int.class);
  }

  /**
   * As integer value.
   *
   * @param defaultValue The default value
   * @return The object as a int
   */
  public int asIntegerValue(int defaultValue) {
    return as(int.class, defaultValue);
  }

  /**
   * As integer value array.
   *
   * @return The object as a int array
   */
  public int[] asIntegerValueArray() {
    return as(int[].class);
  }

  /**
   * As long.
   *
   * @return The object as a Long
   */
  public Long asLong() {
    return as(Long.class);
  }

  /**
   * As long.
   *
   * @param defaultValue The default value
   * @return The object as a long
   */
  public Long asLong(Long defaultValue) {
    return as(Long.class, defaultValue);
  }

  /**
   * As long array.
   *
   * @return The object as a Long array
   */
  public Long[] asLongArray() {
    return as(Long[].class);
  }

  /**
   * As long value.
   *
   * @return The object as a long
   */
  public long asLongValue() {
    return as(long.class);
  }

  /**
   * As long value.
   *
   * @param defaultValue The default value
   * @return The object as a long
   */
  public long asLongValue(long defaultValue) {
    return as(long.class, defaultValue);
  }

  /**
   * As long value array.
   *
   * @return The object as a long array
   */
  public long[] asLongValueArray() {
    return as(long[].class);
  }

  /**
   * As short.
   *
   * @return The object as a Short
   */
  public Short asShort() {
    return as(Short.class);
  }

  /**
   * As short.
   *
   * @param defaultValue The default value
   * @return The object as a short
   */
  public Short asShort(Short defaultValue) {
    return as(Short.class, defaultValue);
  }

  /**
   * As short array.
   *
   * @return The object as a Short array
   */
  public Short[] asShortArray() {
    return as(Short[].class);
  }

  /**
   * As short value.
   *
   * @return The object as a short
   */
  public short asShortValue() {
    return as(short.class);
  }

  /**
   * As short value.
   *
   * @param defaultValue The default value
   * @return The object as a short
   */
  public short asShortValue(short defaultValue) {
    return as(short.class, defaultValue);
  }

  /**
   * As short value array.
   *
   * @return The object as a short array
   */
  public short[] asShortValueArray() {
    return as(short[].class);
  }

  /**
   * As character.
   *
   * @return The object as a Character
   */
  public Character asCharacter() {
    return as(Character.class);
  }

  /**
   * As character.
   *
   * @param defaultValue The default value
   * @return The object as a char
   */
  public Character asCharacter(Character defaultValue) {
    return as(Character.class, defaultValue);
  }

  /**
   * As character array.
   *
   * @return The object as a Character array
   */
  public Character[] asCharacterArray() {
    return as(Character[].class);
  }

  /**
   * As character value.
   *
   * @return The object as a char
   */
  public char asCharacterValue() {
    return as(char.class);
  }

  /**
   * As character value.
   *
   * @param defaultValue The default value
   * @return The object as a char
   */
  public char asCharacterValue(char defaultValue) {
    return as(char.class, defaultValue);
  }

  /**
   * As character value array.
   *
   * @return The object as a char array
   */
  public char[] asCharacterValueArray() {
    return as(char[].class);
  }

  /**
   * Converts the object to a List
   *
   * @param <T>      the type parameter
   * @param itemType The class of the item in the List
   * @return The object as a List
   */
  @SuppressWarnings("unchecked")
  public <T> List<T> asList(Class<T> itemType) {
    return asCollection(List.class, itemType);
  }


  /**
   * Converts the object to resource
   *
   * @return The object as a Resource
   */
  public Resource asResource() {
    return as(Resource.class);
  }

  /**
   * Converts the object to resource with a given default if the conversion results in a null value.
   *
   * @param defaultResource The default value when conversion results in null
   * @return The value as a resource or <code>defaultResource</code> if null
   */
  public Resource asResource(Resource defaultResource) {
    return as(Resource.class, defaultResource);
  }

  /**
   * Converts the object to a Set
   *
   * @param <T>      the type parameter
   * @param itemType The class of the item in the Set
   * @return The object as a Set
   */
  public <T> Set<T> asSet(Class<T> itemType) {
    return asCollection(Set.class, itemType);
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (this == o) {
      return true;
    }
    if (o instanceof Val) {
      return Objects.equals(this.toConvert, ((Val) o).toConvert);
    }
    return Objects.equals(this.toConvert, o);
  }

  @Override
  public String toString() {
    return Objects.toString(toConvert);
  }

  /**
   * Converts an object into an array of objects
   *
   * @param <T>   the type parameter
   * @param clazz The type of the object to create.
   * @return An array of the object
   */
  public <T> T[] asArray(Class<T> clazz) {
    return Cast.as(Convert.convert(toConvert, Array.newInstance(clazz, 0).getClass()));
  }

}//END OF ConvertibleObject
