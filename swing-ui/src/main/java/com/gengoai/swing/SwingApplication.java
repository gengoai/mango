/*
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

package com.gengoai.swing;

import com.gengoai.application.Application;
import com.gengoai.config.Config;

import javax.swing.*;
import java.awt.Dimension;
import java.awt.LayoutManager;
import java.awt.Rectangle;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * <p> Abstract base class for a swing based applications. Child classes should define their UI via the {@link
 * #setup()} method and should define a <code>main</code> method that calls {@link #run(String[])}. An example
 * application is listed below.</p>
 * <pre>
 * {@code
 *    public class MyApplication extends SwingApplication {
 *
 *      public static void main(String[] args)  {
 *        runApplication(MyApplication::new, args)
 *      }
 *
 *      public void initControls() throws Exception {
 *        //GUI setup goes here.
 *      }
 *
 *    }
 * }*
 * </pre>
 *
 * @author David B. Bracewell
 */
public abstract class SwingApplication extends Application {
   private static final long serialVersionUID = 1L;
   public final JFrame mainWindowFrame;
   protected final Properties properties;

   /**
    * Instantiates a new Application.
    */
   protected SwingApplication() {
      this(null);
   }


   /**
    * Instantiates a new SwingApplication.
    *
    * @param name The name of the application
    */
   protected SwingApplication(String name) {
      super(name);
      this.properties = new Properties();
      this.mainWindowFrame = new JFrame();
   }


   public static void runApplication(Supplier<? extends SwingApplication> supplier,
                                     String applicationName,
                                     String[] args) {
      SwingUtilities.invokeLater(() -> {
         Config.loadApplicationConfig(applicationName);
         final String lookAndFeel = Config.get("swing.lookAndFeel")
                                          .asString("javax.swing.plaf.nimbus.NimbusLookAndFeel");
         if(lookAndFeel != null) {
            try {
               if(lookAndFeel.equalsIgnoreCase("system")) {
                  UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
               } else {
                  UIManager.setLookAndFeel(lookAndFeel);
               }
            } catch(Exception e) {
               e.printStackTrace();
            }
         }
         supplier.get().run(args);
      });
   }

   public void add(JComponent component) {
      mainWindowFrame.add(component);
   }

   public void add(JComponent component, int index) {
      mainWindowFrame.add(component, index);
   }

   public void add(JComponent component, Object constraints, int index) {
      mainWindowFrame.add(component, constraints, index);
   }

   public void add(JComponent component, Object constraints) {
      mainWindowFrame.add(component, constraints);
   }

   public int getExtendedState() {
      return mainWindowFrame.getExtendedState();
   }

   public int getHeight() {
      return mainWindowFrame.getHeight();
   }

   public String getTitle() {
      return mainWindowFrame.getTitle();
   }

   public int getWidth() {
      return mainWindowFrame.getWidth();
   }

   protected abstract void initControls() throws Exception;

   public void invalidate() {
      mainWindowFrame.invalidate();
   }

   public void pack() {
      mainWindowFrame.pack();
   }

   @Override
   public final void run() {
      setVisible(true);
   }

   public void setExtendedState(int state) {
      mainWindowFrame.setExtendedState(state);
   }

   public void setJMenuBar(JMenuBar menuBar) {
      mainWindowFrame.setJMenuBar(menuBar);
   }

   public void setLayout(LayoutManager layout) {
      mainWindowFrame.setLayout(layout);
   }

   public void setLocation(int x, int y) {
      mainWindowFrame.setLocation(x, y);
   }

   public void setMaximumSize(Dimension dimension) {
      mainWindowFrame.setMaximumSize(dimension);
   }

   public void setMinimumSize(Dimension dimension) {
      mainWindowFrame.setMinimumSize(dimension);
   }

   public void setPreferredSize(Dimension dimension) {
      mainWindowFrame.setPreferredSize(dimension);
   }

   public void setTitle(String title) {
      mainWindowFrame.setTitle(title);
   }

   public void setVisible(boolean isVisible) {
      mainWindowFrame.setVisible(isVisible);
   }

   @Override
   public final void setup() throws Exception {
      int width = Config.get("swing.width").asIntegerValue(800);
      int height = Config.get("swing.height").asIntegerValue(600);
      setMinimumSize(new Dimension(width, height));

      if(Config.get("swing.maximized").asBooleanValue(false)) {
         setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
      } else {
         Rectangle screenRectangle = mainWindowFrame.getGraphicsConfiguration()
                                                    .getDevice()
                                                    .getDefaultConfiguration()
                                                    .getBounds();
         int xPos = Config.get("swing.position.x").asIntegerValue(screenRectangle.width / 2 - width / 2);
         int yPos = Config.get("swing.position.y").asIntegerValue(screenRectangle.height / 2 - height / 2);
         setLocation(xPos, yPos);
      }

      mainWindowFrame.setTitle(getName());
      mainWindowFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
      this.initControls();
   }

}// END OF SwingApplication
