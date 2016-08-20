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
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.logging.Logger;

import java.io.IOException;
import java.io.StringReader;
import java.util.*;
import java.util.function.Function;

/**
 * Converts objects to collections and iterables
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
      return Cast.as(object);
    } else if (object instanceof Iterator<?>) {
      return Collect.asIterable(Cast.<Iterator<?>>as(object));
    } else if (object instanceof Map) {
      return Cast.as(Cast.<Map>as(object).entrySet());
    } else if (object.getClass().isArray()) {
      return Cast.as(Collect.asIterable(object, Object.class));
    } else if (object instanceof CharSequence) {
      List<String> list = new ArrayList<>();
      try (CSVReader reader = CSV.builder().removeEmptyCells().reader(new StringReader(object.toString()))) {
        List<String> row;
        while ((row = reader.nextRow()) != null) {
          list.addAll(row);
        }
        return Cast.as(list);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }
    return Cast.as(Collections.singletonList(object));
  };

  private static final Logger log = Logger.getLogger(CollectionConverter.class);


  private CollectionConverter() {
  }

  /**
   * Converts an object to a collection
   *
   * @param collectionType the collection type
   * @return the conversion function
   */
  public static Function<Object, Collection<?>> COLLECTION(final Class<? extends Collection> collectionType) {
    return input -> {
      if (input == null) {
        return null;
      } else if (collectionType == null) {
        return null;
      } else if (collectionType.isAssignableFrom(input.getClass())) {
        return Cast.as(input);
      }
      Iterable<?> iterable = ITERABLE.apply(input);
      if (iterable != null) {
        Collection<Object> collection = Cast.as(Collect.create(collectionType));
        if (collection == null) {
          log.fine("Unable to create a collection of type {0}", collectionType);
          return null;
        }
        for (Object o : iterable) {
          collection.add(o);
        }
        return collection;
      }

      log.fine("Unable to convert {0} to a Collection of type {1}", input.getClass(), collectionType);
      return null;
    };
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
    return input -> {
      if (input == null) {
        return null;
      } else if (collectionType == null) {
        log.fine("No collection type was given, returning null");
        return null;
      } else if (componentType == null) {
        log.fine("No component type was given, returning null");
        return null;
      }

      Iterable<?> iterable = ITERABLE.apply(input);
      if (iterable != null) {
        Collection<T> collection = Cast.as(Collect.create(collectionType));
        if (collection == null) {
          log.fine("Unable to create a collection of type {0}", collectionType);
          return null;
        }
        int added = 0;
        for (Object o : iterable) {
          T obj = Convert.convert(o, componentType);
          if (obj != null) {
            collection.add(obj);
            added++;
          }
        }
        if (added == 0) {
          log.fine("Unable to convert components to {0}", componentType);
          return null;
        }
        return collection;
      }

      log.fine("Unable to convert {0} to a Collection of type {1}", input.getClass(), collectionType);
      return null;
    };
  }


}//END OF CollectionConverter
