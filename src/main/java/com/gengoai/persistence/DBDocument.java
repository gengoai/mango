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

import com.gengoai.Validation;
import com.gengoai.annotation.JsonAdapter;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonMarshaller;

import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author David B. Bracewell
 */
@JsonAdapter(value = DBDocument.Marshaller.class, isHierarchical = false)
public final class DBDocument implements Serializable {
   private static final long serialVersionUID = 1L;
   public static final String ID_FIELD = "@id";
   public static final String LAST_MODIFIED = "@modified";
   public static final String VALUE = "@value";

   private final JsonEntry doc;

   public DBDocument() {
      this.doc = JsonEntry.object();
      this.doc.addProperty(ID_FIELD, UniqueId.nextId());
      this.doc.addProperty(LAST_MODIFIED, System.currentTimeMillis());
   }


   public DBDocument(long id, Object object) {
      this();
      JsonEntry entry = JsonEntry.from(object);
      if (entry.isObject()) {
         entry.propertyIterator()
              .forEachRemaining(e -> this.put(e.getKey(), e.getValue()));
      } else {
         put(VALUE, entry);
      }
      doc.addProperty(ID_FIELD, id);
   }

   private DBDocument(JsonEntry entry) {
      this.doc = entry;
   }


   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final DBDocument other = (DBDocument) obj;
      return Objects.equals(this.doc, other.doc);
   }

   public <T> T get(final String key, Type type) {
      return doc.getProperty(key).getAs(type);
   }

   public <T> T getAs(Class<T> clazz) {
      if (doc.hasProperty(VALUE)) {
         return doc.getProperty(VALUE).getAs(clazz);
      }
      return doc.getAs(clazz);
   }

   public <T> T getAs(Type type) {
      if (doc.hasProperty(VALUE)) {
         return doc.getProperty(VALUE).getAs(type);
      }
      return doc.getAs(type);
   }

   @Override
   public int hashCode() {
      return Objects.hash(doc);
   }

   public DBDocument put(final String key, Object value) {
      Validation.checkArgument(!ID_FIELD.equals(key),
                               () -> "Error attempting to override the ID field");
      Validation.checkArgument(!LAST_MODIFIED.equals(key),
                               () -> "Error attempting to override the LAST MODIFIED field");
      doc.addProperty(key, value);
      return this;
   }

   @Override
   public String toString() {
      return doc.toString();
   }

   static class Marshaller extends JsonMarshaller<DBDocument> {

      @Override
      protected DBDocument deserialize(JsonEntry entry, Type type) {
         return new DBDocument(entry);
      }

      @Override
      protected JsonEntry serialize(DBDocument dbDocument, Type type) {
         return dbDocument.doc;
      }
   }


}//END OF DBDocument
