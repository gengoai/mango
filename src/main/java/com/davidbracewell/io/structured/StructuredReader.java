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

import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.CSV;
import com.davidbracewell.string.CSVFormatter;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.io.Closeable;
import java.util.List;
import java.util.Map;

/**
 * Represents a class for reading data in a structured format, e.g. xml, json, yaml, etc. Individual implementations may
 * provide extra functionality (e.g. read xml attributes).
 *
 * @author David B. Bracewell
 */
public abstract class StructuredReader implements Closeable {

  /**
   * Begins an Array
   *
   * @return This array's name
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract String beginArray() throws StructuredIOException;

  /**
   * Begins an array with an expected name.
   *
   * @param expectedName The name that the next array should have
   * @return the structured reader
   * @throws StructuredIOException Something happened reading or the expected name was not found
   */
  public StructuredReader beginArray(String expectedName) throws StructuredIOException {
    String name = beginArray();
    if (expectedName != null && (name == null || !name.equals(expectedName))) {
      throw new StructuredIOException("Expected " + expectedName);
    }
    return this;
  }

  /**
   * Begins the document
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredReader beginDocument() throws StructuredIOException;

  /**
   * Begins the document
   *
   * @return The object's name
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract String beginObject() throws StructuredIOException;

  /**
   * Begins an object with an expected name.
   *
   * @param expectedName The name that the next object should have
   * @return the structured reader
   * @throws StructuredIOException Something happened reading or the expected name was not found
   */
  public StructuredReader beginObject(String expectedName) throws StructuredIOException {
    String name = beginObject();
    if (name == null || !name.equals(expectedName)) {
      throw new StructuredIOException("Expected " + expectedName);
    }
    return this;
  }

  /**
   * Ends an Array
   *
   * @return the structured reader
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredReader endArray() throws StructuredIOException;

  /**
   * Ends the document
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredReader endDocument() throws StructuredIOException;

  /**
   * Ends the document
   *
   * @return the structured reader
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredReader endObject() throws StructuredIOException;

  /**
   * Checks if there is something left to read
   *
   * @return True if there is something in the stream to read
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract boolean hasNext() throws StructuredIOException;

  /**
   * Reads the next array and returns a list of its values
   *
   * @return A list of the values in the array
   * @throws StructuredIOException Something went wrong reading the array
   */
  public final List<Val> nextArray() throws StructuredIOException {
    return nextArray(null);
  }

  /**
   * Reads the next array with an expected name and returns a list of its values
   *
   * @param expectedName The name that the next array should have
   * @return A list of the values in the array
   * @throws StructuredIOException Something went wrong reading the array or the expected name was not found
   */
  public final List<Val> nextArray(String expectedName) throws StructuredIOException {
    beginArray(expectedName);
    List<Val> array = Lists.newArrayList();
    while (peek() != ElementType.END_ARRAY) {
      array.add(nextValue());
    }
    endArray();
    return array;
  }

  /**
   * Next key value tuple 2.
   *
   * @return The next key value Tuple2
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract Tuple2<String, Val> nextKeyValue() throws StructuredIOException;

  /**
   * Reads in a key value with an expected key.
   *
   * @param expectedKey The expected key
   * @return The next key value Tuple2
   * @throws StructuredIOException Something went wrong reading
   */
  public Tuple2<String, Val> nextKeyValue(String expectedKey) throws StructuredIOException {
    Tuple2<String, Val> Tuple2 = nextKeyValue();
    if (expectedKey != null && (Tuple2 == null || !Tuple2.getKey().equals(expectedKey))) {
      throw new StructuredIOException("Expected a Key-Value Tuple2 with named " + expectedKey);
    }
    return Tuple2;
  }

  /**
   * Deserializes an object from the stream.
   *
   * @param <T>   The generic class type
   * @param clazz The class of the object
   * @return A deserialized object
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract <T> T nextObject(Class<T> clazz) throws StructuredIOException;

  /**
   * Next object t.
   *
   * @param <T> the type parameter
   * @return the t
   * @throws StructuredIOException the structured io exception
   */
  public abstract <T> T nextObject() throws StructuredIOException;

  /**
   * Reads the next value
   *
   * @return The next value
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract Val nextValue() throws StructuredIOException;

  /**
   * Examines the type of the next element in the stream without consuming it.
   *
   * @return The type of the next element in the stream
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract ElementType peek() throws StructuredIOException;

  /**
   * Reads an object (but does not beginObject() or endObject()) to a map
   *
   * @return A map of keys and values within an object
   * @throws StructuredIOException Something went wrong reading
   */
  public Map<String, Val> nextMap() throws StructuredIOException {
    Map<String, Val> map = Maps.newHashMap();
    CSVFormatter formatter = CSV.builder().formatter();
    while (peek() != ElementType.END_OBJECT) {
      if (peek() == ElementType.BEGIN_ARRAY) {
        String name = beginArray();
        List<String> array = Lists.newArrayList();
        while (peek() != ElementType.END_ARRAY) {
          if (peek() == ElementType.NAME) {
            array.add(nextKeyValue().getValue().asString());
          } else {
            array.add(nextValue().asString());
          }
        }
        endArray();
        map.put(name, Val.of(formatter.format(array)));
      } else {
        Tuple2<String, Val> keyValue = nextKeyValue();
        map.put(keyValue.getKey(), keyValue.getValue());
      }
    }
    return map;
  }

  /**
   * Skips the next element in the stream
   *
   * @return The type of the element that was skipped
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract ElementType skip() throws StructuredIOException;

}//END OF StructuredReader
