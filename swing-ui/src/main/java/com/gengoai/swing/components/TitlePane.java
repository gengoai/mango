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

package com.gengoai.swing.components;

import com.gengoai.swing.ColorUtils;
import com.gengoai.swing.FontAwesome;
import com.gengoai.swing.fluent.FluentBorderLayoutPanel;
import lombok.NonNull;

import javax.swing.*;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.util.function.Consumer;

import static com.gengoai.swing.components.Components.borderLayoutPanel;
import static com.gengoai.swing.fluent.FluentJButton.button;
import static com.gengoai.swing.fluent.FluentJLabel.label;

/**
 * The type Title pane.
 */
public class TitlePane extends FluentBorderLayoutPanel {
   private final JLabel lblHeading;
   private final JButton btnClose;
   private final JPanel header;
   private Consumer<ActionEvent> onBtnClick = e -> {
   };


   /**
    * Instantiates a new Title pane.
    *
    * @param title              the title
    * @param includeCloseButton the include close button
    * @param component          the component
    */
   public TitlePane(String title, boolean includeCloseButton, @NonNull JComponent component) {
      lblHeading = label(title)
            .alignTextHorizontalCenter()
            .alignTextVerticalCenter();

      btnClose = button(FontAwesome.WINDOW_CLOSE.asString())
            .font(FontAwesome.getFontName())
            .fontSize(getFontMetrics(getFont()).getHeight() - 2)
            .translucent()
            .alignTextVerticalCenter()
            .border(null)
            .onHover(($, isMouseOver) -> {
               if(isMouseOver) {
                  $.foreground(Color.RED);
               } else {
                  $.foreground(ColorUtils.calculateBestFontColor(getBackground()));
               }
            })
            .actionListener(($, e) -> onBtnClick.accept(e));

      header = borderLayoutPanel($ -> {
         $.layout(new BorderLayout())
          .center(lblHeading)
          .lineBorder(SystemColor.controlDkShadow);
         if(includeCloseButton) {
            $.add(btnClose, BorderLayout.EAST);
         }
      });

      emptyBorder();
      north(header);
      center(wrap(component));
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

   /**
    * Sets show title.
    *
    * @param show the show
    */
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
