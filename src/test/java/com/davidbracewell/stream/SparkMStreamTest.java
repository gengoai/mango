package com.davidbracewell.stream;

import com.davidbracewell.config.Config;
import org.junit.Before;

/**
 * @author David B. Bracewell
 */
public class SparkMStreamTest extends BaseMStreamTest {

  @Before
  public void setUp() throws Exception {
    Config.setProperty("spark.master", "local[*]");
    sc = StreamingContext.distributed();
  }

}// END OF SparkMStreamTest
