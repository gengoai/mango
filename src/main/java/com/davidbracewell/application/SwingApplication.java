package com.davidbracewell.application;

import com.davidbracewell.logging.Loggable;
import com.davidbracewell.string.StringUtils;
import lombok.NonNull;
import lombok.SneakyThrows;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

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
 * }*
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
      nativeLookAndFeel();
   }

   /**
    * Mouse clicked mouse adapter.
    *
    * @param consumer the consumer
    * @return the mouse adapter
    */
   public static MouseAdapter mouseClicked(@NonNull Consumer<MouseEvent> consumer) {
      return new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            consumer.accept(e);
            super.mouseClicked(e);
         }
      };
   }

   /**
    * Mouse pressed mouse adapter.
    *
    * @param consumer the consumer
    * @return the mouse adapter
    */
   public static MouseAdapter mousePressed(@NonNull Consumer<MouseEvent> consumer) {
      return new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            consumer.accept(e);
            super.mousePressed(e);
         }
      };
   }

   /**
    * Mouse released mouse adapter.
    *
    * @param consumer the consumer
    * @return the mouse adapter
    */
   public static MouseAdapter mouseReleased(@NonNull Consumer<MouseEvent> consumer) {
      return new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            consumer.accept(e);
            super.mouseReleased(e);
         }
      };
   }

   /**
    * Popup menu will become invisible popup menu listener.
    *
    * @param consumer the consumer
    * @return the popup menu listener
    */
   public static PopupMenuListener popupMenuWillBecomeInvisible(@NonNull Consumer<PopupMenuEvent> consumer) {
      return new PopupMenuListener() {
         @Override
         public void popupMenuCanceled(PopupMenuEvent e) {

         }

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            consumer.accept(e);
         }

         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {

         }
      };
   }

   /**
    * Popup menu will become visible popup menu listener.
    *
    * @param consumer the consumer
    * @return the popup menu listener
    */
   public static PopupMenuListener popupMenuWillBecomeVisible(@NonNull Consumer<PopupMenuEvent> consumer) {
      return new PopupMenuListener() {
         @Override
         public void popupMenuCanceled(PopupMenuEvent e) {

         }

         @Override
         public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {

         }

         @Override
         public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
            consumer.accept(e);
         }
      };
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
   public String[] getNonSpecifiedArguments() {
      return nonNamedArguments;
   }

   @Override
   public void setNonSpecifiedArguments(String[] nonSpecifiedArguments) {
      this.nonNamedArguments = nonSpecifiedArguments;
   }

   /**
    * Native look and feel.
    */
   @SneakyThrows
   protected void nativeLookAndFeel() {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
   }

   @Override
   public void run() {
      SwingUtilities.invokeLater(() -> this.setVisible(true));
   }

}// END OF SwingApplication
