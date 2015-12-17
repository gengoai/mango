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

package com.davidbracewell.io.structured.json;

import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.MultiCounter;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.io.structured.Writeable;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import com.google.gson.stream.JsonWriter;
import lombok.NonNull;

import java.io.IOException;
import java.util.Map;
import java.util.Stack;

/**
 * The type JSON writer.
 *
 * @author David B. Bracewell
 */
public class JSONWriter extends StructuredWriter {

  private final JsonWriter writer;
  private final boolean isArray;
  private final Stack<ElementType> writeStack = new Stack<>();

  /**
   * Instantiates a new JSON writer.
   *
   * @param resource the resource
   * @throws StructuredIOException the structured iO exception
   */
  public JSONWriter(@NonNull Resource resource) throws StructuredIOException {
    this(resource, false);
  }

  /**
   * Instantiates a new JSON writer.
   *
   * @param resource the resource
   * @param isArray  the is array
   * @throws StructuredIOException the structured iO exception
   */
  public JSONWriter(@NonNull Resource resource, boolean isArray) throws StructuredIOException {
    this.isArray = isArray;
    try {
      this.writer = new JsonWriter(resource.writer());
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
  }

  @Override
  public JSONWriter beginArray() throws StructuredIOException {
    try {
      writer.beginArray();
      writeStack.add(ElementType.BEGIN_ARRAY);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter beginArray(String arrayName) throws StructuredIOException {
    try {
      writeName(arrayName);
      writer.beginArray();
      writeStack.add(ElementType.BEGIN_ARRAY);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter beginDocument() throws StructuredIOException {
    try {
      if (isArray) {
        writer.beginArray();
      } else {
        writer.beginObject();
      }
      writeStack.add(ElementType.BEGIN_DOCUMENT);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter beginObject() throws StructuredIOException {
    try {
      writer.beginObject();
      writeStack.add(ElementType.BEGIN_OBJECT);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter beginObject(String objectName) throws StructuredIOException {
    try {
      writeName(objectName);
      writer.beginObject();
      writeStack.add(ElementType.BEGIN_OBJECT);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  private void checkAndPop(ElementType required) throws StructuredIOException {
    if (writeStack.peek() != required) {
      throw new StructuredIOException("Illformed JSON: " + required + " is required, but have the following unclosed: " + writeStack);
    }
    writeStack.pop();
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  @Override
  public JSONWriter endArray() throws StructuredIOException {
    try {
      checkAndPop(ElementType.BEGIN_ARRAY);
      popIf(ElementType.NAME);
      writer.endArray();
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter endDocument() throws StructuredIOException {
    try {
      checkAndPop(ElementType.BEGIN_DOCUMENT);
      if (isArray) {
        writer.endArray();
      } else {
        writer.endObject();
      }
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public JSONWriter endObject() throws StructuredIOException {
    try {
      checkAndPop(ElementType.BEGIN_OBJECT);
      popIf(ElementType.NAME);
      writer.endObject();
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    return this;
  }

  @Override
  public void flush() throws StructuredIOException {
    try {
      writer.flush();
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
  }

  private boolean isInObject() {
    if (writeStack.peek() == ElementType.BEGIN_DOCUMENT && !isArray) {
      return true;
    }
    return writeStack.peek() == ElementType.BEGIN_OBJECT;
  }

  private boolean needsName() {
    return writeStack.peek() != ElementType.BEGIN_ARRAY
      && writeStack.peek() != ElementType.NAME
      && (writeStack.peek() == ElementType.BEGIN_DOCUMENT && !isArray);
  }

  private void popIf(ElementType type) {
    if (writeStack.peek() == type) {
      writeStack.pop();
    }
  }

  /**
   * Sets the output to indented by some number of spaces
   *
   * @param numberOfSpaces The number of spaces to indent by
   * @return This JSONWriter
   */
  public JSONWriter spaceIndent(int numberOfSpaces) {
    Preconditions.checkArgument(numberOfSpaces >= 0);
    String indent = "";
    for (int i = 0; i < numberOfSpaces; i++) {
      indent += " ";
    }
    writer.setIndent(indent);
    return this;
  }

  /**
   * Sets the output to be indented by a tab
   *
   * @return This JSONWriter
   */
  public JSONWriter tabIndent() {
    writer.setIndent("\t");
    return this;
  }

  private boolean needsArray() {
    if (writeStack.peek() == ElementType.BEGIN_DOCUMENT && isArray) {
      return false;
    }
    return writeStack.peek() != ElementType.BEGIN_ARRAY;
  }

  private JSONWriter writeIterable(Iterable<?> iterable) throws StructuredIOException {
    boolean needsArray = needsArray();
    boolean needsName = needsName();
    if (needsArray) {
      if (needsName) {
        beginArray("List");
      } else {
        beginArray();
      }
    }
    for (Object o : iterable) {
      writeValue(o);
    }
    if (needsArray) {
      endArray();
    }
    return this;
  }

  @Override
  public JSONWriter writeKeyValue(String key, Object value) throws StructuredIOException {
    if (inArray()) {
      throw new StructuredIOException("Cannot write key-value pair inside an array.");
    }
    writeName(key);
    writeValue(value);
    return this;
  }

  private JSONWriter writeMap(Map<?, ?> map) throws StructuredIOException {
    boolean needObjectNest = !isInObject();
    boolean needsName = needsName();

    if (needObjectNest) {
      if (needsName) {
        beginObject("Map");
      } else {
        beginObject();
      }
    }

    for (Map.Entry<?, ?> e : map.entrySet()) {
      writeKeyValue(Convert.convert(e.getKey(), String.class), e.getValue());
    }

    if (needObjectNest) {
      endObject();
    }
    return this;
  }

  private void writeName(String name) throws StructuredIOException {
    if (writeStack.peek() == ElementType.NAME) {
      throw new StructuredIOException("Cannot write two consecutive names.");
    }
    try {
      writer.name(name);
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    writeStack.push(ElementType.NAME);
  }

  @Override
  public JSONWriter writeObject(@NonNull Object object) throws StructuredIOException {
    return writeObject(null, object, false);
  }

  @Override
  public JSONWriter writeObject(String name, Object object) throws StructuredIOException {
    return writeObject(name, object, false);
  }

  private boolean inArray() {
    return writeStack.peek() == ElementType.BEGIN_ARRAY || (writeStack.peek() == ElementType.BEGIN_DOCUMENT && isArray);
  }

  private JSONWriter writeObject(String name, Object value, boolean isValue) throws StructuredIOException {
    try {
      if (value == null) {
        if (name != null) writeName(name);
        writer.nullValue();
      } else if (value instanceof Number) {
        if (name != null) writeName(name);
        writer.value((Number) value);
      } else if (value instanceof String) {
        if (name != null) writeName(name);
        writer.value((String) value);
      } else if (value instanceof Boolean) {
        if (name != null) writeName(name);
        writer.value((Boolean) value);
      } else if (value instanceof Map) {
        if (name != null) writeName(name);
        writeMap(Cast.as(value));
      } else if (value instanceof Iterable || value.getClass().isArray()) {
        if (name != null) writeName(name);
        writeIterable(Convert.convert(value, Iterable.class));
      } else if (value instanceof Counter) {
        if (name != null) writeName(name);
        writeMap(Cast.<Counter<?>>as(value).asMap());
      } else if (value instanceof Multimap) {
        if (name != null) writeName(name);
        writeMap(Cast.<Multimap>as(value).asMap());
      } else if (value instanceof MultiCounter) {
        if (name != null) writeName(name);
        return writeMap(Cast.<MultiCounter>as(value).asMap());
      } else if (value instanceof Writeable) {
        if (name != null) beginObject(name);
        writeKeyValue("class", value.getClass().getName());
        Cast.<Writeable>as(value).write(this);
        if (name != null) endObject();
      } else {
        if (name != null) writeName(name);
        writer.value(Convert.convert(value, String.class));
      }
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    popIf(ElementType.NAME);
    return this;
  }

  @Override
  public JSONWriter writeValue(Object value) throws StructuredIOException {
    return writeObject(null, value, true);
  }


}//END OF JSONWriter
