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
 * Wrapper around an object or class allowing easy access to reflection operations.
 *
 * @author David B. Bracewell
 */
@EqualsAndHashCode(callSuper = false)
public final class Reflect extends RBase<Class<?>, Reflect> {
   private final Class<?> clazz;
   private final Object object;
   private boolean privileged;

   private Reflect(Object object, Class<?> clazz, boolean privileged) {
      this.object = object;
      this.clazz = clazz;
      this.privileged = privileged;
   }

   /**
    * Creates a Reflect object for the given class.
    *
    * @param clazz the class we want reflective access to
    * @return the reflect object
    */
   public static Reflect onClass(@NonNull Class<?> clazz) {
      return new Reflect(null, clazz, false);
   }

   /**
    * Creates a Reflect object for the given type.
    *
    * @param clazz the type we want reflective access to
    * @return the reflect object
    */
   public static Reflect onClass(@NonNull Type clazz) {
      return new Reflect(null, TypeUtils.asClass(clazz), false);
   }

   /**
    * Creates a Reflect object for the class represented by the given class name.
    *
    * @param className the name of the class we want reflective access to
    * @return the reflect object
    * @throws ReflectionException the reflection exception
    */
   public static Reflect onClass(String className) throws ReflectionException {
      try {
         return new Reflect(null, ReflectionUtils.getClassForName(className), false);
      } catch (Exception e) {
         throw new ReflectionException(e);
      }
   }

   /**
    * Creates a Reflect object on the given object
    *
    * @param object the object we want reflective access to
    * @return the reflect object
    */
   public static Reflect onObject(Object object) {
      if (object == null) {
         return new Reflect(null, null, false);
      }
      return new Reflect(object, object.getClass(), false);
   }

