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

import com.davidbracewell.string.StringUtils;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.ConcurrentHashMap;

/**
 * <p> A enum-like class that allows for the addition of enum constants. Standard usage is for enum types to to extend
 * {@link EnumValue} and have a static <code>DynamicEnum</code> field in the extended class. </p>
 *
 * @param <E> the type parameter
 * @author David B. Bracewell
 */
public final class DynamicEnum<E extends EnumValue> implements Serializable {

  private static final long serialVersionUID = 1L;
  private volatile ConcurrentHashMap<String, E> values = new ConcurrentHashMap<>();

  /**
   * Normalizes the string.
   *
   * @param input the input
   * @return the string
   */
  public static String normalize(String input) {
    return StringUtils.trim(input.toUpperCase()).replaceAll(StringUtils.MULTIPLE_WHITESPACE, "_");
  }

  /**
   * Registers an enum value into the enum
   *
   * @param value the enum value
   * @return the e
   */
  public final E register(E value) {
    return values.computeIfAbsent(value.name(), v -> value);
//    values.putIfAbsent(value.name(), value);
//    return values.get(value.name());
  }


  /**
   * Gets the enum value associated with a name or throws an <code>IllegalArgumentException</code> if the name is
   * invalid.
   *
   * @param name the name whose enum value we want.
   * @return the enum value
   */
  public final E valueOf(String name) {
    String norm = normalize(name);
    if (values.containsKey(norm)) {
      return values.get(norm);
    }
    for (E v : values()) {
      if (v.fullName().equals(name)) {
        return v;
      }
    }
    throw new IllegalArgumentException(norm + " is not a valid enum value");
  }


  /**
   * Determines if an enum value for the given name is defined or not
   *
   * @param name the name of the enum value
   * @return True if an enum value exists with the given name, False otherwise
   */
  public final boolean isDefined(String name) {
    return values.containsKey(normalize(name));
  }

  /**
   * All enum values known for this enum
   *
   * @return the collection of enum values
   */
  public final Collection<E> values() {
    return Collections.unmodifiableCollection(values.values());
  }


}//END OF DynamicEnum
