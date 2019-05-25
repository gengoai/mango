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

package com.gengoai;

import com.gengoai.annotation.JsonAdapter;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonMarshaller;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import com.gengoai.reflection.TypeUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The type Param map.
 *
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
@JsonAdapter(ParamMap.Marshaller.class)
public class ParamMap<V extends ParamMap> implements Serializable, Copyable<ParamMap<V>> {
   private static final long serialVersionUID = 1L;
   private final Map<String, Parameter<?>> map = new HashMap<>();

   public static class Marshaller extends JsonMarshaller<ParamMap<?>> {

      @Override
      protected ParamMap<?> deserialize(JsonEntry entry, Type type) {
         try {
            ParamMap<?> map = Reflect.onClass(TypeUtils.asClass(type)).create().get();
            entry.propertyIterator().forEachRemaining(e -> {
               String key = e.getKey();
               if (!key.equals("@type") && map.map.containsKey(key)) {
                  ParamMap.Parameter param = map.map.get(key);
                  param.set(e.getValue().getAs(param.param.type));
               }
            });
            return map;
         } catch (ReflectionException e) {
            throw new RuntimeException(e);
         }
      }

      @Override
      protected JsonEntry serialize(ParamMap<?> paramMap, Type type) {
         JsonEntry object = JsonEntry.object();
         paramMap.map.forEach((k, v) -> object.addProperty(k, v.value));
         return object;
      }
   }

   @Override
   public ParamMap<V> copy() {
      return Copyable.deepCopy(this);
   }


   /**
    * Get t.
    *
    * @param <T>   the type parameter
    * @param param the param
    * @return the t
    */
   public <T> T get(Param<T> param) {
      if (map.containsKey(param.name)) {
         Parameter<?> parameter = map.get(param.name);
         parameter.param.checkType(param.type);
         return Cast.as(parameter.value);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param.name);
   }

   public <T> Param<T> getParameter(String name) {
      return Cast.as(map.get(name).param);
   }

   /**
    * Get t.
    *
    * @param <T>   the type parameter
    * @param param the param
    * @return the t
    */
   public <T> T get(String param) {
      if (map.containsKey(param)) {
         return Cast.as(map.get(param).value);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param);
   }

   /**
    * Gets or default.
    *
    * @param <T>          the type parameter
    * @param param        the param
    * @param defaultValue the default value
    * @return the or default
    */
   public <T> T getOrDefault(Param<T> param, T defaultValue) {
      if (map.containsKey(param.name)) {
         return get(param);
      }
      return defaultValue;
   }

   /**
    * Gets or default.
    *
    * @param <T>          the type parameter
    * @param param        the param
    * @param defaultValue the default value
    * @return the or default
    */
   public <T> T getOrDefault(String param, T defaultValue) {
      if (map.containsKey(param)) {
         Parameter<?> parameter = map.get(param);
         parameter.param.checkValue(defaultValue);
         return Cast.as(parameter.value);
      }
      return defaultValue;
   }

   /**
    * Parameter parameter.
    *
    * @param <T>      the type parameter
    * @param param    the param
    * @param defValue the def value
    * @return the parameter
    */
   public <T> Parameter<T> parameter(Param<T> param, T defValue) {
      return new Parameter<>(param, defValue);
   }

   /**
    * Set v.
    *
    * @param <T>   the type parameter
    * @param param the param
    * @param value the value
    * @return the v
    */
   public <T> V set(Param<T> param, T value) {
      if (map.containsKey(param.name)) {
         Parameter<?> parameter = map.get(param.name);
         parameter.param.checkType(param.type);
         parameter.value = Cast.as(value);
         return Cast.as(this);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param.name);
   }

   /**
    * Set v.
    *
    * @param <T>   the type parameter
    * @param param the param
    * @param value the value
    * @return the v
    */
   public <T> V set(String param, T value) {
      if (map.containsKey(param)) {
         Parameter<?> parameter = map.get(param);
         if (value instanceof JsonEntry) {
            value = Cast.<JsonEntry>as(value).getAs(parameter.param.type);
         }
         parameter.param.checkValue(value);
         parameter.value = Cast.as(value);
         return Cast.as(this);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param);
   }

   @Override
   public String toString() {
      return "ParamMap{" + map.values()
                              .stream()
                              .map(p -> p.param.name + "=" + p.value)
                              .collect(Collectors.joining(", ")) + "}";
   }

   /**
    * Update v.
    *
    * @param updater the updater
    * @return the v
    */
   public V update(Consumer<V> updater) {
      updater.accept(Cast.as(this));
      return Cast.as(this);
   }

   /**
    * The type Parameter.
    *
    * @param <T> the type parameter
    */
   public class Parameter<T> implements Serializable {
      private static final long serialVersionUID = 1L;
      /**
       * The Param.
       */
      public final Param<T> param;
      /**
       * The Value.
       */
      private T value;

      /**
       * Instantiates a new Parameter.
       *
       * @param param        the param
       * @param defaultValue the default value
       */
      private Parameter(Param<T> param, T defaultValue) {
         this.param = param;
         map.put(param.name, this);
         this.value = defaultValue;
      }

      public V set(T value) {
         this.value = value;
         return Cast.as(ParamMap.this);
      }

      public T value() {
         if (Number.class.isAssignableFrom(param.type)) {
            return Converter.convertSilently(value, param.type);
         }
         return value;
      }

      @Override
      public String toString() {
         return "Parameter{name=" + param.name + ", value=" + value + "}";
      }
   }


}//END OF ParamMap
