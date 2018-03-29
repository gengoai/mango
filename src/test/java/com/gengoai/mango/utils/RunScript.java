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

package com.gengoai.mango.utils;


import com.gengoai.mango.application.CommandLineApplication;
import com.gengoai.mango.cli.Option;
import com.gengoai.mango.io.resource.Resource;
import com.gengoai.mango.scripting.ScriptEnvironment;
import com.gengoai.mango.scripting.ScriptEnvironmentManager;

/**
 * @author David B. Bracewell
 */
public class RunScript extends CommandLineApplication {


  private static final long serialVersionUID = -2787248453076261103L;
  @Option(description = "The language of the script to run", defaultValue = "javascript")
  protected String language;
  @Option(description = "The script to run")
  protected Resource resource;

  public RunScript() {
    super("ScriptRunner");
  }

  public static void main(String[] args) {
    new RunScript().run(args);
  }

  @Override
  protected void programLogic() throws Exception {
    ScriptEnvironment environment = ScriptEnvironmentManager.getInstance().getEnvironment(language);
    environment.eval(resource);
  }

}
