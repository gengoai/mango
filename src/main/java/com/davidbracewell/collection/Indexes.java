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


import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredFormat;
import com.davidbracewell.io.structured.StructuredReader;
import lombok.NonNull;

import java.io.IOException;
import java.util.Arrays;
import java.util.function.Function;

/**
 * The type Indexes.
 *
 * @author David B. Bracewell
 */
public interface Indexes {


  /**
   * New index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  @SafeVarargs
  static <TYPE> Index<TYPE> create(TYPE... items) {
    Index<TYPE> index = new HashMapIndex<>();
    if (items != null) {
      index.addAll(Arrays.asList(items));
    }
    return index;
  }

  /**
   * New index.
   *
   * @param <TYPE> the type parameter
   * @param items  the items
   * @return the index
   */
  static <TYPE> Index<TYPE> create(@NonNull Iterable<TYPE> items) {
    Index<TYPE> index = new HashMapIndex<>();
    index.addAll(items);
    return index;
  }

  /**
   * New synchronized index.
   *
   * @param <TYPE> the type parameter
   * @return the index
   */
  static <TYPE> Index<TYPE> synchronizedIndex(@NonNull Index<TYPE> index) {
    return new SynchronizedIndex<>(index);
  }

  /**
   * Unmodifiable index.
   *
   * @param <TYPE> the type parameter
   * @param index  the index
   * @return the index
   */
  static <TYPE> Index<TYPE> unmodifiableIndex(@NonNull final Index<TYPE> index) {
    return new UnmodifiableIndex<>(index);
  }


  /**
   * Read counter.
   *
   * @param <TYPE>           the type parameter
   * @param structuredFormat the structured format
   * @param resource         the resource
   * @param keyType          the key type
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Index<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Class<TYPE> keyType) throws IOException {
    return read(structuredFormat, resource, str -> Convert.convert(str, keyType));
  }

  /**
   * Read counter.
   *
   * @param <TYPE>           the type parameter
   * @param structuredFormat the structured format
   * @param resource         the resource
   * @param deserializer     the deserializer
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Index<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Function<String, TYPE> deserializer) throws IOException {
    Index<TYPE> index = new HashMapIndex<>();
    try (StructuredReader reader = structuredFormat.createReader(resource)) {
      reader.beginDocument();
      while (reader.peek() != ElementType.END_DOCUMENT) {
        index.add(deserializer.apply(reader.nextValue().asString()));
      }
      reader.endDocument();
    }
    return index;
  }

  /**
   * Read csv counter.
   *
   * @param <TYPE>   the type parameter
   * @param resource the resource
   * @param keyClass the key class
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Index<TYPE> readCSV(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.CSV, resource, keyClass);
  }

  static <TYPE> Index<TYPE> readJson(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.JSON, resource, keyClass);
  }


}//END OF Indexes
