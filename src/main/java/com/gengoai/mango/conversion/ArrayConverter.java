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

package com.gengoai.mango.conversion;

import com.gengoai.mango.function.SerializableFunction;
import com.gengoai.mango.logging.Loggable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

/**
 * Converts objects to arrays of objects
 *
 * @param <T> the component type of the array
 * @author David B. Bracewell
 */
public class ArrayConverter<T> implements SerializableFunction<Object, T[]>, Loggable {
   private static final long serialVersionUID = 1L;

   private final Class<T> componentType;

   /**
    * Instantiates a new Array converter.
    *
    * @param componentType the component type
    */
   public ArrayConverter(Class<T> componentType) {
      this.componentType = componentType;
   }

   @Override
   public T[] apply(Object o) {

      if (o == null) {
         return null;
      }

      //We already have what we need so just cast and return
      if (o.getClass().isArray() && o.getClass().getComponentType().equals(componentType)) {
         return Cast.as(o);
      }

      List<T> list = new ArrayList<>();
      boolean anyConversionSuccessful = false;
      for (Object component : Convert.convert(o, Iterable.class)) {
         T comp = Convert.convert(component, componentType);
         if (comp != null) {
            anyConversionSuccessful = true;
         }
         list.add(comp);
      }

      if (!anyConversionSuccessful) {
         logFine("Cannot convert {0} into an array of {1}.", o.getClass(), componentType);
         return null;
      }

      T[] array = Cast.as(Array.newInstance(componentType, list.size()));
      if (array == null) {
         logFine("Error creating a new array of {0}", componentType);
         return null;
      }

      return list.toArray(array);
   }


}//END OF ArrayConverter
