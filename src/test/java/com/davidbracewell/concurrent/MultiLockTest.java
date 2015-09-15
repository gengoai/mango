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

package com.davidbracewell.concurrent;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class MultiLockTest {

  MultiLock multiLock;

  @Before
  public void setUp() throws Exception {
    multiLock = new MultiLock(3);
  }

  @Test
  public void testLock() throws Exception {
    assertEquals(0, multiLock.lock());
    assertEquals(1, multiLock.lock());
    assertEquals(2, multiLock.lock());

    multiLock.unlock(2);
    multiLock.unlock(1);

    assertEquals(0, multiLock.lock());
    assertEquals(1, multiLock.lock());

    multiLock.unlock(1);
    multiLock.unlock(0);

    multiLock.setSize(1);

    assertEquals(1, multiLock.size());
    assertEquals(0, multiLock.lock());
    assertEquals(0, multiLock.lock());

  }


}
