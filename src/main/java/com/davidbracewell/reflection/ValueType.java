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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * @author David B. Bracewell
 */
public abstract class ValueType implements Serializable {
   private static final long serialVersionUID = 1L;

   public static final String TYPE_PROPERTY = "type";
   public static final String ELEMENT_TYPE_PROPERTY = "elementType";
   public static final String KEY_TYPE_PROPERTY = "keyType";
   public static final String VALUE_TYPE_PROPERTY = "valueType";

   public abstract Class<?> getType();

   public abstract Class<?>[] getParameterTypes();

   public boolean isCollection() {
      return false;
   }

   public boolean isMap() {
      return false;
   }

   public static ValueType fromConfig(String prefix) {
      if (Config.hasProperty(prefix)) {
         return new SimpleValueType(Config.get(prefix).asClass());
      }

      Map<String, Class> typeInfo = Config.getMap(prefix, String.class, Class.class);
      Class<?> typeClass = typeInfo.getOrDefault(TYPE_PROPERTY, String.class);


      //Check for a collection
      if (Collection.class.isAssignableFrom(typeClass)) {
         return new CollectionValueType(
               typeClass,
               typeInfo.getOrDefault(ELEMENT_TYPE_PROPERTY, String.class)
         );
      }

      //Check for a Map
      if (Map.class.isAssignableFrom(typeClass)) {
         return new MapValueType(
               typeClass,
               typeInfo.getOrDefault(KEY_TYPE_PROPERTY, String.class),
               typeInfo.getOrDefault(VALUE_TYPE_PROPERTY, String.class)
         );
      }


      return new SimpleValueType(typeClass);
   }

   public abstract <T> T convert(Object input);

   @Override
   public String toString() {
      return this.getClass()
                 .getSimpleName() + "{type=" + getType() + ", generics=" + Arrays.toString(getParameterTypes()) + "}";
   }

   private static class SimpleValueType extends ValueType implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Class<?> clazz;

      private SimpleValueType(Class<?> clazz) {
         this.clazz = clazz;
      }

      @Override
      public Class<?> getType() {
         return clazz;
      }

      @Override
      public Class<?>[] getParameterTypes() {
         return new Class<?>[0];
      }

      @Override
      public <T> T convert(Object input) {
         return Cast.as(Convert.convert(input, clazz));
      }

   }

   private static class CollectionValueType extends ValueType implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Class<?> collectionType;
      private final Class<?> genericType;


      public CollectionValueType(Class<?> collectionType, Class<?> genericType) {
         this.collectionType = collectionType;
         this.genericType = genericType;
      }

      @Override
      public Class<?> getType() {
         return collectionType;
      }

      @Override
      public Class<?>[] getParameterTypes() {
         return new Class<?>[]{genericType};
      }

      @Override
      public <T> T convert(Object input) {
         return Cast.as(Convert.convert(input, collectionType, genericType));
      }

      @Override
      public boolean isCollection() {
         return true;
      }
   }

   private static class MapValueType extends ValueType implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Class<?> mapType;
      private final Class<?> keyType;
      private final Class<?> valueType;

      public MapValueType(Class<?> mapType, Class<?> keyType, Class<?> valueType) {
         this.keyType = keyType;
         this.mapType = mapType;
         this.valueType = valueType;
      }

      @Override
      public Class<?> getType() {
         return mapType;
      }

      @Override
      public Class<?>[] getParameterTypes() {
         return new Class<?>[]{keyType, valueType};
      }

      @Override
      public <T> T convert(Object input) {
         return Cast.as(Convert.convert(input, mapType, keyType, valueType));
      }

      @Override
      public boolean isMap() {
         return true;
      }
   }

}//END OF ValueType
