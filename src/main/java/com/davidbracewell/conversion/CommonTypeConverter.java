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

import com.davidbracewell.DateUtils;
import com.davidbracewell.io.CSV;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.ReflectionUtils;
import com.davidbracewell.string.CSVFormatter;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Function;
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
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Functions for converting objects to common types in Java e.g. Class, Object, Character, and String
 *
 * @author David B. Bracewell
 */
public class CommonTypeConverter {

  public static final Function<Object, Date> JAVA_DATE = new Function<Object, Date>() {

    @Override
    public Date apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Date) {
        return Cast.as(input);
      } else if (input instanceof Number) {
        return new Date(Cast.as(input, Number.class).longValue());
      } else if (input instanceof Calendar) {
        return Cast.as(input, Calendar.class).getTime();
      }

      String string = STRING.apply(input);
      if (string != null) {
        string = StringUtils.trim(string.replaceAll("\\p{Z}+", " "));

        Date date = DateUtils.parseQuietly(string, Locale.getDefault());
        if (date != null) {
          return date;
        }
        date = DateUtils.parseQuietly(string, DateUtils.ISO_8601);
        if (date != null) {
          return date;
        }
        date = DateUtils.parseQuietly(string, DateUtils.US_STANDARD);
        if (date != null) {
          return date;
        }
        date = DateUtils.parseQuietly(string, DateFormat.getTimeInstance());
        if (date != null) {
          return date;
        }
      }

      log.fine("Could not convert {0} into java.util.Date", input.getClass());
      return null;
    }
  };

  public static final Function<Object, java.sql.Date> SQL_DATE = new Function<Object, java.sql.Date>() {

    @Override
    public java.sql.Date apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof java.sql.Date) {
        return Cast.as(input);
      }

      Date date = JAVA_DATE.apply(input);
      if (date != null) {
        return new java.sql.Date(date.getTime());
      }

      log.fine("Could not convert {0} into java.util.Date", input.getClass());
      return null;
    }
  };

  /**
   * Converts objects to characters
   */
  public static final Function<Object, Character> CHARACTER = new Function<Object, Character>() {

    @Override
    public Character apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Character) {
        return Cast.as(input);
      } else if (input instanceof Number) {
        return (char) Cast.as(input, Number.class).intValue();
      } else if (input instanceof CharSequence) {
        CharSequence sequence = Cast.as(input);
        if (sequence.length() == 1) {
          return sequence.charAt(0);
        }
      }

      log.fine("Could not convert {0} into Character.", input.getClass());
      return null;
    }
  };
  /**
   * Converts an object to a Class will try to get the class represented in a char sequence. Will only return null when
   * the input is null.
   */
  public static final Function<Object, Class<?>> CLASS = new Function<Object, Class<?>>() {

    @Override
    public Class<?> apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof Class) {
        return Cast.as(input);
      } else if (input instanceof CharSequence) {
        Class<?> clazz = ReflectionUtils.getClassForNameQuietly(input.toString());
        if (clazz != null) {
          return clazz;
        }
      }
      return input.getClass();
    }
  };
  /**
   * Identity function
   */
  public static final Function<Object, Object> OBJECT = new Function<Object, Object>() {

    @Override
    public Object apply(Object input) {
      return input;
    }
  };
  /**
   * Converts objects to strings. Handles collections, arrays, and varios io related objects (e.g. File, URI,
   * InputStream, etc.)
   */
  public static final Function<Object, String> STRING = new Function<Object, String>() {

    @Override
    public String apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof CharSequence) {
        return input.toString();
      } else if (input instanceof char[]) {
        return new String(Cast.as(input, char[].class));
      } else if (input instanceof byte[]) {
        return new String(Cast.as(input, byte[].class));
      } else if (input instanceof Character[]) {
        return new String(Chars.toArray(Arrays.asList(Cast.as(input, Character[].class))));
      } else if (input instanceof Byte[]) {
        return new String(Bytes.toArray(Arrays.asList(Cast.as(input, Byte[].class))));
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
      } else if (input instanceof Map){
        StringBuilder builder = new StringBuilder("{");
        CSVFormatter mapFormat = CSV.builder().delimiter('=').formatter();
        Cast.<Map<?,?>>as(input).forEach((o, o2) -> builder.append(
          mapFormat.format(Convert.convert(o,String.class), Convert.convert(o2,String.class))
        ));
        return builder.append("}").toString();
      }

      return input.toString();
    }
  };
  /**
   * Converts objects to <code>StringBuilder</code>. It uses the {@link #STRING} function to convert items to Strings.
   */
  public static final Function<Object, StringBuilder> STRING_BUILDER = new Function<Object, StringBuilder>() {

    @Override
    public StringBuilder apply(Object input) {
      if (input == null) {
        return null;
      } else if (input instanceof StringBuilder) {
        return Cast.as(input);
      }
      String string = STRING.apply(input);
      if (string != null) {
        return new StringBuilder(string);
      }

      log.fine("Could not convert {0} into StringBuilder.", input.getClass());
      return null;
    }
  };

  private static Logger log = Logger.getLogger(CommonTypeConverter.class);


}//END OF CommonTypeConverter
