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

package com.davidbracewell.application;

import com.davidbracewell.cli.CommandLineParser;
import com.davidbracewell.config.Config;
import com.davidbracewell.io.Resources;
import com.davidbracewell.logging.Loggable;

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>Generic interface for building applications that use Mango's {@link Config} and {@link CommandLineParser} to
 * reduce the boilerplate of application configuration and command line parsing.</p>
 *
 * @author David B. Bracewell
 */
public interface Application extends Runnable, Serializable, Loggable {


  /**
   * Get other arguments.
   *
   * @return Other arguments on the command line that were not paresable
   */
  String[] getNonParsableArguments();

  /**
   * <p>Sets the arguments that were not parsable by the command line parser</p>
   *
   * @param nonParsableArguments the non-parsable arguments
   */
  void setNonParsableArguments(String[] nonParsableArguments);

  /**
   * Get all arguments passed to the application.
   *
   * @return the array of arguments passed to the application
   */
  String[] getAllArguments();

  /**
   * <p>Sets all arguments found on the command line.</p>
   *
   * @param allArguments All arguments from the command line.
   */
  void setAllArguments(String[] allArguments);

  /**
   * <p>Gets the package name to use for loading a default.conf</p>
   *
   * @return the config package name or null if using the application's package
   */
  String getConfigPackageName();

  /**
   * Gets the name of the application
   *
   * @return The name of the application
   */
  String getName();


  /**
   * <p>Runs the application by first parsing the command line arguments and initializing the config. The process then
   * runs the {@link #setup()} method to perform any special user-defined setup and then finally runs the {@link #run()}
   * command which performs the application logic and is specific to each implementation.</p>
   *
   * @param args the args
   */
  default void run(String[] args) {
    if (args == null) {
      args = new String[0];
    }

    String[] allArgs = new String[args.length];
    System.arraycopy(args, 0, allArgs, 0, args.length);
    setAllArguments(allArgs);

    String cliDesc = getName();
    for (Description description : this.getClass().getAnnotationsByType(Description.class)) {
      cliDesc += "\n" + description.value();
    }

    CommandLineParser parser = new CommandLineParser(this, cliDesc);
    setNonParsableArguments(Config.initialize(getName(), args, parser));
    if (getConfigPackageName() != null) {
      Config.loadConfig(Resources.fromClasspath(getConfigPackageName().replace(".", "/") + "/default.conf"));
      Config.setAllCommandLine(parser);
    }


    try {
      setup();
    } catch (Exception e) {
      logSevere(e);
      System.exit(-1);
    }
    run();
  }

  void setup() throws Exception;


  /**
   * Provides a helpful description for an application do display in the application's help
   */
  @Target({ElementType.TYPE})
  @Retention(RetentionPolicy.RUNTIME)
  @interface Description {

    /**
     * Value string.
     *
     * @return the string
     */
    String value() default "";

  }//END OF Description

}//END OF Application
