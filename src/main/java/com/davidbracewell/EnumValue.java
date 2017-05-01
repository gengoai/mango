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
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.ObjectStreamException;
import java.io.Serializable;

import static com.davidbracewell.DynamicEnum.register;

/**
 * <p>A enum like object that can have elements created at runtime as needed. Elements are singleton objects and can
 * have their equality safely checked using the <code>==</code> operator. A python script in the mango tools directory
 * (<code>tools/enumGen.py</code>) bootstraps the creation of basic EnumValues. Names associated with EnumValues are
 * normalized to be uppercase and have all whitespace replaced by underscores with consecutive whitespace becoming a
 * single underscore. Names must not contain a period (.) or be blank.</p>
 *
 * <p>Examples of common usage patterns for EnumValue types generated using <code>tools/enumGen.py</code> are as
 * follows:</p>
 *
 * <pre>
 * {@code
 *    //Enum values can be retrieved or created using the create method.
 *    MyEnum red = MyEnum.create("red");
 *    MyEnum blue = MyEnum.create("blue);
 *
 *    //Can emulate Java enum using the valueOf method
 *    MyEnum green = MyEnum.valueOf("gReEn");
 *
 *    //Can retrieve all instances in an unmodifiable set using the values method
 *    Set<MyEnum> allColors = MyEnum.values();
 * }
 * </pre>
 *
 *
 * <p>
 * If your EnumValue stores other information and want to ensure that declared instances are loaded in memory you can
 * use Mango's {@link com.davidbracewell.config.Preloader} to load during application startup.
 * </p>
 *
 * @author David B. Bracewell
 */
public abstract class EnumValue implements Tag, Serializable, Cloneable {
   private static final long serialVersionUID = 1L;
   private final String name;
   private final String fullName;

   /**
    * Instantiates a new enum value.
    *
    * @param name the name of the enum value
    */
   protected EnumValue(String name) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(name), "[" + name + " ]is invalid.");
      this.name = normalize(name);
      Preconditions.checkArgument(!name.contains(".") && name.length() > 0, name + " is invalid.");
      this.fullName = getClass().getCanonicalName() + "." + this.name;
   }

   protected EnumValue(String cannonicalName, String name) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(name), "[" + name + " ]is invalid.");
      this.name = normalize(name);
      Preconditions.checkArgument(!name.contains(".") && name.length() > 0, name + " is invalid.");
      this.fullName = cannonicalName + "." + this.name;
   }

   /**
    * <p>Normalizes a string to be uppercase and use and underscore in place of whitespace.</p>
    *
    * @param name the name to be normalized
    * @return the normalized version of the name
    * @throws NullPointerException if the name is null
    */
   static String normalize(@NonNull String name) {
      StringBuilder toReturn = new StringBuilder();
      boolean previousSpace = false;
      for( char c : name.toCharArray()){
         if( Character.isWhitespace(c) ){
            if( !previousSpace ){
               toReturn.append('_');
            }
            previousSpace = true;
         } else {
            previousSpace = false;
            toReturn.append(Character.toUpperCase(c));
         }
      }
      return toReturn.toString();//name.toUpperCase().replaceAll("\\s+", "_");
   }


   @Override
   public String name() {
      return name;
   }

   /**
    * <p>Retrieves the canonical name of the enum value, which is the canonical name of the enum class and the specified
    * name of the enum value.</p>
    *
    * @return the canonical name of the enum value
    */
   public final String canonicalName() {
      return fullName;
   }

   @Override
   public final String toString() {
      return name;
   }

   @Override
   public boolean isInstance(Tag value) {
      return value != null && this.equals(value);
   }

   /**
    * <p>Resolves the deserialized object by calling {@link DynamicEnum#register(EnumValue)} ensuring that only one
    * reference exists for this enum value.</p>
    *
    * @return the new or existing enum value
    * @throws ObjectStreamException if there is an error in the object stream
    */
   protected final Object readResolve() throws ObjectStreamException {
      return register(this);
   }

   @Override
   public final int hashCode() {
      return canonicalName().hashCode();
   }

   @Override
   public final boolean equals(Object obj) {
      return obj != null && obj instanceof EnumValue && canonicalName().equals(Cast.<EnumValue>as(obj).canonicalName());
   }

   @Override
   protected final Object clone() throws CloneNotSupportedException {
      throw new CloneNotSupportedException();
   }


}//END OF EnumValue
