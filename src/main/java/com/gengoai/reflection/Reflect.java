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
 *
 */

package com.gengoai.reflection;

import com.gengoai.Validation;
import com.gengoai.collection.Iterables;
import com.gengoai.conversion.Cast;
import com.gengoai.function.SerializablePredicate;
import com.gengoai.string.Strings;
import lombok.EqualsAndHashCode;
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The type Reflect.
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
public class Reflect extends RBase<Class<?>, Reflect> {
   private final Class<?> clazz;
   private final Object object;
   private boolean privileged;

   /**
    * Instantiates a new Reflect.
    */
   public Reflect() {
      this(null, null, false);
   }

   /**
    * Instantiates a new Reflect.
    *
    * @param object     the object
    * @param clazz      the clazz
    * @param privileged the privileged
    */
   public Reflect(Object object, Class<?> clazz, boolean privileged) {
      this.object = object;
      this.clazz = clazz;
      this.privileged = privileged;
   }

   /**
    * On class reflect.
    *
    * @param clazz the clazz
    * @return the reflect
    */
   public static Reflect onClass(Class<?> clazz) {
      return new Reflect(null, clazz, false);
   }

   /**
    * On class reflect.
    *
    * @param clazz the clazz
    * @return the reflect
    */
   public static Reflect onClass(Type clazz) {
      return new Reflect(null, TypeUtils.asClass(clazz), false);
   }

   /**
    * On class reflect.
    *
    * @param className the class name
    * @return the reflect
    * @throws ReflectionException the reflection exception
    */
   public static Reflect onClass(String className) throws ReflectionException {
      try {
         Class<?> clazz = ReflectionUtils.getClassForNameQuietly(className);
         if (clazz != null) {
            return new Reflect(null, clazz, false);
         }
         return new Reflect(null, ReflectionUtils.getClassForName(className), false);
      } catch (Exception e) {
         throw new ReflectionException(e);
      }
   }

   /**
    * On object reflect.
    *
    * @param object the object
    * @return the reflect
    */
   public static Reflect onObject(Object object) {
      if (object == null) {
         return new Reflect(null, null, false);
      }
      return new Reflect(object, object.getClass(), false);
   }

   /**
    * Allow privileged access reflect.
    *
    * @return the reflect
    */
   public Reflect allowPrivilegedAccess() {
      privileged = true;
      return this;
   }

   /**
    * Constructor r constructor.
    *
    * @return the r constructor
    * @throws ReflectionException the reflection exception
    */
   public RConstructor constructor() throws ReflectionException {
      try {
         Constructor<?> c = ClassDescriptorCache.getInstance()
                                                .getClassDescriptor(clazz)
                                                .getConstructors(privileged)
                                                .filter(
                                                   constructor -> (constructor.isVarArgs() && constructor.getParameterCount() == 1)
                                                      || constructor.getParameterCount() == 0)
                                                .findFirst()
                                                .orElse(null);
         if (c == null) {
            c = clazz.getDeclaredConstructor();
         }
         return new RConstructor(this, c);
      } catch (NoSuchMethodException | SecurityException e) {
         throw new ReflectionException(e);
      }
   }

   /**
    * Determines if a field with the given name is associated with the class
    *
    * @param name The field name
    * @return True if there is a field with the given name
    */
   public boolean containsField(String name) {
      if (Strings.isNullOrBlank(name)) {
         return false;
      }
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getField(name, privileged) != null;
   }

   /**
    * Contains method.
    *
    * @param name the name
    * @return the boolean
    */
   public boolean containsMethod(final String name) {
      if (Strings.isNullOrBlank(name)) {
         return false;
      }
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(name, privileged)
                                 .count() > 0;
   }

   /**
    * Creates an instance of the class being reflected using the no-argument constructor.
    *
    * @return A <code>Reflect</code> object to do further reflection
    * @throws ReflectionException Something went wrong constructing the object
    */
   public Reflect create() throws ReflectionException {
      if (isSingleton()) {
         return getSingletonMethod().invokeReflective();
      }
      return constructor().createReflective();
   }

   /**
    * Creates an instance of the class being reflected using the most specific constructor available.
    *
    * @param args The arguments to the constructor.
    * @return A <code>Reflect</code> object to do further reflection
    * @throws ReflectionException Something went wrong constructing the object
    */
   public Reflect create(Object... args) throws ReflectionException {
      if (isSingleton()) {
         if (args == null || args.length == 0) {
            return getSingletonMethod().invokeReflective();
         }
         throw new ReflectionException("Trying to call the constructor of a singleton object");
      }
      return create(getTypes(args), args);
   }

