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

package com.davidbracewell.conversion;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.function.Serialized;
import com.davidbracewell.logging.Logger;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.google.common.base.Function;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * <p> Converts an <code>Object</code> to a <code>Map</code>. </p> <p> If the object is a collection, array, iterator,
 * etc. it must be in the format a[i] = key, a[i+1] = value. </p> <p> Strings are parsed using : as the key value
 * separator. </p> <p> If the key or value converter are null, null will be used. </p>
 *
 * @param <K> The key type
 * @param <V> The value type
 * @param <T> The Map type
 * @author David B. Bracewell
 */
public class MapConverter<K, V, T extends Map<K, V>> implements Function<Object, T> {

  private static final Logger log = Logger.getLogger(MapConverter.class);

  private final Function<Object, K> keyConverter;
  private final Supplier<T> mapSupplier;
  private final Function<Object, V> valueConverter;

  public MapConverter(Function<Object, K> keyConverter, Function<Object, V> valueConverter, final Class<?> mapClass) {
    Preconditions.checkNotNull(mapClass);
    Preconditions.checkNotNull(keyConverter);
    Preconditions.checkNotNull(valueConverter);
    Preconditions.checkArgument(Map.class.isAssignableFrom(mapClass), "Must specify a class that implements Map.");
    this.keyConverter = keyConverter;
    this.valueConverter = valueConverter;
    this.mapSupplier = Serialized.<T>supplier(() -> {
        if (BiMap.class.equals(mapClass)) {
          return Cast.as(HashBiMap.create());
        } else if (Map.class.equals(mapClass)) {
          return Cast.as(new HashMap());
        }
        try {
          return Reflect.onClass(mapClass).create().get();
        } catch (ReflectionException e) {
          throw Throwables.propagate(e);
        }
      }
    );
  }

  @Override
  @SuppressWarnings("unchecked")
  public T apply(Object obj) {
    if (obj == null) {
      return null;
    }
    T map = mapSupplier.get();

    if (obj instanceof Map) {
      for (Map.Entry<?, ?> entry : ((Map<?, ?>) obj).entrySet()) {
        map.put(keyConverter.apply(entry.getKey()), valueConverter.apply(entry.getValue()));
      }
      return map;
    }

    if (obj instanceof CharSequence) {
      return Cast.as(Collect.fromString(obj.toString(), keyConverter, valueConverter));
    } else if (obj.getClass().isArray() && Map.Entry.class.isAssignableFrom(obj.getClass().getComponentType())) {
      for (Object o : Convert.convert(obj, Iterable.class)) {
        Map.Entry<?, ?> e = Cast.as(o);
        map.put(keyConverter.apply(e.getKey()), valueConverter.apply(e.getValue()));
      }
      return map;
    } else if (obj instanceof Map.Entry) {
      Map.Entry<?, ?> e = Cast.as(obj);
      map.put(keyConverter.apply(e.getKey()), valueConverter.apply(e.getValue()));
      return map;
    } else if (obj instanceof Iterable) {
      Object o = Iterables.getFirst(Cast.as(obj, Iterable.class), null);
      if (o != null && o instanceof Map.Entry) {
        for (Object inner : Cast.as(obj, Iterable.class)) {
          Map.Entry<?, ?> e = Cast.as(inner);
          map.put(keyConverter.apply(e.getKey()), valueConverter.apply(e.getValue()));
        }
        return map;
      }
    }

    try {
      return Cast.as(Collect.fillMap(map, Convert.convert(obj, Iterable.class), keyConverter, valueConverter));
    } catch (Exception e) {
      //ignore
    }

    log.fine("Cannot convert {0} to a Map.", obj.getClass());
    return null;
  }

}//END OF MapConverter

