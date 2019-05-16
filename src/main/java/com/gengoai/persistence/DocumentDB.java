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
import com.gengoai.function.SerializableConsumer;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public abstract class DocumentDB implements Serializable, Iterable<DBDocument>, AutoCloseable {

   public final void add(DBDocument document) {
      Validation.notNull(document);
      if (document.getId() < 0) {
         document.setId(nextUniqueId());
      } else if (containsId(document.getId())) {
         throw new IllegalArgumentException(
            "Attempting to add a document with an ID already contained in the DB. Try update instead.");
      }
      document.markModified();
      insertDocument(document);
      updateIndexes(document, true);
   }

   public abstract boolean isClosed();

   public abstract long size();

   public abstract void commit();

   protected abstract boolean containsId(long id);

   public abstract void createIndex(String fieldName, IndexType indexType);

   protected abstract DBDocument delete(long id);

   public abstract void dropIndex(String fieldName);

   public abstract DBDocument get(long id);

   public abstract boolean hasIndex(String fieldName);

   public abstract List<Index> indexes();

   protected abstract void insertDocument(DBDocument document);

   protected abstract long nextUniqueId();

   protected abstract Index getIndex(String field);

   public Stream<DBDocument> query(Filter filter) {
      return filter.apply(this);
   }

   public abstract void drop();

   public abstract Stream<DBDocument> stream();

   public final DBDocument remove(long id) {
      if (containsId(id)) {
         DBDocument document = delete(id);
         updateIndexes(document, false);
         return document;
      }
      return null;
   }

   public final void update(Filter filter, DBDocument document) {
      filter.apply(this)
            .forEach(d -> {
               document.setId(d.getId());
               update(document, false);
            });
   }

   public final void update(DBDocument document) {
      update(document, false);
   }

   public final void update(DBDocument document, boolean upsert) {
      if (document.getId() < 0) {
         if (upsert) {
            add(document);
            return;
         }
      }
      if (!containsId(document.getId())) {
         throw new IllegalArgumentException(
            "Attempting to update a document that does not exist in the DB. Try add or upsert instead.");
      }
      DBDocument orig = get(document.getId());
      updateIndexes(orig, false);
      orig.merge(document);
      orig.markModified();
      updateDocument(orig);
      updateIndexes(orig, true);
   }

   public final void update(long id, SerializableConsumer<DBDocument> updater) {
      if (!containsId(id)) {
         throw new IllegalArgumentException(
            "Attempting to update a document that does not exist in the DB. Try add or upsert instead.");
      }
      DBDocument orig = get(id);
      updateIndexes(orig, false);
      updater.accept(orig);
      orig.markModified();
      updateDocument(orig);
      updateIndexes(orig, true);
   }

   protected abstract void updateDocument(DBDocument document);

   protected abstract void updateIndexes(DBDocument document, boolean add);

}//END OF DocumentDB
