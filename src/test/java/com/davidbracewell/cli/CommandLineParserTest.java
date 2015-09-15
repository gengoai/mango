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

package com.davidbracewell.cli;

public class CommandLineParserTest {

//  final CommandLineParser cmd = new CommandLineParser();
//
//  @Before
//  public void setUp() throws Exception {
//    cmd.addOption("--arg1=ARG+", "");
//    cmd.addOption("--arg2", "");
//    cmd.addOption("--arg3=ARG", "");
//    cmd.addOption("-a=ARG", "");
//    cmd.addOption("-b=ARG+", "", "--blong");
//    cmd.addOption("-c", "");
//  }
//
//  @Test
//  public void correctOptions() {
//    CommandLineOption o = cmd.getOption("--arg1");
//    assertEquals("long option argument name", "--arg1", o.getName());
//    assertEquals("long option argument hasArgument", true, o.isArgumentRequired());
//    assertEquals("long option argument required", true, o.isRequired());
//
//    o = cmd.getOption("--arg2");
//    assertEquals("long option argument name", "--arg2", o.getName());
//    assertEquals("long option argument hasArgument", false, o.isArgumentRequired());
//    assertEquals("long option argument required", false, o.isRequired());
//
//    o = cmd.getOption("--arg3");
//    assertEquals("long option argument name", "--arg3", o.getName());
//    assertEquals("long option argument hasArgument", true, o.isArgumentRequired());
//    assertEquals("long option argument required", false, o.isRequired());
//
//    o = cmd.getOption("-a");
//    assertEquals("short option argument name", "-a", o.getName());
//    assertEquals("short option argument hasArgument", true, o.isArgumentRequired());
//    assertEquals("short option argument required", false, o.isRequired());
//
//    o = cmd.getOption("-b");
//    assertEquals("short option argument name", "-b", o.getName());
//    assertEquals("short option argument hasArgument", true, o.isArgumentRequired());
//    assertEquals("short option argument required", true, o.isRequired());
//
//    o = cmd.getOption("-c");
//    assertEquals("short option argument name", "-c", o.getName());
//    assertEquals("short option argument hasArgument", false, o.isArgumentRequired());
//    assertEquals("short option argument required", false, o.isRequired());
//  }
//
//  @Test
//  public void parseTest() {
//    String[] args = {"-c", "-b=arg", "--arg1=arg", "-l"};
//    cmd.parse(args);
//    assertTrue(cmd.get("-b").asString().equals("arg"));
//    assertTrue(cmd.getOption("-b").wasSeen(cmd));
//    assertTrue(cmd.getOption("-b").getValue(cmd).asString().equals("arg"));
//  }
//
//  @Test(expected = CommandLineParserException.class)
//  public void parseError1() {
//    String[] args = {"--"};
//    cmd.parse(args);
//  }
//
//  @Test(expected = CommandLineParserException.class)
//  public void parseError2() {
//    String[] args = {"-", "Space"};
//    cmd.parse(args);
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void incorrectLongOption1() {
//    cmd.addOption("-arg1=ARG+", "");
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void incorrectLongOption2() {
//    cmd.addOption("--arg 1=ARG+", "");
//  }
//
//  @Test(expected = IllegalArgumentException.class)
//  public void incorrectLongOption3() {
//    cmd.addOption("--arg1=AR+", "");
//  }

}
