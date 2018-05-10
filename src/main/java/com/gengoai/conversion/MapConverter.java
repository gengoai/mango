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

import com.gengoai.collection.Iterables;
import com.gengoai.collection.map.Maps;
import com.gengoai.function.SerializableSupplier;
import com.gengoai.logging.Logger;
import lombok.NonNull;

import java.util.Map;
import java.util.function.Function;

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
   private final SerializableSupplier<T> mapSupplier;
   private final Function<Object, V> valueConverter;

   public MapConverter(@NonNull Function<Object, K> keyConverter, @NonNull Function<Object, V> valueConverter, @NonNull final Class<? extends Map> mapClass) {
      this.keyConverter = keyConverter;
      this.valueConverter = valueConverter;
      this.mapSupplier = () -> Cast.as(Maps.create(Cast.as(mapClass)));
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
         return Cast.as(Maps.parseString(obj.toString(), keyConverter, valueConverter));
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
         Object o = Iterables.getFirst(Cast.as(obj, Iterable.class)).orElse(null);
         if (o != null && o instanceof Map.Entry) {
            for (Object inner : Cast.as(obj, Iterable.class)) {
               Map.Entry<?, ?> e = Cast.as(inner);
               map.put(keyConverter.apply(e.getKey()), valueConverter.apply(e.getValue()));
            }
            return map;
         }
      }

      try {
         return Cast.as(Maps.fillMap(map, Convert.convert(obj, Iterable.class), keyConverter, valueConverter));
      } catch (Exception e) {
         //ignore
      }

      log.fine("Cannot convert {0} to a Map.", obj.getClass());
      return null;
   }

}//END OF MapConverter

