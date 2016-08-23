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

import com.davidbracewell.Primitives;
import com.davidbracewell.collection.list.Lists;
import com.davidbracewell.collection.list.PrimitiveArrayList;
import com.davidbracewell.io.IOUtils;
import com.davidbracewell.logging.Logger;

import java.io.*;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * The type Primitive array converter.
 *
 * @author David B. Bracewell
 */
public final class PrimitiveArrayConverter {


   /**
    * The constant BOOLEAN.
    */
   public static final Function<Object, boolean[]> BOOLEAN = new Function<Object, boolean[]>() {

      @Override
      public boolean[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(boolean.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to boolean[].", input.getClass());
         return null;
      }
   };
   /**
    * The constant BYTE.
    */
   public static final Function<Object, byte[]> BYTE = new Function<Object, byte[]>() {


      @Override
      public byte[] apply(Object o) {
         if (o == null) {
            return null;
         }

         if (o instanceof byte[]) {
            return Cast.as(o);
         } else if (o instanceof Byte[]) {
            return Primitives.toByteArray(Cast.<Byte[]>as(o));
         } else if (o instanceof CharSequence) {
            return o.toString().getBytes();
         } else if (o.getClass().isArray() && o.getClass().getComponentType().isPrimitive()) {
            return Primitives.toByteArray(new PrimitiveArrayList<>(o, Byte.class));
         } else if (o.getClass().isArray()) {
            byte[] bytes = new byte[Array.getLength(o)];
            for (int i = 0; i < bytes.length; i++) {
               bytes[i] = Convert.convert(Array.get(o, i), byte.class);
            }
            return bytes;
         } else if (o instanceof File || o instanceof Path || o instanceof URI || o instanceof URL || o instanceof InputStream || o instanceof Blob) {
            try (InputStream inputStream = IOConverter.INPUT_STREAM.apply(o)) {
               if (inputStream == null) {
                  log.fine("Could not open input stream for {0}", o);
                  return null;
               }
               return IOUtils.readToString(inputStream).getBytes();
            } catch (IOException e) {
               log.fine("Error reading in {0}: {1}", o, e);
               return null;
            }
         } else if (o instanceof Reader) {
            try (Reader reader = Cast.as(o); ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
               int read;
               while ((read = reader.read()) != -1) {
                  baos.write(read);
               }
               return baos.toByteArray();
            } catch (IOException e) {
               log.fine(e);
               return null;
            }
         }

         Iterable<?> iterable = CollectionConverter.ITERABLE.apply(o);
         if (iterable != null) {
            ArrayList<Byte> bytes = new ArrayList<>();
            for (Object comp : iterable) {
               Byte b = Convert.convert(comp, Byte.class);
               if (b != null) {
                  bytes.add(b);
               }
            }
            if (!bytes.isEmpty()) {
               return Primitives.toByteArray(bytes);
            }
         }


         log.fine("Cannot convert {0} to byte[]", o.getClass());
         return null;
      }
   };
   /**
    * The constant CHAR.
    */
   public static final Function<Object, char[]> CHAR = new Function<Object, char[]>() {


      @Override
      public char[] apply(Object o) {
         if (o == null) {
            return null;
         }

         if (o instanceof char[]) {
            return Cast.as(o);
         } else if (o instanceof Character[]) {
            return Primitives.toCharArray(Cast.<Character[]>as(o));
         } else if (o instanceof CharSequence) {
            return o.toString().toCharArray();
         } else if (o.getClass().isArray() && o.getClass().getComponentType().isPrimitive()) {
            return Primitives.toCharArray(new PrimitiveArrayList<>(o, Character.class));
         } else if (o.getClass().isArray()) {
            char[] chars = new char[Array.getLength(o)];
            for (int i = 0; i < chars.length; i++) {
               chars[i] = Convert.convert(Array.get(o, i), char.class);
            }
            return chars;
         } else if (o instanceof File || o instanceof Path || o instanceof URI || o instanceof URL || o instanceof InputStream || o instanceof Blob || o instanceof Reader) {
            try (Reader reader = IOConverter.READER.apply(o)) {
               if (reader == null) {
                  log.fine("Could not open reader for {0}", o);
                  return null;
               }
               return IOUtils.readToString(reader).toCharArray();
            } catch (IOException e) {
               log.fine("Error reading in {0}: {1}", o, e);
               return null;
            }
         }

         Iterable<?> iterable = CollectionConverter.ITERABLE.apply(o);
         if (iterable != null) {
            ArrayList<Character> chars = new ArrayList<>();
            for (Object comp : iterable) {
               Character c = Convert.convert(comp, Character.class);
               if (c != null) {
                  chars.add(c);
               }
            }
            if (!chars.isEmpty()) {
               return Primitives.toCharArray(chars);
            }
         }


         log.fine("Cannot convert {0} to byte[]", o.getClass());
         return null;
      }

   };
   /**
    * The constant DOUBLE.
    */
   public static final Function<Object, double[]> DOUBLE = new Function<Object, double[]>() {

      @Override
      public double[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(double.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to double[].", input.getClass());
         return null;
      }
   };
   /**
    * The constant FLOAT.
    */
   public static final Function<Object, float[]> FLOAT = new Function<Object, float[]>() {

      @Override
      public float[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(float.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to float[].", input.getClass());
         return null;
      }
   };
   /**
    * The constant INT.
    */
   public static final Function<Object, int[]> INT = new Function<Object, int[]>() {

      @Override
      public int[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(int.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to int[].", input.getClass());
         return null;
      }
   };
   /**
    * The constant LONG.
    */
   public static final Function<Object, long[]> LONG = new Function<Object, long[]>() {

      @Override
      public long[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(long.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to long[].", input.getClass());
         return null;
      }
   };
   /**
    * The constant SHORT.
    */
   public static final Function<Object, short[]> SHORT = new Function<Object, short[]>() {

      @Override
      public short[] apply(Object input) {
         if (input == null) {
            return null;
         }
         Object o = convertToArray(short.class, input);
         if (o != null) {
            return Cast.as(o);
         }
         log.fine("Cannot convert {0} to short[].", input.getClass());
         return null;
      }
   };
   private static final Logger log = Logger.getLogger(PrimitiveArrayConverter.class);


   private PrimitiveArrayConverter() {
   }

   private static Object convertToArray(Class<?> itemClass, Object obj) {
      if (obj.getClass().isArray() && obj.getClass().getComponentType().equals(itemClass)) {
         return obj;
      }

      Iterable<?> iterable = CollectionConverter.ITERABLE.apply(obj);
      if (iterable == null) {
         return null;
      }

      List<?> list = Lists.asArrayList(iterable);
      Object array = Array.newInstance(itemClass, list.size());
      for (int i = 0; i < list.size(); i++) {
         Array.set(array, i, Convert.convert(list.get(i), itemClass));
      }

      return array;
   }

}//END OF PrimitiveArrayConverter
