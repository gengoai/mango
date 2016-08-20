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

package com.davidbracewell.reflection;

import com.davidbracewell.Primitives;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Predicate;

/**
 * Static classes to make reflection easier.
 */
public final class ReflectionUtils {

  private final static Logger log = Logger.getLogger(ReflectionUtils.class);


  private ReflectionUtils() {
  }

  /**
   * Gets fields.
   *
   * @param o         the o
   * @param recursive the recursive
   * @return the fields
   */
  public static List<Field> getFields(@NonNull Object o, boolean recursive) {
    return getFields(o.getClass(), recursive);
  }

  /**
   * Gets declared fields.
   *
   * @param o         the o
   * @param recursive the recursive
   * @return the declared fields
   */
  public static List<Field> getDeclaredFields(@NonNull Object o, boolean recursive) {
    return getDeclaredFields(o.getClass(), recursive);
  }

  /**
   * Gets fields.
   *
   * @param clazz     the clazz
   * @param recursive the recursive
   * @return the fields
   */
  public static List<Field> getFields(@NonNull Class<?> clazz, boolean recursive) {
    List<Field> fields = new ArrayList<>();
    do {
      fields.addAll(Arrays.asList(clazz.getFields()));
      clazz = clazz.getSuperclass();
    } while (recursive && clazz != null && clazz != Object.class);
    return fields;
  }

  /**
   * Gets declared fields.
   *
   * @param clazz     the clazz
   * @param recursive the recursive
   * @return the declared fields
   */
  public static List<Field> getDeclaredFields(@NonNull Class<?> clazz, boolean recursive) {
    List<Field> fields = new ArrayList<>();
    do {
      fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
      clazz = clazz.getSuperclass();
    } while (recursive && clazz != null && clazz != Object.class);
    return fields;
  }

  /**
   * Gets field.
   *
   * @param clazz      the clazz
   * @param name       the name
   * @param privileged the privalged
   * @return the field
   */
  public static Field getField(Class<?> clazz, String name, boolean privileged) {
    if (clazz == null || name == null) {
      return null;
    }
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getFields(privileged)
                               .stream()
                               .filter(f -> f.getName().equals(name))
                               .findFirst().orElse(null);
  }

  /**
   * Gets all interfaces that the given object implements
   *
   * @param o The object
   * @return A list of interfaces implemented by the object
   */
  public static List<Class<?>> getAllInterfaces(Object o) {
    return getAllClasses(o, IsInterface.INSTANCE);
  }

  /**
   * Gets all classes associated with (superclasses and interfaces) the given object.
   *
   * @param o The object
   * @return List of classes including the class of the given object that match the given predicate
   */
  public static List<Class<?>> getAllClasses(Object o) {
    return getAllClasses(o, x -> true);
  }

  /**
   * Gets all classes associated with (superclasses and interfaces) the given object that pass the given
   * predicate.
   *
   * @param o         The object
   * @param predicate The predicate that ancestors must pass.
   * @return List of classes including the class of the given object that match the given predicate
   */
  private static List<Class<?>> getAllClasses(Object o, Predicate<? super Class<?>> predicate) {
    if (o == null) {
      return Collections.emptyList();
    }
    List<Class<?>> matches = new ArrayList<>();
    Set<Class<?>> seen = new HashSet<>();
    Queue<Class<?>> queue = new LinkedList<>();
    queue.add(o.getClass());
    while (!queue.isEmpty()) {
      Class<?> clazz = queue.remove();
      if (predicate.test(clazz)) {
        matches.add(clazz);
      }
      seen.add(clazz);
      if (clazz.getSuperclass() != null && !seen.contains(clazz.getSuperclass())) {
        queue.add(clazz.getSuperclass());
      }
      for (Class<?> iface : clazz.getInterfaces()) {
        if (!seen.contains(iface)) {
          queue.add(iface);
        }
      }
    }
    return matches;
  }

