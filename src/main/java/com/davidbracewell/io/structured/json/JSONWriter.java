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
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.io.structured.Writeable;
import com.google.common.base.Preconditions;
import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;
import com.google.common.collect.Multimap;
import com.google.gson.stream.JsonWriter;
import lombok.NonNull;

import java.io.IOException;
import java.util.*;

/**
 * The type JSON writer.
 *
 * @author David B. Bracewell
 */
public class JSONWriter implements StructuredWriter {

  private final JsonWriter writer;
  private final boolean isArray;
  private final Stack<ElementType> writeStack = new Stack<>();

  /**
   * Instantiates a new JSON writer.
   *
   * @param resource the resource
   * @throws StructuredIOException the structured iO exception
   */
  public JSONWriter(@NonNull Resource resource) throws IOException {
    this(resource, false);
  }

  /**
   * Instantiates a new JSON writer.
   *
   * @param resource the resource
   * @param isArray  the is array
   * @throws StructuredIOException the structured iO exception
   */
  public JSONWriter(@NonNull Resource resource, boolean isArray) throws IOException {
    this.isArray = isArray;
    this.writer = new JsonWriter(resource.writer());
  }

  @Override
  public JSONWriter beginArray() throws IOException {
    writer.beginArray();
    writeStack.add(ElementType.BEGIN_ARRAY);
    return this;
  }

  @Override
  public JSONWriter beginArray(String arrayName) throws IOException {
    writeName(arrayName);
    writer.beginArray();
    writeStack.add(ElementType.BEGIN_ARRAY);
    return this;
  }

  @Override
  public JSONWriter beginDocument() throws IOException {
    if (isArray) {
      writer.beginArray();
    } else {
      writer.beginObject();
    }
    writeStack.add(ElementType.BEGIN_DOCUMENT);
    return this;
  }

  @Override
  public JSONWriter beginObject() throws IOException {
    writer.beginObject();
    writeStack.add(ElementType.BEGIN_OBJECT);
    return this;
  }

  @Override
  public JSONWriter beginObject(String objectName) throws IOException {
    writeName(objectName);
    writer.beginObject();
    writeStack.add(ElementType.BEGIN_OBJECT);
    return this;
  }

  private void checkAndPop(ElementType required) throws IOException {
    if (writeStack.peek() != required) {
      throw new IOException("Illformed JSON: " + required + " is required, but have the following unclosed: " + writeStack);
    }
    writeStack.pop();
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  @Override
  public JSONWriter endArray() throws IOException {
    checkAndPop(ElementType.BEGIN_ARRAY);
    popIf(ElementType.NAME);
    writer.endArray();
    return this;
  }

  @Override
  public void endDocument() throws IOException {
    checkAndPop(ElementType.BEGIN_DOCUMENT);
    if (isArray) {
      writer.endArray();
    } else {
      writer.endObject();
    }
  }

  @Override
  public JSONWriter endObject() throws IOException {
    checkAndPop(ElementType.BEGIN_OBJECT);
    popIf(ElementType.NAME);
    writer.endObject();
    return this;
  }

  @Override
  public void flush() throws IOException {
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

  @Override
  public JSONWriter writeKeyValue(String key, Object value) throws IOException {
    if (inArray()) {
      throw new StructuredIOException("Cannot write key-value pair inside an array.");
    }

    if (value instanceof Collection) {
      writeCollection(key, Cast.as(value));
    } else if (value instanceof Map) {
      writeMap(key, Cast.as(value));
    } else if (value.getClass().isArray()) {
      writeArray(key, Cast.as(value));
    } else if (value instanceof Multimap) {
      writeMap(key, Cast.<Multimap>as(value).asMap());
    } else if (value instanceof Counter) {
      writeMap(key, Cast.<Counter>as(value).asMap());
    } else if (value instanceof MultiCounter) {
      writeMap(key, Cast.<MultiCounter>as(value).asMap());
    } else if (value instanceof Iterable) {
      writeCollection(key, new AbstractCollection<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return Cast.<Iterable<Object>>as(value).iterator();
        }

        @Override
        public int size() {
          return Iterables.size(Cast.as(value));
        }
      });
    } else if (value instanceof Iterator) {
      writeCollection(key, new AbstractCollection<Object>() {
        @Override
        public Iterator<Object> iterator() {
          return Cast.as(value);
        }

        @Override
        public int size() {
          return Iterators.size(Cast.as(value));
        }
      });
    } else if (value instanceof Writeable) {
      beginObject(key);
      writeKeyValue("class", value.getClass().getName());
      Cast.<Writeable>as(value).write(this);
      endObject();
    } else {
      writeName(key);
      writeValue(value);
    }
    return this;
  }


  private void writeName(String name) throws IOException {
    if (writeStack.peek() == ElementType.NAME) {
      throw new IOException("Cannot write two consecutive names.");
    }
    writer.name(name);
    writeStack.push(ElementType.NAME);
  }

  @Override
  public StructuredWriter writeValue(Object value) throws IOException {
    StructuredWriter.super.writeValue(value);
    popIf(ElementType.NAME);
    return this;
  }

  @Override
  public boolean inArray() {
    return writeStack.peek() == ElementType.BEGIN_ARRAY || (writeStack.peek() == ElementType.BEGIN_DOCUMENT && isArray);
  }

  @Override
  public boolean inObject() {
    return writeStack.peek() == ElementType.BEGIN_OBJECT || (writeStack.peek() == ElementType.BEGIN_DOCUMENT && !isArray);
  }

  @Override
  public StructuredWriter writeNull() throws IOException {
    writer.nullValue();
    return this;
  }

  @Override
  public StructuredWriter writeNumber(Number number) throws IOException {
    writer.value(number);
    return this;
  }

  @Override
  public StructuredWriter writeString(String string) throws IOException {
    writer.value(string);
    return this;
  }

  @Override
  public StructuredWriter writeBoolean(boolean value) throws IOException {
    writer.value(value);
    return this;
  }


}//END OF JSONWriter
