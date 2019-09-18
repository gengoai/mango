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
 * The type Statusbar.
 *
 * @author David B. Bracewell
 */
public class Statusbar extends JPanel {
   private Border border = BorderFactory.createLoweredSoftBevelBorder();

   /**
    * Instantiates a new Statusbar.
    *
    * @param numberOfComponents the number of components
    */
   public Statusbar(int numberOfComponents) {
      super(new GridLayout(1, numberOfComponents), true);
      for (int i = 0; i < numberOfComponents; i++) {
         JPanel panel = new JPanel(new BorderLayout());
         panel.setBorder(border);
         JLabel label = new JLabel("", JLabel.CENTER);
         label.setVerticalAlignment(SwingConstants.CENTER);
         panel.add(label, BorderLayout.CENTER);
         add(panel);
      }
   }


   /**
    * Sets component border.
    *
    * @param border the border
    */
   public void setComponentBorder(Border border) {
      this.border = border;
      for (Component component : getComponents()) {
         Cast.<JPanel>as(component).setBorder(border);
      }
   }

   /**
    * Sets component.
    *
    * @param index the index
    * @param panel the panel
    */
   public void setComponent(int index, JComponent panel) {
      JPanel container = Cast.as(getComponent(index));
      container.removeAll();
      container.add(panel, BorderLayout.CENTER);
      panel.setOpaque(false);
      panel.setBorder(null);
   }


   /**
    * Sets horizontal alignment.
    *
    * @param index     the index
    * @param alignment the alignment
    */
   public void setHorizontalAlignment(int index, int alignment) {
      JPanel container = Cast.as(getComponent(index));
      Component p = container.getComponent(0);
      if (p instanceof JLabel) {
         Cast.<JLabel>as(p).setHorizontalAlignment(alignment);
      }
   }

   /**
    * Sets text.
    *
    * @param index the index
    * @param text  the text
    */
   public void setText(int index, String text) {
      JPanel container = Cast.as(getComponent(index));
      Component p = container.getComponent(0);
      if (p instanceof JLabel) {
         Cast.<JLabel>as(p).setText(text);
      }
   }

}//END OF Statusbar
