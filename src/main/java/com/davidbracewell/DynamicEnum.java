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
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.davidbracewell.EnumValue.toKey;

/**
 * The type Dynamic enum.
 *
 * @author David B. Bracewell
 */
public final class DynamicEnum implements Serializable {
  private static final long serialVersionUID = 1L;

  private static final Map<String, EnumValue> GLOBAL_REPOSITORY = new ConcurrentHashMap<>();


  private DynamicEnum() {
    throw new IllegalAccessError();
  }

  /**
   * Is defined boolean.
   *
   * @param enumClass the enum class
   * @param name      the name
   * @return the boolean
   */
  public static boolean isDefined(@NonNull Class<? extends EnumValue> enumClass, @NonNull String name) {
    return GLOBAL_REPOSITORY.containsKey(toKey(enumClass, name));
  }

  /**
   * Value of t.
   *
   * @param <T>       the type parameter
   * @param enumClass the enum class
   * @param name      the name
   * @return the t
   */
  public static <T extends EnumValue> T valueOf(@NonNull Class<T> enumClass, @NonNull String name) {
    String key = toKey(enumClass, name);
    T toReturn = Cast.as(GLOBAL_REPOSITORY.get(key));
    if (toReturn == null) {
      throw new IllegalArgumentException("No enum constant " + key);
    }
    return toReturn;
  }

  /**
   * Register t.
   *
   * @param <T>       the type parameter
   * @param enumValue the enum value
   * @return the t
   */
  public static <T extends EnumValue> T register(@NonNull T enumValue) {
    return Cast.as(GLOBAL_REPOSITORY.computeIfAbsent(enumValue.canonicalName(), s -> enumValue));
  }

}//END OF DynamicEnum
