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

package com.davidbracewell.io.structured;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.reflection.BeanMap;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.Supplier;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;

/**
 * Represents a class for reading data in a structured format, e.g. xml, json, yaml, etc. Individual implementations
 * may
 * provide extra functionality (e.g. read xml attributes).
 *
 * @author David B. Bracewell
 */
public abstract class StructuredReader implements Closeable {


  /**
   * Gets document type.
   *
   * @return the document type
   */
  public abstract ElementType getDocumentType();

  /**
   * Begins an Array
   *
   * @return This array's name
   * @throws IOException Something went wrong reading
   */
  public abstract String beginArray() throws IOException;

  /**
   * Begins an array with an expected name.
   *
   * @param expectedName The name that the next array should have
   * @return the structured reader
   * @throws IOException Something happened reading or the expected name was not found
   */
  public final StructuredReader beginArray(String expectedName) throws IOException {
    String name = beginArray();
    if (!StringUtils.isNullOrBlank(expectedName) && (name == null || !name.equals(expectedName))) {
      throw new IOException("Expected " + expectedName);
    }
    return this;
  }

  /**
   * Begins the document
   *
   * @return This structured writer
   * @throws IOException Something went wrong reading
   */
  public abstract StructuredReader beginDocument() throws IOException;

  /**
   * Begins the document
   *
   * @return The object's name
   * @throws IOException Something went wrong reading
   */
  public abstract String beginObject() throws IOException;

  /**
   * Begins an object with an expected name.
   *
   * @param expectedName The name that the next object should have
   * @return the structured reader
   * @throws IOException Something happened reading or the expected name was not found
   */
  public final StructuredReader beginObject(String expectedName) throws IOException {
    String name = beginObject();
    if (!StringUtils.isNullOrBlank(expectedName) && !name.equals(expectedName)) {
      throw new IOException("Expected " + expectedName);
    }
    return this;
  }

  /**
   * Ends an Array
   *
   * @return the structured reader
   * @throws IOException Something went wrong reading
   */
  public abstract StructuredReader endArray() throws IOException;

  /**
   * Ends the document
   *
   * @throws IOException Something went wrong reading
   */
  public abstract void endDocument() throws IOException;

  /**
   * Ends the document
   *
   * @return the structured reader
   * @throws IOException Something went wrong reading
   */
  public abstract StructuredReader endObject() throws IOException;

  /**
   * Checks if there is something left to read
   *
   * @return True if there is something in the stream to read
   * @throws IOException Something went wrong reading
   */
  public abstract boolean hasNext() throws IOException;

  /**
   * Reads the next array and returns a list of its values
   *
   * @return A list of the values in the array
   * @throws IOException Something went wrong reading the array
   */
  public final Val[] nextArray() throws IOException {
    return nextArray(StringUtils.EMPTY);
  }

  /**
   * Next array t [ ].
   *
   * @param <T>         the type parameter
   * @param elementType the element type
   * @return the t [ ]
   * @throws IOException the io exception
   */
  public final <T> T[] nextArray(@NonNull Class<T> elementType) throws IOException {
    return nextArray(StringUtils.EMPTY, elementType);
  }

  /**
   * Reads the next array with an expected name and returns a list of its values
   *
   * @param expectedName The name that the next array should have
   * @return A list of the values in the array
   * @throws IOException Something went wrong reading the array or the expected name was not found
   */
  public final Val[] nextArray(String expectedName) throws IOException {
    beginArray(expectedName);
    List<Val> array = Lists.newArrayList();
    while (peek() != ElementType.END_ARRAY) {
      array.add(nextValue());
    }
    endArray();
    return array.toArray(new Val[array.size()]);
  }

  /**
   * Next array t [ ].
   *
   * @param <T>          the type parameter
   * @param expectedName the expected name
   * @param elementType  the element type
   * @return the t [ ]
   * @throws IOException the io exception
   */
  public final <T> T[] nextArray(String expectedName, @NonNull Class<T> elementType) throws IOException {
    beginArray(expectedName);
    List<T> array = Lists.newArrayList();
    while (peek() != ElementType.END_ARRAY) {
      array.add(nextValue(elementType));
    }
    endArray();
    return array.toArray(Cast.as(Array.newInstance(elementType, array.size())));
  }

