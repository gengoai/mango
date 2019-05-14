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

package com.gengoai.persistence;

import java.io.Serializable;

/**
 * The type Index.
 *
 * @author David B. Bracewell
 */
public abstract class Index implements Serializable {
   private final String fieldName;
   private final String indexName;
   private final IndexType indexType;

   /**
    * Instantiates a new Index.
    *
    * @param fieldName the field name
    * @param indexName the index name
    * @param indexType the index type
    */
   protected Index(String fieldName, String indexName, IndexType indexType) {
      this.fieldName = fieldName;
      this.indexName = indexName;
      this.indexType = indexType;
   }

   /**
    * Gets field name.
    *
    * @return the field name
    */
   public final String getFieldName() {
      return fieldName;
   }

   /**
    * Gets index name.
    *
    * @return the index name
    */
   public final String getIndexName() {
      return indexName;
   }

   /**
    * Gets index type.
    *
    * @return the index type
    */
   public final IndexType getIndexType() {
      return indexType;
   }

}//END OF Index
