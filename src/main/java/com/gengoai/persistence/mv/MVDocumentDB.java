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

import com.gengoai.collection.Iterators;
import com.gengoai.json.JsonEntry;
import com.gengoai.persistence.DBDocument;
import com.gengoai.persistence.DocumentDB;
import com.gengoai.persistence.Index;
import com.gengoai.persistence.IndexType;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author David B. Bracewell
 */
public class MVDocumentDB extends DocumentDB {
   private static final long serialVersionUID = 1L;
   private final LazyMVMap<Long, DBDocument> data;
   private final LazyMVMap<String, String> indexes;
   private final File dbLocation;

   protected MVDocumentDB(File dbLocation) {
      this.data = new LazyMVMap<>(dbLocation, "DB");
      this.indexes = new LazyMVMap<>(dbLocation, "@indexes");
      this.dbLocation = dbLocation;
   }


   public static void main(String[] args) throws Exception {
      MVDocumentDB db = new MVDocumentDB(new File("/home/ik/people.db"));
      db.createIndex("NAME", IndexType.Unique);
      db.add(DBDocument.create()
                       .put("NAME", "David")
                       .put("AGE", 34));
      db.add(DBDocument.create()
                       .put("NAME", "Darth")
                       .put("AGE", 43));
      db.add(DBDocument.create()
                       .put("NAME", "Jiajun")
                       .put("AGE", 31));
      db.add(DBDocument.create()
                       .put("NAME", "Jerome")
                       .put("AGE", 34));
      db.commit();

      db.createIndex("NAME", IndexType.Unique);
      db.createIndex("NAME", IndexType.Unique);


      System.out.println(db.get("AGE", 34));
      System.out.println(db.get("NAME", "Darth"));
      System.out.println(db.get("NAME", "Jiajun"));
   }

   @Override
   public void close() throws Exception {
      data.getStore().close();
   }

   @Override
   protected void insertDocument(DBDocument document) {
      data.put(document.getId(), document);
   }

   @Override
   public Iterator<DBDocument> iterator() {
      return Iterators.unmodifiableIterator(data.values().iterator());
   }

   @Override
   protected void updateDocument(DBDocument document) {
      data.put(document.getId(), document);
   }

   @Override
   protected void updateIndexes(DBDocument document, boolean add) {

   }

   @Override
   protected boolean containsId(long id) {
      return data.containsKey(id);
   }

   @Override
   protected long nextUniqueId() {
      return data.lastKey() == null ? 0 : data.lastKey() + 1;
   }

   @Override
   public void commit() {
      data.getStore().commit();
   }

   @Override
   public void createIndex(String fieldName, IndexType indexType) {
      if (indexes.containsKey(fieldName)) {
         throw new IllegalArgumentException("Index already exists: " + fieldName + " = " + indexes.get(fieldName));
      }
   }

   @Override
   public void dropIndex(String fieldName) {

   }

   @Override
   public List<Index> indexes() {
      return null;
   }

   @Override
   protected DBDocument delete(long id) {
      return data.remove(id);
   }

   @Override
   public DBDocument get(long id) {
      return data.get(id);
   }

   @Override
   public List<DBDocument> get(String fieldName, Object value) {
      JsonEntry je = JsonEntry.from(value);
      return data.values()
                 .stream()
                 .filter(doc -> doc.contains(fieldName) && doc.get(fieldName).equals(je))
                 .collect(Collectors.toList());
   }
}//END OF MVDocumentStore
