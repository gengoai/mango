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

import com.gengoai.conversion.Cast;
import com.gengoai.function.CheckedConsumer;
import com.gengoai.function.CheckedFunction;
import lombok.NonNull;

import java.lang.reflect.AccessibleObject;

/**
 * The type R accessible object.
 *
 * @param <T> the type parameter
 * @param <V> the type parameter
 */
abstract class RAccessibleBase<T extends AccessibleObject, V extends RAccessibleBase> extends RBase<T, V> {
   private boolean privileged = false;

   /**
    * Allow privileged access to the object
    *
    * @return this Object
    */
   public final V allowPrivilegedAccess() {
      this.privileged = true;
      return Cast.as(this);
   }

   /**
    * is privileged access allowed on this object?
    *
    * @return True - privileged access is allowed, False - no privileged access is allowed
    */
   public final boolean isPrivileged() {
      return privileged;
   }

   /**
    * Applies the given {@link CheckedFunction} to the object. This method takes care of accessibility concerns.
    *
    * @param <O>      the return type parameter
    * @param function the function to apply
    * @return the return value of the function
    * @throws ReflectionException Something went wrong during reflection
    */
   public final <O> O process(@NonNull CheckedFunction<T, O> function) throws ReflectionException {
      boolean isAccessible = false;
      try {
         isAccessible = getElement().isAccessible();
         if (isPrivileged()) {
            getElement().setAccessible(true);
         }
         return function.apply(getElement());
      } catch (Throwable e) {
         throw new ReflectionException(e);
      } finally {
         getElement().setAccessible(isAccessible);
      }
   }

   /**
    * Sets whether or not privileged access is allowed on this object
    *
    * @param allowPrivilegedAccess True - privileged access is allowed, False - no privileged access is allowed
    * @return this object
    */
   public final V setIsPrivileged(boolean allowPrivilegedAccess) {
      this.privileged = allowPrivilegedAccess;
      return Cast.as(this);
   }

   /**
    * Applies the given {@link CheckedConsumer} to the object. This method takes care of accessibility concerns.
    *
    * @param consumer the consumer to apply
    * @throws ReflectionException Something went wrong during reflection
    */
   public final void with(@NonNull CheckedConsumer<T> consumer) throws ReflectionException {
      boolean isAccessible = false;
      try {
         isAccessible = getElement().isAccessible();
         if (isPrivileged()) {
            getElement().setAccessible(true);
         }
         consumer.accept(getElement());
      } catch (Throwable e) {
         throw new ReflectionException(e);
      } finally {
         getElement().setAccessible(isAccessible);
      }
   }


}//END OF AccessibleType