   /**
    * Create reflect.
    *
    * @param types the types
    * @param args  the args
    * @return the reflect
    * @throws ReflectionException the reflection exception
    */
   public Reflect create(@NonNull Class[] types, @NonNull Object... args) throws ReflectionException {
      Validation.checkArgument(types.length == args.length);
      if (isSingleton()) {
         if (args.length == 0) {
            return getSingletonMethod().invokeReflective();
         }
         throw new ReflectionException("Trying to call the constructor of a singleton object");
      }
      return getConstructor(types).createReflective(args);
   }

   /**
    * Get t.
    *
    * @param <T> the type parameter
    * @return the t
    */
   public <T> T get() {
      return Cast.as(object);
   }

   /**
    * Ancestors iterable.
    *
    * @param reverseOrder the reverse order
    * @return the iterable
    */
   public Iterable<Reflect> getAncestors(boolean reverseOrder) {
      return () -> ClassDescriptorCache.getInstance()
                                       .getClassDescriptor(clazz)
                                       .getAncestors(reverseOrder);
   }

   /**
    * Gets constructor.
    *
    * @return the constructor
    * @throws ReflectionException the reflection exception
    */
   public RConstructor getConstructor() throws ReflectionException {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getConstructors(privileged)
                                 .filter(c -> c.getParameterCount() == 0 || c.isVarArgs())
                                 .findFirst()
                                 .map(c -> new RConstructor(this, c))
                                 .orElseThrow(() -> new ReflectionException("No such constructor"));
   }

   /**
    * Gets constructor.
    *
    * @param types the types
    * @return the constructor
    * @throws ReflectionException the reflection exception
    */
   public RConstructor getConstructor(@NonNull Type... types) throws ReflectionException {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getConstructors(privileged)
                                 .filter(c -> c.getParameterCount() == types.length)
                                 .map(c -> new RConstructor(this, c))
                                 .filter(c -> c.parameterTypesCompatible(types))
                                 .findFirst()
                                 .orElseThrow(() -> new ReflectionException("No such constructor"));
   }

