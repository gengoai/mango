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

package com.gengoai.reflection;

import com.gengoai.io.resource.Resource;
import com.gengoai.logging.Loggable;
import com.gengoai.logging.Logger;
import com.gengoai.string.Strings;

import java.lang.reflect.Array;

/**
 * Static classes to make reflection easier.
 */
public final class ReflectionUtils implements Loggable {

   private final static Logger log = Logger.getLogger(ReflectionUtils.class);

   private ReflectionUtils() {
      throw new IllegalAccessError();
   }

   /**
    * <p>Creates an object from a string. It first checks if the string is a class name and if so attempts to create an
    * instance or get a singleton instance of the class. Next it checks if the string is class name and a static method
    * or field name and if so invokes the static method or gets the value of the static field. </p>
    *
    * @param string The string containing information about the object to create
    * @return An object or null if the object the string maps to cannot be determined.
    */
   public static Object createObjectFromString(String string) throws Exception {
      if (Strings.isNullOrBlank(string)) {
         return null;
      }

      Class<?> clazz = getClassForNameQuietly(string);
      if (clazz != null) {
         return Reflect.onClass(clazz).create().get();
      }

      int index = string.lastIndexOf(".");
      if (index != -1) {
         String field = string.substring(string.lastIndexOf('.') + 1);
         String cStr = string.substring(0, string.lastIndexOf('.'));
         clazz = getClassForNameQuietly(cStr);

         if (clazz != null) {

            if (Reflect.onClass(clazz).containsField(field)) {
               try {
                  return Reflect.onClass(clazz).getField(field).getReflectValue();
               } catch (ReflectionException e) {
                  //ignore this;
               }
            }

            Reflect r = Reflect.onClass(clazz);
            try {
               return r.getMethod(field).invoke();
            } catch (ReflectionException e) {
               //ignore the error
            }
            return Reflect.onClass(clazz).create(field).get();
         }
      }
      throw new ReflectionException("Unable to create object");
   }

   /**
    * <p> Attempts to determine the Class for a given name in string form. For convenience the String, primitives and
    * Object versions of the primitives can be called with just their name. The following types also have a short hand
    * </p> <ul> <li>List - java.util.List</li> <li>ArrayList - java.util.ArrayList</li> <li>Set - java.util.Set</li>
    * <li>HashSet - java.util.HashSet</li> <li>Map - java.util.Map</li> <li>HashMap - java.util.HashMap</li> </ul> <p>
    * Array versions can be created by appending [] to the end. For example, String[] refers to an array of
    * java.lang.String </p>
    *
    * @param name The class name
    * @return The represented by the name
    * @throws Exception the exception
    */
   public static Class<?> getClassForName(String name) throws Exception {
      if (Strings.isNullOrBlank(name)) {
         throw new ClassNotFoundException();
      }
      name = name.trim();

      boolean isArray = false;
      if (name.endsWith("[]")) {
         isArray = true;
         name = name.substring(0, name.length() - 2);
      } else if (name.startsWith("[L")) {
         isArray = true;
         name = name.substring(2);
      } else if (name.startsWith("[")) {
         isArray = true;
         name = name.substring(1);
      }

      switch (name) {
         case "int":
            return isArray ? int[].class : int.class;
         case "double":
            return isArray ? double[].class : double.class;
         case "float":
            return isArray ? float[].class : float.class;
         case "boolean":
            return isArray ? boolean[].class : boolean.class;
         case "short":
            return isArray ? short[].class : short.class;
         case "byte":
            return isArray ? byte[].class : byte.class;
         case "long":
            return isArray ? long[].class : long.class;
         case "String":
            return isArray ? String[].class : String.class;
         case "Resource":
            return isArray ? Resource[].class : Resource.class;
      }

      Class<?> clazz;
      try {
         clazz = Class.forName(name);
      } catch (Exception e) {
         try {
            clazz = Class.forName("java.lang." + name);
         } catch (Exception e2) {
            try {
               clazz = Class.forName("java.util." + name);
            } catch (Exception e3) {
               try {
                  clazz = Class.forName("com.gengoai." + name);
               } catch (Exception e4) {
                  throw e;
               }
            }
         }
      }

      return isArray ? Array.newInstance(clazz, 0).getClass() : clazz;
   }

   /**
    * <p> Calls {@link #getClassForName(String)}, but suppresses exception to a log warning and returns null instead.
    * Any exceptions are logged to the default logger.(at DEBUG level) and a null is returned. </p>
    *
    * @param name Name of class
    * @return The Class information or null
    */
   public static Class<?> getClassForNameQuietly(String name) {
      if (name == null) {
         return null;
      }
      try {
         return getClassForName(name);
      } catch (Exception | Error cnfe) {
         log.finest(cnfe);
         return null;
      }
   }

}// END OF CLASS ReflectionUtils
