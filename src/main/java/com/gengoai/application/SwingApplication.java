package com.gengoai.application;

import com.gengoai.Validation;
import com.gengoai.config.Config;
import com.gengoai.logging.Loggable;
import com.gengoai.string.Strings;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static com.gengoai.collection.Sets.hashSetOf;

/**
 * <p> Abstract base class for a swing based applications. Child classes should define their UI via the {@link
 * #setup()} method and should define a <code>main</code> method that calls {@link #run(String[])}. An example
 * application is listed below.</p>
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
   private final Set<String> dependencies = hashSetOf();
   private String[] allArgs;
   private String[] nonNamedArguments;


   public static void runApplication(Supplier<? extends SwingApplication> supplier, String[] args) {
      systemLookAndFeel();
      supplier.get().run(args);
   }

   protected static void systemLookAndFeel() {
      try {
         UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      } catch (Exception e) {
         throw new RuntimeException(e);
      }
   }

   /**
    * Instantiates a new Application.
    */
   protected SwingApplication() {
      this(null, new String[0]);
   }


   /**
    * Instantiates a new Application.
    *
    * @param applicationName the application name
    */
   protected SwingApplication(String applicationName) {
      this(applicationName, new String[0]);
   }

   /**
    * Instantiates a new SwingApplication.
    *
    * @param applicationName the application name
    * @param dependencies    the package name to use for the application, which is important for loading the correct
    *                        configuration.
    */
   protected SwingApplication(String applicationName, String[] dependencies) {
      this.applicationName = Strings.isNullOrBlank(applicationName) ? getClass().getSimpleName() : applicationName;
      this.dependencies.addAll(Arrays.asList(Validation.notNull(dependencies)));
   }

   protected void addDependency(String dependency) {
      this.dependencies.add(dependency);
   }

   @Override
   public final void setup() throws Exception {
      int width = Config.get(getClass(), "width").asIntegerValue(800);
      int height = Config.get(getClass(), "height").asIntegerValue(600);
      setMinimumSize(new Dimension(width, height));
      Rectangle screenRectangle = getGraphicsConfiguration().getDevice().getDefaultConfiguration().getBounds();
      int xPos = Config.get(getClass(), "position.x").asIntegerValue(screenRectangle.width / 2 - width / 2);
      int yPos = Config.get(getClass(), "position.y").asIntegerValue(screenRectangle.height / 2 - height / 2);
      setLocation(xPos, yPos);
      setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      pack();
      initControls();
      pack();
   }

   protected abstract void initControls() throws Exception;

   /**
    * Mouse clicked mouse adapter.
    *
    * @param consumer the consumer
    * @return the mouse adapter
    */
   public static MouseAdapter mouseClicked(Consumer<MouseEvent> consumer) {
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
   public static MouseAdapter mousePressed(Consumer<MouseEvent> consumer) {
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
   public static MouseAdapter mouseReleased(Consumer<MouseEvent> consumer) {
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
   public static PopupMenuListener popupMenuWillBecomeInvisible(Consumer<PopupMenuEvent> consumer) {
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
   public static PopupMenuListener popupMenuWillBecomeVisible(Consumer<PopupMenuEvent> consumer) {
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
   public Set<String> getDependentPackages() {
      return dependencies;
   }

   @Override
   public String getName() {
      return applicationName;
   }

   @Override
   public String[] getPositionalArgs() {
      return nonNamedArguments;
   }

   @Override
   public void setPositionalArgs(String[] nonSpecifiedArguments) {
      this.nonNamedArguments = nonSpecifiedArguments;
   }

   @Override
   public void run() {
      SwingUtilities.invokeLater(() -> this.setVisible(true));
   }

}// END OF SwingApplication
