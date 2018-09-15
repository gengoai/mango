package com.gengoai.io;

import com.gengoai.json.JsonWriter;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import static com.gengoai.io.IndexedFile.indexFileFor;

/**
 * @author David B. Bracewell
 */
public final class IndexedFileWriter implements AutoCloseable {
   private final RandomAccessFile randomAccessFile;

   public File getOutputFile() {
      return outputFile;
   }

   private final File outputFile;
   private final JsonWriter indexWriter;

   public IndexedFileWriter(File file) throws IOException {
      this.outputFile = file;
      this.randomAccessFile = new RandomAccessFile(file, "rw");
      this.indexWriter = new JsonWriter(Resources.fromFile(indexFileFor(file)).setIsCompressed(true));
      this.indexWriter.beginDocument();
   }


   public void write(String key, String line) throws IOException {
      long lastPosition = randomAccessFile.getFilePointer();
      if (!line.endsWith("\n")) {
         line += "\n";
      }
      randomAccessFile.writeBytes(line);
      indexWriter.property(key, lastPosition);
   }

   @Override
   public void close() throws IOException {
      indexWriter.endDocument();
      indexWriter.close();
      randomAccessFile.close();
   }
}//END OF IndexedFileWriter
