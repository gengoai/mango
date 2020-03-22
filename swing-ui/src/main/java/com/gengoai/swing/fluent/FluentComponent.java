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

package com.gengoai.swing.fluent;

import com.gengoai.conversion.Cast;
import com.gengoai.swing.Fonts;
import com.gengoai.swing.listeners.ComponentListeners;
import com.gengoai.swing.listeners.FocusListeners;
import com.gengoai.swing.listeners.KeyListeners;
import com.gengoai.swing.listeners.MouseListeners;
import lombok.NonNull;

import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static com.gengoai.swing.listeners.MouseListeners.mouseEntered;
import static com.gengoai.swing.listeners.MouseListeners.mouseExited;

public interface FluentComponent<T extends JComponent, F extends FluentComponent<T, F>> {

   default F alignHorizontalCenter() {
      wrappedComponent().setAlignmentX(JComponent.CENTER_ALIGNMENT);
      return Cast.as(this);
   }

   default F alignHorizontalLeft() {
      wrappedComponent().setAlignmentX(JComponent.LEFT_ALIGNMENT);
      return Cast.as(this);
   }

   default F alignHorizontalRight() {
      wrappedComponent().setAlignmentX(JComponent.RIGHT_ALIGNMENT);
      return Cast.as(this);
   }

   default F alignVerticalBottom() {
      wrappedComponent().setAlignmentY(JComponent.BOTTOM_ALIGNMENT);
      return Cast.as(this);
   }

   default F alignVerticalCenter() {
      wrappedComponent().setAlignmentY(JComponent.CENTER_ALIGNMENT);
      return Cast.as(this);
   }

   default F alignVerticalTop() {
      wrappedComponent().setAlignmentY(JComponent.TOP_ALIGNMENT);
      return Cast.as(this);
   }

   default F background(Color color) {
      wrappedComponent().setBackground(color);
      return Cast.as(this);
   }

   default F border(Border border) {
      wrappedComponent().setBorder(border);
      return Cast.as(this);
   }

   default F clientProperty(Object key, Object value) {
      wrappedComponent().putClientProperty(key, value);
      return Cast.as(this);
   }

   default F emptyBorder() {
      wrappedComponent().setBorder(BorderFactory.createEmptyBorder());
      return Cast.as(this);
   }

   default F emptyBorderWithMargin(int top, int left, int bottom, int right) {
      wrappedComponent().setBorder(BorderFactory.createEmptyBorder(top, left, bottom, right));
      return Cast.as(this);
   }

   default F font(String family) {
      wrappedComponent().setFont(new Font(family,
                                          wrappedComponent().getFont().getStyle(),
                                          wrappedComponent().getFont().getSize()));
      return Cast.as(this);
   }

   default F font(String family, int style, int size) {
      wrappedComponent().setFont(new Font(family, style, size));
      return Cast.as(this);
   }

   default F font(int style, int size) {
      wrappedComponent().setFont(wrappedComponent().getFont().deriveFont(style, size));
      return Cast.as(this);
   }

   default F fontSize(float size) {
      wrappedComponent().setFont(wrappedComponent().getFont().deriveFont(size));
      return Cast.as(this);
   }

   default F fontStyle(int style) {
      wrappedComponent().setFont(wrappedComponent().getFont().deriveFont(style));
      return Cast.as(this);
   }

   default F foreground(Color color) {
      wrappedComponent().setForeground(color);
      return Cast.as(this);
   }

   default FontMetrics getFontMetrics() {
      return wrappedComponent().getFontMetrics(wrappedComponent().getFont());
   }

   default F layout(LayoutManager layout) {
      wrappedComponent().setLayout(layout);
      return Cast.as(this);
   }

   default F lineBorder(@NonNull Color color, int thickness, boolean rounded) {
      wrappedComponent().setBorder(BorderFactory.createLineBorder(color, thickness, rounded));
      return Cast.as(this);
   }

   default F lineBorder(@NonNull Color color, int thickness) {
      wrappedComponent().setBorder(BorderFactory.createLineBorder(color, thickness));
      return Cast.as(this);
   }

   default F lineBorder(@NonNull Color color) {
      wrappedComponent().setBorder(BorderFactory.createLineBorder(color));
      return Cast.as(this);
   }

   default F loweredBevelBorder() {
      wrappedComponent().setBorder(BorderFactory.createLoweredBevelBorder());
      return Cast.as(this);
   }

   default F loweredSoftBevelBorder() {
      wrappedComponent().setBorder(BorderFactory.createLoweredSoftBevelBorder());
      return Cast.as(this);
   }

   default F maxFontSize(float size) {
      Fonts.setMaxFontSize(wrappedComponent(), size);
      return Cast.as(this);
   }

   default F maximumSize(int width, int height) {
      wrappedComponent().setMaximumSize(new Dimension(width, height));
      return Cast.as(this);
   }

   default F minFontSize(float size) {
      Fonts.setMinFontSize(wrappedComponent(), size);
      return Cast.as(this);
   }

   default F minimumSize(int width, int height) {
      wrappedComponent().setMinimumSize(new Dimension(width, height));
      return Cast.as(this);
   }

