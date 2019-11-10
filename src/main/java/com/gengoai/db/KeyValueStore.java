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
 *
 */

package com.gengoai.db;

import com.gengoai.specification.Specification;
import com.gengoai.Validation;
import com.gengoai.conversion.Cast;
import lombok.NonNull;

import java.io.File;
import java.util.Map;

/**
 * The interface Key value store.
 * <code>kv:[type]:[path];compressed=[true,false];readOnly=[true,false];namespace=STRING</code>
 *
 * @param <K> the type parameter
 * @param <V> the type parameter
 * @author David B. Bracewell
 */
public interface KeyValueStore<K, V> extends Map<K, V>, AutoCloseable {

   /**
    * Connect t.
    *
    * @param <K>              the type parameter
    * @param <V>              the type parameter
    * @param <T>              the type parameter
    * @param connectionString the connection string
    * @return the t
    */
   static <K, V, T extends KeyValueStore<K, V>> T connect(@NonNull String connectionString) {
      return connect(Specification.parse(connectionString));
   }

   /**
    * Connect t.
    *
    * @param <K>           the type parameter
    * @param <V>           the type parameter
    * @param <T>           the type parameter
    * @param specification the connection string
    * @return the t
    */
   static <K, V, T extends KeyValueStore<K, V>> T connect(@NonNull Specification specification) {
      Validation.checkArgument(specification.getSchema().equals("kv"),
                               "Unknown key-value specification: " + specification);
      boolean isCompressed = Boolean.parseBoolean(specification.getQueryParameterValue("compressed", "true"));
      boolean isReadOnly = Boolean.parseBoolean(specification.getQueryParameterValue("readOnly", "false"));
      String namespace = specification.getQueryParameterValue("namespace", specification.getPath());

      switch (specification.getProtocol()) {
         case "mem":
            return Cast.as(new InMemoryKeyValueStore<K, V>(namespace, isReadOnly));
         case "disk":
            return Cast.as(new MVKeyValueStore<K, V>(new File(specification.getPath()),
                                                     namespace,
                                                     isCompressed,
                                                     isReadOnly));
         default:
            throw new IllegalArgumentException("Invalid key-value schema: " + specification.getSchema());
      }
   }


   /**
    * Commit.
    */
   void commit();

   /**
    * Gets name space.
    *
    * @return the name space
    */
   String getNameSpace();

   /**
    * Size as long long.
    *
    * @return the long
    */
   long sizeAsLong();

   /**
    * Is read only boolean.
    *
    * @return the boolean
    */
   boolean isReadOnly();

}//END OF KeyValueStore
