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

package com.davidbracewell;

import com.davidbracewell.conversion.Cast;
import com.google.common.base.Throwables;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static com.davidbracewell.EnumValue.normalize;

/**
 * <p>Acts a global repository for dynamically generated {@link EnumValue}s. Each EnumValue acts like a Java
 * <code>enum</code> that can have elements created at runtime. Most interactions should be done with {@link EnumValue}
 * implementations.</p>
 *
 * @author David B. Bracewell
 */
public final class DynamicEnum implements Serializable {
   private static final long serialVersionUID = 1L;

   private static final Map<String, EnumValue> GLOBAL_REPOSITORY = new ConcurrentHashMap<>();
   private static LoadingCache<Class<?>, String> nameCache = CacheBuilder.newBuilder()
                                                                         .build(new CacheLoader<Class<?>, String>() {
                                                                            @Override
                                                                            public String load(Class<?> key) throws Exception {
                                                                               return key.getCanonicalName();
                                                                            }
                                                                         });

   private DynamicEnum() {
      throw new IllegalAccessError();
   }

   private static String toKey(@NonNull Class<? extends EnumValue> enumClass, String name) {
      String canonicalName = null;
      try {
         canonicalName = nameCache.get(enumClass);
      } catch (ExecutionException e) {
         throw Throwables.propagate(e);
      }
      if (name.startsWith(canonicalName)) {
         return name;
      }
      return canonicalName + "." + normalize(name);
   }

   /**
    * <p>Determines if the specified name is a defined value of the specified {@link EnumValue} class}.</p>
    *
    * @param enumClass Class information for the EnumValue that we will check.
    * @param name      the name of the specified value
    * @return True if the specified value has been defined for the given EnumValue class
    * @throws NullPointerException if either the enumClass or name are null
    */
   public static boolean isDefined(@NonNull Class<? extends EnumValue> enumClass, @NonNull String name) {
      return GLOBAL_REPOSITORY.containsKey(toKey(enumClass, name));
   }

   /**
    * <p>Returns the constant of the given {@link EnumValue} class  with the specified name.The normalized version of
    * the specified name will be matched allowing for case and space variations.</p>
    *
    * @param <T>       Specific type of EnumValue being looked up
    * @param enumClass Class information for the EnumValue that we will check.
    * @param name      the name of the specified value
    * @return The constant of enumClass with the specified name
    * @throws IllegalArgumentException if the specified name is not a member of enumClass.
    * @throws NullPointerException     if either the enumClass or name are null
    */
   public static <T extends EnumValue> T valueOf(@NonNull Class<T> enumClass, @NonNull String name) {
      String key = toKey(enumClass, name);
      T toReturn = Cast.as(GLOBAL_REPOSITORY.get(key));
      if (toReturn == null) {
         throw new IllegalArgumentException("No enum constant " + key);
      }
      return toReturn;
   }

   /**
    * <p>Attempts to register a given {@link EnumValue} instance to its associated class type. If an instance with the
    * same name has already been registered, that instance will be returned.</p>
    *
    * @param <T>       Specific type of EnumValue being registered
    * @param enumValue the value to register
    * @return the previously defined EnumValue with specified name and type or the one passed into register
    * @throws NullPointerException if enumValue is null
    */
   public static <T extends EnumValue> T register(@NonNull T enumValue) {
      return Cast.as(GLOBAL_REPOSITORY.computeIfAbsent(enumValue.canonicalName(), s -> enumValue));
   }

}//END OF DynamicEnum
