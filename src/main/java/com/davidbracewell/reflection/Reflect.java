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

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Defaults;
import lombok.NonNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <code>Reflect</code> is a class that allows fluent use of reflection
 *
 * @author David B. Bracewell
 */
public class Reflect {

  private final Object object;
  private final Class<?> clazz;
  private final boolean accessAll;

  private Reflect(Object object, Class<?> clazz) {
    this(object, clazz, false);
  }

  private Reflect(Object object, Class<?> clazz, boolean accessAll) {
    this.object = object;
    this.clazz = clazz;
    this.accessAll = accessAll;
  }

  /**
   * Creates an instance of Reflect associated with an object
   *
   * @param object The object for reflection
   * @return The Reflect object
   */
  public static Reflect onObject(Object object) {
    if (object == null) {
      return new Reflect(null, null);
    }
    return new Reflect(object, object.getClass());
  }

  /**
   * Creates an instance of Reflect associated with a class
   *
   * @param clazz The class for reflection
   * @return The Reflect object
   */
  public static Reflect onClass(@NonNull Class<?> clazz) {
    return new Reflect(null, clazz);
  }

  /**
   * Creates an instance of Reflect associated with a class
   *
   * @param clazz The class for reflection as string
   * @return The Reflect object
   * @throws ClassNotFoundException the class not found exception
   */
  public static Reflect onClass(String clazz) throws Exception {
    return new Reflect(null, ReflectionUtils.getClassForName(clazz));
  }

