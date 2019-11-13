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

import com.gengoai.Lazy;
import com.gengoai.collection.Lists;
import com.gengoai.collection.Streams;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import lombok.NonNull;

import java.lang.reflect.Executable;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import static com.gengoai.reflection.TypeUtils.isAssignable;

/**
 * The type Reflected executable.
 *
 * @param <T> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public abstract class RExecutable<T extends Executable, V extends RExecutable> extends RAccessibleBase<T, V> {
   private static final long serialVersionUID = 1L;
   private final Reflect owner;
   private final Lazy<List<RParameter>> parameters = new Lazy<>(() -> new ArrayList<>(
      Lists.transform(Arrays.asList(getElement().getParameters()), parameter -> new RParameter(this, parameter))));

   protected RExecutable(Reflect owner) {
      this.owner = owner;
   }

   protected final Object[] convertParameters(Object... args) throws TypeConversionException {
      Class[] types = ReflectionUtils.getTypes(args);
      Type[] pTypes = getElement().getGenericParameterTypes();
      Object[] eArgs = new Object[args.length];
      for (int i = 0; i < args.length; i++) {
         if (args[i] == null || isAssignable(pTypes[i], types[i])) {
            eArgs[i] = args[i];
         } else {
            eArgs[i] = Converter.convert(args[i], pTypes[i]);
         }
      }
      return eArgs;
   }

   /**
    * Gets the class that declares this executable as a {@link Reflect} object
    *
    * @return the declaring class as a {@link Reflect} object
    */
   public Reflect getDeclaringClass() {
      return Reflect.onClass(getElement().getDeclaringClass());
   }

   @Override
   public int getModifiers() {
      return getElement().getModifiers();
   }

   @Override
   public final String getName() {
      return getElement().getName();
   }

   /**
    * Gets the {@link Reflect} object from which this executable was created.
    *
    * @return the {@link Reflect} object from which this executable was created.
    */
   public final Reflect getOwner() {
      return owner;
   }

   /**
    * Gets parameter.
    *
    * @param index the index
    * @return the parameter
    */
   public RParameter getParameter(int index) {
      return parameters.get().get(index);
   }

   /**
    * Gets parameters.
    *
    * @return the parameters
    */
   public List<RParameter> getParameters() {
      return Collections.unmodifiableList(parameters.get());
   }

   /**
    * Is var args boolean.
    *
    * @return the boolean
    */
   public boolean isVarArgs() {
      return getElement().isVarArgs();
   }

   /**
    * Number of parameters int.
    *
    * @return the int
    */
   public int numberOfParameters() {
      return getElement().getParameterCount();
   }

   /**
    * Parameter types compatible boolean.
    *
    * @param types the types
    * @return the boolean
    */
   public boolean parameterTypesCompatible(@NonNull Type... types) {
      return types.length == numberOfParameters()
         && Streams.zip(parameters.get().stream(), Stream.of(types))
                   .allMatch(e -> e.getKey().isTypeCompatible(e.getValue()));
   }


}//END OF ReflectedExecutable