   default F onComponentHidden(@NonNull BiConsumer<F, ComponentEvent> consumer) {
      wrappedComponent().addComponentListener(ComponentListeners.componentHidden(e -> consumer.accept(Cast.as(this),
                                                                                                      e)));
      return Cast.as(this);
   }

   default F onComponentMoved(@NonNull BiConsumer<F, ComponentEvent> consumer) {
      wrappedComponent().addComponentListener(ComponentListeners.componentMoved(e -> consumer.accept(Cast.as(this),
                                                                                                     e)));
      return Cast.as(this);
   }

   default F onComponentResized(@NonNull BiConsumer<F, ComponentEvent> consumer) {
      wrappedComponent().addComponentListener(ComponentListeners.componentResized(e -> consumer.accept(Cast.as(this),
                                                                                                       e)));
      return Cast.as(this);
   }

   default F onComponentShown(@NonNull BiConsumer<F, ComponentEvent> consumer) {
      wrappedComponent().addComponentListener(ComponentListeners.componentShown(e -> consumer.accept(Cast.as(this),
                                                                                                     e)));
      return Cast.as(this);
   }

   default F onFocusGained(@NonNull BiConsumer<F, FocusEvent> consumer) {
      wrappedComponent().addFocusListener(FocusListeners.onFocusGained(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onFocusLost(@NonNull BiConsumer<F, FocusEvent> consumer) {
      wrappedComponent().addFocusListener(FocusListeners.onFocusLost(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default FluentJButton onHover(@NonNull BiConsumer<F, Boolean> applicator) {
      wrappedComponent().addMouseListener(mouseEntered(e -> applicator.accept(Cast.as(this), true)));
      wrappedComponent().addMouseListener(mouseExited(e -> applicator.accept(Cast.as(this), false)));
      return Cast.as(this);
   }

   default F onKeyPressed(@NonNull BiConsumer<F, KeyEvent> consumer) {
      wrappedComponent().addKeyListener(KeyListeners.keyPressed(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onKeyReleased(@NonNull BiConsumer<F, KeyEvent> consumer) {
      wrappedComponent().addKeyListener(KeyListeners.keyReleased(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onKeyTyped(@NonNull BiConsumer<F, KeyEvent> consumer) {
      wrappedComponent().addKeyListener(KeyListeners.keyTyped(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseClicked(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseClicked(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseDoubleClicked(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseClicked(e -> {
         if(e.getClickCount() == 2) {
            consumer.accept(Cast.as(this), e);
         }
      }));
      return Cast.as(this);
   }

   default F onMouseDragged(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseMotionListener(MouseListeners.mouseDragged(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseEntered(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseEntered(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseExited(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseExited(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseMoved(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseMotionListener(MouseListeners.mouseMoved(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMousePressed(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mousePressed(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseReleased(@NonNull BiConsumer<F, MouseEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseReleased(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F onMouseWheelMoved(@NonNull BiConsumer<F, MouseWheelEvent> consumer) {
      wrappedComponent().addMouseListener(MouseListeners.mouseWheelMoved(e -> consumer.accept(Cast.as(this), e)));
      return Cast.as(this);
   }

   default F opaque() {
      wrappedComponent().setOpaque(true);
      return Cast.as(this);
   }

   default F opaque(boolean opaque) {
      wrappedComponent().setOpaque(opaque);
      return Cast.as(this);
   }

   default T popupMenu(JPopupMenu component) {
      wrappedComponent().setComponentPopupMenu(component);
      return Cast.as(this);
   }

   default F preferredSize(int width, int height) {
      wrappedComponent().setPreferredSize(new Dimension(width, height));
      return Cast.as(this);
   }

   default F preferredSize(@NonNull Function<F, Dimension> calculator) {
      wrappedComponent().setPreferredSize(calculator.apply(Cast.as(this)));
      return Cast.as(this);
   }

   default F raisedBevelBorder() {
      wrappedComponent().setBorder(BorderFactory.createRaisedBevelBorder());
      return Cast.as(this);
   }

   default F raisedSoftBevelBorder() {
      wrappedComponent().setBorder(BorderFactory.createRaisedSoftBevelBorder());
      return Cast.as(this);
   }

   default F removeAllKeyListeners() {
      for(KeyListener kl : wrappedComponent().getListeners(KeyListener.class)) {
         wrappedComponent().removeKeyListener(kl);
      }
      return Cast.as(this);
   }

   default F removeAllMouseListeners() {
      for(MouseListener kl : wrappedComponent().getListeners(MouseListener.class)) {
         wrappedComponent().removeMouseListener(kl);
      }
      return Cast.as(this);
   }

   default F size(int width, int height) {
      wrappedComponent().setSize(width, height);
      return Cast.as(this);
   }

   default F tooltip(String tooltip) {
      wrappedComponent().setToolTipText(tooltip);
      return Cast.as(this);
   }

   default F translucent() {
      wrappedComponent().setOpaque(false);
      return Cast.as(this);
   }

   default F visible(boolean value) {
      wrappedComponent().setVisible(value);
      return Cast.as(this);
   }

   T wrappedComponent();

}//END OF ComponentUpdate
