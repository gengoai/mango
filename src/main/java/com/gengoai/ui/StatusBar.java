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

import com.gengoai.Validation;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;

/**
 * @author David B. Bracewell
 */
public class StatusBar extends JPanel {
   private Border paneBorder;
   private final int numberOfPanels;
   private final int widths[];

   public StatusBar(int numberOfPanels) {
      this.numberOfPanels = numberOfPanels;
      this.widths = new int[numberOfPanels];
      BoxLayout boxLayout = new BoxLayout(this, BoxLayout.X_AXIS);
      setLayout(boxLayout);
      for (int i = 0; i < numberOfPanels; i++) {
         JPanel panel = new JPanel();
         this.widths[i] = -1;
         switch (i) {
            case 0:
               panel.setBackground(Color.GREEN);
               break;
            case 1:
               panel.setBackground(Color.BLACK);
               break;
            case 2:
               panel.setBackground(Color.BLUE);
               break;
            case 3:
               panel.setBackground(Color.YELLOW);
               break;
         }
         add(panel);
      }
   }


   public void setPanelWidth(int index, double pct) {
      Validation.checkArgument(pct > 0 && pct <= 1, "Invalid width %: " + pct);
      setPanelWidth(index, (int) (getWidth() * pct));
   }

   public void setPanelWidth(int index, int width) {
      this.widths[index] = width;
   }


   public static void main(String[] args) throws Exception {
      JFrame frame = new JFrame();
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      StatusBar sb = new StatusBar(4);
      frame.add(sb, BorderLayout.SOUTH);
      frame.setMinimumSize(new Dimension(800, 600));
      frame.setVisible(true);
   }


}//END OF StatusBar
