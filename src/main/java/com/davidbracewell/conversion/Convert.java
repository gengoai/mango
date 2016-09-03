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
import com.davidbracewell.logging.Logger;
import com.google.common.base.Defaults;
import lombok.NonNull;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;

/**
 * The type Convert.
 *
 * @author David B. Bracewell
 */
public final class Convert {

   private static final Map<Class<?>, Function<Object, ?>> converters = new ConcurrentHashMap<>();
   private static final Logger log = Logger.getLogger(Convert.class);


   static {
      //------- Number Converters
      register(Integer.class, NumberConverter.INTEGER);
      register(int.class, NumberConverter.INTEGER);
      register(Double.class, NumberConverter.DOUBLE);
      register(double.class, NumberConverter.DOUBLE);
      register(Float.class, NumberConverter.FLOAT);
      register(float.class, NumberConverter.FLOAT);
      register(Short.class, NumberConverter.SHORT);
      register(short.class, NumberConverter.SHORT);
      register(Long.class, NumberConverter.LONG);
      register(long.class, NumberConverter.LONG);
      register(Byte.class, NumberConverter.BYTE);
      register(byte.class, NumberConverter.BYTE);
      register(BigDecimal.class, NumberConverter.BIG_DECIMAL);
      register(BigInteger.class, NumberConverter.BIG_INTEGER);
      register(Number.class, NumberConverter.BIG_DECIMAL);
      register(Boolean.class, NumberConverter.BOOLEAN);
      register(boolean.class, NumberConverter.BOOLEAN);


      //------- IO Converters
      register(File.class, IOConverter.FILE);
      register(Path.class, IOConverter.PATH);
      register(InputStream.class, IOConverter.INPUT_STREAM);
      register(OutputStream.class, IOConverter.OUTPUT_STREAM);
      register(Writer.class, IOConverter.WRITER);
      register(Reader.class, IOConverter.READER);
      register(Charset.class, IOConverter.CHARSET);
      register(URI.class, IOConverter.URI);
      register(URL.class, IOConverter.URL);
      register(Resource.class, IOConverter.RESOUCE);

      //------- Collection Converters
      register(Iterable.class, CollectionConverter.ITERABLE);
      register(List.class, CollectionConverter.COLLECTION(ArrayList.class));
      register(ArrayList.class, CollectionConverter.COLLECTION(ArrayList.class));
      register(LinkedList.class, CollectionConverter.COLLECTION(LinkedList.class));
      register(Set.class, CollectionConverter.COLLECTION(HashSet.class));
      register(TreeSet.class, CollectionConverter.COLLECTION(TreeSet.class));
      register(HashSet.class, CollectionConverter.COLLECTION(HashSet.class));
      register(LinkedHashSet.class, CollectionConverter.COLLECTION(LinkedHashSet.class));
      register(Stack.class, CollectionConverter.COLLECTION(Stack.class));
      register(Queue.class, CollectionConverter.COLLECTION(LinkedList.class));
      register(Deque.class, CollectionConverter.COLLECTION(LinkedList.class));

      //------ Primitive Arrays
      register(byte[].class, PrimitiveArrayConverter.BYTE);
      register(char[].class, PrimitiveArrayConverter.CHAR);
      register(int[].class, PrimitiveArrayConverter.INT);
      register(long[].class, PrimitiveArrayConverter.LONG);
      register(double[].class, PrimitiveArrayConverter.DOUBLE);
      register(float[].class, PrimitiveArrayConverter.FLOAT);
      register(short[].class, PrimitiveArrayConverter.SHORT);
      register(boolean[].class, PrimitiveArrayConverter.BOOLEAN);


      //------ CharSequence
      register(String.class, CommonTypeConverter.STRING);
      register(Character.class, CommonTypeConverter.CHARACTER);
      register(char.class, CommonTypeConverter.CHARACTER);
      register(StringBuilder.class, CommonTypeConverter.STRING_BUILDER);

      register(Date.class, CommonTypeConverter.JAVA_DATE);
      register(java.sql.Date.class, CommonTypeConverter.SQL_DATE);

      register(Object.class, CommonTypeConverter.OBJECT);

      register(Class.class, CommonTypeConverter.CLASS);

   }


