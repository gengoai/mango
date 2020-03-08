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

package com.gengoai.swing.borders;

import lombok.Data;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import java.awt.*;

@Data
public class ComponentBorder implements Border {
   public static final int LEFT = 1;
   public static final int RIGHT = 2;
   public static final float LEADING = 0.0f;
   public static final float CENTER = 0.5f;
   public static final float TRAILING = 1.0f;
   private final JComponent component;
   private JComponent parent;
   private int edge;
   private float alignment;
   private Insets borderInsets = new Insets(0, 0, 0, 0);
   private int gap = 5;


   /**
    * Convenience constructor that uses the default edge (Edge.RIGHT) and
    * alignment (CENTER).
    *
    * @param component the component to be added in the Border area
    */
   public ComponentBorder(JComponent component) {
      this(component, RIGHT);
   }

   /**
    * Convenience constructor that uses the default alignment (CENTER).
    *
    * @param component the component to be added in the Border area
    * @param edge      a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
    */
   public ComponentBorder(JComponent component, int edge) {
      this(component, edge, CENTER);
   }

   /**
    * Main constructor to create a ComponentBorder.
    *
    * @param component the component to be added in the Border area
    * @param edge      a valid Edge enum of TOP, LEFT, BOTTOM, RIGHT
    * @param alignment the alignment of the component along the
    *                  specified Edge. Must be in the range 0 - 1.0.
    */
   public ComponentBorder(JComponent component, int edge, float alignment) {
      this.component = component;
      component.setSize(component.getPreferredSize());
      component.setCursor(Cursor.getDefaultCursor());
      setEdge(edge);
      setAlignment(alignment);
   }

   @Override
   public Insets getBorderInsets(Component component) {
      return borderInsets;
   }

   @Override
   public boolean isBorderOpaque() {
      return false;
   }

   @Override
   public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
      float x2 = (width - component.getWidth()) * component.getAlignmentX() + x;
      float y2 = (height - component.getHeight()) * component.getAlignmentY() + y;
      component.setLocation((int) x2, (int) y2);
   }

   public void setAlignment(float alignment) {
      this.alignment = (float) Math.max(0, Math.min(1.0, alignment));
   }

   public void setParent(JComponent parent) {
      this.parent = parent;
      if(edge == LEFT) {
         borderInsets.left = component.getPreferredSize().width + gap;
         component.setAlignmentX(0.0f);
      } else {
         borderInsets.right = component.getPreferredSize().width + gap;
         component.setAlignmentX(1.0f);
      }
      component.setAlignmentY(alignment);
      Insets parentInsets = parent.getInsets();
      int componentHeight = component.getHeight();
      int parentHeight = parent.getPreferredSize().height - parentInsets.top - parentInsets.bottom;
      int heightDiff = componentHeight - parentHeight;
      if(heightDiff > 0) {
         int topDiff = (int) (heightDiff * alignment);
         int bottomDiff = heightDiff - topDiff;
         borderInsets.top += topDiff;
         borderInsets.bottom += bottomDiff;
      }
      parent.setBorder(parent.getBorder() == null
                       ? this
                       : new CompoundBorder(parent.getBorder(), this));
      parent.add(component);
   }
}