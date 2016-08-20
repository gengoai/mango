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

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredWriter;
import com.google.gson.stream.JsonWriter;
import lombok.NonNull;

import java.io.IOException;
import java.util.Stack;

import static com.davidbracewell.Validations.validateState;

/**
 * The type JSON writer.
 *
 * @author David B. Bracewell
 */
public class JSONWriter extends StructuredWriter {

  private final JsonWriter writer;
  private final Stack<ElementType> writeStack = new Stack<>();
  private boolean isArray;

  /**
   * Instantiates a new JSON writer.
   *
   * @param resource the resource
   * @throws IOException the structured iO exception
   */
  public JSONWriter(@NonNull Resource resource) throws IOException {
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
  public JSONWriter beginDocument(boolean isArray) throws IOException {
    this.isArray = isArray;
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
      throw new IOException("Ill-formed JSON: " + required + " is required, but have the following unclosed: " + writeStack);
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
    writer.flush();
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
    if (numberOfSpaces >= 0) {
      String indent = "";
      for (int i = 0; i < numberOfSpaces; i++) {
        indent += " ";
      }
      writer.setIndent(indent);
    }
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

  @Override
  public JSONWriter writeKeyValue(String key, Object value) throws IOException {
    if (inArray()) {
      throw new IOException("Cannot write key-value pair inside an array.");
    }
    writeName(key);
    writeValue(value);
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
    if (!inArray()) {
      validateState(writeStack.peek() == ElementType.NAME,
                    "Expecting an array or a name, but found " + writeStack.peek()
                   );
    }
    super.writeValue(value);
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
  protected StructuredWriter writeNull() throws IOException {
    writer.nullValue();
    return this;
  }

  @Override
  protected StructuredWriter writeNumber(Number number) throws IOException {
    writer.value(number);
    return this;
  }

  @Override
  protected StructuredWriter writeString(String string) throws IOException {
    writer.value(string);
    return this;
  }

  @Override
  protected StructuredWriter writeBoolean(boolean value) throws IOException {
    writer.value(value);
    return this;
  }


}//END OF JSONWriter
