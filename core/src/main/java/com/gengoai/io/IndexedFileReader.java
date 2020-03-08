package com.gengoai.io;

import com.gengoai.Validation;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.io.Serializable;
import java.util.Collections;
import java.util.Map;
import java.util.Set;

import static com.gengoai.io.IndexedFile.indexFileFor;

/**
 * <p>A specialized file reader that retrieves lines in a file given a key, which it looks up in an index.</p>
 *
 * @author David B. Bracewell
 */
public final class IndexedFileReader implements Serializable {
   private static final long serialVersionUID = 1L;
   private final File backingFile;
   private volatile Map<String, Long> index = null;

   /**
    * Instantiates a new Indexed file.
    *
    * @param backingFile the backing file
    * @throws IllegalArgumentException If no index exists
    */
   public IndexedFileReader(File backingFile) {
      this.backingFile = backingFile;
      Validation.checkArgument(indexFileFor(backingFile).exists(),
                               () -> "No index file exists for " + backingFile);
   }


   private String readLineAt(long offset) throws IOException {
      try (RandomAccessFile raf = new RandomAccessFile(backingFile, "r")) {
         raf.seek(offset);
         return raf.readLine();
      }
   }

   /**
    * Gets index file.
    *
    * @return the index file
    */
   public File getIndexFile() {
      return indexFileFor(backingFile);
   }

   /**
    * Gets backing file.
    *
    * @return the backing file
    */
   public File getBackingFile() {
      return backingFile;
   }

   private Map<String, Long> getIndex() {
      if (index == null) {
         synchronized (this) {
            if (index == null) {
               try {
                  index = IndexedFile.loadIndexFor(backingFile);
               } catch (IOException e) {
                  throw new RuntimeException(e);
               }
            }
         }
      }
      return index;
   }

   /**
    * Reads the line from input associated with the given key
    *
    * @param key the key
    * @return the line associated with the given key
    * @throws IOException Something went wrong reading the file or the key is invalid
    */
   public String get(String key) throws IOException {
      if (getIndex().containsKey(key)) {
         return readLineAt(getIndex().get(key));
      }
      throw new IOException();
   }


   /**
    * Number of indexed keys
    *
    * @return number of keys in the index
    */
   public int numberOfKeys() {
      return getIndex().size();
   }

   /**
    * Checks if a key is in the index or not
    *
    * @param key the key
    * @return True - the key is in the index, False otherwise
    */
   public boolean containsKey(String key) {
      return getIndex().containsKey(key);
   }

   /**
    * The set of keys in the index
    *
    * @return the set of keys
    */
   public Set<String> keySet() {
      return Collections.unmodifiableSet(getIndex().keySet());
   }


}//END OF IndexedFileReader