  /**
   * Next collection t.
   *
   * @param <T>      the type parameter
   * @param supplier the supplier
   * @return the t
   * @throws IOException the io exception
   */
  public <T extends Collection<Val>> T nextCollection(@NonNull Supplier<T> supplier) throws IOException {
    return nextCollection(supplier, StringUtils.EMPTY);
  }

  /**
   * Next collection t.
   *
   * @param <T>         the type parameter
   * @param <R>         the type parameter
   * @param supplier    the supplier
   * @param elementType the element type
   * @return the t
   * @throws IOException the io exception
   */
  public <T extends Collection<R>, R> T nextCollection(@NonNull Supplier<T> supplier, @NonNull Class<R> elementType) throws IOException {
    return nextCollection(supplier, null, elementType);
  }

  /**
   * Next collection t.
   *
   * @param <T>          the type parameter
   * @param supplier     the supplier
   * @param expectedName the expected name
   * @return the t
   * @throws IOException the io exception
   */
  public <T extends Collection<Val>> T nextCollection(@NonNull Supplier<T> supplier, String expectedName) throws IOException {
    beginArray(expectedName);
    T collection = supplier.get();
    while (peek() != ElementType.END_ARRAY) {
      collection.add(nextValue());
    }
    endArray();
    return collection;
  }

  /**
   * Next collection t.
   *
   * @param <T>          the type parameter
   * @param <R>          the type parameter
   * @param supplier     the supplier
   * @param expectedName the expected name
   * @param elementType  the element type
   * @return the t
   * @throws IOException the io exception
   */
  public <T extends Collection<R>, R> T nextCollection(@NonNull Supplier<T> supplier, String expectedName, @NonNull Class<R> elementType) throws IOException {
    beginArray(expectedName);
    T collection = supplier.get();
    while (peek() != ElementType.END_ARRAY) {
      collection.add(nextValue(elementType));
    }
    endArray();
    return collection;
  }

  /**
   * Next key value tuple 2.
   *
   * @return The next key value Tuple2
   * @throws IOException Something went wrong reading
   */
  public abstract Tuple2<String, Val> nextKeyValue() throws IOException;

  /**
   * Next key value tuple 2.
   *
   * @param <T>   the type parameter
   * @param clazz the clazz
   * @return the tuple 2
   * @throws IOException the io exception
   */
  public abstract <T> Tuple2<String, T> nextKeyValue(Class<T> clazz) throws IOException;

  /**
   * Reads in a key value with an expected key.
   *
   * @param expectedKey The expected key
   * @return The next key value Tuple2
   * @throws IOException Something went wrong reading
   */
  public final Val nextKeyValue(String expectedKey) throws IOException {
    Tuple2<String, Val> Tuple2 = nextKeyValue();
    if (expectedKey != null && (Tuple2 == null || !Tuple2.getKey().equals(expectedKey))) {
      throw new IOException("Expected a Key-Value Tuple2 with named " + expectedKey);
    }
    return Tuple2.getV2();
  }

  /**
   * Next key value t.
   *
   * @param <T>         the type parameter
   * @param expectedKey the expected key
   * @param clazz       the clazz
   * @return the t
   * @throws IOException the io exception
   */
  public final <T> T nextKeyValue(String expectedKey, Class<T> clazz) throws IOException {
    Tuple2<String, T> Tuple2 = nextKeyValue(clazz);
    if (expectedKey != null && (Tuple2 == null || !Tuple2.getKey().equals(expectedKey))) {
      throw new IOException("Expected a Key-Value Tuple2 with named " + expectedKey);
    }
    return Tuple2.getV2();
  }


  /**
   * Reads the next value
   *
   * @return The next value
   * @throws IOException Something went wrong reading
   */
  public final Val nextValue() throws IOException {
    switch (peek()) {
      case BEGIN_ARRAY:
        return Val.of(nextCollection(ArrayList::new));
      case BEGIN_OBJECT:
        return Val.of(nextMap());
      case NAME:
        return nextKeyValue().getV2();
      default:
        return nextSimpleValue();
    }
  }

  /**
   * Next simple value val.
   *
   * @return the val
   * @throws IOException the io exception
   */
  protected abstract Val nextSimpleValue() throws IOException;

