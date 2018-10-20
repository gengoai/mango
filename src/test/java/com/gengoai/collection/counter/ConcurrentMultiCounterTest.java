package com.gengoai.collection.counter;

import static com.gengoai.tuple.Tuples.$;

/**
 * @author David B. Bracewell
 */
public class ConcurrentMultiCounterTest extends BaseMultiCounterTest {

   public MultiCounter<String, String> getEmptyCounter() {
      return MultiCounters.newConcurrentCounter();
   }

   public MultiCounter<String, String> getEntryCounter() {
      return MultiCounters.newConcurrentCounter(MultiCounters.newMultiCounter($("A", "B"),
                                                                              $("A", "C"),
                                                                              $("A", "D"),
                                                                              $("B", "E"),
                                                                              $("B", "G"),
                                                                              $("B", "H")
                                                                             ));
   }
}//END OF ConcurrentMultiCounterTest
