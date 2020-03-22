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

import static com.gengoai.swing.SizeType.PreferredSize;

public class VerticalLayout implements LayoutManager2 {
   private int gap = 0;
   private JComponent resizeWith;

   public VerticalLayout() {
      this(0);
   }

   public VerticalLayout(int gap) {
      Validation.checkArgument(gap >= 0, "Gap must be >= 0");
      this.gap = gap;
   }

   @Override
   public void addLayoutComponent(String name, Component comp) {

   }

   @Override
   public void addLayoutComponent(Component comp, Object constraints) {

   }

   @Override
   public float getLayoutAlignmentX(Container target) {
      return 0.5f;
   }

   @Override
   public float getLayoutAlignmentY(Container target) {
      return 0.5f;
   }

   @Override
   public void invalidateLayout(Container target) {

   }

   @Override
   public void layoutContainer(Container parent) {
      synchronized(parent.getTreeLock()) {
         Insets insets = parent.getInsets();
         int x = insets.left;
         int y = insets.top;
         int width = parent.getWidth() - insets.left - insets.right;
         int slop = 0;
         if(resizeWith != null) {
            int h = 0;
            for(Component component : parent.getComponents()) {
               if(h > 0) {
                  h += gap;
               }
               h += PreferredSize.calculate(component).height;
            }
            slop = parent.getHeight() - h;
         }
         for(Component component : parent.getComponents()) {
            int height = PreferredSize.calculate(component).height;
            if(component == resizeWith) {
               height += slop;
            }
            component.setBounds(x, y, width, height);
            y += height + gap;
         }
      }
   }

   @Override
   public Dimension maximumLayoutSize(Container target) {
      return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
   }

   @Override
   public Dimension minimumLayoutSize(Container parent) {
      return preferredLayoutSize(parent);
   }

   @Override
   public Dimension preferredLayoutSize(Container parent) {
      synchronized(parent.getTreeLock()) {
         int height = 0;
         int width = parent.getWidth();
         for(Component component : parent.getComponents()) {
            if(height > 0) {
               height += gap;
            }
            height += component.getPreferredSize().height;
            width = Math.max(width, component.getPreferredSize().width);
         }
         Insets insets = parent.getInsets();
         if(resizeWith != null) {
            height = Math.max(height, parent.getHeight() - insets.top - insets.bottom);
         }
         return new Dimension(width + insets.left + insets.right,
                              height + insets.top + insets.bottom);
      }
   }

   @Override
   public void removeLayoutComponent(Component comp) {

   }

   public void setResizeWith(JComponent resizeWith) {
      this.resizeWith = resizeWith;
   }
}//END OF VerticalLayout
