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

package com.gengoai.scripting;

import com.gengoai.io.Resources;
import com.gengoai.io.resource.Resource;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * @author David B. Bracewell
 */
public class ScriptEnvironmentTest {

  ScriptEnvironment env;

  @Before
  public void setUp() throws Exception {
    env = ScriptEnvironmentManager.getInstance().getEnvironmentForExtension("js");
  }

  @Test
  public void testEval() throws Exception {
    Resource stringResource = Resources.fromString("function runner (){ return 22.0; }");
    env.eval(stringResource);
  }


  @Test
  public void testInvokeFunction() throws Exception {
    Resource stringResource = Resources.fromString("function runner (){ return 22.0; }");
    env.eval(stringResource);
    assertEquals(22.0, env.invokeFunction("runner"));
  }

  @Test
  public void testGetObject() throws Exception {
    env.eval("var age = 24.0 + 11.0;");
    assertEquals(35.0, env.getObject("age"));
  }

}
