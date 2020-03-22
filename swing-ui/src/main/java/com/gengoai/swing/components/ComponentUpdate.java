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

package com.gengoai.swing.components;

import com.gengoai.reflection.Reflect;
import com.gengoai.reflection.ReflectionException;
import lombok.NonNull;
import lombok.extern.java.Log;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.function.Consumer;

import static com.gengoai.LogUtils.logWarning;

@Log
public class ComponentUpdate<T extends JComponent> {
   public final T component;


   public ComponentUpdate(T component) {
      this.component = component;
   }

   public ComponentUpdate<T> actionListener(ActionListener listener) {
      Reflect r = Reflect.onObject(component);
      if(r.containsMethod("addActionListener", ActionListener.class)) {
         try {
            r.getMethod("addActionListener")
             .invoke(listener);
         } catch(ReflectionException e) {
            throw new RuntimeException(e);
         }
      } else {
         logWarning(log, "{0} does not allow adding action listeners.", component.getClass().getName());
      }
      return this;
   }

   public final ComponentUpdate<T> background(Color color) {
      component.setBackground(color);
      return this;
   }

   public final ComponentUpdate<T> border(Border border) {
      component.setBorder(border);
      return this;
   }

   public final ComponentUpdate<T> emptyBorderWithMargin(int top, int left, int bottom, int right) {
      component.setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
      return this;
   }

   public final ComponentUpdate<T> font(String family, int style, int size) {
      component.setFont(new Font(family, style, size));
      return this;
   }

   public final ComponentUpdate<T> font(int style, int size) {
      component.setFont(component.getFont().deriveFont(style, size));
      return this;
   }

   public final ComponentUpdate<T> fontSize(float size) {
      component.setFont(component.getFont().deriveFont(size));
      return this;
   }

   public final ComponentUpdate<T> fontStyle(int style) {
      component.setFont(component.getFont().deriveFont(style));
      return this;
   }

   public final ComponentUpdate<T> foreground(Color color) {
      component.setForeground(color);
      return this;
   }

   public T getComponent() {
      return component;
   }

   public final ComponentUpdate<T> opaque() {
      component.setOpaque(true);
      return this;
   }

   public final ComponentUpdate<T> opaque(boolean opaque) {
      component.setOpaque(opaque);
      return this;
   }

   public ComponentUpdate<T> selected(boolean selected) {
      Reflect r = Reflect.onObject(component);
      if(r.containsMethod("setSelected", boolean.class)) {
         try {
            r.getMethod("setSelected")
             .invoke(selected);
         } catch(ReflectionException e) {
            throw new RuntimeException(e);
         }
      } else {
         logWarning(log, "{0} does not have a setSelected method.", component.getClass().getName());
      }
      return this;
   }

   public final ComponentUpdate<T> tooltip(String tooltip) {
      component.setToolTipText(tooltip);
      return this;
   }

   public final ComponentUpdate<T> translucent() {
      component.setOpaque(false);
      return this;
   }

   public final ComponentUpdate<T> with(@NonNull Consumer<T> consumer) {
      consumer.accept(component);
      return this;
   }

}//END OF ComponentUpdate
