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
import com.google.common.base.Throwables;
import com.google.common.collect.Maps;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

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
    readMethods = Maps.newConcurrentMap();
    writeMethods = Maps.newConcurrentMap();
    try {
      //TODO Custom BeanInfo
      BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class,Introspector.USE_ALL_BEANINFO);
      PropertyDescriptor[] descriptors = beanInfo.getPropertyDescriptors();

      if (descriptors == null || descriptors.length == 0) {
        return;
      }

      for (PropertyDescriptor descriptor : descriptors) {
        String name = descriptor.getName();
        Method readMethod = descriptor.getReadMethod();
        Method writeMethod = descriptor.getWriteMethod();

        if (readMethod != null) {
          readMethods.put(name, readMethod);
        }
        if (writeMethod != null) {
          writeMethods.put(name, writeMethod);
        }

      }

    } catch (IntrospectionException e) {
      throw Throwables.propagate(e);
    }


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
