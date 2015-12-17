package com.davidbracewell.io.structured;

import com.davidbracewell.reflection.BeanMap;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public interface Writeable {

  default void write(StructuredWriter writer) throws IOException{
    writer.writeValue(new BeanMap(this));
  }

}//END OF Writeable