  /**
   * Creates an instance Reflect for an object that gets constructed using the supplied constructor and arguments.
   *
   * @param constructor           The constructor to call
   * @param allowPrivilegedAccess Allows access to all methods on the object or class
   * @param args                  The arguments to pass to the constructor
   * @return A Reflect wrapper around the constructed object
   * @throws ReflectionException Something went wrong constructing the object
   */
  public static Reflect on(@NonNull Constructor constructor, boolean allowPrivilegedAccess, Object... args) throws ReflectionException {
    boolean accessible = constructor.isAccessible();
    try {
      if (!accessible && allowPrivilegedAccess) {
        constructor.setAccessible(true);
      }
      if (args != null) {
        Class<?>[] parameterTypes = constructor.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
          args[i] = convertValueType(args[i], parameterTypes[i]);
        }
      }
      Object object = constructor.newInstance(args);
      return new Reflect(object, object.getClass(), allowPrivilegedAccess);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
      throw new ReflectionException(e);
    } finally {
      if (!accessible && allowPrivilegedAccess) {
        constructor.setAccessible(false);
      }
    }
  }

  /**
   * Creates an instance Reflect for an object returned by invoking a method.
   *
   * @param method                The method to call
   * @param owner                 The object to call the method on (null for static)
   * @param allowPrivilegedAccess Allows access to all methods on the object or class
   * @param args                  The arguments to pass to the constructor
   * @return A Reflect wrapper around the constructed object
   * @throws ReflectionException Something went wrong invoking the method
   */
  public static Reflect on(@NonNull Method method, Object owner, boolean allowPrivilegedAccess, Object... args) throws ReflectionException {
    boolean accessible = method.isAccessible();
    try {
      if (!accessible && allowPrivilegedAccess) {
        method.setAccessible(true);
      }
      Object[] convertedArgs = null;
      if (args != null) {
        convertedArgs = new Object[args.length];
        Class<?>[] parameterTypes = method.getParameterTypes();
        for (int i = 0; i < args.length; i++) {
          convertedArgs[i] = convertValueType(args[i], parameterTypes[i]);
        }
      }
      Object object = method.invoke(owner, convertedArgs);
      return new Reflect(object, object == null ? Void.class : object.getClass(), allowPrivilegedAccess);
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new ReflectionException(e);
    } finally {
      if (!accessible && allowPrivilegedAccess) {
        method.setAccessible(false);
      }
    }
  }

  private static Object convertValueType(Object value, Class<?> toClass) {
    if (value == null) {
      return Defaults.defaultValue(toClass);
    }
    if (Val.class.isAssignableFrom(toClass)) {
      return Cast.as(value, Val.class).as(toClass);
    }
    if (toClass.isAssignableFrom(value.getClass())) {
      return value;
    }
    Object out = Convert.convert(value, toClass);
    return out == null ? value : out;
  }

  /**
   * Determines if a field with the given name is associated with the class
   *
   * @param name The field name
   * @return True if there is a field with the given name
   */
  public boolean containsField(String name) {
    if (StringUtils.isNullOrBlank(name)) {
      return false;
    }
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getFields(accessAll)
                               .stream()
                               .anyMatch(f -> f.getName().equals(name));
  }

  /**
   * Allow privileged access.
   *
   * @return An new Reflect object based on the current one that allows privileged access to methods, fields, and
   * constructors.
   */
  public Reflect allowPrivilegedAccess() {
    return new Reflect(object, clazz, true);
  }

  /**
   * Allow public access.
   *
   * @return An new Reflect object based on the current one that allows public access to methods, fields, and
   * constructors.
   */
  public Reflect allowPublicAccess() {
    return new Reflect(object, clazz, false);
  }

  /**
   * Get t.
   *
   * @param <T> The object type
   * @return The object used in reflection or null if there is not one
   */
  public <T> T get() {
    return Cast.as(object);
  }

  /**
   * Gets reflected class.
   *
   * @return Class information for what is being reflected on
   */
  public Class<?> getReflectedClass() {
    return clazz;
  }

  /**
   * Creates an instance of the class being reflected using the no-argument constructor.
   *
   * @return A <code>Reflect</code> object to do further reflection
   * @throws ReflectionException Something went wrong constructing the object
   */
  public Reflect create() throws ReflectionException {
    try {
      return on(clazz.getConstructor(), accessAll);
    } catch (NoSuchMethodException e) {
      if (accessAll) {
        try {
          return on(clazz.getDeclaredConstructor(), accessAll);
        } catch (NoSuchMethodException e2) {
          throw new ReflectionException(e2);
        }
      }
      throw new ReflectionException(e);
    }
  }

  /**
   * Creates an instance of the class being reflected using the most specific constructor available.
   *
   * @param args The arguments to the constructor.
   * @return A <code>Reflect</code> object to do further reflection
   * @throws ReflectionException Something went wrong constructing the object
   */
  public Reflect create(Object... args) throws ReflectionException {
    return create(ReflectionUtils.getTypes(args), args);
  }

  /**
   * Create reflect.
   *
   * @param types the types
   * @param args  the args
   * @return the reflect
   * @throws ReflectionException the reflection exception
   */
  public Reflect create(Class[] types, Object... args) throws ReflectionException {
    try {
      return on(clazz.getConstructor(types), accessAll, args);
    } catch (NoSuchMethodException e) {
      for (Constructor constructor : getConstructors()) {
        if (ReflectionUtils.typesMatch(constructor.getParameterTypes(), types)) {
          return on(constructor, accessAll, args);
        }
      }
      throw new ReflectionException(e);
    }
  }

  /**
   * Gets constructors.
   *
   * @return The set of constructors for the object which is a combination of and if <code>allowPrivilegedAccess</code>
   * was called.
   */
  public Set<Constructor<?>> getConstructors() {
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getConstructors(accessAll);
  }

  /**
   * Gets methods.
   *
   * @return The set of methods for the object which is a combination of and if <code>allowPrivilegedAccess</code> was
   * called.
   */
  public Set<Method> getMethods() {
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getMethods(accessAll);
  }

  public Method getMethod(String name) {
    if (StringUtils.isNullOrBlank(name)) {
      return null;
    }
    return getMethods().stream().filter(method -> method.getName().equals(name)).findFirst().orElse(null);
  }

  /**
   * Contains method.
   *
   * @param name the name
   * @return the boolean
   */
  public boolean containsMethod(final String name) {
    if (StringUtils.isNullOrBlank(name)) {
      return false;
    }
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getMethods(accessAll)
                               .stream()
                               .anyMatch(f -> f.getName().equals(name));
  }

  /**
   * Gets methods.
   *
   * @param name      the name
   * @param numParams the num params
   * @return the methods
   */
  public Set<Method> getMethods(final String name, final int numParams) {
    if (StringUtils.isNullOrBlank(name) || numParams < 0) {
      return Collections.emptySet();
    }
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getMethods(accessAll)
                               .stream()
                               .filter(f -> f.getName().equals(name) && f.getParameterCount() == numParams)
                               .collect(Collectors.toSet());
  }

  /**
   * Invokes a method with a given name and arguments with the most specific method possible
   *
   * @param methodName The name of the method
   * @param args       The arguments to the method
   * @return A Reflect object representing the results
   * @throws ReflectionException Something went wrong invoking the method
   */
  public Reflect invoke(String methodName, Object... args) throws ReflectionException {

    Class[] types = ReflectionUtils.getTypes(args);
    try {
      return on(clazz.getMethod(methodName, types), object, accessAll, args);
    } catch (NoSuchMethodException e) {
      Method method = ReflectionUtils.bestMatchingMethod(getMethods(), methodName, types);
      if (method != null) {
        return on(method, object, accessAll, args);
      }
      throw new ReflectionException(e);
    }

  }

  /**
   * Sets the value of a field. Will perform conversion on the value if needed.
   *
   * @param fieldName The name of the field to set
   * @param value     The value to set the field to
   * @return This instance of Reflect
   * @throws ReflectionException Something went wrong setting the value of field
   */
  public Reflect set(String fieldName, Object value) throws ReflectionException {
    Field field = null;
    boolean hasAccess = false;

    try {
      if (ReflectionUtils.hasField(clazz, fieldName)) {
        field = clazz.getField(fieldName);
      } else if (accessAll && ReflectionUtils.hasDeclaredField(clazz, fieldName)) {
        field = clazz.getDeclaredField(fieldName);
      } else if (ReflectionUtils.hasField(clazz.getSuperclass(), fieldName)) {
        field = clazz.getSuperclass().getField(fieldName);
      } else if (accessAll && ReflectionUtils.hasDeclaredField(clazz.getSuperclass(), fieldName)) {
        field = clazz.getSuperclass().getDeclaredField(fieldName);
      } else {
        throw new NoSuchElementException();
      }

      hasAccess = field.isAccessible();
      field.setAccessible(true);
      value = convertValueType(value, field.getType());
      field.set(object, value);
      return this;

    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new ReflectionException(e);
    } finally {
      if (field != null) {
        field.setAccessible(hasAccess);
      }
    }

  }

  /**
   * Gets the value of a field.
   *
   * @param fieldName The name of the field to get
   * @return An instance of Reflect wrapping the result of the field value
   * @throws ReflectionException Something went wrong getting the value of field
   */
  public Reflect get(String fieldName) throws ReflectionException {
    Field f = ReflectionUtils.getField(clazz, fieldName, accessAll);

    if (f == null) {
      Class<?> parent = clazz.getSuperclass();
      while (f == null && parent != null) {
        f = ReflectionUtils.getField(parent, fieldName, accessAll);
        parent = parent.getSuperclass();
      }
    }

    if (f == null) {
      throw new ReflectionException(new NoSuchFieldException(fieldName + " is not a valid field for " + clazz));
    }

    boolean isAccessible = f.isAccessible();
    try {
      if (accessAll) {
        f.setAccessible(true);
      }
      return onObject(f.get(object));
    } catch (IllegalAccessException e) {
      e.printStackTrace();
      throw new ReflectionException(e);
    } finally {
      f.setAccessible(isAccessible);
    }

  }


  /**
   * Gets fields.
   *
   * @return The set of fields for the object which is a combination of and if <code>allowPrivilegedAccess</code> was
   * called.
   */
  public Set<Field> getFields() {
    return ClassDescriptorCache.getInstance()
                               .getClassDescriptor(clazz)
                               .getFields(accessAll);
  }

  @Override
  public String toString() {
    return object == null ? clazz.toString() : object.toString();
  }


}//END OF Reflect
