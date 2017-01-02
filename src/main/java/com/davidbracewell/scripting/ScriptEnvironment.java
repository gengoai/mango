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

package com.davidbracewell.scripting;

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;

import javax.script.Invocable;
import javax.script.ScriptContext;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import java.io.IOException;

/**
 * <p> This class wraps the scripting functionality in Java to centralize the most used methods. </p>
 *
 * @author David B. Bracewell
 */
public class ScriptEnvironment {

  private final ScriptEngine engine;

  /**
   * <p> Default constructor. </p>
   *
   * @param engine The scripting engine
   */
  ScriptEnvironment(ScriptEngine engine) {
    this.engine = engine;
  }

  /**
   * <p> Evaluates a script contained in the file. The encoding of the file is expected to be UTF-8. </p>
   *
   * @param script - The resource containing the script
   * @return The result of evaluating the script
   * @throws javax.script.ScriptException Something went wrong
   * @throws java.io.IOException     Something went wrong reading the file
   */
  public Object eval(@NonNull Resource script) throws ScriptException, IOException {
    String fileText = script.readToString();
    return eval(fileText, null);
  }

  /**
   * <p> Evaluates a script contained in the file. The encoding of the file is expected to be UTF-8. </p>
   *
   * @param script  - The resource containing the script
   * @param context - The ScriptContext passed to the script engine.
   * @return The result of evaluating the script
   * @throws javax.script.ScriptException Something went wrong
   * @throws java.io.IOException     Something went wrong reading the file
   */
  public Object eval(@NonNull Resource script, ScriptContext context) throws ScriptException, IOException {
    String fileText = script.readToString();
    if (context != null) {
      return eval(fileText, context);
    } else {
      return eval(fileText);
    }
  }

  /**
   * <p> Evaluates a script. Returns null if the script is null or empty </p>
   *
   * @param script The script as a string
   * @return The result of evaluating the script
   * @throws javax.script.ScriptException Something went wrong
   */
  public Object eval(String script) throws ScriptException {
    return eval(script, null);
  }

  /**
   * <p> Evaluates a script using a specified context. Returns null if the script is null or empty  </p>
   *
   * @param script  The script as a string
   * @param context - The ScriptContext passed to the script engine.
   * @return The result of evaluating the script
   * @throws javax.script.ScriptException Something went wrong
   */
  public Object eval(String script, ScriptContext context) throws ScriptException {
    if (StringUtils.isNullOrBlank(script)) {
      return null;
    }
    Object obj;
    if (context == null) {
      obj = engine.eval(script);
    } else {
      obj = engine.eval(script, context);
    }
    return obj;
  }

  /**
   * <p> Used to call top-level procedures and functions defined in scripts. </p>
   *
   * @param name Name of the procedure or function
   * @param args Arguments to pass to the procedure or function
   * @return The result returned by the procedure or function
   * @throws javax.script.ScriptException       Something went wrong
   * @throws NoSuchMethodException Method did not exist
   */
  public Object invokeFunction(String name, Object... args) throws ScriptException,
    NoSuchMethodException {
    Invocable inv = (Invocable) engine;
    return inv.invokeFunction(name, args);
  }

  /**
   * <p> Calls a method on a script object compiled during a previous script execution, which is retained in the state
   * of the ScriptEngine. </p>
   *
   * @param o    If the procedure is a member of a class defined in the script and o is an instance of that class
   *             returned by a previous execution or invocation, the named method is called through that instance.
   * @param name The name of the procedure to be called.
   * @param args Arguments to pass to the procedure. The rules for converting the arguments to scripting variables are
   *             implementation-specific.
   * @return The value returned by the procedure. The rules for converting the scripting variable returned by the
   * script
   * method to a Java Object are implementation-specific.
   * @throws javax.script.ScriptException       Something went wrong
   * @throws NoSuchMethodException Method did not exist
   */
  public Object invokeMethod(Object o, String name, Object... args) throws ScriptException,
    NoSuchMethodException {
    Invocable inv = (Invocable) engine;
    return inv.invokeMethod(o, name, args);
  }

  /**
   * <p> Gets an object created in the scripting environment and converts it to a Java Interface. </p>
   *
   * @param <T>        The type of the Java Interface
   * @param objectName The name of the object in the script
   * @param clazz      The class information for the Java interface
   * @return The object as the Java interface or null if the object does not exist
   */
  public <T> T getInterface(String objectName, Class<T> clazz) {
    Invocable inv = (Invocable) engine;
    Object o = engine.get(objectName);
    if (o == null) {
      return null;
    }
    return inv.getInterface(o, clazz);
  }

  /**
   * <p> Sets a key/value pair in the state of the ScriptEngine that may either create a Java Language Binding to be
   * used in the execution of scripts or be used in some other way, depending on whether the key is reserved. </p>
   *
   * @param varName The name to call the object in the script
   * @param obj     The object
   */
  public void addBinding(String varName, Object obj) {
    engine.put(varName, obj);
  }

  /**
   * <p> Retrieves a value set in the state of this engine. </p>
   *
   * @param varName The name of the variable in the script
   * @return The object represented by the variable
   */
  public Object getObject(String varName) {
    return engine.get(varName);
  }

  /**
   * <p> Gets the name of the engine being used by the environment. </p>
   *
   * @return A String representing the engine name
   */
  public String getEngineName() {
    return engine.getFactory().getEngineName();
  }

  /**
   * <p> Gets the name of the language being used by the environment. </p>
   *
   * @return A String representing the language name
   */
  public String getLanguageName() {
    return engine.getFactory().getLanguageName();
  }

}// END OF CLASS ScriptEnvironment
