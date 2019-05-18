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

package com.gengoai.persistence.mv;

import com.gengoai.Language;
import com.gengoai.Validation;
import com.gengoai.collection.Iterators;
import com.gengoai.collection.Lists;
import com.gengoai.function.Unchecked;
import com.gengoai.persistence.DBDocument;
import com.gengoai.persistence.DocumentDB;
import com.gengoai.persistence.Index;
import com.gengoai.persistence.IndexType;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.*;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class MVDocumentDB extends DocumentDB {
   public static final String DATA_MAP = "DATA";
   public static final String INDEX_MAP = "INDEXES";
   public static final String METADATA = "METADATA";
   private static final long serialVersionUID = 1L;
   private MVStore store;
   private MVMap<Long, String> data;
   private MVMap<String, IndexType> indexTypes;
   private Map<String, MVIndex> indexes;
   private Map<String, String> metadata;
   private Language language = null;
   private boolean readOnly;
   private final File dbLocation;

   public MVDocumentDB(File dbLocation) {
      this(dbLocation, false, null);
   }

   public MVDocumentDB(File dbLocation, boolean readOnly, Language language) {
      this.dbLocation = dbLocation;
      this.language = language;
      this.readOnly = readOnly;
   }

   private void open() {
      if (store == null || store.isClosed()) {
         synchronized (this) {
            if (store == null || store.isClosed()) {
               MVStore.Builder builder = new MVStore.Builder()
                                            .fileName(dbLocation.getAbsolutePath())
                                            .compress();
               if (readOnly) {
                  builder = builder.readOnly();
               }
               this.store = builder.open();
               this.metadata = store.openMap(METADATA);
               if (language == null) {
                  this.language = Language.fromString(
                     metadata.getOrDefault("Language", Language.fromLocale(Locale.getDefault()).name()));
               } else {
                  this.language = Language.fromLocale(Locale.getDefault());
               }

               if (!readOnly) {
                  metadata.put("Language", this.language.name());
               }

               this.data = store.openMap(DATA_MAP);
               this.indexTypes = store.openMap(INDEX_MAP);
               this.indexes = new HashMap<>();
               indexTypes.forEach(
                  Unchecked.biConsumer((field, type) -> indexes.put(field, new MVIndex(field, type, store))));
            }
         }
      }
   }

   @Override
   public void drop() {
      open();
      data.clear();
      indexes.values().forEach(Index::drop);
      indexes.clear();
      indexTypes.clear();
   }

   @Override
   public void close() throws Exception {
      open();
      data.getStore().close();
   }

   @Override
   public long size() {
      open();
      return data.sizeAsLong();
   }

   @Override
   public void commit() {
      open();
      data.getStore().commit();
   }

   @Override
   protected boolean containsId(long id) {
      open();
      return data.containsKey(id);
   }

   @Override
   public void createIndex(String fieldName, IndexType indexType) {
      open();
      if (!indexes.containsKey(fieldName)) {
         MVIndex index = new MVIndex(fieldName, indexType, store);
         indexes.put(fieldName, index);
         indexTypes.put(fieldName, indexType);
         data.values().forEach(json -> index.add(DBDocument.fromJson(json)));
      }
   }

   @Override
   protected DBDocument delete(long id) {
      open();
      String json = data.remove(id);
      return json == null ? null : DBDocument.fromJson(json);
   }

   @Override
   public void dropIndex(String fieldName) {
      open();
      Validation.checkArgument(indexes.containsKey(fieldName), "Index does not exist for field: " + fieldName);
      indexTypes.remove(fieldName);
      indexes.remove(fieldName).drop();
   }

   @Override
   public DBDocument get(long id) {
      open();
      String json = data.get(id);
      return json == null ? null : DBDocument.fromJson(json);
   }

   @Override
   public boolean hasIndex(String fieldName) {
      open();
      return indexes.containsKey(fieldName);
   }


   @Override
   public List<Index> indexes() {
      open();
      return Lists.asArrayList(indexes.values());
   }

   @Override
   protected void insertDocument(DBDocument document) {
      open();
      data.put(document.getId(), document.toString());
   }

   @Override
   public Iterator<DBDocument> iterator() {
      open();
      return Iterators.unmodifiableIterator(Iterators.transform(data.values().iterator(), DBDocument::fromJson));
   }

   @Override
   public boolean isClosed() {
      open();
      return store.isClosed();
   }

   @Override
   protected long nextUniqueId() {
      open();
      return data.lastKey() == null ? 0 : data.lastKey() + 1;
   }

   @Override
   protected Index getIndex(String field) {
      open();
      return indexes.get(field);
   }

   @Override
   public Stream<DBDocument> stream() {
      open();
      return data.values().stream().map(DBDocument::fromJson);
   }

   @Override
   protected void updateDocument(DBDocument document) {
      open();
      data.put(document.getId(), document.toString());
   }

   @Override
   protected void updateIndexes(DBDocument document, boolean add) {
      open();
      for (MVIndex index : indexes.values()) {
         if (add) {
            index.add(document);
         } else {
            index.remove(document);
         }
      }
   }
}//END OF MVDocumentStore
