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

import com.davidbracewell.logging.Logger;
import com.davidbracewell.string.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Contains basic information about the read and write methods for a class.
 *
 * @author David B. Bracewell
 */
public class BeanDescriptor implements Serializable {

  private static final Logger log = Logger.getLogger(BeanDescriptor.class);
  private static final long serialVersionUID = -6445604079340822462L;
  private final Map<String, Method> readMethods;
  private final Map<String, Method> writeMethods;
  private final Class<?> clazz;

  /**
   * Default Constructor that initializes the descriptor using class information
   *
   * @param clazz The class associated with this descriptor
   */
  public BeanDescriptor(Class<?> clazz) {
    this.clazz = clazz;
    readMethods = new ConcurrentHashMap<>();
    writeMethods = new ConcurrentHashMap<>();
    setReadWrite(clazz.getSuperclass());
    Class<?>[] interfaces = clazz.getInterfaces();
    if (interfaces != null) {
      for (Class<?> iface : interfaces) {
        setReadWrite(iface);
      }
    }
    setReadWrite(this.clazz);
  }

  private void setReadWrite(Class<?> clazz) {
    if (clazz == null) {
      return;
    }
    Reflect.onClass(clazz).getMethods().forEach(method -> {
      String name = method.getName();
      if (!name.equals("getClass") && method.getAnnotation(Ignore.class) == null) {
        if (name.startsWith("get") || name.startsWith("is")) {
          readMethods.put(transformName(name), method);
        } else if (name.startsWith("set")) {
          writeMethods.put(transformName(name), method);
        }
      }
    });
  }

  private String transformName(String name) {
    int prefixLen = 3;
    if (name.startsWith("is")) {
      prefixLen = 2;
    }
    if (name.length() == prefixLen) {
      return StringUtils.EMPTY;
    }
    char[] carrry = name.substring(prefixLen, name.length()).toCharArray();
    carrry[0] = Character.toLowerCase(carrry[0]);
    return new String(carrry);
  }

  /**
   * Determines if the descriptor has a read method named with the given string.
   *
   * @param methodName The read method we want to check for
   * @return True if the method exists, false otherwise
   */
  public boolean hasReadMethod(String methodName) {
    return readMethods.containsKey(methodName);
  }

  /**
   * Determines if the descriptor has a write method named with the given string.
   *
   * @param methodName The write method we want to check for
   * @return True if the method exists, false otherwise
   */
  public boolean hasWriteMethod(String methodName) {
    return writeMethods.containsKey(methodName);
  }

  /**
   * Gets a read method by its name
   *
   * @param methodName The name of the method
   * @return The method with the given name or <code>null</code>
   */
  public Method getReadMethod(String methodName) {
    return readMethods.get(methodName);
  }

  /**
   * Gets a write method by its name
   *
   * @param methodName The name of the method
   * @return The method with the given name or <code>null</code>
   */
  public Method getWriteMethod(String methodName) {
    return writeMethods.get(methodName);
  }

  /**
   * @return All of the read methods
   */
  public Collection<Method> getReadMethods() {
    return Collections.unmodifiableCollection(readMethods.values());
  }

  /**
   * @return All of the names of the read methods
   */
  public Set<String> getReadMethodNames() {
    return Collections.unmodifiableSet(readMethods.keySet());
  }

  /**
   * @return All of the write methods
   */
  public Collection<Method> getWriteMethods() {
    return Collections.unmodifiableCollection(writeMethods.values());
  }

  /**
   * @return All of the names of the write methods
   */
  public Set<String> getWriteMethodNames() {
    return Collections.unmodifiableSet(writeMethods.keySet());
  }

  /**
   * @return The associated class information.
   */
  public Class<?> getBeanClass() {
    return clazz;
  }

  /**
   * @return The number of read methods
   */
  public int numberOfReadMethods() {
    return readMethods.size();
  }

  /**
   * @return The number of write methods
   */
  public int numberOfWriteMethods() {
    return writeMethods.size();
  }

  /**
   * Constructs an instance of the wrapped class.
   *
   * @return An instance of the wrapped class
   * @throws InstantiationException Something went wrong during instantiation.
   * @throws IllegalAccessException Couldn't access the class.
   */
  public Object createInstance() throws InstantiationException, IllegalAccessException {
    return clazz.newInstance();
  }

  /**
   * Constructs an instance of the wrapped class ignoring any errors.
   *
   * @return An instance of the class or <code>null</code> if something went wrong.
   */
  public Object createInstanceQuietly() {
    try {
      return clazz.newInstance();
    } catch (InstantiationException e) {
      log.finest(e);
      return null;
    } catch (IllegalAccessException e) {
      log.finest(e);
      return null;
    }
  }

}// END OF CLASS BeanDescriptor
