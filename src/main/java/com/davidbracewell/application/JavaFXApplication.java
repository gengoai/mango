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


import javafx.stage.Stage;
import lombok.NonNull;

import java.util.List;

/**
 * <p>An JavaFX {@link Application} implementation. Child classes should define their UI via the {@link #setup()}
 * method and should define a <code>main</code> method that calls {@link #launch(String...)}. </p>
 *
 * @author David B. Bracewell
 */
public abstract class JavaFXApplication extends javafx.application.Application implements Application {

  private static final long serialVersionUID = 1L;
  private final String applicationName;
  private final String packageName;
  private String[] nonNamedArguments;
  private String[] allArgs;
  private Stage stage;

  /**
   * Instantiates a new Java fx application.
   *
   * @param applicationName the application name
   */
  public JavaFXApplication(String applicationName) {
    this(applicationName, null);
  }

  /**
   * Instantiates a new Application.
   *
   * @param applicationName the application name
   * @param packageName     the package name to use for the application, which is important for loading the correct
   *                        configuration.
   */
  protected JavaFXApplication(@NonNull String applicationName, String packageName) {
    this.applicationName = applicationName;
    this.packageName = packageName;
  }


  @Override
  public String getConfigPackageName() {
    return packageName;
  }

  @Override
  public final String[] getAllArguments() {
    return allArgs;
  }

  @Override
  public final void setAllArguments(String[] allArguments) {
    this.allArgs = allArguments;
  }

  @Override
  public String getName() {
    return applicationName;
  }

  @Override
  public final String[] getNonParsableArguments() {
    return nonNamedArguments;
  }

  @Override
  public final void setNonParsableArguments(String[] nonParsableArguments) {
    this.nonNamedArguments = nonParsableArguments;
  }

  /**
   * Gets stage.
   *
   * @return the stage
   */
  protected final Stage getStage() {
    return stage;
  }

  @Override
  public final void run(String[] args) {
    Application.super.run(args);
  }

  @Override
  public final void run() {

  }

  @Override
  public final void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle(getName());
    this.stage = primaryStage;
    List<String> parameters = getParameters().getRaw();
    if (parameters == null) {
      run(new String[0]);
    } else {
      run(parameters.toArray(new String[parameters.size()]));
    }
  }

}//END OF JavaFXApplication
