package com.gengoai.io;

import com.gengoai.function.SerializableFunction;
import com.gengoai.io.resource.Resource;
import com.gengoai.json.Json;
import com.gengoai.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>Helper class for creating Indexed file readers and writers.</p>
 *
 * @author David B. Bracewell
 */
public final class IndexedFile {
   /**
    * The constant INDEX_EXT.
    */
   public static final String INDEX_EXT = ".idx.json.gz";

   private IndexedFile() {
      throw new IllegalAccessError();
   }

   /**
    * Gets the File location of the index for the given file.
    *
    * @param file the file whose index we want
    * @return the file containing the index
    */
   public static File indexFileFor(File file) {
      return new File(file.getAbsolutePath() + INDEX_EXT);
   }


   /**
    * Creates an {@link IndexedFileWriter} for the given resource
    *
    * @param resource the resource (file) to write to and for which an index will be created
    * @return the indexed file writer
    * @throws IOException Something went wrong opening the file
    */
   public static IndexedFileWriter writer(Resource resource) throws IOException {
      return new IndexedFileWriter(resource.asFile().orElseThrow(IOException::new));
   }

   /**
    * Creates an {@link IndexedFileWriter} for the given resource
    *
    * @param output the file to write to and for which an index will be created
    * @return the indexed file writer
    * @throws IOException Something went wrong opening the file
    */
   public static IndexedFileWriter writer(File output) throws IOException {
      return new IndexedFileWriter(output);
   }

   /**
    * Creates an {@link IndexedFileReader} for the given resource
    *
    * @param file the indexed resource to read
    * @return the indexed file reader
    * @throws IOException tSomething went wrong opening the file
    */
   public static IndexedFileReader reader(File file) throws IOException {
      return new IndexedFileReader(file);
   }

   /**
    * Creates an {@link IndexedFileReader} for the given resource
    *
    * @param file the indexed resource to read
    * @return the indexed file reader
    * @throws IOException tSomething went wrong opening the file
    */
   public static IndexedFileReader reader(Resource file) throws IOException {
      return new IndexedFileReader(file.asFile().orElseThrow(IOException::new));
   }

   /**
    * Create index open object long hash map.
    *
    * @param rawFile   the raw file
    * @param lineToKey the line to key
    * @return the open object long hash map
    * @throws IOException the io exception
    */
   public static Map<String, Long> indexFile(File rawFile,
                                             SerializableFunction<String, String> lineToKey
                                            ) throws IOException {
      return indexFile(rawFile, lineToKey, false);
   }

   /**
    * Create index open object long hash map.
    *
    * @param fileToIndex the raw file
    * @param lineToKey   the line to key
    * @param saveIndex   the save index
    * @return the open object long hash map
    * @throws IOException the io exception
    */
   public static Map<String, Long> indexFile(File fileToIndex,
                                             SerializableFunction<String, String> lineToKey,
                                             boolean saveIndex
                                            ) throws IOException {
      Map<String, Long> index = new HashMap<>();
      try (RandomAccessFile raf = new RandomAccessFile(fileToIndex, "rw")) {
         long lastOffset = 0;
         String line;

         JsonWriter indexWriter = null;
         if (saveIndex) {
            indexWriter = new JsonWriter(Resources.fromFile(indexFileFor(fileToIndex)).setIsCompressed(true));
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


   /**
    * Load index for map.
    *
    * @param rawFile the raw file
    * @return the map
    * @throws IOException the io exception
    */
   public static Map<String, Long> loadIndexFor(File rawFile) throws IOException {
      File indexFile = indexFileFor(rawFile);
      return Json.parse(Resources.fromFile(indexFile)).getAsMap(Long.class);
   }

}//END OF IndexedFile