   /**
    * Gets constructors.
    *
    * @return The set of constructors for the object which is a combination of and if <code>allowPrivilegedAccess</code>
    * was called.
    */
   public List<Constructor<?>> getConstructors() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getConstructors(privileged)
                                 .collect(Collectors.toList());
   }

   /**
    * Gets constructors where.
    *
    * @param predicate the predicate
    * @return the constructors where
    */
   public final List<RConstructor> getConstructorsWhere(@NonNull SerializablePredicate<? super RConstructor> predicate) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getConstructors(privileged)
                                 .map(c -> new RConstructor(this, c))
                                 .filter(predicate)
                                 .collect(Collectors.toList());
   }

   /**
    * Gets constructors with annotation.
    *
    * @param annotationClasses the annotation classes
    * @return the constructors with annotation
    */
   @SafeVarargs
   public final List<RConstructor> getConstructorsWithAnnotation(@NonNull Class<? extends Annotation>... annotationClasses) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getConstructors(privileged)
                                 .filter(c -> RBase.isAnnotationPresent(c, annotationClasses))
                                 .map(c -> new RConstructor(this, c))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets declaring class.
    *
    * @return the declaring class
    */
   public Reflect getDeclaringClass() {
      return Reflect.onClass(clazz.getDeclaringClass());
   }

   @Override
   public Class<?> getElement() {
      return clazz;
   }

   /**
    * Gets field.
    *
    * @param name the name
    * @return the field
    * @throws ReflectionException the reflection exception
    */
   public RField getField(String name) throws ReflectionException {
      Validation.notNullOrBlank(name);
      Field f = ClassDescriptorCache.getInstance()
                                    .getClassDescriptor(clazz)
                                    .getField(name, privileged);
      if (f == null) {
         throw new ReflectionException("No such field: " + name);
      }
      return new RField(this, f);
   }

   /**
    * Gets fields.
    *
    * @return the fields
    */
   public List<RField> getFields() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getFields(privileged)
                                 .map(f -> new RField(this, f))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets fields where.
    *
    * @param predicate the predicate
    * @return the fields where
    */
   public List<RField> getFieldsWhere(@NonNull SerializablePredicate<RField> predicate) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getFields(privileged)
                                 .map(f -> new RField(this, f))
                                 .filter(predicate)
                                 .collect(Collectors.toList());
   }

   /**
    * Gets fields with annotation.
    *
    * @param annotationClasses the annotation classes
    * @return the fields with annotation
    */
   @SafeVarargs
   public final List<RField> getFieldsWithAnnotation(@NonNull Class<? extends Annotation>... annotationClasses) {
      Validation.checkArgument(annotationClasses.length > 0, "Must specify at least one annotation class");
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getFields(privileged)
                                 .filter(f -> RBase.isAnnotationPresent(f, annotationClasses))
                                 .map(f -> new RField(this, f))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets method.
    *
    * @param name the name
    * @return the method
    * @throws ReflectionException the reflection exception
    */
   public RMethod getMethod(String name) throws ReflectionException {
      try {
         return new RMethod(this, clazz.getMethod(name));
      } catch (NoSuchMethodException e) {
         return Iterables.getFirst(getMethods(name))
                         .orElseThrow(() -> new ReflectionException("No Such Method: " + name));
      }
   }

   /**
    * Gets method.
    *
    * @param name  the name
    * @param types the types
    * @return the method
    * @throws ReflectionException the reflection exception
    */
   public RMethod getMethod(String name, Type... types) throws ReflectionException {
      return Iterables.getFirst(getMethodsWhere(name, m -> m.parameterTypesCompatible(types)))
                      .orElseThrow(() -> new ReflectionException("No Such Method: " + name));
   }

   /**
    * Gets methods.
    *
    * @param name the name
    * @return the methods
    */
   public List<RMethod> getMethods(String name) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(name, privileged)
                                 .map(m -> new RMethod(this, m))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets methods.
    *
    * @return the methods
    */
   public List<RMethod> getMethods() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(privileged)
                                 .map(m -> new RMethod(this, m))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets methods where.
    *
    * @param name      the name
    * @param predicate the predicate
    * @return the methods where
    */
   public List<RMethod> getMethodsWhere(String name, @NonNull SerializablePredicate<? super RMethod> predicate) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(name, privileged)
                                 .map(m -> new RMethod(this, m))
                                 .filter(predicate)
                                 .collect(Collectors.toList());
   }

   /**
    * Gets methods where.
    *
    * @param predicate the predicate
    * @return the methods where
    */
   public List<RMethod> getMethodsWhere(@NonNull SerializablePredicate<? super RMethod> predicate) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(privileged)
                                 .map(m -> new RMethod(this, m))
                                 .filter(predicate)
                                 .collect(Collectors.toList());
   }

   /**
    * Gets methods with annotation.
    *
    * @param annotationClasses the annotation classes
    * @return the methods with annotation
    */
   @SafeVarargs
   public final List<RMethod> getMethodsWithAnnotation(@NonNull Class<? extends Annotation>... annotationClasses) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(privileged)
                                 .filter(m -> RBase.isAnnotationPresent(m, annotationClasses))
                                 .map(m -> new RMethod(this, m))
                                 .collect(Collectors.toList());
   }

   @Override
   public int getModifiers() {
      return clazz.getModifiers();
   }

   @Override
   public String getName() {
      return clazz.getName();
   }

   /**
    * Gets singleton method.
    *
    * @return the singleton method
    * @throws ReflectionException the reflection exception
    */
   public RMethod getSingletonMethod() throws ReflectionException {
      return Optional.ofNullable(ClassDescriptorCache.getInstance()
                                                     .getClassDescriptor(clazz)
                                                     .getSingletonMethod())
                     .map(m -> new RMethod(new Reflect(null, clazz, privileged), m))
                     .orElseThrow(() -> new ReflectionException("No Singleton Static Method"));
   }

   /**
    * Gets super class.
    *
    * @return the super class
    */
   public Reflect getSuperClass() {
      return Reflect.onClass(clazz.getSuperclass());
   }

   public Class<?> getType() {
      return clazz;
   }

   /**
    * Is privileged boolean.
    *
    * @return the boolean
    */
   public boolean isPrivileged() {
      return privileged;
   }

   /**
    * Is singleton boolean.
    *
    * @return the boolean
    */
   public boolean isSingleton() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getSingletonMethod() != null;
   }

   /**
    * Sets is privileged.
    *
    * @param allowPrivilegedAccess the allow privileged access
    * @return the is privileged
    */
   public Reflect setIsPrivileged(boolean allowPrivilegedAccess) {
      this.privileged = allowPrivilegedAccess;
      return Cast.as(this);
   }

}//END OF R2
