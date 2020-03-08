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

import jiconfont.icons.font_awesome.FontAwesome;
import jiconfont.swing.IconFontSwing;
import lombok.NonNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import static com.gengoai.function.Functional.with;

/**
 * The type Title pane.
 */
public class TitlePane extends JPanel {
   private final JLabel lblHeading;
   private final JButton btnClose;
   private final JPanel header;
   private JComponent component = null;
   private Consumer<ActionEvent> onBtnClick = e -> {
   };

   /**
    * Instantiates a new Title pane.
    *
    * @param title              the title
    * @param includeCloseButton the include close button
    */
   public TitlePane(String title, boolean includeCloseButton) {
      this(title, includeCloseButton, null);
   }

   /**
    * Instantiates a new Title pane.
    *
    * @param title              the title
    * @param includeCloseButton the include close button
    * @param component          the component
    */
   public TitlePane(String title, boolean includeCloseButton, JComponent component) {
      header = with(new JPanel(), h -> {
         h.setLayout(new BorderLayout());
         h.setBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow));
      });
      lblHeading = with(new JLabel(title), l -> {
         l.setHorizontalAlignment(SwingConstants.CENTER);
         l.setVerticalTextPosition(JLabel.CENTER);
      });
      header.add(lblHeading, BorderLayout.CENTER);

      if(includeCloseButton) {
         btnClose = with(new JButton(), b -> {
            b.setOpaque(true);
            b.setBorder(null);
            final Icon icon = IconFontSwing.buildIcon(FontAwesome.WINDOW_CLOSE,
                                                      getFontMetrics(getFont()).getHeight(),
                                                      getBackground().darker());
            b.setIcon(icon);
            b.addActionListener(e -> onBtnClick.accept(e));
         });
         header.add(btnClose, BorderLayout.EAST);
      } else {
         btnClose = null;
      }

      setLayout(new BorderLayout(2, 2));
      setBorder(BorderFactory.createLineBorder(SystemColor.controlDkShadow));
      add(header, BorderLayout.NORTH);
      if(component != null) {
         this.component = wrap(component);
         add(this.component, BorderLayout.CENTER);
      }
   }

   /**
    * Sets component.
    *
    * @param newComponent the new component
    */
   public void setComponent(JComponent newComponent) {
      if(component != null) {
         remove(component);
      }
      if(newComponent != null) {
         this.component = wrap(newComponent);
         add(this.component, BorderLayout.CENTER);
         invalidate();
      }
   }

   /**
    * Sets label text.
    *
    * @param text the text
    */
   public void setLabelText(String text) {
      this.lblHeading.setText(text);
   }

   /**
    * Sets on close handler.
    *
    * @param handler the handler
    */
   public void setOnCloseHandler(@NonNull Consumer<ActionEvent> handler) {
      this.onBtnClick = handler;
   }

   public void setShowTitle(boolean show) {
      if(show) {
         header.setVisible(true);
      } else {
         header.setVisible(false);
      }
   }

   private JComponent wrap(JComponent component) {
      return component instanceof JScrollPane
             ? component
             : new JScrollPane(component);
   }

}//END OF TitlePane
