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

package com.gengoai.swing.layout;

import com.gengoai.Validation;

import javax.swing.JComponent;
import java.awt.*;

public class HorizontalLayout implements LayoutManager {
   private int gap = 0;
   private JComponent resizeWith;

   public HorizontalLayout() {
      this(0);
   }

   public HorizontalLayout(int gap) {
      Validation.checkArgument(gap >= 0, "Gap must be >= 0");
      this.gap = gap;
   }

   @Override
   public void addLayoutComponent(String name, Component comp) {

   }

   @Override
   public void layoutContainer(Container parent) {
      Insets insets = parent.getInsets();
      int x = insets.left;
      int y = insets.top;
      int height = parent.getHeight() - insets.top - insets.bottom;

      int slop = 0;
      if(resizeWith != null) {
         int w = 0;
         for(Component component : parent.getComponents()) {
            if(w > 0) {
               w += gap;
            }
            w += component.getPreferredSize().width;
         }
         slop = parent.getWidth() - w;
      }
      for(Component component : parent.getComponents()) {
         int width = component.getPreferredSize().width;
         if(component == resizeWith) {
            width += slop;
         }
         component.setBounds(x, y, width, height);
         x += width + gap;
      }
   }

   @Override
   public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
   }

   @Override
   public Dimension preferredLayoutSize(Container parent) {
      int height = parent.getHeight();
      int width = 0;
      for(Component component : parent.getComponents()) {
         if(width > 0) {
            width += gap;
         }
         height = Math.max(height, component.getPreferredSize().height);
         width += component.getPreferredSize().width;
      }
      Insets insets = parent.getInsets();
      if(resizeWith != null) {
         Insets rwInsets = resizeWith.getInsets();
         width = parent.getWidth() - insets.left - insets.right - rwInsets.left - rwInsets.right;
      }
      System.out.println(height);
      return new Dimension(width + insets.left + insets.right,
                           height + insets.top + insets.bottom);
   }

   @Override
   public void removeLayoutComponent(Component comp) {

   }

   public void setResizeWith(JComponent component) {
      this.resizeWith = component;
   }
}//END OF HorizontalLayout
