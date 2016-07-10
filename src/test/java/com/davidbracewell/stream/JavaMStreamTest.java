package com.davidbracewell.stream;

import org.junit.Before;

/**
 * @author David B. Bracewell
 */
public class JavaMStreamTest extends BaseMStreamTest {

  @Before
  public void setUp() throws Exception {
    sc = StreamingContext.local();
  }

}// END OF JavaMStreamTest
