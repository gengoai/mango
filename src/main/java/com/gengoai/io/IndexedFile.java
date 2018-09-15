package com.gengoai.io;

import com.gengoai.function.SerializableFunction;
import com.gengoai.io.resource.Resource;

import java.io.File;
import java.io.IOException;

/**
 * The type Indexed file.
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
    * Index file for file.
    *
    * @param file the file
    * @return the file
    */
   public static File indexFileFor(File file) {
      return new File(file.getAbsolutePath() + INDEX_EXT);
   }


   /**
    * Writer indexed file writer.
    *
    * @param resource the resource
    * @return the indexed file writer
    * @throws IOException the io exception
    */
   public static IndexedFileWriter writer(Resource resource) throws IOException {
      return new IndexedFileWriter(resource.asFile().orElseThrow(IOException::new));
   }

   /**
    * Writer indexed file writer.
    *
    * @param output the output
    * @return the indexed file writer
    * @throws IOException the io exception
    */
   public static IndexedFileWriter writer(File output) throws IOException {
      return new IndexedFileWriter(output);
   }

   /**
    * Reader indexed file reader.
    *
    * @param resource  the resource
    * @param lineToKey the line to key
    * @return the indexed file reader
    * @throws IOException the io exception
    */
   public static IndexedFileReader reader(Resource resource,
                                          SerializableFunction<String, String> lineToKey
                                         ) throws IOException {
      return new IndexedFileReader(resource.asFile().orElseThrow(IOException::new),
                                   true,
                                   lineToKey);
   }

   /**
    * Reader indexed file reader.
    *
    * @param input     the input
    * @param lineToKey the line to key
    * @return the indexed file reader
    * @throws IOException the io exception
    */
   public static IndexedFileReader reader(File input,
                                          SerializableFunction<String, String> lineToKey
                                         ) throws IOException {
      return new IndexedFileReader(input, true, lineToKey);
   }

}//END OF IndexedFile
