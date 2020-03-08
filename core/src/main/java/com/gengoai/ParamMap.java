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

import com.gengoai.annotation.JsonHandler;
import com.gengoai.conversion.Cast;
import com.gengoai.conversion.Converter;
import com.gengoai.conversion.TypeConversionException;
import com.gengoai.json.JsonEntry;
import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import com.gengoai.reflection.TypeUtils;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * A Map containing {@link ParameterDef}s and their values
 *
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
@JsonHandler(ParamMap.Marshaller.class)
public class ParamMap<V extends ParamMap> implements Serializable, Copyable<ParamMap<V>> {
   private static final long serialVersionUID = 1L;
   private final Map<String, Parameter<?>> map = new HashMap<>();

   @Override
   public ParamMap<V> copy() {
      return Copyable.deepCopy(this);
   }

   /**
    * Gets the value of the given {@link ParameterDef}
    *
    * @param <T>   the type of the Param parameter
    * @param param the param whose value we want
    * @return the value of the param
    * @throws IllegalArgumentException if the parm is unknown to this map
    */
   public <T> T get(@NonNull ParameterDef<T> param) {
      if (map.containsKey(param.name)) {
         Parameter<?> parameter = map.get(param.name);
         parameter.param.checkType(param.type);
         return Cast.as(parameter.value);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param.name);
   }

   /**
    * Gets the value of the given {@link ParameterDef} name
    *
    * @param <T>   the type of the Param parameter
    * @param param the param whose value we want
    * @return the value of the param
    * @throws IllegalArgumentException if the parm is unknown to this map
    */
   public <T> T get(String param) {
      if (map.containsKey(param)) {
         return Cast.as(map.get(param).value);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param);
   }

   /**
    * Gets the value of the given {@link ParameterDef} or the default value if the Param is not in the map
    *
    * @param <T>          the type of the Param parameter
    * @param param        the param whose value we want
    * @param defaultValue the default value to return if the param is not in the map
    * @return the value of the param or the default value if the Param is not in the map
    */
   public <T> T getOrDefault(@NonNull ParameterDef<T> param, T defaultValue) {
      if (map.containsKey(param.name)) {
         return get(param);
      }
      return defaultValue;
   }

   /**
    * Gets the value of the given {@link ParameterDef} or the default value if the Param is not in the map
    *
    * @param <T>          the type of the Param parameter
    * @param param        the param whose value we want
    * @param defaultValue the default value to return if the param is not in the map
    * @return the value of the param or the default value if the Param is not in the map
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
    * Gets the {@link ParameterDef} with the given name in the map.
    *
    * @param <T>  the Param type parameter
    * @param name the name of the param
    * @return the param
    * @throws IllegalArgumentException if the parm is unknown to this map
    */
   public <T> ParameterDef<T> getParam(String name) {
      if (map.containsKey(name)) {
         return Cast.as(map.get(name).param);
      }
      throw new IllegalArgumentException("Unknown Parameter: " + name);
   }

   /**
    * Creates a {@link Parameter} of the given {@link ParameterDef} with the given default value registering it with
    * this {@link ParamMap} and returning the created Parameter.
    *
    * @param <T>      the parameter type parameter
    * @param param    the parameter definition
    * @param defValue the default value
    * @return the parameter
    */
   public <T> Parameter<T> parameter(@NonNull ParameterDef<T> param, T defValue) {
      return new Parameter<>(param, defValue);
   }

   /**
    * Gets the parameter names associated with this ParamMap
    *
    * @return the set
    */
   public Set<String> parameterNames() {
      return Collections.unmodifiableSet(map.keySet());
   }

   /**
    * Sets the value for the given parameter
    *
    * @param <T>   the type parameter
    * @param param the param
    * @param value the value
    * @return this ParamMap
    */
   public <T> V set(@NonNull ParameterDef<T> param, T value) {
      if (map.containsKey(param.name)) {
         return map.get(param.name).set(Cast.as(value));
      }
      throw new IllegalArgumentException("Unknown Parameter: " + param.name);
   }

   /**
    * Sets the value for the given parameter
    *
    * @param <T>   the type parameter
    * @param param the param
    * @param value the value
    * @return this ParamMap
    */
   public <T> V set(String param, T value) {
      if (map.containsKey(param)) {
         try {
            return map.get(param).set(Cast.as(Converter.convert(value, map.get(param).param.type)));
         } catch (TypeConversionException e) {
            throw new IllegalArgumentException("Unknown Parameter: " + param);
         }
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

//   /**
//    * Updates the ParamMap using the given Map of param names and values
//    *
//    * @param parameters the parameters
//    */
//   public void update(@NonNull Map<String, ?> parameters) {
//      parameters.forEach((k, v) -> {
//         if (map.containsKey(k)) {
//            Parameter<?> param = map.get(k);
//            try {
//               param.set(Cast.as(Converter.convert(v, param.param.type)));
//            } catch (TypeConversionException e) {
//               throw new RuntimeException(e);
//            }
//         }
//      });
//   }

   /**
    * Updates the ParamMap using the given consumer.
    *
    * @param updater the updater
    * @return this ParamMap
    */
   public V update(@NonNull Consumer<V> updater) {
      updater.accept(Cast.as(this));
      return Cast.as(this);
   }

   /**
    * The type Marshaller.
    */
   public static class Marshaller extends com.gengoai.json.JsonMarshaller<ParamMap<?>> {

      @Override
      @SuppressWarnings("unchecked")
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

   /**
    * Represents a {@link ParameterDef} and its value within a {@link ParamMap}.
    *
    * @param <T> the type parameter
    */
   @EqualsAndHashCode(callSuper = false)
   @ToString
   public class Parameter<T> implements Serializable {
      private static final long serialVersionUID = 1L;
      /**
       * The Parameter definition.
       */
      public final ParameterDef<T> param;
      /**
       * The Value.
       */
      private T value;

      private Parameter(ParameterDef<T> param, T defaultValue) {
         this.param = param;
         map.put(param.name, this);
         this.value = defaultValue;
      }

      /**
       * Sets the value of the parameter.
       *
       * @param value the value
       * @return the param map the parameter belongs to
       */
      public V set(T value) {
         if (value instanceof JsonEntry && param.type != JsonEntry.class) {
            value = Cast.<JsonEntry>as(value).getAs(param.type);
         }
         param.checkValue(value);
         this.value = Cast.as(value);
         return Cast.as(ParamMap.this);
      }

      /**
       * Gets the value of the parameter
       *
       * @return the value
       */
      public T value() {
//         if (Number.class.isAssignableFrom(param.type)) {
//            return Converter.convertSilently(value, param.type);
//         }
         return value;
      }
   }


}//END OF ParamMap
