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

package com.davidbracewell.reflection;

import com.davidbracewell.config.Config;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
                Object val = null;
                if (Config.isBean(property)) {
                   val = Config.get(property);
                } else {
                   ValueType valueType = ValueType.fromConfig(className + "." + propertyName);
                   val = valueType.convert(Config.get(className, propertyName).asString());
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
      return parameterizeObject(Reflect.onClass(clazz).create().<T>get());
   }

   /**
    * Instantiates a named bean (defined via the Config) which can describe the class (name.class) and properties or
    * can instantiate a bean that is described as a script.
    *
    * @param name  The name of the bean
    * @param clazz The class type of the bean
    * @return The named bean
    */
   public static <T> T getNamedBean(String name, Class<T> clazz) throws ReflectionException {
      if (SINGLETONS.containsKey(name)) {
         return Cast.as(SINGLETONS.get(name));
      }

      if (Config.valueIsScript(name)) {
         return Config.get(name).as(clazz);
      }

      Reflect reflect;
      if (Config.hasProperty(name + ".class")) {
         reflect = Reflect.onClass(Config.get(name + ".class").asClass());
      } else {
         reflect = Reflect.onClass(clazz);
      }

      boolean isSingleton = Config.get(name + ".singleton").asBoolean(false);

      List<Class<?>> paramTypes = new ArrayList<>();
      List<Object> values = new ArrayList<>();
      List<String> rawValues = new ArrayList<>();
      boolean hadType = false;

      for (int i = 1; i <= 1000; i++) {
         String cParam = name + ".constructor.param" + i;
         if (Config.hasProperty(cParam) || Config.hasProperty(cParam, "value")) {
            ValueType valueType = ValueType.fromConfig(cParam);
            paramTypes.add(valueType.getType());
            String valueCfg = Config.hasProperty(cParam, "value")
                              ? Config.get(cParam, "value").asString()
                              : Config.get(cParam).asString();
            if (Config.hasProperty(cParam, "type")) {
               hadType = true;
            }
            rawValues.add(valueCfg);
            values.add(valueType.convert(valueCfg));
         }
      }

      if (paramTypes.size() != values.size()) {
         throw new IllegalStateException("Number of parameters does not equal the number of values");
      }

      BeanMap beanMap;
      if (values.isEmpty()) {
         beanMap = new BeanMap(parameterizeObject(reflect.create().get()));
      } else if (hadType) {
         beanMap = new BeanMap(parameterizeObject(reflect.create(paramTypes.toArray(new Class[paramTypes.size()]),
                                                                 values.toArray()).<T>get()));
      } else {
         Constructor<?> constructor = ClassDescriptorCache.getInstance()
                                                          .getClassDescriptor(clazz)
                                                          .getConstructors(false)
                                                          .stream()
                                                          .filter(
                                                             c -> c.getParameterTypes().length == values.size()
                                                                 )
                                                          .findFirst()
                                                          .orElse(null);
         if (constructor == null) {
            throw new ReflectionException("Cannot find a matching constructor for " +
                                             reflect.getReflectedClass() + " that takes " + values);
         }
         try {
            Object[] newValues = new Object[values.size()];
            for (int i = 0; i < newValues.length; i++) {
               newValues[i] = Convert.convert(rawValues.get(i), constructor.getParameterTypes()[i]);
            }
            beanMap = new BeanMap(parameterizeObject(constructor.newInstance(newValues)));
         } catch (InstantiationException | IllegalAccessException | InvocationTargetException e1) {
            throw new ReflectionException(e1);
         }
      }

      doParametrization(beanMap, name);
      Object bean = beanMap.getBean();
      if (isSingleton) {
         SINGLETONS.putIfAbsent(name, bean);
         bean = SINGLETONS.get(name);
      }
      return Cast.as(bean);
   }

   /**
    * Sets properties on an object using the values defined in the Config. Will set properties defined in the Config
    * for all of this object's super classes as well.
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
