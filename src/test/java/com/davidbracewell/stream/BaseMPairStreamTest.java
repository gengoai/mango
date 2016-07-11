package com.davidbracewell.stream;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.tuple.Tuple2;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

/**
 * @author David B. Bracewell
 */
public abstract class BaseMPairStreamTest {
  StreamingContext sc;


  @Test
  public void filter() throws Exception {
    Map.Entry<String, String> g = sc.stream(Collect.map("C", "D", "A", "B"))
      .filter((k, v) -> k.equals("A"))
      .collectAsList().get(0);
    assertEquals("A", g.getKey());
    assertEquals("B", g.getValue());

    g = sc.stream(Collect.map("C", "D", "A", null))
      .filter((k, v) -> k != null && v != null)
      .collectAsList().get(0);
    assertEquals("C", g.getKey());
    assertEquals("D", g.getValue());
  }


  @Test
  public void join() throws Exception {
    MPairStream<String, Integer> s1 = sc.stream(Collect.map("A", 1, "B", 2, "C", 3));
    MPairStream<String, Integer> s2 = sc.stream(Collect.map("A", 4, "B", 1));
    Map<String, Map.Entry<Integer, Integer>> joined = s1.join(s2).collectAsMap();

    assertEquals(1, joined.get("A").getKey().intValue());
    assertEquals(4, joined.get("A").getValue().intValue());
    assertEquals(2, joined.get("B").getKey().intValue());
    assertEquals(1, joined.get("B").getValue().intValue());
    assertFalse(joined.containsKey("C"));
  }

  @Test
  public void leftOuterJoin() throws Exception {
    MPairStream<String, Integer> s1 = sc.stream(Collect.map("A", 1, "B", 2, "C", 3));
    MPairStream<String, Integer> s2 = sc.stream(Collect.map("A", 4, "B", 1));
    Map<String, Map.Entry<Integer, Integer>> joined = s1.leftOuterJoin(s2).collectAsMap();

    assertEquals(1, joined.get("A").getKey().intValue());
    assertEquals(4, joined.get("A").getValue().intValue());
    assertEquals(2, joined.get("B").getKey().intValue());
    assertEquals(1, joined.get("B").getValue().intValue());
    assertEquals(3, joined.get("C").getKey().intValue());
    assertNull(joined.get("C").getValue());
  }

  @Test
  public void rightOuterJoin() throws Exception {
    MPairStream<String, Integer> s1 = sc.stream(Collect.map("A", 1, "B", 2, "C", 3));
    MPairStream<String, Integer> s2 = sc.stream(Collect.map("A", 4, "B", 1, "D", 4));
    Map<String, Map.Entry<Integer, Integer>> joined = s1.rightOuterJoin(s2).collectAsMap();

    assertEquals(1, joined.get("A").getKey().intValue());
    assertEquals(4, joined.get("A").getValue().intValue());

    assertEquals(2, joined.get("B").getKey().intValue());
    assertEquals(1, joined.get("B").getValue().intValue());

    assertNull(joined.get("D").getKey());
    assertEquals(4, joined.get("D").getValue().intValue());

    assertFalse(joined.containsKey("C"));
  }
  @Test
  public void reduceByKey() throws Exception {
    Map<String, Integer> r = sc.stream(
      Arrays.asList(
        Tuple2.of("A", 1),
        Tuple2.of("B", 2),
        Tuple2.of("A", 4),
        Tuple2.of("C", 1),
        Tuple2.of("B", 10)
      )
    )
      .mapToPair(t -> t)
      .reduceByKey((v1, v2) -> v1 + v2)
      .collectAsMap();
    assertEquals(5, r.get("A").intValue());
    assertEquals(12, r.get("B").intValue());
    assertEquals(1, r.get("C").intValue());
  }


  @Test
  public void streamOps() throws Exception {
    MPairStream<String, Integer> stream = sc.stream(Collect.map("A", 1, "B", 2, "C", 3));

    AtomicBoolean closed = new AtomicBoolean(false);
    stream.cache();
    stream.repartition(10);
    stream.onClose(() -> {
      closed.set(true);
    });
    stream.close();

    assertTrue(closed.get());
  }

  @Test
  public void shuffle() throws Exception {
    Map<String,Integer> orig = new TreeMap<>(Collect.map("A", 1, "B", 2, "C", 3));
    List<Map.Entry<String, Integer>> origS = sc.stream(orig).collectAsList();
    boolean diff = false;
    for (int i = 0; !diff && i < 10; i++) {
      if (!origS.equals(new ArrayList<>(sc.stream(orig).shuffle().collectAsList()))) {
        diff = true;
      }
    }
    assertTrue(diff);
  }

}// END OF BaseMPairStreamTest

