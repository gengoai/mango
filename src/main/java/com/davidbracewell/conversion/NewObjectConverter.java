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

package com.davidbracewell.conversion;


import com.davidbracewell.EnumValue;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.BeanUtils;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.reflection.ReflectionUtils;
import lombok.NonNull;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

/**
 * <p> Converter that tries to create a new object from the object it is given. </p> <p> <ul> <li>If the object is an
 * instance of the class, the object is cast and returned.</li> <li>If the object is of type Class, a new object is
 * created and returned if it is of the correct type, otherwise an exception thrown.</li> <li>If the object is a
 * string,
 * it is checked to see if it is a class name or static method name. If not, it will see if the class has a string
 * constructor.</li> <li>Last effort is to try and create the class with the object as the argument.</li> </ul> </p>
 *
 * @param <T> The type of object to create.
 * @author David B. Bracewell
 */
public class NewObjectConverter<T> implements Function<Object, T> {

   private static final Logger log = Logger.getLogger(NewObjectConverter.class);


   private final Class<T> convertToClass;

   public NewObjectConverter(@NonNull Class<T> convertToClass) {
      this.convertToClass = convertToClass;
   }

   @Override
   @SuppressWarnings("unchecked")
   public T apply(Object obj) {

      if (obj == null) {
         return null;
      }

      if (convertToClass != Object.class && Convert.hasConverter(convertToClass)) {
         return Cast.as(Convert.convert(obj, convertToClass));
      } else if (Map.class.isAssignableFrom(convertToClass)) {
         return Cast.as(Convert.convert(obj, convertToClass, Object.class, Object.class));
      } else if (Collection.class.isAssignableFrom(convertToClass)) {
         return Cast.as(Convert.convert(obj, convertToClass, Object.class));
      } else if (convertToClass.isEnum()) {
         return Cast.as(Convert.convert(obj, Cast.<Class<? extends Enum>>as(convertToClass)));
      } else if (convertToClass != Object.class && convertToClass.isInstance(obj)) {
         return Cast.as(obj);
      }


      if (obj instanceof CharSequence) {
         String seq = obj.toString();
         int index = seq.lastIndexOf('.');
         if (index > 0) {
            Class<?> clazz = ReflectionUtils.getClassForNameQuietly(seq.substring(0, index));
            if (clazz != null && EnumValue.class.isAssignableFrom(clazz)) {
               try {
                  Object converted = Reflect.onClass(clazz)
                                            .allowPrivilegedAccess()
                                            .invoke("create", seq.substring(index + 1))
                                            .get();
                  if (EnumValue.class.isAssignableFrom(convertToClass) || convertToClass == Object.class) {
                     return Cast.as(converted);
                  }
                  return apply(converted);
               } catch (ReflectionException e) {
                  //No opt
               }
            }
         }

         if (EnumValue.class.isAssignableFrom(convertToClass)) {
            try {
               return BeanUtils.parameterizeObject(Reflect.onClass(convertToClass).allowPrivilegedAccess()
                                                          .invoke("create", obj).get());
            } catch (ReflectionException e) {
               //nopt
            }
         }

         Object o = ReflectionUtils.createObject(obj.toString());
         if (convertToClass.isInstance(o)) {
            return Cast.as(BeanUtils.parameterizeObject(o));
         }

      }


      try {
         return BeanUtils.parameterizeObject(Reflect.onClass(convertToClass).create(obj).get());
      } catch (ReflectionException e) {
         //ignore
      }

      log.fine("Could not convert {0} to {1}.", obj.getClass(), convertToClass);
      return null;
   }
}
