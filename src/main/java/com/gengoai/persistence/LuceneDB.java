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

import com.gengoai.collection.DiskMap;
import com.gengoai.io.Resources;
import com.gengoai.json.Json;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.*;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TermQuery;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import java.io.IOException;
import java.nio.file.Paths;

/**
 * @author David B. Bracewell
 */
public class LuceneDB {
   public static final String ID_FIELD = "@id";
   final Directory directory;
   final DiskMap<String, Object> metadata;

   public LuceneDB(String location) {
      try {
         this.directory = FSDirectory.open(Paths.get(location));
         this.metadata = new DiskMap<>(Resources.fromFile(location)
                                                .getChild("metadata.db")
                                                .asFile()
                                                .orElseThrow(IllegalStateException::new))
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

   public void add(DBDocument document) {
      try (IndexWriter writer = getWriter()) {
         Document d = new Document();
         long nextID = ((Long) metadata.get(ID_FIELD)) + 1L;
         metadata.put(ID_FIELD, nextID);
         metadata.commit();

      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }


   private IndexWriter getWriter() {
      final IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
      try {
         return new IndexWriter(directory, config);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }


   public static Document toLucene(DBDocument document) {
      Document doc = new Document();


      return doc;
   }

   public static DBDocument toDBDocument(Document document) {
      DBDocument dbDoc = DBDocument.create();
      for (IndexableField field : document.getFields()) {
         try {
            dbDoc.put(field.name(), Json.parse(field.stringValue()));
         } catch (IOException e) {
            throw new RuntimeException(e);
         }
      }

      return dbDoc;
   }

   public DBDocument get(Object id) {
      try (IndexReader reader = DirectoryReader.open(directory)) {
         final IndexSearcher searcher = new IndexSearcher(reader);
         TopDocs docs = searcher.search(new TermQuery(new Term(ID_FIELD, id.toString())), 1);
         if (docs.scoreDocs.length > 0) {
            return toDBDocument(reader.document(docs.scoreDocs[0].doc));
         }
         return null;
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }

}//END OF LuceneDB
