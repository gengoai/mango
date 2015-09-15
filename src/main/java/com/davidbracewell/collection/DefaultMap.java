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

package com.davidbracewell.collection;

import com.davidbracewell.conversion.Cast;
import com.google.common.collect.ForwardingMap;
import lombok.NonNull;

import java.io.Serializable;
import java.util.Map;
import java.util.function.Supplier;

/**
 * The type Default map.
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
class DefaultMap<K, V> extends ForwardingMap<K, V> implements Serializable {
  private static final long serialVersionUID = 1L;
  private final Map<K, V> backingMap;
  private final Supplier<V> defaultValueSupplier;

  /**
   * Instantiates a new Default map.
   *
   * @param backingMap           the backing map
   * @param defaultValueSupplier the default value supplier
   */
  public DefaultMap(@NonNull Map<K, V> backingMap, @NonNull Supplier<V> defaultValueSupplier) {
    this.backingMap = backingMap;
    this.defaultValueSupplier = defaultValueSupplier;
  }

  @Override
  protected Map<K, V> delegate() {
    return backingMap;
  }

  @Override
  public V get(Object key) {
    if (!containsKey(key)) {
      put(Cast.as(key), defaultValueSupplier.get());
    }
    return super.get(key);
  }

}//END OF DefaultMap
