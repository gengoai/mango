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

import lombok.NonNull;

import java.io.ObjectStreamException;
import java.io.Serializable;

import static com.davidbracewell.DynamicEnum.register;

/**
 * <p>A enum value associated with a {@link DynamicEnum}. Standard usage is for enum types to to extend
 * {@link EnumValue} and have a static <code>DynamicEnum</code> field in the extended class. </p>
 *
 * @author David B. Bracewell
 */
public abstract class EnumValue implements Tag, Serializable, Cloneable {
  private static final long serialVersionUID = 1L;
  private final String name;
  private final String fullName;

  /**
   * Instantiates a new Enum value.
   *
   * @param name the name of the enum value
   */
  protected EnumValue(String name) {
    this.name = normalize(name);
    this.fullName = getClass().getCanonicalName() + "." + name;
  }

  /**
   * Normalize string.
   *
   * @param name the name
   * @return the string
   */
  static String normalize(@NonNull String name) {
    return name.toUpperCase().replaceAll("\\s+", "_");
  }

  /**
   * To key string.
   *
   * @param enumClass the enum class
   * @param name      the name
   * @return the string
   */
  static String toKey(@NonNull Class<? extends EnumValue> enumClass, String name) {
    return enumClass.getCanonicalName() + "." + normalize(name);
  }

  @Override
  public String name() {
    return name;
  }

  /**
   * Canonical name string.
   *
   * @return the string
   */
  public String canonicalName() {
    return fullName;
  }

  @Override
  public String toString() {
    return name;
  }

  @Override
  public boolean isInstance(Tag value) {
    return value != null && this.equals(value);
  }

  /**
   * Read resolve object.
   *
   * @return the object
   * @throws ObjectStreamException the object stream exception
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
    return this == obj;
  }

  @Override
  protected final Object clone() throws CloneNotSupportedException {
    throw new CloneNotSupportedException();
  }


}//END OF EnumValue
