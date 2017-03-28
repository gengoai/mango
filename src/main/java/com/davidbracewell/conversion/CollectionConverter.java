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
import com.davidbracewell.function.SerializableFunction;
import com.davidbracewell.logging.Loggable;
import com.davidbracewell.string.StringUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Function;

import static com.davidbracewell.conversion.Cast.as;

/**
 * Converts objects to collections, iterators, and iterables
 *
 * @author David B. Bracewell
 */
public final class CollectionConverter {
   /**
    * Converts an object to an iterable. Will only return null if the input is null
    */
   public static final Function<Object, Iterable<?>> ITERABLE = object -> {
      if (object == null) {
         return Collections.emptyList();
      } else if (object instanceof Iterable<?>) {
         return as(object);
      } else if (object instanceof Iterator<?>) {
         return Collect.asIterable(Cast.<Iterator<?>>as(object));
      } else if (object instanceof Map) {
         return as(Cast.<Map>as(object).entrySet());
      } else if (object.getClass().isArray()) {
         return as(Collect.asIterable(object, Object.class));
      } else if (object instanceof CharSequence) {
         return StringUtils.split(object.toString()
                                        .replaceAll("^\\[", "")
                                        .replaceAll("\\]$", ""), ',');
      }
      return as(Collections.singletonList(object));
   };


   /**
    * Converts an object to an iterator
    */
   public static final Function<Object, Iterator<?>> ITERATOR = object -> {
      Iterable<?> iterable = ITERABLE.apply(object);
      if (iterable == null) {
         return null;
      }
      return iterable.iterator();
   };

   private CollectionConverter() {
      throw new IllegalAccessError();
   }

   /**
    * Converts an object to a collection
    *
    * @param collectionType the collection type
    * @return the conversion function
    */
   public static Function<Object, Collection<?>> COLLECTION(final Class<? extends Collection> collectionType) {
      return as(new CollectionConverterImpl<>(collectionType, null));
   }

   /**
    * Converts an object to a collection and converts the components as well.
    *
    * @param <T>            the component type parameter
    * @param collectionType the collection type
    * @param componentType  the component type
    * @return the conversion function
    */
   public static <T> Function<Object, Collection<T>> COLLECTION(final Class<? extends Collection> collectionType, final Class<T> componentType) {
      return new CollectionConverterImpl<>(collectionType, componentType);
   }

   private static class CollectionConverterImpl<T> implements SerializableFunction<Object, Collection<T>>, Loggable {
      private static final long serialVersionUID = 1L;
      private final Class<? extends Collection> collectionType;
      private final Class<T> componentType;

      private CollectionConverterImpl(Class<? extends Collection> collectionType, Class<T> componentType) {
         this.collectionType = collectionType;
         this.componentType = componentType;
      }

      @Override
      public Collection<T> apply(Object input) {

         if (input == null) {
            return null;
         } else if (collectionType == null) {
            logFine("No collection type was given, returning null");
            return null;
         }

         Iterable<?> iterable = ITERABLE.apply(input);
         if (iterable != null) {
            Collection<T> collection = as(Collect.create(collectionType));
            if (collection == null) {
               logFine("Unable to create a collection of type {0}", collectionType);
               return null;
            }
            int added = 0;
            if (componentType == null) {
               for (Object o : iterable) {
                  collection.add(as(o));
                  added++;
               }
            } else {
               for (Object o : iterable) {
                  T obj = Convert.convert(o, componentType);
                  if (obj != null) {
                     collection.add(obj);
                     added++;
                  }
               }
            }
            if (added == 0) {
               logFine("Unable to convert components to {0}", componentType);
               return null;
            }
            return collection;
         }


         logFine("Unable to convert {0} to a Collection of type {1}", input.getClass(), collectionType);
         return null;
      }
   }


}//END OF CollectionConverter
