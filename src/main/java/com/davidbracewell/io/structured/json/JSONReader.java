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

import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.tuple.Tuple2;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;

import java.io.IOException;
import java.io.Reader;
import java.util.Stack;

import static com.google.gson.stream.JsonToken.*;

/**
 * @author David B. Bracewell
 */
public class JSONReader extends StructuredReader {

  private final JsonReader reader;
  private Tuple2<JsonToken, Val> currentValue = Tuple2.of(null, null);
  private JsonToken documentType;
  private Stack<JsonToken> readStack = new Stack<>();

  /**
   * Creates a JSONReader from a reader
   *
   * @param reader The reader
   * @throws IOException Something went wrong reading
   */
  public JSONReader(Reader reader) throws IOException {
    this.reader = new JsonReader(reader);
    consume();
  }

  /**
   * Creates a JSONReader
   *
   * @param resource The resource to read json from
   * @throws IOException Something went wrong reading
   */
  public JSONReader(Resource resource) throws IOException {
    try {
      this.reader = new JsonReader(resource.reader());
      consume();
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public ElementType getDocumentType() {
    return jsonTokenToStructuredElement(documentType);
  }

  @Override
  public String beginArray() throws IOException {
    String name = null;
    if (currentValue.getKey() == NAME) {
      name = currentValue.getValue().asString();
      consume();
    }
    if (currentValue.getKey() == BEGIN_ARRAY) {
      consume();
    } else if (readStack.peek() != BEGIN_ARRAY) {
      throw new IOException("Expecting BEGIN_ARRAY, but found " + jsonTokenToStructuredElement(null));
    }
    return name;
  }

  @Override
  public JSONReader beginDocument() throws IOException {
    if (currentValue.getKey() != BEGIN_OBJECT && currentValue.getKey() != BEGIN_ARRAY) {
      throw new IOException("Expecting BEGIN_OBJECT or BEGIN_ARRAY, but found " + jsonTokenToStructuredElement(null));
    }
    documentType = currentValue.getKey();
    consume();
    return this;
  }

  @Override
  public String beginObject() throws IOException {
    String name = null;
    if (currentValue.getKey() == NAME) {
      name = currentValue.getValue().asString();
      consume();
    }
    if (currentValue.getKey() == BEGIN_OBJECT) {
      consume();
    } else if (readStack.peek() != BEGIN_OBJECT) {
      throw new IOException("Expecting BEGIN_OBJECT, but found " + jsonTokenToStructuredElement(null));
    }
    return name;
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  private void consume() throws IOException {
    try {
      JsonToken next = reader.peek();
      switch (next) {
        case END_ARRAY:
          currentValue = Tuple2.of(next, Val.NULL);
          reader.endArray();
          if (readStack.size() == 1 && readStack.peek() == BEGIN_ARRAY) {
            currentValue = Tuple2.of(END_DOCUMENT, Val.NULL);
          } else if (readStack.pop() != BEGIN_ARRAY) {
            throw new IOException("Illformed JSON");
          }
          break;
        case END_DOCUMENT:
          currentValue = Tuple2.of(next, Val.NULL);
          break;
        case END_OBJECT:
          currentValue = Tuple2.of(next, Val.NULL);
          reader.endObject();
          if (readStack.size() == 1 && readStack.peek() == BEGIN_OBJECT) {
            currentValue = Tuple2.of(END_DOCUMENT, Val.NULL);
          } else if (readStack.pop() != BEGIN_OBJECT) {
            throw new IOException("Illformed JSON");
          }
          break;
        case BEGIN_ARRAY:
          currentValue = Tuple2.of(next, Val.NULL);
          reader.beginArray();
          readStack.push(BEGIN_ARRAY);
          break;
        case BEGIN_OBJECT:
          currentValue = Tuple2.of(next, Val.NULL);
          reader.beginObject();
          readStack.push(BEGIN_OBJECT);
          break;
        case NAME:
          currentValue = Tuple2.of(next, Val.of(reader.nextName()));
          break;
        case STRING:
          currentValue = Tuple2.of(next, Val.of(reader.nextString()));
          break;
        case BOOLEAN:
          currentValue = Tuple2.of(next, Val.of(reader.nextBoolean()));
          break;
        case NUMBER:
          currentValue = Tuple2.of(next, Val.of(reader.nextDouble()));
          break;
        case NULL:
          reader.nextNull();
          currentValue = Tuple2.of(next, Val.NULL);
          break;
        default:
          currentValue = Tuple2.of(null, Val.NULL);
      }
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

  @Override
  public StructuredReader endArray() throws IOException {
    if (currentValue.getKey() != END_ARRAY) {
      throw new IOException("Expecting END_ARRAY, but found " + jsonTokenToStructuredElement(null));
    }
    consume();
    return this;
  }

  @Override
  public void endDocument() throws IOException {
    close();
  }

  @Override
  public StructuredReader endObject() throws IOException {
    if (currentValue.getKey() != END_OBJECT) {
      throw new IOException("Expecting END_OBJECT, but found " + jsonTokenToStructuredElement(null));
    }
    consume();
    return this;
  }

  @Override
  public boolean hasNext() throws IOException {
    return currentValue.getKey() != null && currentValue.getValue() != null;
  }

  private ElementType jsonTokenToStructuredElement(JsonToken jsonToken) {
    switch (currentValue.getKey()) {
      case NULL:
      case STRING:
      case BOOLEAN:
      case NUMBER:
        return ElementType.VALUE;
      case BEGIN_OBJECT:
        return ElementType.BEGIN_OBJECT;
      case END_OBJECT:
        return ElementType.END_OBJECT;
      case BEGIN_ARRAY:
        return ElementType.BEGIN_ARRAY;
      case END_ARRAY:
        return ElementType.END_ARRAY;
      case END_DOCUMENT:
        return ElementType.END_DOCUMENT;
      case NAME:
        if (jsonToken == BEGIN_ARRAY) {
          return ElementType.BEGIN_ARRAY;
        }
        if (jsonToken == BEGIN_OBJECT) {
          return ElementType.BEGIN_OBJECT;
        }
        return ElementType.NAME;
    }
    return ElementType.OTHER;
  }

  @Override
  public Tuple2<String, Val> nextKeyValue() throws IOException {
    if (currentValue.getKey() != NAME) {
      throw new IOException("Expecting NAME, but found " + jsonTokenToStructuredElement(null));
    }
    String name = currentValue.getValue().asString();
    consume();
    return Tuple2.of(name, nextValue());
  }

  @Override
  public <T> Tuple2<String, T> nextKeyValue(Class<T> clazz) throws IOException {
    if (currentValue.getKey() != NAME) {
      throw new IOException("Expecting NAME, but found " + jsonTokenToStructuredElement(null));
    }
    String name = currentValue.getValue().asString();
    consume();
    return Tuple2.of(name, nextValue(clazz));
  }

  @Override
  protected Val nextSimpleValue() throws IOException {
    switch (currentValue.getKey()) {
      case NULL:
      case STRING:
      case BOOLEAN:
      case NUMBER:
        Val object = currentValue.v2;
        consume();
        return object;
      default:
        throw new IOException("Expecting VALUE, but found " + jsonTokenToStructuredElement(null));
    }
  }

  @Override
  public ElementType peek() throws IOException {
    return jsonTokenToStructuredElement(reader.peek());
  }

  @Override
  public ElementType skip() throws IOException {
    try {
      ElementType element = jsonTokenToStructuredElement(reader.peek());
      JsonToken token = currentValue.getKey();
      if (token == NAME &&
        (element == ElementType.BEGIN_OBJECT || element == ElementType.BEGIN_ARRAY)) {
        reader.skipValue();
      }
      consume();
      return element;
    } catch (IOException e) {
      throw new IOException(e);
    }
  }

}//END OF JSONReader
