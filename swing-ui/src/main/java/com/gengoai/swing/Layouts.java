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

import com.gengoai.swing.layout.HorizontalLayout;
import com.gengoai.swing.layout.VerticalLayout;
import lombok.NonNull;

import javax.swing.JComponent;
import java.awt.BorderLayout;
import java.awt.Component;

public final class Layouts {

   private Layouts() {
      throw new IllegalAccessError();
   }

   public static <T extends JComponent> T horizontal(@NonNull T parent) {
      return horizontal(parent, 0);
   }

   public static <T extends JComponent> T horizontal(@NonNull T parent, int gap) {
      parent.setLayout(new HorizontalLayout(gap));
      return parent;
   }

   public static <T extends JComponent> T vertical(@NonNull T parent) {
      return vertical(parent, 0);
   }

   public static <T extends JComponent> T vertical(@NonNull T parent, int gap) {
      parent.setLayout(new VerticalLayout(gap));
      return parent;
   }

   public static <T extends JComponent> BorderLayoutWrapper<T> borderLayout(@NonNull T parent) {
      return new BorderLayoutWrapper<>(parent);
   }

   public static final class BorderLayoutWrapper<T extends JComponent> extends BorderLayout {
      private final T parent;

      private BorderLayoutWrapper(T component) {
         this.parent = component;
         this.parent.setLayout(this);
      }

      public BorderLayoutWrapper<T> center(Component component) {
         this.parent.add(component, BorderLayout.CENTER);
         return this;
      }

      public BorderLayoutWrapper<T> east(Component component) {
         this.parent.add(component, BorderLayout.EAST);
         return this;
      }

      public BorderLayoutWrapper<T> hGap(int hgap) {
         setHgap(hgap);
         return this;
      }

      public BorderLayoutWrapper<T> north(Component component) {
         this.parent.add(component, BorderLayout.NORTH);
         return this;
      }

      public BorderLayoutWrapper<T> south(Component component) {
         this.parent.add(component, BorderLayout.SOUTH);
         return this;
      }

      public BorderLayoutWrapper<T> vGap(int vgap) {
         setVgap(vgap);
         return this;
      }

      public BorderLayoutWrapper<T> west(Component component) {
         this.parent.add(component, BorderLayout.WEST);
         return this;
      }

   }


}//END OF Layouts
