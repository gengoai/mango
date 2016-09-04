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

import com.davidbracewell.EnumValue;
import com.davidbracewell.io.CSV;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.reflection.ReflectionUtils;
import com.davidbracewell.string.CSVFormatter;
import com.davidbracewell.string.StringUtils;
import com.google.common.primitives.Bytes;
import com.google.common.primitives.Chars;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URL;
import java.nio.file.Path;
import java.sql.Blob;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import static com.davidbracewell.conversion.Cast.as;

/**
 * Functions for converting objects to common types in Java e.g. Class, Object, Character, and String
 *
 * @author David B. Bracewell
 */
public class CommonTypeConverter {

   /**
    * Converts an object to a Class will try to get the class represented in a char sequence. Will only return null when
    * the input is null.
    */
   public static final Function<Object, Class<?>> CLASS = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof Class) {
         return as(input);
      } else if (input instanceof CharSequence) {
         Class<?> clazz = ReflectionUtils.getClassForNameQuietly(input.toString());
         if (clazz != null) {
            return clazz;
         }
      }
      return input.getClass();
   };
   /**
    * Identity function
    */
   public static final Function<Object, Object> OBJECT = input -> input;
   /**
    * The constant DYNAMIC_ENUM.
    */
   public static final Function<Object, EnumValue> DYNAMIC_ENUM = o -> {
      if (o == null) {
         return null;
      } else if (o instanceof EnumValue) {
         return as(o);
      } else if (o instanceof CharSequence) {

         String string = o.toString();
         if (StringUtils.isNullOrBlank(string)) {
            return null;
         }

         int index = string.lastIndexOf('.');
         if (index == -1) {
            return null;
         }
         Class<?> clazz = ReflectionUtils.getClassForNameQuietly(string.substring(0, index));
         if (clazz != null) {
            try {
               return Reflect.onClass(clazz).allowPrivilegedAccess().get(string.substring(index + 1)).get();
            } catch (ReflectionException e) {
               return null;
            }
         }
      }
      return null;
   };
   /**
    * Converts objects to strings. Handles collections, arrays, and varios io related objects (e.g. File, URI,
    * InputStream, etc.)
    */
   @SuppressWarnings("unchecked")
   public static final Function<Object, String> STRING = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof CharSequence) {
         return input.toString();
      } else if (input instanceof char[]) {

         char[] chars = Cast.as(input);
         return new String(chars);

      } else if (input instanceof byte[]) {

         byte[] bytes = Cast.as(input);
         return new String(bytes);

      } else if (input instanceof Character[]) {

         Character[] characters = Cast.as(input);
         return new String(Chars.toArray(Arrays.asList(characters)));

      } else if (input instanceof Byte[]) {

         Byte[] bytes = Cast.as(input);
         return new String(Bytes.toArray(Arrays.asList(bytes)));

      } else if (input instanceof File || input instanceof Path || input instanceof URI || input instanceof URL || input instanceof InputStream || input instanceof Blob || input instanceof Reader) {
         byte[] bytes = PrimitiveArrayConverter.BYTE.apply(input);
         if (bytes != null) {
            return new String(bytes);
         }
      } else if (input.getClass().isArray()) {
         String array = "[";
         for (int i = 0; i < Array.getLength(input); i++) {
            if (i != 0) {
               array += ", ";
            }
            array += Convert.convert(Array.get(input, i), String.class);
         }
         return array + "]";
      } else if (input instanceof Date) {
         return SimpleDateFormat.getDateTimeInstance().format(input);
      } else if (input instanceof Map) {
         StringBuilder builder = new StringBuilder("{");
         CSVFormatter mapFormat = CSV.builder().delimiter('=').formatter();
         Cast.<Map<?, ?>>as(input).forEach((o, o2) -> builder.append(
               mapFormat.format(Convert.convert(o, String.class), Convert.convert(o2, String.class))
                                                                    ));
         return builder.append("}").toString();
      }

      return input.toString();
   };

   private static Logger log = Logger.getLogger(CommonTypeConverter.class);

   public static final Function<Object, Date> JAVA_DATE = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof Date) {
         return as(input);
      } else if (input instanceof Number) {
         return new Date(as(input, Number.class).longValue());
      } else if (input instanceof Calendar) {
         return as(input, Calendar.class).getTime();
      }

      String string = STRING.apply(input);
      if (string != null) {
         string = StringUtils.trim(string.replaceAll(StringUtils.MULTIPLE_WHITESPACE, " "));

         for (DateFormat format : new DateFormat[]{
               SimpleDateFormat.getDateTimeInstance(),
               DateFormat.getDateInstance(DateFormat.SHORT),
               DateFormat.getDateInstance(DateFormat.MEDIUM),
               DateFormat.getDateInstance(DateFormat.LONG),
               DateFormat.getDateInstance(DateFormat.FULL),
               new SimpleDateFormat("yyyy-MM-dd"),
               new SimpleDateFormat("MM/dd/yyyy")}
               ) {
            try {
               return format.parse(string);
            } catch (ParseException e) {
               //no op
            }
         }

      }

      log.fine("Could not convert {0} into java.util.Date", input.getClass());
      return null;
   };
   public static final Function<Object, java.sql.Date> SQL_DATE = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof java.sql.Date) {
         return as(input);
      }

      Date date = JAVA_DATE.apply(input);
      if (date != null) {
         return new java.sql.Date(date.getTime());
      }

      log.fine("Could not convert {0} into java.util.Date", input.getClass());
      return null;
   };
   /**
    * Converts objects to characters
    */
   public static final Function<Object, Character> CHARACTER = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof Character) {
         return as(input);
      } else if (input instanceof Number) {
         return (char) as(input, Number.class).intValue();
      } else if (input instanceof CharSequence) {
         CharSequence sequence = as(input);
         if (sequence.length() == 1) {
            return sequence.charAt(0);
         }
      }

      log.fine("Could not convert {0} into Character.", input.getClass());
      return null;
   };
   /**
    * Converts objects to <code>StringBuilder</code>. It uses the {@link #STRING} function to convert items to Strings.
    */
   public static final Function<Object, StringBuilder> STRING_BUILDER = input -> {
      if (input == null) {
         return null;
      } else if (input instanceof StringBuilder) {
         return as(input);
      }
      String string = STRING.apply(input);
      if (string != null) {
         return new StringBuilder(string);
      }

      log.fine("Could not convert {0} into StringBuilder.", input.getClass());
      return null;
   };

   private CommonTypeConverter() {
      throw new IllegalAccessError();
   }

}//END OF CommonTypeConverter