   /**
    * Allow privileged access during reflective calls.
    *
    * @return this reflect object
    */
   public Reflect allowPrivilegedAccess() {
      privileged = true;
      return this;
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
    * Determines if a method with the given name is associated with the class
    *
    * @param name The method name
    * @return True if there is a method with the given name
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
      return getConstructor().createReflective();
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
    * Creates an instance of the class being reflected using the best constructor that matches the given types.
    *
    * @param types The type of the given arguments.
    * @param args  The arguments to the constructor.
    * @return A <code>Reflect</code> object to do further reflection
    * @throws ReflectionException Something went wrong constructing the object
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
    * Gets the underlying object.
    *
    * @param <T> the type parameter
    * @return the underlying object or null if we are reflecting a class
    */
   public <T> T get() {
      return Cast.as(object);
   }

   /**
    * Gets an iterable of the ancestors of this class including super classes and interfaces.
    *
    * @param reverseOrder True - order starting at Object, False order starting at superclass.
    * @return the iterable of Reflect objects representing super-classes and interfaces
    */
   public Iterable<Reflect> getAncestors(boolean reverseOrder) {
      return () -> ClassDescriptorCache.getInstance()
                                       .getClassDescriptor(clazz)
                                       .getAncestors(reverseOrder);
   }


   /**
    * Gets the best constructor for the class matching the given types
    *
    * @param types The types (possibly empty) of the constructor parameters
    * @return the best constructor constructor for the class
    * @throws ReflectionException Either could not find an appropriate constructor or security did not allow reflective
    *                             access.
    */
   public RConstructor getConstructor(@NonNull Type... types) throws ReflectionException {
      if (types.length == 0) {
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
    * Gets the constructors for the class matching the given predicate
    *
    * @param predicate The predicate to use for filtering the constructors
    * @return the constructors for the class matching the given predicate
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
    * Gets the constructors for the class with at least of the given annotations.
    *
    * @param annotationClasses The annotation classes to search for
    * @return the constructors for the class  with at least of the given annotations.
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
    * Gets the class that declares the reflected object
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
    * Gets the field with the given name.
    *
    * @param name the name of the field
    * @return the reflected field
    * @throws ReflectionException No field found
    */
   public RField getField(String name) throws ReflectionException {
      Field f = ClassDescriptorCache.getInstance()
                                    .getClassDescriptor(clazz)
                                    .getField(name, privileged);
      if (f == null) {
         throw new ReflectionException("No such field: " + name);
      }
      return new RField(this, f);
   }

   /**
    * Gets all fields.
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
    * Gets all fields that match the given predicate
    *
    * @param predicate the predicate to use for filtering the fields
    * @return the fields matching the given predicate
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
    * Gets the fields for the class with at least of the given annotations.
    *
    * @param annotationClasses The annotation classes to search for
    * @return the fields for the class  with at least of the given annotations.
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
    * Gets the method with the given name.
    *
    * @param name the name of the method
    * @return the reflected method
    * @throws ReflectionException No such method
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
    * Gets the method with the given name and with types compatible with the given types.
    *
    * @param name  the name of the method
    * @param types the types of the method parameters
    * @return the reflected method
    * @throws ReflectionException No such method
    */
   public RMethod getMethod(String name, Type... types) throws ReflectionException {
      return Iterables.getFirst(getMethodsWhere(name, m -> m.parameterTypesCompatible(types)))
                      .orElseThrow(() -> new ReflectionException("No Such Method: " + name));
   }

   /**
    * Gets all methods with the given name.
    *
    * @param name the name of the method
    * @return the list of reflected methods
    */
   public List<RMethod> getMethods(String name) {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(name, privileged)
                                 .map(m -> new RMethod(this, m))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets all methods.
    *
    * @return the list of reflected methods
    */
   public List<RMethod> getMethods() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getMethods(privileged)
                                 .map(m -> new RMethod(this, m))
                                 .collect(Collectors.toList());
   }

   /**
    * Gets the methods with the given name for the class matching the given predicate
    *
    * @param name      the name of the method
    * @param predicate The predicate to use for filtering the constructors
    * @return the methods for the class matching the given predicate
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
    * Gets the methods for the class matching the given predicate
    *
    * @param predicate The predicate to use for filtering the constructors
    * @return the methods for the class matching the given predicate
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
    * Gets the methods for the class with at least of the given annotations.
    *
    * @param annotationClasses The annotation classes to search for
    * @return the methods for the class  with at least of the given annotations.
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
    * Gets the singleton method of the object.
    *
    * @return the reflected singleton method
    * @throws ReflectionException No Such Method
    */
   public RMethod getSingletonMethod() throws ReflectionException {
      return Optional.ofNullable(ClassDescriptorCache.getInstance()
                                                     .getClassDescriptor(clazz)
                                                     .getSingletonMethod())
                     .map(m -> new RMethod(new Reflect(null, clazz, privileged), m))
                     .orElseThrow(() -> new ReflectionException("No Singleton Static Method"));
   }

   /**
    * Gets the super-class of the class being reflected
    *
    * @return the reflected super class
    */
   public Reflect getSuperClass() {
      return Reflect.onClass(clazz.getSuperclass());
   }

   @Override
   public Class<?> getType() {
      return clazz;
   }

   /**
    * is privileged access allowed on this object?
    *
    * @return True - privileged access is allowed, False - no privileged access is allowed
    */
   public boolean isPrivileged() {
      return privileged;
   }

   /**
    * Does the reflected class have a singleton creation method (getInstance, getSingleton, or createInstance)
    *
    * @return True - if the class being reflected is a singleton, False otherwise
    */
   public boolean isSingleton() {
      return ClassDescriptorCache.getInstance()
                                 .getClassDescriptor(clazz)
                                 .getSingletonMethod() != null;
   }

   /**
    * Sets whether or not privileged access is allowed on this object
    *
    * @param allowPrivilegedAccess True - privileged access is allowed, False - no privileged access is allowed
    * @return this object
    */
   public Reflect setIsPrivileged(boolean allowPrivilegedAccess) {
      this.privileged = allowPrivilegedAccess;
      return Cast.as(this);
   }

}//END OF R2
