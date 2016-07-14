package com.davidbracewell.stream;

import org.junit.Before;

/**
 * @author David B. Bracewell
 */
public class JavaDoubleStreamTest extends BaseDoubleStreamTest {

  @Before
  public void setUp() throws Exception {
    sc = StreamingContext.local();
  }


}// END OF JavaDoubleStreamTest
