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

import lombok.NonNull;

import javax.swing.JComponent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.function.Consumer;

public final class KeyListeners {

   public KeyListeners() {
      throw new IllegalAccessError();
   }


   public static KeyListener keyPressed(@NonNull Consumer<KeyEvent> listener) {
      return new KeyAdapter() {
         @Override
         public void keyPressed(KeyEvent e) {
            listener.accept(e);
         }
      };
   }

   public static <T extends JComponent> T keyReleased(@NonNull T component,
                                                      @NonNull Consumer<KeyEvent> listener) {
      component.addKeyListener(keyReleased(listener));
      return component;
   }

   public static KeyListener keyReleased(@NonNull Consumer<KeyEvent> eventHandler) {
      return new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            eventHandler.accept(e);
         }
      };
   }

   public static KeyListener keyTyped(@NonNull Consumer<KeyEvent> listener) {
      return new KeyAdapter() {
         @Override
         public void keyTyped(KeyEvent keyEvent) {
            listener.accept(keyEvent);
         }
      };
   }

}//END OF KeyListeners
