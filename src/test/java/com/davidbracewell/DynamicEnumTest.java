package com.davidbracewell;/*
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

import org.junit.Test;

import java.util.Collection;

import static com.davidbracewell.collection.Sets.set;
import static org.junit.Assert.*;

public class DynamicEnumTest {


   @Test
   public void testStaticDeclerations() throws Exception {
      assertEquals(NamesEnum.PEDRO, NamesEnum.valueOf("pedro"));
      assertEquals(NamesEnum.AKI, NamesEnum.valueOf("AkI"));
      assertEquals("PABLO", NamesEnum.PABLO.name());
   }

   @Test(expected = IllegalArgumentException.class)
   public void testBadValue() throws Exception {
      assertNotEquals(NamesEnum.PEDRO, NamesEnum.valueOf("pEdRos"));
   }

   @Test
   public void testValues() throws Exception {
      assertEquals(
            set(NamesEnum.PEDRO, NamesEnum.PABLO, NamesEnum.AKI),
            set(NamesEnum.values())
                  );
   }

   private static class NamesEnum extends EnumValue {
      private static final DynamicEnum<NamesEnum> ENUM = new DynamicEnum<>();
      private static final long serialVersionUID = -2068686615518994186L;

      public NamesEnum(String name) {
         super(name);
      }

      public static NamesEnum create(String name) {
         return ENUM.register(new NamesEnum(name));
      }

      public static NamesEnum valueOf(String name) {
         return ENUM.valueOf(name);
      }

      public static Collection<NamesEnum> values() {
         return ENUM.values();
      }

      public static final NamesEnum AKI = create("AKI");
      public static final NamesEnum PABLO = create("PABLO");
      public static final NamesEnum PEDRO = create("PEDRO");

   }


}