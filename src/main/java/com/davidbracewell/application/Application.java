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

import java.io.Serializable;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The interface Application.
 *
 * @author David B. Bracewell
 */
public interface Application extends Runnable, Serializable {


  /**
   * Get other arguments.
   *
   * @return Other arguments on the command line that were not paresable
   */
  String[] getOtherArguments();

  /**
   * Get all arguments passed to the application.
   *
   * @return the array of arguments passed to the application
   */
  String[] getAllArguments();

  /**
   * Sets all arguments.
   *
   * @param allArguments the all arguments
   */
  void setAllArguments(String[] allArguments);

  /**
   * Sets other arguments.
   *
   * @param otherArguments the other arguments
   */
  void setOtherArguments(String[] otherArguments);

  /**
   * Gets config package name.
   *
   * @return the config package name
   */
  default String getConfigPackageName() {
    return null;
  }

  /**
   * Gets the name of the application
   *
   * @return The name of the application
   */
  String getName();


  /**
   * Run void.
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
    setOtherArguments(Config.initialize(getName(), args, parser));
    if (getConfigPackageName() != null) {
      Config.loadConfig(Resources.fromClasspath(getConfigPackageName().replace(".", "/") + "/default.conf"));
      Config.setAllCommandLine(allArgs);
    }

    try {
      setup();
      run();
    } catch (Exception e) {
      e.printStackTrace();
      System.exit(-1);
    }
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
