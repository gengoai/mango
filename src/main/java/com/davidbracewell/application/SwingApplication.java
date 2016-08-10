package com.davidbracewell.application;

import lombok.NonNull;

import javax.swing.*;

/**
 * @author David B. Bracewell
 */
public abstract class SwingApplication extends JFrame implements Application {
  private static final long serialVersionUID = 1L;
  private final String applicationName;
  private final String packageName;
  private String[] nonNamedArguments;
  private String[] allArgs;


  /**
   * Instantiates a new Java fx application.
   *
   * @param applicationName the application name
   */
  public SwingApplication(String applicationName) {
    this(applicationName, null);
  }

  /**
   * Instantiates a new Application.
   *
   * @param applicationName the application name
   * @param packageName     the package name to use for the application, which is important for loading the correct
   *                        configuration.
   */
  protected SwingApplication(@NonNull String applicationName, String packageName) {
    this.applicationName = applicationName;
    this.packageName = packageName;
  }

  @Override
  public String[] getNonParsableArguments() {
    return nonNamedArguments;
  }

  @Override
  public void setNonParsableArguments(String[] nonParsableArguments) {
    this.nonNamedArguments = nonParsableArguments;
  }

  @Override
  public String[] getAllArguments() {
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
  public final void run() {
    SwingUtilities.invokeLater(
      () -> this.setVisible(true)
    );
  }

}// END OF SwingApplication
