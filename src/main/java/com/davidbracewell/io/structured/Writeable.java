package com.davidbracewell.io.structured;

import com.davidbracewell.reflection.BeanMap;

/**
 * @author David B. Bracewell
 */
public interface Writeable {

  default void write(StructuredWriter writer) throws StructuredIOException {
    writer.writeObject(new BeanMap(this));
  }

}//END OF Writeable
