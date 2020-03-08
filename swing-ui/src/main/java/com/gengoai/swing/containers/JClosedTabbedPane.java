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

package com.gengoai.swing.containers;

import com.gengoai.conversion.Cast;
import com.gengoai.swing.ColorUtils;
import com.gengoai.swing.FontAwesome;
import com.gengoai.swing.MouseListeners;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

import static com.gengoai.function.Functional.with;

public class JClosedTabbedPane extends JTabbedPane {
   @NonNull
   private Consumer<Component> onCloseClicked = this::remove;

   public JClosedTabbedPane() {
   }

   public JClosedTabbedPane(int tabPlacement) {
      super(tabPlacement);
   }

   public JClosedTabbedPane(int tabPlacement, int tabLayoutPolicy) {
      super(tabPlacement, tabLayoutPolicy);
   }

   @Override
   public void insertTab(String title, Icon icon, Component component, String tip, int index) {
      super.insertTab(title, icon, component, tip, index);
      setTabComponentAt(index, new CloseButtonTab(component, title, icon));
   }

   public void setCloseButtonVisible(int index, boolean visible) {
      Cast.<CloseButtonTab>as(getTabComponentAt(index)).button.setVisible(visible);
   }

   @Override
   public void setComponentAt(int index, Component component) {
      super.setComponentAt(index, component);
      Cast.<CloseButtonTab>as(getTabComponentAt(index)).tab = component;
   }

   @Override
   public void setIconAt(int index, Icon icon) {
      super.setIconAt(index, icon);
      Cast.<CloseButtonTab>as(getTabComponentAt(index)).label.setIcon(icon);
   }

   public void setOnCloseClicked(Consumer<Component> onCloseClicked) {
      this.onCloseClicked = onCloseClicked;
   }

   @Override
   public void setTitleAt(int index, String title) {
      Cast.<CloseButtonTab>as(getTabComponentAt(index)).label.setText(title);
   }

   public class CloseButtonTab extends JPanel {
      private final JLabel label;
      private final JButton button;
      private Component tab;

      public CloseButtonTab(final Component tab, String title, Icon icon) {
         this.tab = tab;
         setOpaque(false);
         setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
         label = with(new JLabel(title), l -> {
            l.setIcon(icon);
         });
         final Icon cBtnIcon = FontAwesome.WINDOW_CLOSE.create(getFontMetrics(getFont()).getHeight(),
                                                               ColorUtils.getContrastingFontColor(getBackground()));
         final Icon cHover = FontAwesome.WINDOW_CLOSE.create(getFontMetrics(getFont()).getHeight(),
                                                             Color.RED);
         button = with(new JButton(cBtnIcon),
                       b -> {
                          b.setOpaque(false);
                          b.setBorder(BorderFactory.createEmptyBorder());
                          b.addMouseListener(MouseListeners.mouseClicked(e -> {
                             onCloseClicked.accept(this.tab);
                          }));
                          b.setMargin(new Insets(0, 0, 0, 0));
                          b.setMaximumSize(new Dimension(cBtnIcon.getIconWidth() + 2, cBtnIcon.getIconWidth() + 2));
                          b.addMouseListener(new MouseAdapter() {
                             @Override
                             public void mouseEntered(MouseEvent e) {
                                b.setIcon(cHover);
                             }

                             @Override
                             public void mouseExited(MouseEvent e) {
                                b.setIcon(cBtnIcon);
                             }
                          });
                       });

         add(label);
         add(button);
      }
   }
}//END OF JClosedTabbedPane
