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

import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * The type Reflected method.
 *
 * @author David B. Bracewell
 */
public class RMethod extends RExecutable<Method, RMethod> {
   private static final long serialVersionUID = 1L;
   private final Method method;

   /**
    * Instantiates a new Reflected method.
    *
    * @param owner  the owner
    * @param method the method
    */
   RMethod(Reflect owner, Method method) {
      super(owner);
      this.method = method;
      setIsPrivileged(owner.isPrivileged());
   }

   @Override
   public Method getElement() {
      return method;
   }

   @Override
   public Type getType() {
      return method.getGenericReturnType();
   }

   /**
    * Invokes this method with the given arguments.
    *
    * @param args The arguments to the method
    * @return The return value of the method call
    * @throws ReflectionException Something went wrong invoking the method
    */
   public <T> T invoke(Object... args) throws ReflectionException {
      return process(method -> {
         if (args == null || args.length == 0) {
            return Cast.as(method.invoke(getOwner().get(), args));
         }
         return Cast.as(method.invoke(getOwner().get(), convertParameters(args)));
      });
   }

   /**
    * Invokes this method with the given arguments.
    *
    * @param args The arguments to the method
    * @return A Reflect object representing the results
    * @throws ReflectionException Something went wrong invoking the method
    */
   public Reflect invokeReflective(Object... args) throws ReflectionException {
      return Reflect.onObject(invoke(args));
   }

}//END OF R2Method
