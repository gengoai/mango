package com.davidbracewell.io.structured;

import java.io.IOException;

/**
 * @author David B. Bracewell
 */
public interface Readable {

  void read(StructuredReader reader) throws StructuredIOException;

}//END OF Readable
