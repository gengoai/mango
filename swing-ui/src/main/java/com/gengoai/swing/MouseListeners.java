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

import lombok.NonNull;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.util.function.Consumer;

public final class MouseListeners {

   private MouseListeners() {
      throw new IllegalAccessError();
   }

   public static MouseMotionListener onMouseMoved(@NonNull Consumer<MouseEvent> listener) {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent mouseEvent) {

         }

         @Override
         public void mouseMoved(MouseEvent mouseEvent) {
            listener.accept(mouseEvent);
         }
      };
   }

   public static MouseMotionListener onMouseDragged(@NonNull Consumer<MouseEvent> listener) {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent mouseEvent) {
            listener.accept(mouseEvent);
         }

         @Override
         public void mouseMoved(MouseEvent mouseEvent) {

         }
      };
   }

   public static MouseMotionListener onMouseMotion(@NonNull Consumer<MouseEvent> dragListener,
                                                   @NonNull Consumer<MouseEvent> movedListener) {
      return new MouseMotionListener() {
         @Override
         public void mouseDragged(MouseEvent mouseEvent) {
            dragListener.accept(mouseEvent);
         }

         @Override
         public void mouseMoved(MouseEvent mouseEvent) {
            movedListener.accept(mouseEvent);
         }
      };
   }

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

}//END OF MouseListeners
