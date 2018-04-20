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

package com.gengoai.logging;

import org.junit.Test;

import java.util.logging.Level;

import static org.junit.Assert.*;

public class LoggerTest {

  @Test
  public void test() {
    StringBuilderHandler sbh = new StringBuilderHandler();
    sbh.setFormatter(new SuperSimpleFormatter());
    LogManager.setFormatter(new SuperSimpleFormatter());
    LogManager.clearHandlers();
    LogManager.addHandler(sbh);
    LogManager.getLogManager().setLevel(LoggerTest.class.getName(), Level.ALL);

    assertEquals(java.util.logging.Logger.GLOBAL_LOGGER_NAME, Logger.getGlobalLogger().getName());

    Logger.getGlobalLogger().log(Level.INFO, "This is Global Logger test 1");
    assertEquals("This is Global Logger test 1", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "This is Global Logger test 2");
    assertEquals("This is Global Logger test 2", sbh.getContent());
    sbh.clear();

    sbh.clear();
    Logger.getLogger(LoggerTest.class).fine("This is Global Logger test 3");
    assertEquals("This is Global Logger test 3", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).finer("This is Global Logger test 4");
    assertEquals("This is Global Logger test 4", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).finest("This is Global Logger test 5");
    assertEquals("This is Global Logger test 5", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).info("This is Global Logger test 6");
    assertEquals("This is Global Logger test 6", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).warn("This is Global Logger test 7");
    assertEquals("This is Global Logger test 7", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).severe("This is Global Logger test 8");
    assertEquals("This is Global Logger test 8", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).fine(new Throwable("This is Global Logger test 9"));
    assertEquals("This is Global Logger test 9", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).finer(new Throwable("This is Global Logger test 10"));
    assertEquals("This is Global Logger test 10", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).finest(new Throwable("This is Global Logger test 11"));
    assertEquals("This is Global Logger test 11", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).info(new Throwable("This is Global Logger test 12"));
    assertEquals("This is Global Logger test 12", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).warn(new Throwable("This is Global Logger test 13"));
    assertEquals("This is Global Logger test 13", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).severe(new Throwable("This is Global Logger test 14"));
    assertEquals("This is Global Logger test 14", sbh.getContent());
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, null, new Throwable("This is Global Logger test"));
    assertTrue(sbh.getContent().equals("This is Global Logger test"));
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "1", new Throwable("This is Global Logger test"));
    assertTrue(sbh.getContent().equals("This is Global Logger test"));
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "1", new Throwable("This is Global Logger test"), "1");
    assertTrue(sbh.getContent().equals("This is Global Logger test"));
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "1", "1,", new Throwable("This is Global Logger test"));
    assertTrue(sbh.getContent().equals("This is Global Logger test"));
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "This is Global Logger test {0}", "1");
    System.out.println(sbh.getContent());
    assertTrue(sbh.getContent().equals("This is Global Logger test 1"));
    sbh.clear();

    Logger.getLogger(LoggerTest.class).log(Level.INFO, "This is Global Logger test");
    assertTrue(sbh.getContent().equals("This is Global Logger test"));
    sbh.clear();
  }

}
