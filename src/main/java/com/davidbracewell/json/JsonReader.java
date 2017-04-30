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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.reflection.BeanMap;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.gson.stream.JsonToken;
import lombok.NonNull;

import java.io.Closeable;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.function.Supplier;

import static com.google.gson.stream.JsonToken.*;

/**
 * @author David B. Bracewell
 */
public final class JsonReader implements AutoCloseable, Closeable {

   private final com.google.gson.stream.JsonReader reader;
   private Tuple2<JsonToken, Val> currentValue = Tuple2.of(null, null);
   private JsonToken documentType;
   private Stack<JsonToken> readStack = new Stack<>();

   /**
    * Creates a JSONReader from a reader
    *
    * @param reader The reader
    * @throws IOException Something went wrong reading
    */
   public JsonReader(Reader reader) throws IOException {
      this.reader = new com.google.gson.stream.JsonReader(reader);
      consume();
   }

   /**
    * Creates a JSONReader
    *
    * @param resource The resource to read json from
    * @throws IOException Something went wrong reading
    */
   public JsonReader(Resource resource) throws IOException {
      try {
         this.reader = new com.google.gson.stream.JsonReader(resource.reader());
         consume();
      } catch (IOException e) {
         throw new IOException(e);
      }
   }

   /**
    * Begins an Array
    *
    * @return This array's name
    * @throws IOException Something went wrong reading
    */
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

