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
package com.davidbracewell.json;

import com.davidbracewell.EnumValue;
import com.davidbracewell.collection.Collect;
import com.davidbracewell.collection.counter.Counter;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import lombok.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

/**
 * The type JSON writer.
 *
 * @author David B. Bracewell
 */
public final class JsonWriter implements AutoCloseable, Closeable {
   private final com.google.gson.stream.JsonWriter writer;
   private final Stack<JsonTokenType> writeStack = new Stack<>();
   private boolean isArray;

   /**
    * Instantiates a new JSON writer.
    *
    * @param resource the resource
    * @throws IOException the structured iO exception
    */
   public JsonWriter(@NonNull Resource resource) throws IOException {
      this.writer = new com.google.gson.stream.JsonWriter(resource.writer());
//      this.writer.setHtmlSafe(true);
//      this.writer.setLenient(true);
   }

   /**
    * Begins a new array
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginArray() throws IOException {
      writer.beginArray();
      writeStack.add(JsonTokenType.BEGIN_ARRAY);
      return this;
   }

   /**
    * Begins a new array with given name
    *
    * @param arrayName the name
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginArray(String arrayName) throws IOException {
      writeName(arrayName);
      writer.beginArray();
      writeStack.add(JsonTokenType.BEGIN_ARRAY);
      return this;
   }

   /**
    * Begin document as an object structure
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginDocument() throws IOException {
      return beginDocument(false);
   }

   /**
    * Begin document
    *
    * @param isArray True the document is an array structure, false is an object structure
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginDocument(boolean isArray) throws IOException {
      this.isArray = isArray;
      if (isArray) {
         writer.beginArray();
      } else {
         writer.beginObject();
      }
      writeStack.add(JsonTokenType.BEGIN_DOCUMENT);
      return this;
   }

   /**
    * Begins a new object
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginObject() throws IOException {
      writer.beginObject();
      writeStack.add(JsonTokenType.BEGIN_OBJECT);
      return this;
   }

   /**
    * Begins a new object with a given name
    *
    * @param objectName the name
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter beginObject(String objectName) throws IOException {
      writeName(objectName);
      writer.beginObject();
      writeStack.add(JsonTokenType.BEGIN_OBJECT);
      return this;
   }

   private void checkAndPop(JsonTokenType required) throws IOException {
      if (writeStack.peek() != required) {
         throw new IOException("Ill-formed JSON: " + required + " is required, but have the following unclosed: " + writeStack);
      }
      writeStack.pop();
   }

   @Override
   public void close() throws IOException {
      writer.close();
   }

   /**
    * Ends the current array
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter endArray() throws IOException {
      checkAndPop(JsonTokenType.BEGIN_ARRAY);
      popIf(JsonTokenType.NAME);
      writer.endArray();
      return this;
   }

   /**
    * End document.
    *
    * @throws IOException Something went wrong writing
    */
   public void endDocument() throws IOException {
      checkAndPop(JsonTokenType.BEGIN_DOCUMENT);
      if (isArray) {
         writer.endArray();
      } else {
         writer.endObject();
      }
   }

   /**
    * Ends the current object
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter endObject() throws IOException {
      checkAndPop(JsonTokenType.BEGIN_OBJECT);
      popIf(JsonTokenType.NAME);
      writer.endObject();
      return this;
   }

   /**
    * Flushes the writer.
    *
    * @throws IOException Something went wrong writing
    */
   public void flush() throws IOException {
      writer.flush();
   }

   /**
    * Determines if the writer is currently in an array
    *
    * @return True if in an array, False if not
    */
   public boolean inArray() {
      return writeStack.peek() == JsonTokenType.BEGIN_ARRAY || (writeStack.peek() == JsonTokenType.BEGIN_DOCUMENT && isArray);
   }

   /**
    * Determines if the writer is currently in an object
    *
    * @return True if in an object, False if not
    */
   public boolean inObject() {
      return writeStack.peek() == JsonTokenType.BEGIN_OBJECT || (writeStack.peek() == JsonTokenType.BEGIN_DOCUMENT && !isArray);
   }

   /**
    * Writes a  null value
    *
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter nullValue() throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      popIf(JsonTokenType.NAME);
      writer.nullValue();
      return this;
   }

   private void popIf(JsonTokenType type) {
      if (writeStack.peek() == type) {
         writeStack.pop();
      }
   }

   /**
    * Writes an array with the given key name
    *
    * @param key   the key name for the array
    * @param array the array to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected JsonWriter property(String key, Object[] array) throws IOException {
      return property(key, Arrays.asList(array));
   }

   /**
    * Writes an iterable with the given key name
    *
    * @param key      the key name for the collection
    * @param iterable the iterable to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected JsonWriter property(String key, Iterable<?> iterable) throws IOException {
      Preconditions.checkArgument(!inArray(), "Cannot write a property inside an array.");
      beginArray(key);
      for (Object o : iterable) {
         value(o);
      }
      endArray();
      return this;
   }

   /**
    * Writes an iterator with the given key name
    *
    * @param key      the key name for the collection
    * @param iterator the iterator to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected JsonWriter property(String key, Iterator<?> iterator) throws IOException {
      return property(key, Collect.asIterable(iterator));
   }

   /**
    * Writes a  key value pair
    *
    * @param key   the key
    * @param value the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter property(String key, Object value) throws IOException {
      Preconditions.checkArgument(!inArray(), "Cannot write a property inside an array.");
      writeName(key);
      value(value);
      return this;
   }

   /**
    * Writes a map with the given key name
    *
    * @param key the key name for the map
    * @param map the map to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter property(String key, Map<String, ?> map) throws IOException {
      Preconditions.checkArgument(!inArray(), "Cannot write a property inside an array.");
      writeName(key);
      value(map);
      return this;
   }

   /**
    * Sets the output to indented by some number of spaces
    *
    * @param numberOfSpaces The number of spaces to indent by
    * @return This JSONWriter
    */
   public JsonWriter spaceIndent(int numberOfSpaces) {
      if (numberOfSpaces >= 0) {
         writer.setIndent(StringUtils.repeat(' ', numberOfSpaces));
      }
      return this;
   }

