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
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * The type Indexes.
 *
 * @author David B. Bracewell
 */
public interface Indexes {

  /**
   * New synchronized index.
   *
   * @param <TYPE> the type parameter
   * @param index  the index
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
   * @param supplier         the supplier
   * @return the counter
   * @throws IOException the io exception
   */
  static <TYPE> Index<TYPE> read(@NonNull StructuredFormat structuredFormat, @NonNull Resource resource, @NonNull Function<String, TYPE> deserializer, @NonNull Supplier<Index<TYPE>> supplier) throws IOException {
    Index<TYPE> index = supplier.get();
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
    return read(structuredFormat, resource, deserializer, HashMapIndex::new);
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

  /**
   * Read json index.
   *
   * @param <TYPE>   the type parameter
   * @param resource the resource
   * @param keyClass the key class
   * @return the index
   * @throws IOException the io exception
   */
  static <TYPE> Index<TYPE> readJson(@NonNull Resource resource, @NonNull Class<TYPE> keyClass) throws IOException {
    return read(StructuredFormat.JSON, resource, keyClass);
  }


}//END OF Indexes