  /**
   * <p>Creates an object from a string. It first checks if the string is a class name and if so attempts to create an
   * instance or get a singleton instance of the class. Next it checks if the string is class name and a static method
   * or field name and if so invokes the static method or gets the value of the static field. </p>
   *
   * @param string The string containing information about the object to create
   * @return An object or null if the object the string maps to cannot be determined.
   */
  public static Object createObject(String string) {
    if (StringUtils.isNullOrBlank(string)) {
      return null;
    }
    if (ReflectionUtils.isClassName(string)) {
      try {
        Class<?> clazz = ReflectionUtils.getClassForNameQuietly(string);
        if (ReflectionUtils.isSingleton(clazz)) {
          return ReflectionUtils.getSingletonFor(clazz);
        } else {
          return BeanUtils.getBean(clazz);
        }
      } catch (Exception e) {
        return null;
      }
    }
    int index = string.lastIndexOf(".");
    if (index != -1) {
      String field = string.substring(string.lastIndexOf('.') + 1);
      String cStr = string.substring(0, string.lastIndexOf('.'));
      if (ReflectionUtils.isClassName(cStr)) {
        Class<?> clazz = ReflectionUtils.getClassForNameQuietly(cStr);

        if (Reflect.onClass(clazz).containsField(field)) {
          try {
            return Reflect.onClass(clazz).get(field).get();
          } catch (ReflectionException e) {
            //ignore this;
          }
        }

        if (Reflect.onClass(clazz).containsMethod(field)) {
          try {
            return Reflect.onClass(clazz).invoke(field).get();
          } catch (ReflectionException e) {
            //ignore the error
          }
        }

        try {
          return Reflect.onClass(clazz).create(field).get();
        } catch (ReflectionException e) {
          return null;
        }

      }
    }
    return null;
  }

  /**
   * Determines if a class is a singleton by looking for certain methods on the class.  Looks for a
   * <code>getInstance</code>, <code>getSingleton</code> or <code>createInstance</code> method.
   *
   * @param clazz The class
   * @return True if it appears to be a singleton.
   */
  public static boolean isSingleton(Class<?> clazz) {
    return clazz != null && (Reflect.onClass(clazz).containsMethod("getInstance") || Reflect.onClass(clazz)
                                                                                            .containsMethod(
                                                                                              "getSingleton") || Reflect
      .onClass(clazz)
      .containsMethod("createInstance"));
  }

  /**
   * Determines if a string is a class name.
   *
   * @param string The string
   * @return True if value of the string is a class name.
   */
  public static boolean isClassName(String string) {
    return StringUtils.isNotNullOrBlank(string) && getClassForNameQuietly(string) != null;
  }

  /**
   * Gets the singleton instance of a class. Looks for a <code>getInstance</code>, <code>getSingleton</code> or
   * <code>createInstance</code> method.
   *
   * @param <T> the type parameter
   * @param cz  The class
   * @return The singleton instance or null if the class is not a singleton.
   */
  @SneakyThrows
  public static <T> T getSingletonFor(Class<?> cz) {
    if (Reflect.onClass(cz).containsMethod("getInstance")) {
      return Reflect.onClass(cz).invoke("getInstance").get();
    } else if (Reflect.onClass(cz).containsMethod("getSingleton")) {
      return Reflect.onClass(cz).invoke("getSingleton").get();
    } else if (Reflect.onClass(cz).containsMethod("createInstance")) {
      return Reflect.onClass(cz).invoke("createInstance").get();
    }
    return null;
  }

  /**
   * Determines if two constructors have the same signature.
   *
   * @param c1 Constructor 1
   * @param c2 Constructor 2
   * @return True if they have the same signature, false if not
   */
  public static boolean constructorsEqual(Constructor<?> c1, Constructor<?> c2) {
    return c1.getName().equals(c2.getName()) && typesMatch(c1.getParameterTypes(), c2.getParameterTypes());
  }

  /**
   * Determines if two fields have the same signature (i.e. name)
   *
   * @param f1 Field 1
   * @param f2 Field 2
   * @return True if they have the same signature, false if not
   */
  public static boolean fieldsEqual(Field f1, Field f2) {
    return f1.getName().equals(f2.getName());
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
    return ClassDescriptorCache.getInstance().getClassForName(name);
  }

  /**
   * <p> Calls {@link #getClassForName(String)}, but suppresses exception to a log warning and returns null instead.
   * Any exceptions are logged to the default logger.(at DEBUG level) and a null is returned. </p>
   *
   * @param name Name of class
   * @return The Class information or null
   */
  public static Class<?> getClassForNameQuietly(String name) {
    try {
      return getClassForName(name);
    } catch (Exception cnfe) {
      log.finest(cnfe);
      return null;
    }
  }