  private <T> T readReadable(Class<T> clazz) throws IOException {
    try {
      T object = Reflect.onClass(clazz).create().get();
      boolean objectWrapped = peek() == ElementType.BEGIN_OBJECT;
      if (objectWrapped) beginObject();
      Cast.<Readable>as(object).read(this);
      if (objectWrapped) endObject();
      return object;
    } catch (ReflectionException e) {
      throw new IOException(e);
    }
  }

  /**
   * Reads the next value
   *
   * @param <T>   the type parameter
   * @param clazz the clazz
   * @return The next value
   * @throws IOException Something went wrong reading
   */
  public final <T> T nextValue(@NonNull Class<T> clazz) throws IOException {
    if (Readable.class.isAssignableFrom(clazz)) {
      return readReadable(clazz);
    } else if (peek() == ElementType.BEGIN_OBJECT) {
      Reflect reflected = Reflect.onClass(clazz);
      Optional<Method> staticRead = reflected.getMethods("read", 1).stream()
        .filter(m -> StructuredReader.class.isAssignableFrom(m.getParameterTypes()[0]))
        .filter(m -> Modifier.isStatic(m.getModifiers()))
        .findFirst();

      if (staticRead.isPresent()) {
        try {
          beginObject();
          T result = Cast.as(staticRead.get().invoke(null, this));
          endObject();
          return result;
        } catch (IllegalAccessException | InvocationTargetException e) {
          throw new IOException(e);
        }
      }

      try {
        T object = Reflect.onClass(clazz).create().get();
        beginObject();
        new BeanMap(object).putAll(nextMap());
        endObject();
        return object;
      } catch (ReflectionException e) {
        throw new IOException(e);
      }
    }
    return nextValue().as(clazz);
  }

  /**
   * Examines the type of the next element in the stream without consuming it.
   *
   * @return The type of the next element in the stream
   * @throws IOException Something went wrong reading
   */
  public abstract ElementType peek() throws IOException;

  /**
   * Reads an object (but does not beginObject() or endObject()) to a map
   *
   * @return A map of keys and values within an object
   * @throws IOException Something went wrong reading
   */
  public Map<String, Val> nextMap() throws IOException {
    return nextMap(StringUtils.EMPTY);
  }

  /**
   * Next map map.
   *
   * @param expectedName the expected name
   * @return the map
   * @throws IOException the io exception
   */
  public Map<String, Val> nextMap(String expectedName) throws IOException {
    boolean ignoreObject = peek() != ElementType.BEGIN_OBJECT && StringUtils.isNullOrBlank(expectedName);
    if (!ignoreObject) beginObject(expectedName);
    Map<String, Val> map = Maps.newHashMap();
    while (peek() != ElementType.END_OBJECT && peek() != ElementType.END_DOCUMENT) {
      Tuple2<String, Val> kv = nextKeyValue();
      map.put(kv.getKey(), kv.getValue());
    }
    if (!ignoreObject) endObject();
    return map;
  }

  /**
   * Next map map.
   *
   * @param <T>       the type parameter
   * @param valueType the value type
   * @return the map
   * @throws IOException the io exception
   */
  public <T> Map<String, T> nextMap(@NonNull Class<T> valueType) throws IOException {
    return nextMap(null, valueType);
  }

  /**
   * Next map map.
   *
   * @param <T>          the type parameter
   * @param expectedName the expected name
   * @param valueType    the value type
   * @return the map
   * @throws IOException the io exception
   */
  public <T> Map<String, T> nextMap(String expectedName, @NonNull Class<T> valueType) throws IOException {
    boolean ignoreObject = peek() != ElementType.BEGIN_OBJECT && expectedName == null;
    if (!ignoreObject) beginObject(expectedName);
    Map<String, T> map = Maps.newHashMap();
    while (peek() != ElementType.END_OBJECT) {
      Tuple2<String, T> kv = nextKeyValue(valueType);
      map.put(kv.getKey(), kv.getValue());
    }
    if (!ignoreObject) endObject();
    return map;
  }

  /**
   * Skips the next element in the stream
   *
   * @return The type of the element that was skipped
   * @throws IOException Something went wrong reading
   */
  public abstract ElementType skip() throws IOException;

}//END OF StructuredReader
