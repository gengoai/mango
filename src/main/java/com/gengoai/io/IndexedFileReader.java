package com.gengoai.io;

import com.gengoai.collection.Sets;
import com.gengoai.function.SerializableFunction;
import com.gengoai.json.Json;
import com.gengoai.json.JsonWriter;
import org.apache.mahout.math.map.OpenObjectLongHashMap;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Set;

import static com.gengoai.io.IndexedFile.indexFileFor;

/**
 * The type Indexed file.
 *
 * @author David B. Bracewell
 */
public final class IndexedFileReader implements Serializable {
   private static final long serialVersionUID = 1L;
   private final File backingFile;
   private final boolean persistIndex;
   private volatile OpenObjectLongHashMap<String> index = null;
   private final SerializableFunction<String, String> lineToKey;

   /**
    * Instantiates a new Indexed file.
    *
    * @param backingFile the backing file
    * @param lineToKey   the line to key
    */
   public IndexedFileReader(File backingFile, SerializableFunction<String, String> lineToKey) {
      this(backingFile, true, lineToKey);
   }

   /**
    * Instantiates a new Indexed file.
    *
    * @param backingFile  the backing file
    * @param persistIndex the persist index
    * @param lineToKey    the line to key
    */
   public IndexedFileReader(File backingFile, boolean persistIndex, SerializableFunction<String, String> lineToKey) {
      this.backingFile = backingFile;
      this.persistIndex = persistIndex;
      this.lineToKey = lineToKey;
   }


   private void ensureIndex() {
      if (index == null) {
         synchronized (this) {
            if (index == null) {
               try {
                  if (indexFileFor(backingFile).exists()) {
                     index = loadIndexFor(backingFile);
                  } else {
                     try {
                        index = createIndex(backingFile, lineToKey, persistIndex);
                     } catch (IOException e2) {
                        index = createIndex(backingFile, lineToKey, false);
                     }
                  }
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }
   }

   private String readLineAt(long offset) throws IOException {
      try (RandomAccessFile raf = new RandomAccessFile(backingFile, "r")) {
         raf.seek(offset);
         return raf.readLine();
      }
   }


   public File getIndexFile() {
      return indexFileFor(backingFile);
   }

   public File getBackingFile() {
      return backingFile;
   }

   /**
    * Get string.
    *
    * @param key the key
    * @return the string
    * @throws IOException the io exception
    */
   public String get(String key) throws IOException {
      ensureIndex();
      if (index.containsKey(key)) {
         return readLineAt(index.get(key));
      }
      throw new IOException();
   }


   public int numberOfKeys() {
      ensureIndex();
      return index.size();
   }

   /**
    * Create index open object long hash map.
    *
    * @param rawFile   the raw file
    * @param lineToKey the line to key
    * @return the open object long hash map
    * @throws IOException the io exception
    */
   public static OpenObjectLongHashMap<String> createIndex(File rawFile,
                                                           SerializableFunction<String, String> lineToKey
                                                          ) throws IOException {
      return createIndex(rawFile, lineToKey, false);
   }


   public boolean containsKey(String key) {
      ensureIndex();
      return index.containsKey(key);
   }

   public Set<String> keySet() {
      ensureIndex();
      return Sets.asHashSet(index.keys().iterator());
   }


   public static OpenObjectLongHashMap<String> loadIndexFor(File rawFile) throws IOException {
      File indexFile = indexFileFor(rawFile);
      OpenObjectLongHashMap<String> index = new OpenObjectLongHashMap<>();
      Json.parse(Resources.fromFile(indexFile))
         .<Long>getAsMap(Long.class)
         .forEach(index::put);
      return index;
   }

   /**
    * Create index open object long hash map.
    *
    * @param rawFile   the raw file
    * @param lineToKey the line to key
    * @param saveIndex the save index
    * @return the open object long hash map
    * @throws IOException the io exception
    */
   public static OpenObjectLongHashMap<String> createIndex(File rawFile,
                                                           SerializableFunction<String, String> lineToKey,
                                                           boolean saveIndex
                                                          ) throws IOException {
      OpenObjectLongHashMap<String> index = new OpenObjectLongHashMap<>();
      try (RandomAccessFile raf = new RandomAccessFile(rawFile, "rw")) {
         long lastOffset = 0;
         String line;

         JsonWriter indexWriter = null;
         if (saveIndex) {
            indexWriter = new JsonWriter(Resources.fromFile(indexFileFor(rawFile)).setIsCompressed(true));
            indexWriter.beginDocument();
         }

         while ((line = raf.readLine()) != null) {
            String key = lineToKey.apply(line);
            index.put(key, lastOffset);
            if (saveIndex) {
               indexWriter.property(key, lastOffset);
            }
            lastOffset = raf.getFilePointer();
         }

         if (saveIndex) {
            indexWriter.endDocument();
            indexWriter.close();
         }
      }

      return index;
   }


}//END OF IndexedFileReader
