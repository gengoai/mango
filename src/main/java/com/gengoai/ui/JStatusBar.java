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
import java.awt.*;

/**
 * @author David B. Bracewell
 */
public class JStatusBar extends Box {
   private final JPanel[] panels;
   private final int panelHeight;

   public JStatusBar(int numberOfPanels, JFrame owner) {
      super(0);
      setMinimumSize(new Dimension(owner.getWidth(), 20));
      setOpaque(true);
      setBackground(SystemColor.control);
      setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.controlShadow));
      this.panels = new JPanel[numberOfPanels];
      this.panelHeight = owner.getFontMetrics(owner.getFont()).getHeight() + 8;
      Dimension defaultDim = new Dimension(owner.getWidth() / numberOfPanels, this.panelHeight);

      for (int i = 0; i < numberOfPanels; i++) {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setMinimumSize(defaultDim);
         panel.setPreferredSize(defaultDim);
         panel.setOpaque(false);
         if (i + 1 < numberOfPanels) {
            panel.setBorder(BorderFactory.createCompoundBorder(
               BorderFactory.createEmptyBorder(0, 3, 0, 0),
               BorderFactory.createMatteBorder(0, 0, 0, 1, SystemColor.controlShadow)));
         } else {
            panel.setBorder(BorderFactory.createEmptyBorder(0, 3, 0, 0));
         }
         this.panels[i] = panel;
         add(panel);
      }
   }


   protected JPanel getPanel(int index) {
      return panels[index];
   }

   public void setText(int index, String text) {
      JPanel panel = panels[index];
      if (panel.getComponentCount() == 0 || !(panel.getComponent(0) instanceof JLabel)) {
         JLabel label = new JLabel(text, JLabel.LEFT);
         label.setOpaque(false);
         panel.removeAll();
         panel.add(label, BorderLayout.WEST);
      } else {
         Cast.<JLabel>as(panel.getComponent(0)).setText(text);
      }
   }

   public void setComponent(int index, JComponent component) {
      JPanel panel = panels[index];
      panel.removeAll();
      panel.add(component);
      component.setOpaque(true);
   }

   public void setComponentSize(int index, int width) {
      panels[index].setPreferredSize(new Dimension(width, this.panelHeight));
      panels[index].setMinimumSize(new Dimension(width, this.panelHeight));
      panels[index].setMaximumSize(new Dimension(width, this.panelHeight));
   }

   public static void main(String[] args) throws Exception {
      JFrame frame = new JFrame();
      frame.setMinimumSize(new Dimension(800, 600));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      JStatusBar sb = new JStatusBar(3, frame);
      frame.add(sb, BorderLayout.SOUTH);
      frame.pack();
      sb.setText(0, "Hi this is a long text in the Status Bar");
      sb.setText(1, "Windows");
      sb.setComponentSize(0, 300);
      sb.setComponentSize(1, 100);
      sb.setComponentSize(2, 50);
      sb.setComponent(2, new JButton("Click me!"));
      frame.setVisible(true);
   }

}
