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

package com.gengoai.conversion.impl;

import com.gengoai.collection.Sets;
import com.gengoai.collection.list.Lists;
import com.gengoai.conversion.CollectionConverter;
import com.gengoai.conversion.Convert;
import com.gengoai.conversion.Val;
import org.junit.Assert;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;

public class CollectionConverterTest {

   @Test
   public void testRawCollection() throws Exception {
      //Null
      assertNull(CollectionConverter.COLLECTION(List.class).apply(null));
      assertNull(CollectionConverter.COLLECTION(null).apply("1"));

      List<String> stringList = Lists.list("A", "B", "C");
      Set<String> stringSet = Sets.asTreeSet(stringList);

      //Test cast
      assertEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class).apply(stringList));

      //Test collection conversion
      assertEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class).apply(stringSet));

      //Test conversion
      assertEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class).apply("A,B,C"));


      //Check that no conversion is done
      assertNotEquals(stringList, CollectionConverter.COLLECTION(Set.class).apply(new char[]{'A', 'B', 'C'}));

      assertNotEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class).apply(new int[]{'A', 'B', 'C'}));

   }

   @Test
   public void iterator() throws Exception {
      Iterator<?> itr = Convert.convert("1,2,3", Iterator.class);
      assertTrue(itr.hasNext());
      assertEquals("1", itr.next());
      assertTrue(itr.hasNext());
      assertEquals("2", itr.next());
      assertTrue(itr.hasNext());
      assertEquals("3", itr.next());
      assertFalse(itr.hasNext());
   }

   @Test
   public void testComponentConversionCollection() throws Exception {
      //Null
      assertNull(CollectionConverter.COLLECTION(List.class, String.class).apply(null));
      assertNull(CollectionConverter.COLLECTION(null, String.class).apply("1"));

      assertEquals(Sets.set("1"), CollectionConverter.COLLECTION(HashSet.class, null).apply("1"));


      List<String> stringList = Lists.list("A", "B", "C");
      List<Integer> integerList = Lists.list(1, 3, 4);
      Set<String> stringSet = Sets.asTreeSet(stringList);

      //Test cast
      assertEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class, String.class).apply(stringList));

      //Test collection conversion
      assertEquals(stringList, CollectionConverter.COLLECTION(ArrayList.class, String.class).apply(stringSet));


      //Test conversion
      assertEquals(integerList, CollectionConverter.COLLECTION(ArrayList.class, Integer.class).apply("1,3,4.678"));


      //Check failed conversion
      assertNull(CollectionConverter.COLLECTION(Set.class, Integer.class).apply(new Object[]{new ArrayList<>()}));


      //Val
      Assert.assertEquals(integerList, Val.of("1,3,4.678").asList(Integer.class));
      Assert.assertEquals(Sets.asLinkedHashSet(integerList), Val.of("1,3,4.678").asSet(Integer.class));
      assertEquals(integerList, Val.of("1,3,4.678").asCollection(LinkedList.class, Integer.class));
      Assert.assertEquals(Sets.asLinkedHashSet(integerList),
                          Val.of("1,3,4.678").asCollection(LinkedHashSet.class, Integer.class));
      assertEquals(integerList, Val.of("1,3,4.678").asCollection(Queue.class, Integer.class));
      assertEquals(integerList, Val.of("1,3,4.678").asCollection(Deque.class, Integer.class));
      assertEquals(integerList, Val.of("1,3,4.678").asCollection(Stack.class, Integer.class));
   }

}//END OF CollectionConverterTest