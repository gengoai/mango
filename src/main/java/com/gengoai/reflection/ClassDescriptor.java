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

import com.gengoai.collection.multimap.HashSetMultimap;
import com.gengoai.collection.multimap.Multimap;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.*;
import java.util.stream.Stream;

/**
 * Contains basic information about the methods, fields and constructors for a class.
 *
 * @author David B. Bracewell
 */
public final class ClassDescriptor implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Class<?> clazz;
   private final Set<Constructor<?>> constructors = new HashSet<>();
   private final Map<String, Field> fields = new HashMap<>();
   private final Multimap<String, Method> methods = new HashSetMultimap<>();

   /**
    * Instantiates a new Class descriptor.
    *
    * @param clazz the clazz
    */
   public ClassDescriptor(Class<?> clazz) {
      this.clazz = clazz;
      for (Method method : clazz.getMethods()) {
         methods.put(method.getName(), method);
      }
      for (Method method : clazz.getDeclaredMethods()) {
         methods.put(method.getName(), method);
      }
      for (Field field : clazz.getFields()) {
         fields.put(field.getName(), field);
      }
      for (Field field : clazz.getDeclaredFields()) {
         fields.put(field.getName(), field);
      }
      Collections.addAll(constructors, clazz.getConstructors());
      Collections.addAll(constructors, clazz.getConstructors());
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof ClassDescriptor)) return false;
      ClassDescriptor that = (ClassDescriptor) o;
      return Objects.equals(clazz, that.clazz);
   }

   /**
    * Gets clazz.
    *
    * @return the clazz
    */
   public Class<?> getClazz() {
      return clazz;
   }

   /**
    * Gets constructors.
    *
    * @param privileged the privileged
    * @return the constructors
    */
   public Stream<Constructor<?>> getConstructors(boolean privileged) {
      Stream<Constructor<?>> stream = constructors.stream();
      if (!privileged) {
         stream = stream.filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0);
      }
      return stream;
   }

   /**
    * Gets field.
    *
    * @param name       the name
    * @param privileged the privileged
    * @return the field
    */
   public Field getField(String name, boolean privileged) {
      Field f = fields.get(name);
      if (f != null) {
         return (privileged || (f.getModifiers() & Modifier.PUBLIC) != 0)
                ? f
                : null;
      }
      return null;
   }


   /**
    * Gets fields.
    *
    * @param privileged the privileged
    * @return the fields
    */
   public Stream<Field> getFields(boolean privileged) {
      Stream<Field> stream = fields.values().stream();
      if (!privileged) {
         stream = stream.filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0);
      }
      return stream;
   }

   /**
    * Gets methods.
    *
    * @param privileged the privileged
    * @return the methods
    */
   public Stream<Method> getMethods(boolean privileged) {
      Stream<Method> stream = methods.values().stream();
      if (!privileged) {
         stream = stream.filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0);
      }
      return stream;
   }

   /**
    * Gets methods.
    *
    * @param name       the name
    * @param privileged the privileged
    * @return the methods
    */
   public Stream<Method> getMethods(String name, boolean privileged) {
      Stream<Method> stream = methods.get(name).stream();
      if (!privileged) {
         stream = stream.filter(m -> (m.getModifiers() & Modifier.PUBLIC) != 0);
      }
      return stream;
   }

   @Override
   public int hashCode() {
      return Objects.hash(clazz);
   }
}//END OF ClassDescriptor
