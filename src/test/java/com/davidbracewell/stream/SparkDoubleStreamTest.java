package com.davidbracewell.stream;

import org.junit.Before;

/**
 * @author David B. Bracewell
 */
public class SparkDoubleStreamTest extends BaseDoubleStreamTest {

  @Before
  public void setUp() throws Exception {
    sc = StreamingContext.distributed();
  }


}// END OF SparkDoubleStreamTest
