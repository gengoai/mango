package com.davidbracewell.application;

import com.davidbracewell.logging.Loggable;
import com.davidbracewell.string.StringUtils;
import lombok.SneakyThrows;

import javax.swing.*;

/**
 * <p> Abstract base class for a swing based applications. Child classes should define their UI via the {@link #setup()}
 * method and should define a <code>main</code> method that calls {@link #run(String[])}. An example application is
 * listed below.</p>
 * <pre>
 * {@code
 *    public class MyApplication extends SwingApplication {
 *
 *      public static void main(String[] args)  {
 *        new new MyApplication().run(args);
 *      }
 *
 *      public void setup() throws Exception {
 *        //GUI setup goes here.
 *      }
 *
 *    }
 * }
 * </pre>
 *
 * @author David B. Bracewell
 */
public abstract class SwingApplication extends JFrame implements Application, Loggable {
   private static final long serialVersionUID = 1L;
   private final String applicationName;
   private final String packageName;
   private String[] nonNamedArguments;
   private String[] allArgs;


   /**
    * Instantiates a new Swing application.
    */
   public SwingApplication() {
      this(null, null);
   }

   /**
    * Instantiates a new SwingApplication.
    *
    * @param applicationName the application name
    */
   public SwingApplication(String applicationName) {
      this(applicationName, null);
   }

   /**
    * Instantiates a new SwingApplication.
    *
    * @param applicationName the application name
    * @param packageName     the package name to use for the application, which is important for loading the correct
    *                        configuration.
    */
   protected SwingApplication(String applicationName, String packageName) {
      this.applicationName = StringUtils.isNullOrBlank(applicationName) ? getClass().getSimpleName() : applicationName;
      this.packageName = packageName;
   }

   @Override
   public String[] getNonSpecifiedArguments() {
      return nonNamedArguments;
   }

   @Override
   public void setNonSpecifiedArguments(String[] nonSpecifiedArguments) {
      this.nonNamedArguments = nonSpecifiedArguments;
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
   public void run() {
      nativeLookAndFeel();
      SwingUtilities.invokeLater(() -> this.setVisible(true));
   }

   @SneakyThrows
   protected void nativeLookAndFeel() {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
   }

}// END OF SwingApplication
