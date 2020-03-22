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

package com.gengoai.swing.listeners;

import com.gengoai.conversion.Cast;
import lombok.NonNull;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * Static methods for constructing MouseListeners and MouseMotionListeners
 */
public final class MouseListeners {


   private MouseListeners() {
      throw new IllegalAccessError();
   }

   private static void interceptMouse(MouseListener listener, Component child, boolean recursive) {
      child.addMouseListener(listener);
      if(recursive && child instanceof JComponent) {
         for(Component component : Cast.<JComponent>as(child).getComponents()) {
            interceptMouse(listener, component, recursive);
         }
      }
   }

   public static void interceptMouse(@NonNull JComponent source,
                                     @NonNull JComponent dispatchTo,
                                     boolean recursive) {
      final MouseListener listener = new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
            super.mouseClicked(e);
         }

         @Override
         public void mouseEntered(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
            super.mouseEntered(e);
         }

         @Override
         public void mouseExited(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
            super.mouseExited(e);
         }

         @Override
         public void mousePressed(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
            super.mousePressed(e);
         }

         @Override
         public void mouseReleased(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
            super.mouseReleased(e);
         }
      };
      interceptMouse(listener, source, recursive);
   }

   private static void interceptMouseMotion(MouseMotionListener listener, Component child, boolean recursive) {
      child.addMouseMotionListener(listener);
      if(recursive && child instanceof JComponent) {
         for(Component component : Cast.<JComponent>as(child).getComponents()) {
            interceptMouseMotion(listener, component, recursive);
         }
      }
   }

   public static void interceptMouseMotion(@NonNull JComponent source,
                                           @NonNull JComponent dispatchTo,
                                           boolean recursive) {
      final MouseMotionListener listener = new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
         }

         @Override
         public void mouseMoved(MouseEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
         }
      };
      interceptMouseMotion(listener, source, recursive);
   }

   private static void interceptMouseWheel(MouseWheelListener listener, Component child, boolean recursive) {
      child.addMouseWheelListener(listener);
      if(recursive && child instanceof JComponent) {
         for(Component component : Cast.<JComponent>as(child).getComponents()) {
            interceptMouseWheel(listener, component, recursive);
         }
      }
   }

   public static void interceptMouseWheel(@NonNull JComponent source,
                                          @NonNull JComponent dispatchTo,
                                          boolean recursive) {
      final MouseWheelListener listener = new MouseWheelListener() {
         @Override
         public void mouseWheelMoved(MouseWheelEvent e) {
            dispatchTo.dispatchEvent(SwingUtilities.convertMouseEvent(e.getComponent(), e, dispatchTo));
         }
      };
      interceptMouseWheel(listener, source, recursive);
   }

   /**
    * Constructs a MouseAdapter that handles mouseClicked events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mouseClicked(Consumer<MouseEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mouseClicked(MouseEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseMotionListener that handles mouseDragged events.
    *
    * @param mouseDragged the mouseDragged handler
    * @return the MouseMotionListener
    */
   public static MouseMotionListener mouseDragged(@NonNull Consumer<MouseEvent> mouseDragged) {
      return new MouseMotionAdapter() {
         @Override
         public void mouseDragged(MouseEvent e) {
            mouseDragged.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseAdapter that handles mouseEntered events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mouseEntered(@NonNull Consumer<MouseEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mouseEntered(MouseEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseAdapter that handles mouseExited events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mouseExited(@NonNull Consumer<MouseEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mouseExited(MouseEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseMotionListener that handles mouseMoved and mouseDragged events.
    *
    * @param mouseMoved   the mouseMove handler
    * @param mouseDragged the mouseDragged handler
    * @return the MouseMotionListener
    */
   public static MouseMotionListener mouseMotion(@NonNull Consumer<MouseEvent> mouseMoved,
                                                 @NonNull Consumer<MouseEvent> mouseDragged) {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent mouseEvent) {
            mouseDragged.accept(mouseEvent);
         }

         @Override
         public void mouseMoved(MouseEvent mouseEvent) {
            mouseMoved.accept(mouseEvent);
         }
      };
   }

   /**
    * Constructs a MouseMotionListener that handles mouseMoved events.
    *
    * @param mouseMoved the mouseMove handler
    * @return the MouseMotionListener
    */
   public static MouseMotionListener mouseMoved(@NonNull Consumer<MouseEvent> mouseMoved) {
      return new MouseMotionAdapter() {
         @Override
         public void mouseMoved(MouseEvent e) {
            mouseMoved.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseAdapter that handles mousePressed events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mousePressed(Consumer<MouseEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mousePressed(MouseEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   /**
    * Constructs a MouseAdapter that handles mouseReleased events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mouseReleased(@NonNull Consumer<MouseEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mouseReleased(MouseEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   public static <T extends JComponent> T mouseReleased(@NonNull T component,
                                                        @NonNull Consumer<MouseEvent> eventHandler) {

      component.addMouseListener(mouseReleased(eventHandler));
      return component;
   }

   /**
    * Constructs a MouseAdapter that handles mouseWheelMoved events.
    *
    * @param eventHandler the event handler
    * @return the MouseAdapter
    */
   public static MouseAdapter mouseWheelMoved(@NonNull Consumer<MouseWheelEvent> eventHandler) {
      return new MouseAdapter() {
         @Override
         public void mouseWheelMoved(MouseWheelEvent e) {
            eventHandler.accept(e);
         }
      };
   }


}//END OF MouseListeners
