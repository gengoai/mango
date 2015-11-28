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

import java.util.List;

/**
 * The type Java fX application.
 *
 * @author David B. Bracewell
 */
public abstract class JavaFXApplication extends javafx.application.Application implements Application {

  private static final long serialVersionUID = 1L;
  private final String applicationName;
  private String[] nonNamedArguments;
  private String[] allArgs;
  private Stage stage;

  public JavaFXApplication(String applicationName) {
    this.applicationName = applicationName;
  }

  @Override
  public final String[] getOtherArguments() {
    return nonNamedArguments;
  }

  @Override
  public final void setOtherArguments(String[] otherArguments) {
    this.nonNamedArguments = otherArguments;
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
  public final void run(String[] args) {
    Application.super.run(args);
  }

  @Override
  public String getName() {
    return applicationName;
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


  /**
   * Gets stage.
   *
   * @return the stage
   */
  protected final Stage getStage() {
    return stage;
  }

}//END OF JavaFXApplication
