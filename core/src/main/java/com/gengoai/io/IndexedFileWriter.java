package com.gengoai.io;

import com.gengoai.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.gengoai.io.IndexedFile.indexFileFor;

/**
 * <p>An Indexed File Writer writes lines to the output file indexed by a corresponding key. The index (keys -&gt; file
 * positions) is stored in another file in the same directory as the output file.</p>
 *
 * @author David B. Bracewell
 */
public final class IndexedFileWriter implements AutoCloseable {
   private final RandomAccessFile randomAccessFile;
   private final File outputFile;
   private final JsonWriter indexWriter;

   /**
    * Instantiates a new Indexed file writer.
    *
    * @param file the file
    * @throws IOException the io exception
    */
   public IndexedFileWriter(File file) throws IOException {
      this.outputFile = file;
      this.randomAccessFile = new RandomAccessFile(file, "rw");
      this.indexWriter = new JsonWriter(Resources.fromFile(indexFileFor(file)).setIsCompressed(true));
      this.indexWriter.beginDocument();
   }

   @Override
   public void close() throws IOException {
      indexWriter.endDocument();
      indexWriter.close();
      randomAccessFile.close();
   }

   /**
    * Gets the index file being created
    *
    * @return the index file
    */
   public File getIndexFile() {
      return indexFileFor(outputFile);
   }

   /**
    * Gets the file that output is being written to
    *
    * @return the output file
    */
   public File getOutputFile() {
      return outputFile;
   }

   /**
    * Writes a line to output, indexing it by the given key.
    *
    * @param key  the key to index on
    * @param line the line to output
    * @throws IOException something went wrong writing to the file
    */
   public void write(String key, String line) throws IOException {
      long lastPosition = randomAccessFile.getFilePointer();
      if (!line.endsWith("\n")) {
         line += "\n";
      }
      randomAccessFile.writeBytes(line);
      indexWriter.property(key, lastPosition);
   }
}//END OF IndexedFileWriter
