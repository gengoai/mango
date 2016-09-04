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

package com.davidbracewell.cache;

import com.davidbracewell.concurrent.Threads;
import com.davidbracewell.config.Config;
import com.google.common.base.Stopwatch;
import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class CacheProxyTest {

  @Cached(name = "com.davidbracewell.cache.globalCache")
  public static class TestFunction implements Function<String, String> {


    @Override
    @Cached(keyMaker = KeyMaker.HashCodeKeyMaker.class)
    public String apply(String input) {
      Threads.sleep(500);
      return input;
    }
  }

  @Before
  public void setUp() throws Exception {
    Config.clear();
    Config.initialize("CacheProxyTest", new String[0]);
    Config.setProperty("com.davidbracewell.cache.globalCache.class", "com.davidbracewell.cache.impl.GuavaCache");
    Config.setProperty("com.davidbracewell.cache.globalCache.constructor.param1.type", "String");
    Config.setProperty("com.davidbracewell.cache.globalCache.constructor.param1.value", "maximumSize=10000");
    CacheManager.getGlobalCache().invalidateAll();
  }

  @Test
  public void testNewInstance() throws Exception {
    Function<String, String> func = CacheProxy.cache(new TestFunction());
    Stopwatch sw1 = Stopwatch.createStarted();
    for (int i = 0; i < 3; i++) {
      func.apply(Integer.toString(i));
    }
    sw1.stop();
    Stopwatch sw2 = Stopwatch.createStarted();
    for (int i = 0; i < 3; i++) {
      func.apply(Integer.toString(i));
    }
    sw2.stop();
    assertTrue(sw2.elapsed(TimeUnit.MILLISECONDS) <= sw1.elapsed(TimeUnit.MILLISECONDS));
  }
}
