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

import com.gengoai.annotation.JsonAdapter;
import com.gengoai.json.Json;
import com.gengoai.json.JsonEntry;
import com.gengoai.json.JsonMarshaller;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.util.Objects;

/**
 * @author David B. Bracewell
 */
@JsonAdapter(value = DBDocument.Marshaller.class, isHierarchical = false)
public final class DBDocument implements Serializable {
   public static final String ID_FIELD = "@id";
   public static final String LAST_MODIFIED = "@modified";
   public static final String VALUE = "@value";
   private static final long serialVersionUID = 1L;
   private transient JsonEntry doc;


   public DBDocument() {
      this.doc = JsonEntry.object();
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

   public static DBDocument create() {
      return new DBDocument();
   }

   public static DBDocument from(Object o) {
      return new DBDocument(JsonEntry.from(o));
   }

   public boolean contains(String field) {
      return doc.hasProperty(field);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {return true;}
      if (obj == null || getClass() != obj.getClass()) {return false;}
      final DBDocument other = (DBDocument) obj;
      return Objects.equals(this.doc, other.doc);
   }

   public JsonEntry get(String field) {
      return doc.getProperty(field);
   }

   public Object getAsObject(String field) {
      if (contains(field)) {
         JsonEntry e = get(field);
         if (e.isBoolean()) {
            return e.getAsBoolean();
         }
         if (e.isNumber()) {
            return e.getAsNumber();
         }
         if (e.isString()) {
            return e.getAsString();
         }
         if (e.isObject()) {
            return e;
         }
         if (e.isArray()) {
            return e.getAsArray();
         }
      }
      return null;
   }

   public static DBDocument fromJson(String json) {
      try {
         return new DBDocument(Json.parse(json));
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public long getId() {
      return doc.getLongProperty(ID_FIELD, -1);
   }

   public void setId(long id) {
      doc.addProperty(ID_FIELD, id);
   }

   public long getLastModified() {
      return doc.getLongProperty(LAST_MODIFIED, -1);
   }

   @Override
   public int hashCode() {
      return Objects.hash(doc);
   }

   public void markModified() {
      doc.addProperty(LAST_MODIFIED, System.currentTimeMillis());
   }

   public void merge(DBDocument document) {
      document.doc
         .propertyIterator()
         .forEachRemaining(e -> {
            String key = e.getKey();
            JsonEntry value = e.getValue();
            if (!key.equals(ID_FIELD) && !key.equals(LAST_MODIFIED)) {
               doc.addProperty(key, value);
            }
         });

   }

   public DBDocument put(final String key, Object value) {
      doc.addProperty(key, value);
      return this;
   }

   private void readObject(ObjectInputStream ois) throws ClassNotFoundException, IOException {
      ois.defaultReadObject();
      this.doc = Json.parse(ois.readUTF());
   }

   @Override
   public String toString() {
      return doc.toString();
   }

   private void writeObject(ObjectOutputStream out) throws IOException {
      out.defaultWriteObject();
      String json = doc.toString();
      System.out.println(json);
      out.writeUTF(json);
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
