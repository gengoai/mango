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

package com.gengoai.mango.scripting;

import com.gengoai.mango.string.StringUtils;
import com.google.common.base.Preconditions;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Manages scripting environments giving each language a singleton instance.
 *
 * @author David B. Bracewell
 */
public final class ScriptEnvironmentManager {

   private final Map<String, ScriptEnvironment> environments;
   private final ScriptEngineManager engineManager;

   private static ScriptEnvironmentManager INSTANCE;

   /**
    * @return The instance of ScriptEnvironmentManager
    */
   public static synchronized ScriptEnvironmentManager getInstance() {
      if (INSTANCE == null) {
         INSTANCE = new ScriptEnvironmentManager();
      }
      return INSTANCE;
   }

   private ScriptEnvironmentManager() {
      environments = new ConcurrentHashMap<>();
      engineManager = new ScriptEngineManager();
   }

   /**
    * Gets the scripting environment given the environment name.
    *
    * @param environmentName the scripting environment name
    * @return the scripting environment
    */
   public ScriptEnvironment getEnvironment(String environmentName) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(environmentName),
                                  "Environment name cannot be null or empty"
                                 );
      ScriptEngine engine = engineManager.getEngineByName(environmentName);
      Preconditions.checkArgument(engine != null, environmentName + " is an unknown.");
      environmentName = engine.getFactory().getEngineName();
      if (!environments.containsKey(environmentName)) {
         environments.put(environmentName, new ScriptEnvironment(engine));
      }
      return environments.get(environmentName);
   }

   /**
    * Gets a temporary scripting environment given the environment name.
    *
    * @param environmentName the scripting environment name
    * @return the scripting environment
    */
   public static ScriptEnvironment getTemporaryEnvironment(String environmentName) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(environmentName),
                                  "Environment name cannot be null or empty"
                                 );
      ScriptEngine engine = ScriptEnvironmentManager.getInstance().engineManager.getEngineByName(environmentName);
      Preconditions.checkArgument(engine != null, environmentName + " is an unknown.");
      return new ScriptEnvironment(engine);
   }


   /**
    * Gets the scripting environment given the script extension.
    *
    * @param extension the scripting environment extension
    * @return the scripting environment
    */
   public ScriptEnvironment getEnvironmentForExtension(String extension) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(extension), "Extension name cannot be null or empty");
      return getEnvironment(getEnvironmentNameForExtension(extension));
   }

   /**
    * Gets the scripting environment given the script extension.
    *
    * @param extension the scripting environment extension
    * @return the scripting environment
    */
   public static ScriptEnvironment getTemporaryEnvironmentForExtension(String extension) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(extension), "Extension name cannot be null or empty");
      return getTemporaryEnvironment(ScriptEnvironmentManager.getInstance().getEnvironmentNameForExtension(extension));
   }


   /**
    * Gets the scripting environment name given the script extension.
    *
    * @param extension the scripting environment extension
    * @return the scripting environment name
    */
   public String getEnvironmentNameForExtension(String extension) {
      Preconditions.checkArgument(StringUtils.isNotNullOrBlank(extension), "Extension name cannot be null or empty");
      ScriptEngine engine = engineManager.getEngineByExtension(extension);
      Preconditions.checkArgument(engine != null, extension + " is not a recognized scripting extension.");
      return engine.getFactory().getLanguageName();
   }

   /**
    * <p> Returns an array whose elements are instances of all the ScriptEngineFactory classes found by the discovery
    * mechanism. </p>
    *
    * @return an array whose elements are instances of all the ScriptEngineFactory classes found by the discovery
    * mechanism.
    * @see javax.script.ScriptEngineManager#getEngineFactories()
    */
   public List<ScriptEngineFactory> getAvailableEngines() {
      ScriptEngineManager engineManager = new ScriptEngineManager();
      return engineManager.getEngineFactories();
   }

}//END OF ScriptEnvironmentManager

