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
import lombok.NonNull;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

/**
 * <p>Encapsulates the definition of an object including methods to read its definition via configuration and convert
 * values to is type. Definition via configuration is done in the following manner:</p> <p>For simple types:</p>
 * <pre>{@code
 *    property = TypeName
 *    # or
 *    property.type = TypeName
 *    # or
 *    property {
 *       _ = TypeName
 *    }
 * }</pre>
 * <p>where <code>property</code> is the name of the object whose value type we want. Collections can be defined in the
 * following manner:</p>
 * <pre>{@code
 *    collection {
 *       type = List
 *       elementType = Integer
 *    }
 * } </pre>
 * <p>where <code>collection</code> is the name of the collection'whose value type we want, type is the collection type,
 * and elementType is the generic type (omitted elementType will result in String being used). Maps can be defined in
 * the following manner:</p>
 * <pre>{@code
 *    map {
 *       type = List
 *       keyType = Integer
 *       valueType = Double
 *    }
 * } </pre>
 * <p>where <code>map</code> is the name of the map whose value type we want, type is the map type, keyType is the class
 * of the key, and valueType is the class of the value (omitted keyType or valueType will result in String being
 * used).</p>
 *
 * @author David B. Bracewell
 */
public abstract class ValueType implements Serializable {
   private static final long serialVersionUID = 1L;
   /**
    * The property component defining the class type of a value
    */
   public static final String TYPE_PROPERTY = "type";
   /**
    * The property component defining the class type of the elements in a collection
    */
   public static final String ELEMENT_TYPE_PROPERTY = "elementType";
   /**
    * The property component defining the class type of the keys in a map
    */
   public static final String KEY_TYPE_PROPERTY = "keyType";
   /**
    * The property component defining the class type of the values in a map
    */
   public static final String VALUE_TYPE_PROPERTY = "valueType";

   /**
    * Gets the type, i.e. class, this value type will convert.
    *
    * @return the type this value represents and will convert to
    */
   public abstract Class<?> getType();

   /**
    * Gets the parameter types, e.g. elementType, keyType, valueType.
    *
    * @return An array of the parameter, e.g. generic types.
    */
   public abstract Class<?>[] getParameterTypes();

   /**
    * Determines if this value type represents a collection
    *
    * @return True if this value type is a collection, False otherwise
    */
   public boolean isCollection() {
      return false;
   }

   /**
    * Determines if this value type represents a map
    *
    * @return True if this value type is a map, False otherwise
    */
   public boolean isMap() {
      return false;
   }

   /**
    * Creates a <code>ValueType</code> for the given configuration property. T
    *
    * @param property the property name to create a ValueType from
    * @return the value type representing the property
    */
   public static ValueType fromConfig(String property) {
      //If the property is valid and we cannot find a "property.type" then the value of the property represents its simple type.
      if (Config.hasProperty(property) && !Config.hasProperty(property, TYPE_PROPERTY)) {
         return new SimpleValueType(Config.get(property).asClass());
      }

      if( Config.isBean(property) ){
         Object o = Config.get(property);
         return new SimpleValueType(o.getClass());
      }

      //Read in the value type parameters from the configuration using the property as the prefix
      Map<String, Class> typeInfo = Config.getMap(property, String.class, Class.class);
      //Set the type class to the type property defaulting to String if not found
      Class<?> typeClass = typeInfo.getOrDefault(TYPE_PROPERTY, String.class);


      //Check for a collection
      if (Collection.class.isAssignableFrom(typeClass)) {
         return new CollectionValueType(typeClass,
                                        typeInfo.getOrDefault(ELEMENT_TYPE_PROPERTY, String.class)
         );
      }

      //Check for a Map
      if (Map.class.isAssignableFrom(typeClass)) {
         return new MapValueType(typeClass,
                                 typeInfo.getOrDefault(KEY_TYPE_PROPERTY, String.class),
                                 typeInfo.getOrDefault(VALUE_TYPE_PROPERTY, String.class)
         );
      }

      return new SimpleValueType(typeClass);
   }

   /**
    * Converts the given input to the type wrapped by the ValueType
    *
    * @param <T>   the wrapped type
    * @param input the object to be converted
    * @return the result of the conversion
    */
   public abstract <T> T convert(Object input);

   @Override
   public String toString() {
      return this.getClass()
                 .getSimpleName() + "{type=" + getType() + ", generics=" + Arrays.toString(getParameterTypes()) + "}";
   }


   /**
    * Creates a value type for the given class
    *
    * @param clazz the class information for the value type
    * @return the value type
    */
   public static ValueType of(@NonNull Class<?> clazz) {
      return new SimpleValueType(clazz);
   }

   private static class SimpleValueType extends ValueType implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Class<?> clazz;

      SimpleValueType(Class<?> clazz) {
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
         if (clazz.isInstance(input)) {
            return Cast.as(input);
         }
         return Cast.as(Convert.convert(input, clazz));
      }

   }

   private static class CollectionValueType extends ValueType implements Serializable {
      private static final long serialVersionUID = 1L;

      private final Class<?> collectionType;
      private final Class<?> genericType;


      /**
       * Instantiates a new Collection value type.
       *
       * @param collectionType the collection type
       * @param genericType    the generic type
       */
      CollectionValueType(Class<?> collectionType, Class<?> genericType) {
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

      /**
       * Instantiates a new Map value type.
       *
       * @param mapType   the map type
       * @param keyType   the key type
       * @param valueType the value type
       */
      MapValueType(Class<?> mapType, Class<?> keyType, Class<?> valueType) {
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