   /**
    * Sets the output to be indented by a tab
    *
    * @return This JSONWriter
    */
   public JsonWriter tabIndent() {
      writer.setIndent("\t");
      return this;
   }

   /**
    * Writes an array
    *
    * @param array the array to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected JsonWriter value(@NonNull Object[] array) throws IOException {
      return value(Arrays.asList(array));
   }

   /**
    * Writes a boolean.
    *
    * @param value the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(boolean value) throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      popIf(JsonTokenType.NAME);
      writer.value(value);
      return this;
   }

   /**
    * Writes an iterable
    *
    * @param value the iterable to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(@NonNull Iterable<?> value) throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      popIf(JsonTokenType.NAME);
      beginArray();
      for (Object o : value) {
         value(o);
      }
      endArray();
      return this;
   }

   /**
    * Writes an Iterator
    *
    * @param value the Iterator to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(@NonNull Iterator<?> value) throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      popIf(JsonTokenType.NAME);
      return value(Collect.asIterable(value));
   }

   /**
    * Writes a map
    *
    * @param map the map to be written
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(Map<String, ?> map) throws IOException {
      if (map == null) {
         nullValue();
      } else {
         boolean inObject = inObject();
         if (!inObject) beginObject();
         for (Map.Entry<String, ?> entry : map.entrySet()) {
            property(entry.getKey(), entry.getValue());
         }
         if (!inObject) endObject();
      }
      popIf(JsonTokenType.NAME);
      return this;
   }

   /**
    * Writes a number
    *
    * @param number the number
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(Number number) throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      popIf(JsonTokenType.NAME);
      writer.value(number);
      return this;
   }

   /**
    * Writes a string
    *
    * @param string the string
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(String string) throws IOException {
      writer.value(string);
      return this;
   }

   /**
    * Writes an array value
    *
    * @param value the value
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   public JsonWriter value(Object value) throws IOException {
      Preconditions.checkArgument(inArray() || writeStack.peek() == JsonTokenType.NAME,
                                  "Expecting an array or a name, but found " + writeStack.peek());
      writeObject(value);
      popIf(JsonTokenType.NAME);
      return this;
   }

   private void writeName(String name) throws IOException {
      if (writeStack.peek() == JsonTokenType.NAME) {
         throw new IOException("Cannot write two consecutive names.");
      }
      writer.name(name);
      writeStack.push(JsonTokenType.NAME);
   }

   /**
    * Serializes an object
    *
    * @param object the object to serialize
    * @return This structured writer
    * @throws IOException Something went wrong writing
    */
   protected JsonWriter writeObject(Object object) throws IOException {
      if (object == null) {
         nullValue();
      } else if (object instanceof JsonSerializable) {
         JsonSerializable structuredSerializable = Cast.as(object);
         if (object instanceof JsonArraySerializable) {
            beginArray();
         } else {
            beginObject();
         }
         structuredSerializable.toJson(this);
         if (object instanceof JsonArraySerializable) {
            endArray();
         } else {
            endObject();
         }
      } else if (object instanceof Number) {
         value(Cast.<Number>as(object));
      } else if (object instanceof String) {
         value(Cast.<String>as(object));
      } else if (object instanceof Boolean) {
         value(Cast.<Boolean>as(object).toString());
      } else if (object instanceof Enum) {
         value(Cast.<Enum>as(object).name());
      } else if (object instanceof EnumValue) {
         value(Cast.<EnumValue>as(object).name());
      } else if (object instanceof Map) {
         value(Cast.<Map<String, ?>>as(object));
      } else if (object.getClass().isArray()) {
         value(Cast.<Object[]>as(object));
      } else if (object instanceof Multimap) {
         value(Cast.<Multimap<String, ?>>as(object).asMap());
      } else if (object instanceof Counter) {
         value(Cast.<Counter<String>>as(object).asMap());
      } else if (object instanceof Iterable) {
         value(Cast.<Iterable>as(object));
      } else if (object instanceof Iterator) {
         value(Cast.<Iterator>as(object));
      } else {
         value(Convert.convert(object, String.class));
      }
      return this;
   }
}//END OF JSONWriter