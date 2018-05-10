/*
 * (c) 2005 David B. Bracewell
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gengoai.collection;


import com.gengoai.collection.list.Lists;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CollectTest {

   @Test
   public void iteratorToIterable() throws Exception {
      Iterable<CharSequence> ibl1 = Iterables.asIterable(Lists.list("A", "B", "C").iterator());
      assertEquals(3, Iterables.size(ibl1));

      Iterable<CharSequence> ibl2 = Iterables.asIterable(null);
      assertEquals(0, Iterables.size(ibl2));

      Iterable<CharSequence> ibl3 = Iterables.asIterable(Collections.emptyIterator());
      assertEquals(0, Iterables.size(ibl3));
   }

   @Test
   public void sort() throws Exception {
      Assert.assertEquals(Lists.list("a", "b", "c"), Iterables.sort(Sets.set("c", "b", "a")));
      Assert.assertEquals(Lists.list(3, 2, 1), Iterables.sort(Lists.list(1, 2, 3), Sorting.natural().reversed()));
   }


   @Test
   public void arrayAsIterable() throws Exception {
      Assert.assertEquals(Lists.list(1, 2, 3),
                          Lists.asArrayList(Iterables.asIterable(new int[]{1, 2, 3}, Integer.class)));
      Assert.assertEquals(Lists.list(1, 2, 3),
                          Lists.asArrayList(Iterables.asIterable(new Integer[]{1, 2, 3}, Integer.class)));
      Assert.assertEquals(Lists.list(1, 2, 3),
                          Lists.asArrayList(Iterables.asIterable(new double[]{1.0, 2.0, 3.0}, Integer.class)));
   }

   @Test(expected = ClassCastException.class)
   public void arrayAsIterableBadCast() throws Exception {
      Assert.assertEquals(Lists.list(1, 2, 3),
                          Lists.asArrayList(Iterables.asIterable(new Double[]{1.0, 2.0, 3.0}, Integer.class)));
   }


   @Test
   public void getFirst() throws Exception {
      assertTrue(Iterables.getFirst(Arrays.asList("A", "B", "C")).isPresent());
      assertFalse(Iterables.getFirst(Collections.emptySet()).isPresent());
      assertEquals("A", Iterables.getFirst(Arrays.asList("A", "B", "C")).orElse(null));
   }

   @Test
   public void getLast() throws Exception {
      assertTrue(Iterables.getLast(Arrays.asList("A", "B", "C")).isPresent());
      assertFalse(Iterables.getLast(Collections.emptySet()).isPresent());
      assertEquals("C", Iterables.getLast(Arrays.asList("A", "B", "C")).orElse(null));
   }


   @Test
   public void create() throws Exception {
      assertNull(Collect.create(null));
      assertTrue(Collect.create(List.class) instanceof ArrayList);
      assertTrue(Collect.create(Set.class) instanceof HashSet);
      assertTrue(Collect.create(NavigableSet.class) instanceof TreeSet);
      assertTrue(Collect.create(Queue.class) instanceof LinkedList);
      assertTrue(Collect.create(Deque.class) instanceof LinkedList);
      assertTrue(Collect.create(Stack.class).getClass() == Stack.class);
      assertTrue(Collect.create(LinkedHashSet.class).getClass() == LinkedHashSet.class);
   }

   @Test(expected = RuntimeException.class)
   public void badCreate() throws Exception {
      Collect.create(NoNoArg.class);
   }

   @Test
   public void zip() throws Exception {
      Assert.assertEquals(Lists.list(new AbstractMap.SimpleEntry<>("A", 1),
                                     new AbstractMap.SimpleEntry<>("B", 2),
                                     new AbstractMap.SimpleEntry<>("C", 3)
                                    ),
                          Iterables.zip(Arrays.asList("A", "B", "C"), Arrays.asList(1, 2, 3, 4))
                                   .collect(Collectors.toList())
                         );

      assertEquals(0L, Iterables.zip(Collections.emptySet(), Arrays.asList(1, 2, 3, 4)).count());
   }


   static class NoNoArg extends AbstractCollection<String> {

      public NoNoArg(String param) {

      }

      @Override
      public Iterator<String> iterator() {
         return null;
      }

      @Override
      public int size() {
         return 0;
      }
   }

}//END OF CollectionUtilsTest