  /**
   * Best matching constructor constructor.
   *
   * @param clazz              the clazz
   * @param numberOfParameters the number of parameters
   * @return the constructor
   */
  public static Constructor<?> bestMatchingConstructor(Class<?> clazz, int numberOfParameters) {
    if (numberOfParameters <= 0) {
      try {
        return clazz.getConstructor();
      } catch (NoSuchMethodException e) {
        return null;
      }
    }

    for (Constructor<?> constructor : clazz.getConstructors()) {
      Class<?>[] parameters = constructor.getParameterTypes();
      if (parameters.length == numberOfParameters) {
        return constructor;
      }
    }

    return null;
  }

  /**
   * Finds the best Method match for a given method name and types against a collection of methods.
   *
   * @param methods    The collections of methods to choose from
   * @param methodName The name of the method we want to match
   * @param types      The types that we want to pass to the method.
   * @return Null if there is no match, otherwise the Method which bests fits the given method name and types
   */
  public static Method bestMatchingMethod(Collection<Method> methods, String methodName, Class[] types) {
    if (methods == null || StringUtils.isNullOrBlank(methodName) || types == null) {
      return null;
    }
    for (Method method : methods) {
      if (method.getName().equals(methodName) && typesMatch(method.getParameterTypes(), types)) {
        return method;
      }
    }
    return null;
  }

  /**
   * Gets the types for an array of objects
   *
   * @param args The arguments to get types for
   * @return A 0 sized array if the array is null, otherwise an array of class equalling the classes of the given args
   * or <code>Object.cass</code> if the arg is null.
   */
  public static Class[] getTypes(Object... args) {
    if (args.length == 0) {
      return new Class[0];
    }
    Class[] types = new Class[args.length];
    for (int i = 0; i < args.length; i++) {
      types[i] = args[i] == null ? Object.class : args[i].getClass();
    }
    return types;
  }

  /**
   * Determines if two methods have the same signature.
   *
   * @param m1 Method 1
   * @param m2 Method 2
   * @return True if they have the same signature, false if not
   */
  public static boolean methodsEqual(Method m1, Method m2) {
    return m1.getName().equals(m2.getName()) && typesMatch(m1.getParameterTypes(), m2.getParameterTypes());
  }

  /**
   * Has field boolean.
   *
   * @param clazz     the clazz
   * @param fieldName the field name
   * @return the boolean
   */
  public static boolean hasField(Class<?> clazz, String fieldName) {
    if (clazz == null) {
      return false;
    }
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getFields(false)
                               .stream()
                               .anyMatch(f -> f.getName().equals(fieldName));
  }

  /**
   * Has declared field boolean.
   *
   * @param clazz     the clazz
   * @param fieldName the field name
   * @return the boolean
   */
  public static boolean hasDeclaredField(Class<?> clazz, String fieldName) {
    if (clazz == null) {
      return false;
    }
    try {
      clazz.getDeclaredField(fieldName);
    } catch (NoSuchFieldException e) {
      return false;
    }
    return true;
  }

  /**
   * Determines if the two given arrays of class are compatible with one another.
   *
   * @param c1 array 1
   * @param c2 array 2
   * @return True if all classes are the same or those c1 are assignable from c2, otherwise false
   */
  @SuppressWarnings("unchecked")
  public static boolean typesMatch(Class<?>[] c1, Class[] c2) {
    if (c1.length != c2.length) {
      return false;
    }
    for (int i = 0; i < c1.length; i++) {
      if (!inSameHierarchy(c1[i], c2[i]) && !isConvertible(c1[i], c2[i])) {
        return false;
      }
    }
    return true;
  }

  private static boolean isConvertible(Class<?> c1, Class<?> c2) {
    return Val.class.isAssignableFrom(c1) || Val.class.isAssignableFrom(c2);
  }

  private static boolean inSameHierarchy(Class<?> c1, Class<?> c2) {
    return Primitives.wrap(c1).isAssignableFrom(c2) || Primitives.wrap(c2).isAssignableFrom(c1);
  }

  private enum IsInterface implements Predicate<Class<?>> {
    /**
     * Instance is interface.
     */
    INSTANCE;

    @Override
    public boolean test(Class<?> input) {
      return input != null && input.isInterface();
    }
  }


}// END OF CLASS ReflectionUtils
