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

import com.gengoai.json.JsonEntry;

import java.io.Serializable;
import java.util.stream.LongStream;

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

   public abstract void drop();

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

   public abstract LongStream lookup(Object value);

   public abstract LongStream range(Object lower, Object upper);

   protected void validate(Object value, long id) {
      if (indexType == IndexType.Unique) {
         if (lookup(value).filter(ii -> ii != id).count() > 0) {
            throw new IllegalStateException("Unique Key Violation: " + getFieldName() + " = " + value);
         }
      }
   }

   public final void add(DBDocument document) {
      if (document.contains(getFieldName())) {
         add(document.get(getFieldName()), document.getId());
      }
   }

   private void add(Object value, long docId) {
      validate(value, docId);
      indexValue(value, docId);
   }


   protected abstract void indexValue(Object value, long docId);

   public final void add(JsonEntry value, long docId) {
      if (value.isArray()) {
         value.elementIterator().forEachRemaining(e -> add(e, docId));
      } else if (value.isNumber()) {
         add(value.getAsNumber(), docId);
      } else if (value.isBoolean()) {
         add(value.getAsBoolean(), docId);
      } else {
         add(value.getAsString(), docId);
      }
   }


   public void remove(DBDocument document) {
      if (document.contains(getFieldName())) {
         remove(document.get(getFieldName()), document.getId());
      }
   }

   protected void remove(JsonEntry value, long docId) {
      if (value.isArray()) {
         value.elementIterator().forEachRemaining(e -> remove(e, docId));
      } else if (value.isNumber()) {
         remove(value.getAsNumber(), docId);
      } else if (value.isBoolean()) {
         remove(value.getAsBoolean(), docId);
      } else {
         remove(value.getAsString(), docId);
      }
   }

   protected abstract void remove(Object value, long docId);

}//END OF Index
