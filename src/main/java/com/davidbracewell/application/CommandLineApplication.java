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
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;

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
  private static final long serialVersionUID = 1L;
  private static final Logger log = Logger.getLogger(CommandLineApplication.class);

  /**
   * The name of the application
   */
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
   * Gets the name of the application
   *
   * @return The name of the application
   */
  @Override
  public String getName() {
    return applicationName;
  }

  /**
   * Instantiates a new Application.
   *
   * @param applicationName the application name
   * @param packageName     the package name to use for the application, which is important for loading the correct
   *                        configuration.
   */
  protected CommandLineApplication(String applicationName, String packageName) {
    Preconditions.checkArgument(!StringUtils.isNullOrBlank(applicationName));
    this.applicationName = applicationName;
    this.packageName = packageName;
  }

  /**
   * Get other arguments.
   *
   * @return Other arguments on the command line not specified by the annotations.
   */
  @Override
  public final String[] getOtherArguments() {
    return nonNamedArguments;
  }

  /**
   * Get all arguments passed to the application.
   *
   * @return the array of arguments passed to the application
   */
  @Override
  public final String[] getAllArguments() {
    return allArgs;
  }

  @Override
  public final void run() {
    try {
      programLogic();
    } catch (Exception e) {
      log.severe(e);
      System.exit(-1);
    }
  }

  /**
   * Child classes override this method adding their program logic.
   *
   * @throws Exception Something abnormal happened.
   */
  protected abstract void programLogic() throws Exception;


  @Override
  public void setAllArguments(String[] allArguments) {
    this.allArgs = allArguments;
  }

  @Override
  public void setOtherArguments(String[] otherArguments) {
    this.nonNamedArguments = otherArguments;
  }

  @Override
  public String getConfigPackageName() {
    return packageName;
  }

}//END OF Application