   /**
    * Register void.
    *
    * @param clazz     the clazz
    * @param converter the converter
    */
   public static void register(Class<?> clazz, Function<Object, ?> converter) {
      if (clazz == null || converter == null) {
         log.warn("Trying to register either a null class ({0}) or a null converter ({1}). Ignoring!",
                  clazz,
                  converter);
         return;
      }
      converters.put(clazz, converter);
   }

   /**
    * Has converter.
    *
    * @param <T>   the type parameter
    * @param clazz the clazz
    * @return the boolean
    */
   public static <T> boolean hasConverter(Class<T> clazz) {
      if (clazz == null) {
         return false;
      } else if (converters.containsKey(clazz)) {
         return true;
      }
      return clazz.isArray() && converters.containsKey(clazz.getComponentType());
   }

   /**
    * Gets a converter for a given class
    *
    * @param <T>   the type parameter
    * @param clazz The class to convert to
    * @return A converter
    */
   public static <T> Function<Object, T> getConverter(final Class<T> clazz) {
      return object -> Convert.convert(object, clazz);
   }


   /**
    * Convert t.
    *
    * @param <T>         the type parameter
    * @param object      the object
    * @param desiredType the desired type
    * @return the t
    */
   @SuppressWarnings("unchecked")
   public static <T> T convert(Object object, @NonNull Class<T> desiredType) {
      if (object == null) {
         if (desiredType != null && desiredType.isPrimitive()) {
            return Defaults.defaultValue(desiredType);
         }
         return null;
      }

//      if (EnumValue.class.isAssignableFrom(desiredType)) {
//         return Cast.as(CommonTypeConverter.DYNAMIC_ENUM.apply(object));
//      }

      if (object instanceof Val) {
         return convert(Cast.as(object, Val.class).get(), desiredType);
      }

      //Handle Enums
      if (Enum.class.isAssignableFrom(desiredType)) {
         Class<? extends Enum> enumClass = Cast.as(desiredType);
         return Cast.as(new EnumConverter(enumClass).apply(object));
      }

      //Handle Object arrays
      if (desiredType.isArray() && !desiredType.getComponentType().isPrimitive()) {
         Class<?> componentType = desiredType.getComponentType();
         return Cast.as(new ArrayConverter<>(componentType).apply(object));
      }

      if (converters.containsKey(desiredType)) {
         Object output = converters.get(desiredType).apply(object);
         if (output == null && desiredType.isPrimitive()) {
            return Defaults.defaultValue(desiredType);
         }
         return Cast.as(output);
      }

      if (Map.class.isAssignableFrom(desiredType)) {
         return Cast.as(convert(object, desiredType, Object.class, Object.class));
      }


      T result = new NewObjectConverter<>(desiredType).apply(object);
      if (result != null) {
         return result;
      }

      if (desiredType.isAssignableFrom(object.getClass())) {
         return Cast.as(object);
      }

      log.fine("Unable to convert object of type {0} to desired type of {1}", object.getClass(), desiredType);
      return null;
   }

   /**
    * Convert mAP.
    *
    * @param <KEY>      the type parameter
    * @param <VALUE>    the type parameter
    * @param <MAP>      the type parameter
    * @param object     the object
    * @param mapClass   the map class
    * @param keyClass   the key class
    * @param valueClass the value class
    * @return the mAP
    */
   @SuppressWarnings("unchecked")
   public static <KEY, VALUE, MAP extends Map<KEY, VALUE>> MAP convert(Object object, Class<?> mapClass, Class<KEY> keyClass, Class<VALUE> valueClass) {
      return Cast.as(new MapConverter<KEY, VALUE, MAP>(getConverter(keyClass),
                                                       getConverter(valueClass),
                                                       Cast.as(mapClass)
      ).apply(object));
   }


   /**
    * Convert c.
    *
    * @param <T>             the type parameter
    * @param <C>             the type parameter
    * @param object          the object
    * @param collectionClass the collection class
    * @param componentClass  the component class
    * @return the c
    */
   @SuppressWarnings("unchecked")
   public static <T, C extends Collection<T>> C convert(Object object, Class<?> collectionClass, Class<T> componentClass) {
      if (collectionClass == null || !Collection.class.isAssignableFrom(collectionClass)) {
         log.fine("{0} does not extend collection.", collectionClass);
         return null;
      }
      return Cast.as(CollectionConverter.COLLECTION(Cast.<Class<C>>as(collectionClass), componentClass).apply(object));
   }

}//END OF Convert
