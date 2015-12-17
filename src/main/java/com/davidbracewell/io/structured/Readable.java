package com.davidbracewell.io.structured;

import com.davidbracewell.reflection.BeanMap;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public interface Readable {

  default void read(StructuredReader reader) throws IOException {
    new BeanMap(this).putAll(reader.nextMap());
  }

}//END OF Readable
