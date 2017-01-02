package com.davidbracewell.io.structured;

import com.davidbracewell.reflection.BeanMap;
import lombok.NonNull;

import java.io.IOException;

/**
 * Defines custom methods writing to and reading from structured formats
 *
 * @author David B. Bracewell
 */
public interface StructuredSerializable {

   /**
    * Write.
    *
    * @param writer the writer
    * @throws IOException the io exception
    */
   default void write(@NonNull StructuredWriter writer) throws IOException {
      writer.writeMap(new BeanMap(this));
   }

   /**
    * Read.
    *
    * @param reader the reader
    * @throws IOException the io exception
    */
   default void read(@NonNull StructuredReader reader) throws IOException {
      new BeanMap(this).putAll(reader.nextMap());
   }

}//END OF Writable