   /**
    * Begins an array with an expected name.
    *
    * @param expectedName The name that the next array should have
    * @return the structured reader
    * @throws IOException Something happened reading or the expected name was not found
    */
   public JsonReader beginArray(String expectedName) throws IOException {
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
   public JsonReader beginDocument() throws IOException {
      if (currentValue.getKey() != BEGIN_OBJECT && currentValue.getKey() != BEGIN_ARRAY) {
         throw new IOException("Expecting BEGIN_OBJECT or BEGIN_ARRAY, but found " + jsonTokenToStructuredElement(
            null));
      }
      documentType = currentValue.getKey();
      consume();
      return this;
   }

   /**
    * Begins the document
    *
    * @return The object's name
    * @throws IOException Something went wrong reading
    */
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

   /**
    * Begins an object with an expected name.
    *
    * @param expectedName The name that the next object should have
    * @return the structured reader
    * @throws IOException Something happened reading or the expected name was not found
    */
   public JsonReader beginObject(String expectedName) throws IOException {
      String name = beginObject();
      if (!StringUtils.isNullOrBlank(expectedName) && !name.equals(expectedName)) {
         throw new IOException("Expected " + expectedName);
      }
      return this;
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
               double val = reader.nextDouble();
               if (val == (int) val) {
                  currentValue = Tuple2.of(next, Val.of((int) val));
               } else if (val == (long) val) {
                  currentValue = Tuple2.of(next, Val.of((long) val));
               } else {
                  currentValue = Tuple2.of(next, Val.of(val));
               }
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

   /**
    * Ends an Array
    *
    * @return the structured reader
    * @throws IOException Something went wrong reading
    */
   public JsonReader endArray() throws IOException {
      if (currentValue.getKey() != END_ARRAY) {
         throw new IOException("Expecting END_ARRAY, but found " + jsonTokenToStructuredElement(null));
      }
      consume();
      return this;
   }

   /**
    * Ends the document
    *
    * @throws IOException Something went wrong reading
    */
   public void endDocument() throws IOException {
      if (readStack.pop() != documentType) {
         throw new IOException("Premature end document call.");
      }
      close();
   }

   /**
    * Ends the document
    *
    * @return the structured reader
    * @throws IOException Something went wrong reading
    */
   public JsonReader endObject() throws IOException {
      if (currentValue.getKey() != END_OBJECT) {
         throw new IOException("Expecting END_OBJECT, but found " + jsonTokenToStructuredElement(null));
      }
      consume();
      return this;
   }

   /**
    * Gets document type.
    *
    * @return the document type
    */
   public JsonTokenType getDocumentType() {
      return jsonTokenToStructuredElement(documentType);
   }

   /**
    * Checks if there is something left to read
    *
    * @return True if there is something in the stream to read
    * @throws IOException Something went wrong reading
    */
   public boolean hasNext() throws IOException {
      return currentValue.getKey() != null && currentValue.getValue() != null;
   }

   private JsonTokenType jsonTokenToStructuredElement(JsonToken jsonToken) {
      switch (currentValue.getKey()) {
         case NULL:
         case STRING:
         case BOOLEAN:
         case NUMBER:
            return JsonTokenType.VALUE;
         case BEGIN_OBJECT:
            return JsonTokenType.BEGIN_OBJECT;
         case END_OBJECT:
            return JsonTokenType.END_OBJECT;
         case BEGIN_ARRAY:
            return JsonTokenType.BEGIN_ARRAY;
         case END_ARRAY:
            return JsonTokenType.END_ARRAY;
         case END_DOCUMENT:
            return JsonTokenType.END_DOCUMENT;
         case NAME:
            if (jsonToken == BEGIN_ARRAY) {
               return JsonTokenType.BEGIN_ARRAY;
            }
            if (jsonToken == BEGIN_OBJECT) {
               return JsonTokenType.BEGIN_OBJECT;
            }
            return JsonTokenType.NAME;
      }
      return JsonTokenType.OTHER;
   }

   /**
    * Reads the next array and returns a list of its values
    *
    * @return A list of the values in the array
    * @throws IOException Something went wrong reading the array
    */
   public Val[] nextArray() throws IOException {
      return nextArray(StringUtils.EMPTY);
   }

   /**
    * Reads the next array
    *
    * @param <T>         the component type of the array
    * @param elementType class information for the component type
    * @return the array
    * @throws IOException Something went wrong reading the array
    */
   public <T> T[] nextArray(@NonNull Class<T> elementType) throws IOException {
      return nextArray(StringUtils.EMPTY, elementType);
   }

   /**
    * Reads the next array with an expected name and returns a list of its values
    *
    * @param expectedName The name that the next array should have
    * @return A list of the values in the array
    * @throws IOException Something went wrong reading the array or the expected name was not found
    */
   public Val[] nextArray(String expectedName) throws IOException {
      beginArray(expectedName);
      List<Val> array = new ArrayList<>();
      while (peek() != JsonTokenType.END_ARRAY) {
         array.add(nextValue());
      }
      endArray();
      return array.toArray(new Val[array.size()]);
   }

   /**
    * Reads the next array with an expected name and returns a list of its values
    *
    * @param <T>          the component type of the array
    * @param expectedName The name that the next array should have
    * @param elementType  class information for the component type
    * @return the array
    * @throws IOException Something went wrong reading the array
    */
   public <T> T[] nextArray(String expectedName, @NonNull Class<T> elementType) throws IOException {
      beginArray(expectedName);
      List<T> array = new ArrayList<>();
      while (peek() != JsonTokenType.END_ARRAY) {
         array.add(nextValue(elementType));
      }
      endArray();
      return array.toArray(Cast.as(Array.newInstance(elementType, array.size())));
   }

   /**
    * Reads the next array as a collection.
    *
    * @param <T>      the collection type
    * @param supplier the supplier to create a new collection
    * @return the collection containing the items in the next array
    * @throws IOException Something went wrong reading
    */
   public <T extends Collection<Val>> T nextCollection(@NonNull Supplier<T> supplier) throws IOException {
      return nextCollection(supplier, StringUtils.EMPTY);
   }


   /**
    * Reads the next array as a collection.
    *
    * @param <T>         the collection type
    * @param <R>         the component type of the collection
    * @param supplier    the supplier to create a new collection
    * @param elementType The class of the collection component type
    * @return the collection containing the items in the next array
    * @throws IOException Something went wrong reading
    */
   public <T extends Collection<R>, R> T nextCollection(@NonNull Supplier<T> supplier, @NonNull Class<R> elementType) throws IOException {
      return nextCollection(supplier, null, elementType);
   }

   /**
    * Reads the next array as a collection with an expected name and returns a list of its values
    *
    * @param <T>          the collection type
    * @param expectedName The name that the next collection should have
    * @return the collection containing the items in the next array
    * @throws IOException Something went wrong reading
    */
   public <T extends Collection<Val>> T nextCollection(@NonNull Supplier<T> supplier, String expectedName) throws IOException {
      beginArray(expectedName);
      T collection = supplier.get();
      while (peek() != JsonTokenType.END_ARRAY) {
         collection.add(nextValue());
      }
      endArray();
      return collection;
   }

   /**
    * Reads the next array as a collection with an expected name and returns a list of its values
    *
    * @param <T>          the collection type
    * @param <R>          the component type of the collection
    * @param supplier     the supplier to create a new collection
    * @param expectedName The name that the next collection should have
    * @param elementType  The class of the collection component type
    * @return the collection containing the items in the next array
    * @throws IOException Something went wrong reading
    */
   public <T extends Collection<R>, R> T nextCollection(@NonNull Supplier<T> supplier, String expectedName, @NonNull Class<R> elementType) throws IOException {
      beginArray(expectedName);
      T collection = supplier.get();
      while (peek() != JsonTokenType.END_ARRAY) {
         collection.add(nextValue(elementType));
      }
      endArray();
      return collection;
   }

   /**
    * Reads the next key-value pair
    *
    * @return The next key value pair
    * @throws IOException Something went wrong reading
    */
   public Tuple2<String, Val> nextKeyValue() throws IOException {
      if (currentValue.getKey() != NAME) {
         throw new IOException("Expecting NAME, but found " + jsonTokenToStructuredElement(null));
      }
      String name = currentValue.getValue().asString();
      consume();
      return Tuple2.of(name, nextValue());
   }

   /**
    * Reads in a key value with an expected key.
    *
    * @param expectedKey The expected key
    * @return The next key value Tuple2
    * @throws IOException Something went wrong reading
    */
   public Val nextKeyValue(String expectedKey) throws IOException {
      Tuple2<String, Val> Tuple2 = nextKeyValue();
      if (expectedKey != null && (Tuple2 == null || !Tuple2.getKey().equals(expectedKey))) {
         throw new IOException("Expected a Key-Value Tuple2 with named " + expectedKey);
      }
      return Tuple2.getV2();
   }

   /**
    * Reads the next key-value pair with the value being of the given type
    *
    * @param <T>   the value type parameter
    * @param clazz the clazz associated with the value type
    * @return the next key-value pair
    * @throws IOException Something went wrong reading
    */
   public <T> Tuple2<String, T> nextKeyValue(Class<T> clazz) throws IOException {
      if (currentValue.getKey() != NAME) {
         throw new IOException("Expecting NAME, but found " + jsonTokenToStructuredElement(null));
      }
      String name = currentValue.getValue().asString();
      consume();
      return Tuple2.of(name, nextValue(clazz));
   }

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
    * Reads in the next object as a map with an expected name
    *
    * @param expectedName the expected name of the next object
    * @return the map
    * @throws IOException Something went wrong reading
    */
   public Map<String, Val> nextMap(String expectedName) throws IOException {
      boolean ignoreObject = peek() != JsonTokenType.BEGIN_OBJECT && StringUtils.isNullOrBlank(expectedName);
      if (!ignoreObject) beginObject(expectedName);
      Map<String, Val> map = new HashMap<>();
      while (peek() != JsonTokenType.END_OBJECT && peek() != JsonTokenType.END_DOCUMENT) {
         Tuple2<String, Val> kv = nextKeyValue();
         map.put(kv.getKey(), kv.getValue());
      }
      if (!ignoreObject) endObject();
      return map;
   }

   /**
    * Reads in the next object as a map with an expected value type
    *
    * @param <T>       the value type parameter
    * @param valueType the value type class information
    * @return the map
    * @throws IOException Something went wrong reading
    */
   public <T> Map<String, T> nextMap(@NonNull Class<T> valueType) throws IOException {
      return nextMap(null, valueType);
   }

   /**
    * Reads in the next object as a map with an expected object name and value type
    *
    * @param <T>          the value type parameter
    * @param expectedName the expected name of the next object
    * @param valueType    the value type class information
    * @return the map
    * @throws IOException Something went wrong reading
    */
   public <T> Map<String, T> nextMap(String expectedName, @NonNull Class<T> valueType) throws IOException {
      boolean ignoreObject = peek() != JsonTokenType.BEGIN_OBJECT && expectedName == null;
      if (!ignoreObject) beginObject(expectedName);
      Map<String, T> map = new HashMap<>();
      while (peek() != JsonTokenType.END_OBJECT) {
         Tuple2<String, T> kv = nextKeyValue(valueType);
         map.put(kv.getKey(), kv.getValue());
      }
      if (!ignoreObject) endObject();
      return map;
   }

   /**
    * Next simple value val.
    *
    * @return the val
    * @throws IOException the io exception
    */
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

   /**
    * Reads the next value
    *
    * @return The next value
    * @throws IOException Something went wrong reading
    */
   public Val nextValue() throws IOException {
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
    * Reads the next value with given type
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return The next value
    * @throws IOException Something went wrong reading
    */
   public final <T> T nextValue(@NonNull Class<T> clazz) throws IOException {
      if (JsonSerializable.class.isAssignableFrom(clazz)) {
         return readReadable(clazz);
      } else if (peek() == JsonTokenType.BEGIN_OBJECT) {
         Reflect reflected = Reflect.onClass(clazz);
         Optional<Method> staticRead = reflected.getMethods("read", 1).stream()
                                                .filter(m -> JsonReader.class.isAssignableFrom(
                                                   m.getParameterTypes()[0]))
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
   public JsonTokenType peek() throws IOException {
      return jsonTokenToStructuredElement(reader.peek());
   }

   /**
    * Peeks at the next name (key-value pair, array, object).
    *
    * @return The name of the next item
    * @throws IOException Something went wrong reading
    */
   public String peekName() throws IOException {
      if (currentValue.getKey() != NAME) {
         return StringUtils.EMPTY;
      }
      return currentValue.getValue().asString();
   }

   /**
    * Reads a class implementing readable
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return the t
    * @throws IOException the io exception
    */
   protected <T> T readReadable(Class<T> clazz) throws IOException {
      try {
         T object = Reflect.onClass(clazz).allowPrivilegedAccess().create().get();
         JsonSerializable readable = Cast.as(object);
         boolean isArray = JsonArraySerializable.class.isAssignableFrom(clazz);//(object instanceof ArrayValue);
         if (isArray && peek() == JsonTokenType.BEGIN_ARRAY) {
            beginArray();
         } else if (peek() == JsonTokenType.BEGIN_OBJECT) {
            beginObject();
         }
         readable.fromJson(this);
         if (isArray && peek() == JsonTokenType.END_ARRAY) {
            endArray();
         } else if (peek() == JsonTokenType.END_OBJECT) {
            endObject();
         }
         return object;
      } catch (ReflectionException e) {
         throw new IOException(e);
      }
   }

   /**
    * Skips the next element in the stream
    *
    * @return The type of the element that was skipped
    * @throws IOException Something went wrong reading
    */
   public JsonTokenType skip() throws IOException {
      try {
         JsonTokenType element = jsonTokenToStructuredElement(reader.peek());
         JsonToken token = currentValue.getKey();
         if (token == NAME &&
                (element == JsonTokenType.BEGIN_OBJECT || element == JsonTokenType.BEGIN_ARRAY)) {
            reader.skipValue();
         }
         consume();
         return element;
      } catch (IOException e) {
         throw new IOException(e);
      }
   }

}//END OF JSONReader
