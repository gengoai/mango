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

package com.davidbracewell;

import com.davidbracewell.config.Config;
import org.junit.Before;
import org.junit.Test;

import static com.davidbracewell.RanksEnum.ROOT;
import static com.davidbracewell.collection.list.Lists.list;
import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class HierarchicalEnumValueTest {

   public static final RanksEnum PRESIDENT = RanksEnum.create("PRESIDENT", ROOT);
   public static final RanksEnum GENERAL = RanksEnum.create("GENERAL", PRESIDENT);
   public static final RanksEnum COLONEL = RanksEnum.create("COLONEL", GENERAL);
   public static final RanksEnum MAJOR = RanksEnum.create("MAJOR");
   public static final RanksEnum CAPTAIN = RanksEnum.create("CAPTAIN");

   @Before
   public void setUp() throws Exception {
      Config.initializeTest();
      Config.setProperty(MAJOR.canonicalName() + ".parent", COLONEL.canonicalName());
      Config.setProperty(CAPTAIN.canonicalName() + ".parent", "ADMIRAL");
      Config.setProperty(RanksEnum.class.getCanonicalName() + ".ADMIRAL", "PRESIDENT");
   }

   @Test
   public void isRoot() throws Exception {
      assertFalse(PRESIDENT.isRoot());
      assertFalse(MAJOR.isRoot());
      assertTrue(RanksEnum.ROOT.isRoot());
   }

   @Test
   public void getChildren() throws Exception {
      assertTrue(PRESIDENT.getChildren().contains(GENERAL)); //ADMIRAL isn't defined yet.
      assertTrue(COLONEL.getChildren().contains(MAJOR));
      assertTrue(MAJOR.getChildren().isEmpty());
   }

   @Test
   public void isLeaf() throws Exception {
      assertFalse(PRESIDENT.isLeaf());
      assertTrue(MAJOR.isLeaf());
      assertFalse(RanksEnum.ROOT.isLeaf());
   }

   @Test
   public void getParent() throws Exception {
      //Dynamically create ADMIRAL from Config and set parent of Captain
      assertEquals("ADMIRAL", CAPTAIN.getParent().name());
   }

   @Test
   public void values() throws Exception {
      assertTrue(RanksEnum.values().contains(CAPTAIN));
   }

   @Test
   public void valueOf() throws Exception {
      assertEquals(CAPTAIN, RanksEnum.valueOf("captain"));
   }

   @Test
   public void isInstance() throws Exception {
      assertTrue(MAJOR.isInstance(GENERAL));
      assertTrue(MAJOR.isInstance(GENERAL, COLONEL, CAPTAIN));
      assertFalse(MAJOR.isInstance(CAPTAIN));
      assertFalse(COLONEL.isInstance(CAPTAIN, MAJOR));
   }

   @Test
   public void getAncestors() throws Exception {
      assertEquals(list(GENERAL, PRESIDENT), COLONEL.getAncestors());
   }

}