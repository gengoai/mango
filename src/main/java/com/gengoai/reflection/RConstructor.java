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

import java.lang.reflect.Constructor;
import java.lang.reflect.Type;

/**
 * The type Reflected constructor.
 *
 * @author David B. Bracewell
 */
public class RConstructor extends RExecutable<Constructor<?>, RConstructor> {
   private final Constructor<?> constructor;

   RConstructor(Reflect owner, Constructor<?> constructor) {
      super(owner);
      this.constructor = constructor;
      setIsPrivileged(owner.isPrivileged());
   }

   /**
    * Create t.
    *
    * @param <T>  the type parameter
    * @param args the args
    * @return the t
    * @throws ReflectionException the reflection exception
    */
   public <T> T create(Object... args) throws ReflectionException {
      return Cast.as(process(constructor -> {
         if (args == null || args.length == 0) {
            return Cast.as(constructor.newInstance());
         }
         return Cast.as(constructor.newInstance(convertParameters(args)));
      }));
   }

   /**
    * Create reflective reflect.
    *
    * @param args the args
    * @return the reflect
    * @throws ReflectionException the reflection exception
    */
   public Reflect createReflective(Object... args) throws ReflectionException {
      return Reflect.onObject(create(args));
   }

   @Override
   public Constructor<?> getElement() {
      return constructor;
   }

   @Override
   public Type getType() {
      return constructor.getAnnotatedReturnType().getType();
   }

}//END OF ReflectedConstructor
