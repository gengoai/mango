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

package com.gengoai.conversion;


import com.gengoai.logging.Loggable;
import lombok.NonNull;

import java.util.function.Function;

/**
 * Converts objects into an enum value.
 *
 * @author David B. Bracewell
 */
public class EnumConverter<T extends Enum<T>> implements Function<Object, T>, Loggable {

  private final Class<T> enumClass;

  /**
   * Default Constructor
   *
   * @param enumClass Class information for the type of enum we want to convert to
   */
  public EnumConverter(@NonNull Class<T> enumClass) {
    this.enumClass = enumClass;
  }

  @Override
  public T apply(Object obj) {
    if (obj == null) {
      return null;
    } else if (enumClass.isAssignableFrom(obj.getClass())) {
      return Cast.as(obj);
    }
    String string = obj.toString();
    try {
      return Enum.valueOf(enumClass, string);
    } catch (Exception e) {
      //ignore
    }
    if (string.contains(".")) {
      try {
        return Enum.valueOf(enumClass, string.substring(string.lastIndexOf(".") + 1));
      } catch (Exception e) {
        //ignore
      }
    }

    logFine("Could not convert {0} to an enum of type {1}.", obj.getClass(), enumClass);
    return null;
  }

}// END OF EnumConverter
