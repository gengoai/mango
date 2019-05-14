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

import com.gengoai.function.SerializableConsumer;
import com.gengoai.json.JsonEntry;
import com.gengoai.tuple.Tuple2;
import org.h2.mvstore.MVMap;
import org.h2.mvstore.MVStore;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class MVDocumentDB implements DocumentDB {
   private static final long serialVersionUID = 1L;
   private final File dbLocation;
   private volatile transient MVMap<Long, DBDocument> map;
   private volatile transient Map<String, MVMap<String, Long>> indexedFields;

   protected MVDocumentDB(File dbLocation) {
      this.dbLocation = dbLocation;
   }

   @Override
   public void add(DBDocument document) {
      if (document.getId() < 0) {
         document.setId(nextKey());
      } else if (map().containsKey(document.getId())) {
         throw new IllegalArgumentException(
            "Attempting to add a document with an ID already contained in the DB. Try update instead.");
      }
      document.markModified();
      map().put(document.getId(), document);
   }

   @Override
   public void close() throws Exception {
      if (map != null) {
         map.getStore().close();
      }
   }

   @Override
   public void commit() {
      if (map != null) {
         map.getStore().commit();
      }
   }

   public List<DBDocument> get(String field, Object value) {
      JsonEntry vJe = JsonEntry.from(value);
      String vstr = vJe.toString();
      map();
      String index = "index_" + field;
      if (indexedFields.containsKey(index)) {
         MVMap<String, Long> ii = indexedFields.get(index);
         if (ii.containsKey(vstr)) {
            return Collections.singletonList(map.get(ii.get(vstr)));
         }
         return Collections.emptyList();
      }
      return map.values()
                .stream()
                .filter(d -> d.contains(field) && d.get(field).equals(vJe))
                .collect(Collectors.toList());
   }

   @Override
   public void addIndex(String fieldName) {
      MVStore store = map().getStore();
      String index = "index_" + fieldName;
      if (store.hasMap(index)) {
         throw new IllegalArgumentException("Index already exists for field: " + fieldName);
      }
      MVMap<String, Long> ii = store.openMap(index);
      indexedFields.put(index, ii);
      for (DBDocument doc : map.values()) {
         if (doc.contains(fieldName)) {
            ii.put(doc.get(fieldName).toString(), doc.getId());
         }
      }
   }

   @Override
   public Iterator<DBDocument> iterator() {
      return map().values().iterator();
   }

   private MVMap<Long, DBDocument> map() {
      if (map == null) {
         synchronized (this) {
            if (map == null) {
               MVStore store = new MVStore.Builder()
                                  .fileName(dbLocation.getAbsolutePath())
                                  .compress()
                                  .open();
               map = store.openMap("DB");
               indexedFields = store.getMapNames()
                                    .stream()
                                    .filter(n -> n.startsWith("index_"))
                                    .map(n -> $(n, store.<String, Long>openMap(n)))
                                    .collect(Collectors.toMap(Tuple2::getKey,
                                                              Tuple2::getValue));

            }
         }

      }
      return map;
   }

   private long nextKey() {
      return map().lastKey() == null ? 0L : map().lastKey() + 1;
   }

   @Override
   public DBDocument remove(long id) {
      return map().remove(id);
   }

   @Override
   public void update(DBDocument document, boolean upsert) {
      if (document.getId() < 0) {
         if (upsert) {
            add(document);
            return;
         }
      }
      if (map().containsKey(document.getId())) {
         throw new IllegalArgumentException(
            "Attempting to update a document that does not exist in the DB. Try add or upsert instead.");
      }
      DBDocument orig = map().get(document.getId());
      orig.merge(document);
      orig.markModified();
      map().put(document.getId(), orig);
   }

   @Override
   public void update(long id, SerializableConsumer<DBDocument> updater) {
      if (map().containsKey(id)) {
         throw new IllegalArgumentException(
            "Attempting to update a document that does not exist in the DB. Try add or upsert instead.");
      }
      DBDocument orig = map().get(id);
      updater.accept(orig);
      orig.markModified();
      map().put(id, orig);
   }


   public static void main(String[] args) throws Exception {
      MVDocumentDB db = new MVDocumentDB(new File("/home/ik/people.db"));
//      db.add(DBDocument.create()
//                       .put("NAME", "David")
//                       .put("AGE", 34));
//      db.add(DBDocument.create()
//                       .put("NAME", "Jiajun")
//                       .put("AGE", 31));
//      db.addIndex("NAME");
      db.commit();

      System.out.println(db.get("NAME", "David"));
      System.out.println(db.get("NAME", "John"));
      System.out.println(db.get("NAME", "Jiajun"));
   }

}//END OF MVDocumentStore
