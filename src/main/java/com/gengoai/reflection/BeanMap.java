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

import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.logging.Logger;
import com.gengoai.tuple.Tuple2;

import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p> A Map based interface for accessing the properties of a bean object. </p> <p> Property information is cached
 * using the {@link BeanDescriptorCache}. </p>
 *
 * @author David B. Bracewell
 */
public class BeanMap extends AbstractMap<String, Object> {

   private static final Logger log = Logger.getLogger(BeanMap.class);
   private final Object bean;
   private final BeanDescriptor beanDescriptor;

   /**
    * Constructs a bean map for a given bean
    *
    * @param bean The bean
    */
   public BeanMap(Object bean) {
      this.bean = bean;
      this.beanDescriptor = BeanDescriptorCache.getInstance().get(bean.getClass());
   }


   /**
    * @return The names of the setter methods
    */
   public Set<String> getSetters() {
      return beanDescriptor.getWriteMethodNames();
   }

   /**
    * @return The bean in the bean map
    */
   public Object getBean() {
      return bean;
   }

   @Override
   public boolean containsKey(Object arg0) {
      return beanDescriptor.hasReadMethod(arg0.toString());
   }

   @Override
   public boolean containsValue(Object arg0) {
      for (String key : keySet()) {
         if (arg0.equals(get(key))) {
            return true;
         }
      }
      return false;
   }

   @Override
   public Set<Entry<String, Object>> entrySet() {
      return this.keySet().stream()
                 .map(key -> Cast.<Map.Entry<String, Object>>as(Tuple2.of(key, get(key))))
                 .collect(Collectors.toSet());
   }

   @Override
   public Object get(Object arg0) {
      Method m = beanDescriptor.getReadMethod(arg0.toString());
      if (m != null) {
         try {
            return m.invoke(bean);
         } catch (Exception e) {
            log.finest(e);
         }
      }
      return null;
   }

   /**
    * Gets the type of the parameter on the setter method.
    *
    * @param key The setter method
    * @return A <code>Class</code> representing the parameter type of the setter method
    */
   public Class<?> getType(String key) {
      if (beanDescriptor.hasReadMethod(key)) {
         return beanDescriptor.getReadMethod(key).getReturnType();
      } else if (beanDescriptor.hasWriteMethod(key)) {
         Class<?>[] paramTypes = beanDescriptor.getWriteMethod(key).getParameterTypes();
         if (paramTypes.length > 0) {
            return paramTypes[0];
         }
      }
      return null;
   }

   @Override
   public boolean isEmpty() {
      return beanDescriptor.numberOfReadMethods() == 0;
   }

   @Override
   public Set<String> keySet() {
      return beanDescriptor.getReadMethodNames();
   }

   @Override
   public Object put(String arg0, Object arg1) {
      Method m = beanDescriptor.getWriteMethod(arg0);
      if (m != null) {
         try {
            Class<?> clazz = getType(arg0);
            if (clazz.isAssignableFrom(arg1.getClass())) {
               return m.invoke(bean, arg1);
            }
            if (Map.class.isAssignableFrom(clazz)) {
               return m.invoke(bean, Converter.convertSilently(arg1, clazz, Object.class, Object.class));
            } else if (Collection.class.isAssignableFrom(clazz)) {
               return m.invoke(bean, Converter.convertSilently(arg1, clazz, Object.class));
            }
            return m.invoke(bean, Converter.convertSilently(arg1, clazz));
         } catch (Exception e) {
            throw new RuntimeException(e);
         }
      } else {
         log.finest("{0} is not a setter on {1}.", arg0, bean.getClass());
      }
      return null;
   }

   @Override
   public int size() {
      return beanDescriptor.numberOfReadMethods();
   }

   @Override
   public Collection<Object> values() {
      return beanDescriptor.getReadMethodNames().stream().map(this::get).collect(Collectors.toList());
   }

}// END OF CLASS BeanMap
