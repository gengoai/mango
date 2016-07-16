package com.davidbracewell.stream;

import com.davidbracewell.config.Config;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;

/**
 * @author David B. Bracewell
 */
public class SparkMStreamTest extends BaseMStreamTest {

  @Before
  public void setUp() throws Exception {
    Config.setProperty("spark.master", "local[*]");
    sc = StreamingContext.distributed();
    Logger.getLogger("org.apache.spark").setLevel(Level.WARN);
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.OFF);
  }

}// END OF SparkMStreamTest
