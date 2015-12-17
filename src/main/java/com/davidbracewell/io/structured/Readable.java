package com.davidbracewell.io.structured;

/**
 * @author David B. Bracewell
 */
public interface Readable {

  void read(StructuredReader reader) throws StructuredIOException;

}//END OF Readable
