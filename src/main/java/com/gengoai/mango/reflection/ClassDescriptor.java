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

package com.gengoai.mango.reflection;

import com.gengoai.mango.collection.Sets;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Contains basic information about the methods, fields and constructors for a class.
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
public final class ClassDescriptor implements Serializable {
   private static final long serialVersionUID = 1L;
   private final Set<Method> methods = new HashSet<>();
   private final Set<Method> declaredMethods = new HashSet<>();
   private final Set<Field> fields = new HashSet<>();
   private final Set<Field> declaredFields = new HashSet<>();
   private final Set<Constructor<?>> constructors = new HashSet<>();
   private final Set<Constructor<?>> declaredConstructors = new HashSet<>();
   private final Class<?> clazz;

   /**
    * Instantiates a new Class descriptor.
    *
    * @param clazz the clazz
    */
   public ClassDescriptor(@NonNull Class<?> clazz) {
      this.clazz = clazz;
      this.methods.addAll(Arrays.asList(clazz.getMethods()));
      this.declaredMethods.addAll(Arrays.asList(clazz.getDeclaredMethods()));
      this.constructors.addAll(Arrays.asList(clazz.getConstructors()));
      this.declaredConstructors.addAll(Arrays.asList(clazz.getDeclaredConstructors()));
      this.fields.addAll(Arrays.asList(clazz.getFields()));
      this.declaredFields.addAll(ReflectionUtils.getDeclaredFields(clazz, true));
   }


   /**
    * Gets methods.
    *
    * @param privileged the privileged
    * @return the methods
    */
   public Set<Method> getMethods(boolean privileged) {
      if (privileged) {
         return Collections.unmodifiableSet(Sets.union(methods, declaredMethods));
      } else {
         return Collections.unmodifiableSet(methods);
      }
   }

   /**
    * Gets constructors.
    *
    * @param privileged the privileged
    * @return the constructors
    */
   public Set<Constructor<?>> getConstructors(boolean privileged) {
      if (privileged) {
         return Collections.unmodifiableSet(Sets.union(constructors, declaredConstructors));
      } else {
         return Collections.unmodifiableSet(constructors);
      }
   }

   /**
    * Gets fields.
    *
    * @param privileged the privileged
    * @return the fields
    */
   public Set<Field> getFields(boolean privileged) {
      if (privileged) {
         return Collections.unmodifiableSet(Sets.union(fields, declaredFields));
      } else {
         return Collections.unmodifiableSet(fields);
      }
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
    * Gets super class descriptor.
    *
    * @return the super class descriptor
    */
   public ClassDescriptor getSuperClassDescriptor() {
      return ClassDescriptorCache.getInstance().getClassDescriptor(this.clazz.getSuperclass());
   }

}//END OF ClassDescriptor
