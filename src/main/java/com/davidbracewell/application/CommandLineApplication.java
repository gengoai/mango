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

import com.davidbracewell.logging.Logger;
import lombok.NonNull;

import java.io.Serializable;

/**
 * <p>
 * Abstract base class for an application. The Application class takes care of boilerplate command line and config
 * methodology. Calling the <code>run(String[] args)</code> method will parse the command line, set the values of the
 * associated fields and initialize the configuration. Non-named arguments are stored in a private array and can be
 * accessed through the <code>getOtherArguments</code> method. An <code>Description</code> annotation can be added to a
 * class extending Application to provide a short description of what the program does for use when displaying help.
 * </p>
 * <p>
 * Child classes should implement the <code>programLogic</code> method and create the following main method:
 * <code>
 * public static void main(String[] args) {
 * new MyApplication.run(args);
 * }
 * </code>
 * where <code>MyApplication</code> is the name of your child class.
 * </p>
 *
 * @author David B. Bracewell
 */
public abstract class CommandLineApplication implements Application, Serializable {
  private static final Logger log = Logger.getLogger(CommandLineApplication.class);
  private static final long serialVersionUID = 1L;

  public final String applicationName;
  private String[] nonNamedArguments;
  private String[] allArgs;
  private String packageName;

  /**
   * Instantiates a new Application.
   *
   * @param applicationName the application name
   */
  protected CommandLineApplication(String applicationName) {
    this(applicationName, null);
  }

  /**
   * Instantiates a new Application.
   *
   * @param applicationName the application name
   * @param packageName     the package name to use for the application, which is important for loading the correct
   *                        configuration.
   */
  protected CommandLineApplication(@NonNull String applicationName, String packageName) {
    this.applicationName = applicationName;
    this.packageName = packageName;
  }

  @Override
  public final String[] getAllArguments() {
    return allArgs;
  }

  @Override
  public void setAllArguments(String[] allArguments) {
    this.allArgs = allArguments;
  }

  @Override
  public String getConfigPackageName() {
    return packageName;
  }

  @Override
  public String getName() {
    return applicationName;
  }


  @Override
  public final String[] getNonParsableArguments() {
    return nonNamedArguments;
  }

  /**
   * Child classes override this method adding their program logic.
   *
   * @throws Exception Something abnormal happened.
   */
  protected abstract void programLogic() throws Exception;

  @Override
  public final void run() {
    try {
      programLogic();
    } catch (Exception e) {
      log.severe(e);
      System.exit(-1);
    }
  }

  @Override
  public void setNonParsableArguments(String[] nonParsableArguments) {
    this.nonNamedArguments = nonParsableArguments;
  }

  @Override
  public void setup() throws Exception {

  }

}//END OF Application
