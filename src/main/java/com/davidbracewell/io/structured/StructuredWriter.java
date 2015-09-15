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

import java.io.Closeable;

/**
 * Writes data in a structured format, e.g. xml, json, yaml, etc. Individual implementations
 * may provide extra functionality (e.g. write xml attributes).
 *
 * @author David B. Bracewell
 */
public abstract class StructuredWriter implements Closeable, AutoCloseable {

  /**
   * Begins an Array
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter beginArray() throws StructuredIOException;

  /**
   * Begins an Array
   *
   * @param arrayName The name for the array section
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter beginArray(String arrayName) throws StructuredIOException;

  /**
   * Begins the document
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter beginDocument() throws StructuredIOException;

  /**
   * Begins the object
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter beginObject() throws StructuredIOException;

  /**
   * Begins the object
   *
   * @param objectName The name for the object section
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter beginObject(String objectName) throws StructuredIOException;

  /**
   * Ends an Array
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter endArray() throws StructuredIOException;

  /**
   * Ends the document
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter endDocument() throws StructuredIOException;

  /**
   * Ends the object
   *
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter endObject() throws StructuredIOException;

  /**
   * Flush void.
   *
   * @throws StructuredIOException the structured iO exception
   */
  public abstract void flush() throws StructuredIOException;

  /**
   * Writes a key value pair
   *
   * @param key The key for the value
   * @param value The value
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter writeKeyValue(String key, Object value) throws StructuredIOException;

  /**
   * Serializes an object to the stream. Support is determined by implementation.
   *
   * @param <T>  the type parameter
   * @param object Object to serialize
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract <T> StructuredWriter writeObject(T object) throws StructuredIOException;

  /**
   * Serializes an object to the stream. Support is determined by implementation.
   *
   * @param <T>  the type parameter
   * @param name the name
   * @param object Object to serialize
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract <T> StructuredWriter writeObject(String name, T object) throws StructuredIOException;

  /**
   * Writes a value
   *
   * @param value The value
   * @return This structured writer
   * @throws StructuredIOException Something went wrong reading
   */
  public abstract StructuredWriter writeValue(Object value) throws StructuredIOException;

}//END OF StructuredWriter
