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
 * @author David B. Bracewell
 */
public class JStatusBar extends Box {
   private final JPanel[] panels;
   private final int panelHeight;
   private final Border padding = BorderFactory.createEmptyBorder(1, 1, 1, 1);

   public JStatusBar(int numberOfPanels, JFrame owner) {
      super(0);
      setMinimumSize(new Dimension(owner.getWidth(), 20));
      setOpaque(true);
      setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, SystemColor.windowBorder));
      this.panels = new JPanel[numberOfPanels];
      this.panelHeight = Math.max(owner.getFontMetrics(owner.getFont()).getHeight() + 8, 25);
      Dimension defaultDim = new Dimension(owner.getWidth() / numberOfPanels, this.panelHeight);

      for (int i = 0; i < numberOfPanels; i++) {
         JPanel panel = new JPanel(new BorderLayout());
         this.panels[i] = panel;
         setComponentSize(0, defaultDim.width);
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


   protected JPanel getPanel(int index) {
      return panels[index];
   }

   public void setText(int index, String text) {
      setText(index, text, JLabel.LEFT);
   }

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
         JLabel label = Cast.<JLabel>as(panel.getComponent(0));
         label.setText(text);
         label.setToolTipText(text);
      }
   }

   public void setComponent(int index, JComponent component) {
      JPanel panel = panels[index];
      panel.removeAll();
      panel.add(component, BorderLayout.CENTER);
      component.setOpaque(true);
   }

   public void setComponentSize(int index, int width) {
      panels[index].setPreferredSize(new Dimension(width, this.panelHeight));
//      panels[index].setMinimumSize(new Dimension(width, this.panelHeight));
      panels[index].setMaximumSize(new Dimension(width, this.panelHeight));
   }

   public static void main(String[] args) throws Exception {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
      JFrame frame = new JFrame();
      frame.setMinimumSize(new Dimension(800, 600));
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      frame.pack();
      JStatusBar sb = new JStatusBar(3, frame);
      frame.add(sb, BorderLayout.SOUTH);
      frame.pack();
      sb.setText(0, "Hi this is a long text in the Status Bar");
      sb.setText(1, "Windows", JLabel.CENTER);
      sb.setComponentSize(0, 100);
      sb.setComponentSize(1, 150);
      frame.pack();
      sb.setPreferredSize(new Dimension(sb.getWidth(), 30));
      sb.setComponentSize(2, 100);
      sb.setComponent(2, new JTextField(25));
      frame.setVisible(true);
   }

}
