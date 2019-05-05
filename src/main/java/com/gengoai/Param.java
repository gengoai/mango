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

import com.gengoai.reflection.TypeUtils;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * The type Param.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class Param<T> implements Serializable {
   private static final long serialVersionUID = 1L;
   /**
    * The Name.
    */
   public final String name;
   /**
    * The Type.
    */
   public final Class<T> type;


   /**
    * Instantiates a new Param.
    *
    * @param name the name
    * @param type the type
    */
   public Param(String name, Class<T> type) {
      this.name = name;
      this.type = type;
   }

   /**
    * Instantiates a new Param.
    *
    * @param name the name
    * @param type the type
    */
   public Param(String name, Type type) {
      this.name = name;
      this.type = TypeUtils.asClass(type);
   }

   /**
    * Bool param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<Boolean> boolParam(String name) {
      return new Param<>(name, Boolean.class);
   }

   /**
    * Double param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<Double> doubleParam(String name) {
      return new Param<>(name, Double.class);
   }

   /**
    * Float param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<Float> floatParam(String name) {
      return new Param<>(name, Float.class);
   }

   /**
    * Int param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<Integer> intParam(String name) {
      return new Param<>(name, Integer.class);
   }

   /**
    * Long param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<Long> longParam(String name) {
      return new Param<>(name, Long.class);
   }

   /**
    * Str param param.
    *
    * @param name the name
    * @return the param
    */
   public static Param<String> strParam(String name) {
      return new Param<>(name, String.class);
   }

   /**
    * Check type.
    *
    * @param type the type
    */
   public void checkType(Class<?> type) {
      if (!this.type.isAssignableFrom(type)) {
         throw new IllegalArgumentException(
            "Invalid type: " + type.getSimpleName() + ", expecting " + this.type.getSimpleName()
         );
      }
   }

   /**
    * Check value.
    *
    * @param value the value
    */
   public void checkValue(Object value) {
      if (Number.class.isAssignableFrom(type) && value instanceof Number) {
         return;
      }
      if (!type.isInstance(value)) {
         throw new IllegalArgumentException(
            "Invalid value: " + value + ", expecting " + type.getSimpleName()
         );
      }
   }

   @Override
   public boolean equals(Object o) {
      if (this == o) return true;
      if (!(o instanceof Param)) return false;
      Param<?> param = (Param<?>) o;
      return Objects.equals(name, param.name) &&
                Objects.equals(type, param.type);
   }

   @Override
   public int hashCode() {
      return Objects.hash(name, type);
   }

   @Override
   public String toString() {
      return "Param{" +
                "name='" + name + '\'' +
                ", type=" + type +
                '}';
   }
}//END OF Param
