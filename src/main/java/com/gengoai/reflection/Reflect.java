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
import lombok.NonNull;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

import static com.gengoai.collection.Lists.linkedListOf;
import static com.gengoai.collection.Sets.hashSetOf;

/**
 * The type Reflect.
 *
 * @author David B. Bracewell
 */
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

//   /**
//    * Creates an instance Reflect for an object that gets constructed using the supplied constructor and arguments.
//    *
//    * @param constructor           The constructor to call
//    * @param allowPrivilegedAccess Allows access to all methods on the object or class
//    * @param args                  The arguments to pass to the constructor
//    * @return A Reflect wrapper around the constructed object
//    * @throws ReflectionException Something went wrong constructing the object
//    */
//   private static Reflect on(Constructor constructor,
//                             boolean allowPrivilegedAccess,
//                             Object... args) throws ReflectionException {
//      boolean accessible = constructor.isAccessible();
//      try {
//         if (!accessible && allowPrivilegedAccess) {
//            constructor.setAccessible(true);
//         }
//         if (args != null) {
//            Class<?>[] parameterTypes = constructor.getParameterTypes();
//            for (int i = 0; i < args.length; i++) {
//               args[i] = convertValueType(args[i], parameterTypes[i]);
//            }
//         }
//
//         if (constructor.isVarArgs() && constructor.getParameterTypes()[0].isArray()) {
//            Object object = constructor.newInstance(
//               Array.newInstance(constructor.getParameterTypes()[0].getComponentType(), 0));
//            return new Reflect(object, object.getClass(), allowPrivilegedAccess);
//         }
//
//
//         Object object = constructor.newInstance(args);
//         return new Reflect(object, object.getClass(), allowPrivilegedAccess);
//      } catch (InstantiationException | IllegalAccessException | InvocationTargetException | TypeConversionException e) {
//         throw new ReflectionException(e);
//      } finally {
//         if (!accessible && allowPrivilegedAccess) {
//            constructor.setAccessible(false);
//         }
//      }
//   }

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
    * @param className the class name
    * @return the reflect
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
    * Ancestors iterable.
    *
    * @return the iterable
    */
   public Iterable<Reflect> ancestors() {
      return AncestorIterator::new;
   }

   /**
    * Constructor r constructor.
    *
    * @return the r constructor
    * @throws ReflectionException the reflection exception
    */
   public RConstructor constructor() throws ReflectionException {
      for (Constructor<?> constructor : getConstructors()) {
         if ((constructor.isVarArgs() && constructor.getParameterCount() == 1) || constructor.getParameterCount() == 0) {
            return new RConstructor(this, constructor);
         }
      }
      throw new ReflectionException("No Constructor found");
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
   public Reflect create(@NonNull Class[] types, @NonNull Object... args) throws ReflectionException {
      Validation.checkArgument(types.length == args.length);
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
    * Sets is privileged.
    *
    * @param allowPrivilegedAccess the allow privileged access
    * @return the is privileged
    */
   public Reflect setIsPrivileged(boolean allowPrivilegedAccess) {
      this.privileged = allowPrivilegedAccess;
      return Cast.as(this);
   }

   private class AncestorIterator implements Iterator<Reflect> {
      /**
       * The Queue.
       */
      final Queue<Class<?>> queue = linkedListOf(clazz);
      /**
       * The Seen.
       */
      final Set<Class<?>> seen = hashSetOf(clazz);
      /**
       * The Next.
       */
      Reflect next = null;

      /**
       * Advance boolean.
       *
       * @return the boolean
       */
      boolean advance() {
         while (next == null && queue.size() > 0) {
            Class<?> nextClazz = queue.remove();
            if (nextClazz != clazz) {
               next = Reflect.onClass(nextClazz);
            }
            if (nextClazz.getSuperclass() != null && !seen.contains(nextClazz.getSuperclass())) {
               queue.add(clazz.getSuperclass());
               seen.add(clazz.getSuperclass());
            }
            for (Class<?> iface : nextClazz.getInterfaces()) {
               if (!seen.contains(iface)) {
                  queue.add(iface);
                  seen.add(iface);
               }
            }
         }
         return next != null;
      }

      @Override
      public boolean hasNext() {
         return advance();
      }

      @Override
      public Reflect next() {
         if (!advance()) {
            throw new NoSuchElementException();
         }
         Reflect r = next;
         next = null;
         return r;
      }
   }

}//END OF R2
