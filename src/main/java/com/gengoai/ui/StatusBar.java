/*
 * (c) 2005 David B. Bracewell
 *
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
 *
 */

package com.gengoai.ui;

import com.gengoai.conversion.Cast;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * The type J status bar.
 *
 * @author David B. Bracewell
 */
public class StatusBar extends Box {
   private final JPanel[] panels;
   private final Border padding = BorderFactory.createEmptyBorder(1, 1, 1, 1);

   /**
    * Instantiates a new J status bar.
    *
    * @param numberOfPanels the number of panels
    */
   public StatusBar(int numberOfPanels) {
      super(0);
      setDoubleBuffered(true);
      setOpaque(true);
      setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.windowBorder));
      this.panels = new JPanel[numberOfPanels];
      int panelHeight = Math.max(getFontMetrics(new JPanel().getFont()).getHeight() + 8, 25);
      setMinimumSize(new Dimension(getWidth(), panelHeight));
      setPreferredSize(new Dimension(getWidth(), panelHeight));
      for (int i = 0; i < numberOfPanels; i++) {
         JPanel panel = new JPanel(new BorderLayout());
         this.panels[i] = panel;
         panel.setOpaque(false);
         if (i + 1 < numberOfPanels) {
            panel.setBorder(BorderFactory.createCompoundBorder(
               padding,
               BorderFactory.createMatteBorder(0, 0, 0, 1, SystemColor.windowBorder)));
         } else {
            panel.setBorder(padding);
         }
         add(panel);
      }
      add(Box.createHorizontalGlue());
   }


   /**
    * Gets panel.
    *
    * @param index the index
    * @return the panel
    */
   protected JPanel getPanel(int index) {
      return panels[index];
   }

   /**
    * Sets text.
    *
    * @param index the index
    * @param text  the text
    */
   public void setText(int index, String text) {
      setText(index, text, JLabel.LEFT);
   }

   /**
    * Sets text.
    *
    * @param index     the index
    * @param text      the text
    * @param alignment the alignment
    */
   public void setText(int index, String text, int alignment) {
      JPanel panel = panels[index];
      if (panel.getComponentCount() == 0 || !(panel.getComponent(0) instanceof JLabel)) {
         JLabel label = new JLabel(text, alignment);
         label.setOpaque(false);
         panel.removeAll();
         panel.add(label, BorderLayout.CENTER);
         if (panel.getWidth() < getFontMetrics(getFont()).charsWidth(text.toCharArray(), 0, text.length())) {
            label.setToolTipText(text);
         }
      } else {
         JLabel label = Cast.as(panel.getComponent(0));
         label.setText(text);
         label.setToolTipText(text);
      }
   }

   /**
    * Sets component.
    *
    * @param index     the index
    * @param component the component
    */
   public void setComponent(int index, JComponent component) {
      JPanel panel = panels[index];
      panel.removeAll();
      panel.add(component, BorderLayout.CENTER);
      component.setOpaque(true);
   }

   /**
    * Sets component size.
    *
    * @param index the index
    * @param width the width
    */
   public void setComponentSize(int index, int width) {
      panels[index].setPreferredSize(new Dimension(width, getHeight()));
      panels[index].setMaximumSize(new Dimension(width, getHeight()));
   }

}
