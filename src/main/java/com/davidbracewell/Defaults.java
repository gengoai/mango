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

import java.util.Map;

import static com.davidbracewell.collection.map.Maps.asMap;
import static com.davidbracewell.tuple.Tuples.$;

/**
 * <p>Provides default values for a number of different types including all primitives and their boxed variants.</p>
 *
 * @author David B. Bracewell
 */
public final class Defaults {

  private final static Map<Class<?>, Object> values = asMap($(Short.class, (short) 0),
                                                            $(Long.class, 0L),
                                                            $(Integer.class, 0),
                                                            $(Double.class, 0d),
                                                            $(Float.class, 0f),
                                                            $(Boolean.class, false),
                                                            $(Character.class, (char) 0),
                                                            $(Byte.class, (byte) 0),
                                                            $(byte.class, (byte) 0),
                                                            $(short.class, (short) 0),
                                                            $(long.class, 0L),
                                                            $(int.class, 0),
                                                            $(double.class, 0d),
                                                            $(float.class, 0f),
                                                            $(boolean.class, false),
                                                            $(char.class, (char) 0),
                                                            $(String.class, StringUtils.EMPTY)
  );

  private Defaults() {
    throw new IllegalAccessError();
  }

  /**
   * Default value t.
   *
   * @param <T>   the type parameter
   * @param clazz the clazz
   * @return the t
   */
  public static <T> T defaultValue(Class<T> clazz) {
    if (clazz == null) {
      return null;
    }
    return Cast.as(values.get(clazz));
  }

}//END OF Defaults
