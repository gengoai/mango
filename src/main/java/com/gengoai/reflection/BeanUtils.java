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

package com.gengoai.reflection;

import com.gengoai.collection.Iterables;
import com.gengoai.config.Config;
import com.gengoai.conversion.Cast;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentSkipListMap;

/**
 * Methods for constructing beans and setting their parameters using value in the {@link Config}
 *
 * @author David B. Bracewell
 */
public class BeanUtils {
   private static final ConcurrentSkipListMap<String, Object> SINGLETONS = new ConcurrentSkipListMap<>();

   private static void doParametrization(BeanMap beanMap, String className) {
      beanMap.getSetters()
             .stream()
             .filter(propertyName -> Config.hasProperty(className, propertyName))
             .forEach(propertyName -> {
                String property = className + "." + propertyName;
                Object val;
                if (Config.isBean(property)) {
                   val = Config.get(property);
                } else {
                   Type type = Object.class;
                   if (Config.hasProperty(property + ".@type")) {
                      type = Types.parse(Config.get(property + ".@type").asString());
                   }
                   try {
                      val = Json.parse(Config.get(className, propertyName).asString()).getAs(type);
                   } catch (IOException e) {
                      throw new RuntimeException(e);
                   }
                }
                beanMap.put(propertyName, val);
             });
   }

   /**
    * Constructs a new instance of the given class and then sets it properties using configuration.
    *
    * @param clazz The class that we want to instantiate
    * @return A new instance of the given class
    */
   public static <T> T getBean(Class<T> clazz) throws ReflectionException {
      return parameterizeObject(Reflect.onClass(clazz).create().get());
   }

   /**
    * Instantiates a named bean (defined via the Config)
    *
    * @param name  The name of the bean
    * @param clazz The class type of the bean
    * @return The named bean
    */
   public static <T> T getNamedBean(String name, Class<T> clazz) throws ReflectionException {
      if (SINGLETONS.containsKey(name)) {
         return Cast.as(SINGLETONS.get(name));
      }


      Reflect reflect;
      if (Config.hasProperty(name + ".@type")) {
         reflect = Reflect.onClass(Config.get(name + ".@type").asClass()).allowPrivilegedAccess();
      } else {
         reflect = Reflect.onClass(clazz).allowPrivilegedAccess();
      }

      boolean isSingleton = Config.get(name + ".singleton").asBoolean(false);

      List<Class<?>> paramTypes = new ArrayList<>();
      List<Object> values = new ArrayList<>();

      if (Config.hasProperty(name + ".@constructor")) {
         try {
            JsonEntry cons = Json.parse(Config.get(name + ".@constructor").asString());
            for (Map.Entry<String, JsonEntry> e : Iterables.asIterable(cons.propertyIterator())) {
               Type type = Types.parse(e.getKey());
               values.add(e.getValue().getAs(type));
               paramTypes.add(Types.asClass(type));
            }
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      Object bean;
      if (values.isEmpty()) {
         bean = reflect.create().get();
      } else {
         bean = reflect.create(paramTypes.toArray(new Class[1]),
                               values.toArray()).get();
      }

      bean = parameterizeObject(bean);
      if (isSingleton) {
         SINGLETONS.putIfAbsent(name, bean);
         bean = SINGLETONS.get(name);
      }
      return Cast.as(bean);
   }

   /**
    * Sets properties on an object using the values defined in the Config. Will set properties defined in the Config for
    * all of this object's super classes as well.
    *
    * @param object The object to parameterize
    * @return The object
    */
   public static <T> T parameterizeObject(T object) {
      if (object == null) {
         return null;
      }
      BeanMap beanMap = new BeanMap(object);
      List<Class<?>> list = ReflectionUtils.getAncestorClasses(object);
      Collections.reverse(list);
      for (Class<?> clazz : list) {
         doParametrization(beanMap, clazz.getName());
      }
      return object;
   }

}// END OF CLASS BeanUtils
