package com.davidbracewell.stream;

import org.junit.Test;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author David B. Bracewell
 */
public abstract class BaseDoubleStreamTest {

  StreamingContext sc;

  @Test
  public void streamOps() throws Exception {
    MDoubleStream stream = sc.doubleStream(1.0, 2.0, 3, 4);
    assertEquals(sc, stream.getContext());
    AtomicBoolean closed = new AtomicBoolean(false);
    stream.cache();
    stream.repartition(10);
    stream.onClose(() -> {
      closed.set(true);
    });
    stream.close();
    assertTrue(closed.get());
  }


}// END OF BaseDoubleStreamTest
