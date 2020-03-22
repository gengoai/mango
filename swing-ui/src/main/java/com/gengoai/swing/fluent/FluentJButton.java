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

import lombok.NonNull;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import java.awt.event.ActionEvent;
import java.util.function.BiConsumer;

public class FluentJButton extends JButton implements FluentComponent<JButton, FluentJButton> {

   public FluentJButton(String text) {
      super(text);
   }

   public FluentJButton() {
   }

   public FluentJButton(Icon icon) {
      super(icon);
   }

   public FluentJButton(String text, Icon icon) {
      super(text, icon);
   }

   public static FluentJButton button(String text) {
      return new FluentJButton(text);
   }

   public static FluentJButton button(String text, Icon icon) {
      return new FluentJButton(text, icon);
   }

   public static FluentJButton button(Icon icon) {
      return new FluentJButton(icon);
   }

   public static FluentJButton button() {
      return new FluentJButton();
   }

   public FluentJButton actionListener(@NonNull BiConsumer<FluentJButton, ActionEvent> listener) {
      addActionListener(e -> listener.accept(this, e));
      return this;
   }

   public FluentJButton alignTextHorizontalCenter() {
      this.setHorizontalAlignment(SwingConstants.CENTER);
      return this;
   }

   public FluentJButton alignTextHorizontalLeading() {
      this.setHorizontalAlignment(SwingConstants.LEADING);
      return this;
   }

   public FluentJButton alignTextHorizontalLeft() {
      this.setHorizontalAlignment(SwingConstants.LEFT);
      return this;
   }

   public FluentJButton alignTextHorizontalRight() {
      this.setHorizontalAlignment(SwingConstants.RIGHT);
      return this;
   }

   public FluentJButton alignTextHorizontalTrailing() {
      this.setHorizontalAlignment(SwingConstants.TRAILING);
      return this;
   }

   public FluentJButton alignTextVerticalBottom() {
      this.setVerticalTextPosition(SwingConstants.BOTTOM);
      return this;
   }

   public int getTextWidthInPixels(){
      return getFontMetrics().stringWidth(getText());
   }

   public FluentJButton alignTextVerticalCenter() {
      this.setVerticalTextPosition(SwingConstants.CENTER);
      return this;
   }

   public FluentJButton alignTextVerticalTop() {
      this.setVerticalTextPosition(SwingConstants.TOP);
      return this;
   }

   public FluentJButton disabledIcon(Icon icon) {
      setDisabledIcon(icon);
      return this;
   }

   public FluentJButton disabledSelectedIcon(Icon icon) {
      setDisabledSelectedIcon(icon);
      return this;
   }

   public FluentJButton icon(Icon icon) {
      this.setIcon(icon);
      return this;
   }

   @Override
   public FluentJButton opaque() {
      setOpaque(true);
      setContentAreaFilled(true);
      return this;
   }

   @Override
   public FluentJButton opaque(boolean opaque) {
      return opaque
             ? opaque()
             : translucent();
   }

   public FluentJButton rolloverIcon(Icon icon) {
      setRolloverIcon(icon);
      return this;
   }

   public FluentJButton rolloverSelectedIcon(Icon icon) {
      setRolloverSelectedIcon(icon);
      return this;
   }

   public FluentJButton selected(boolean selected) {
      setSelected(selected);
      return this;
   }

   public FluentJButton selectedIcon(Icon icon) {
      setSelectedIcon(icon);
      return this;
   }

   public FluentJButton text(String text) {
      this.setText(text);
      return this;
   }

   @Override
   public FluentJButton translucent() {
      setOpaque(false);
      setContentAreaFilled(false);
      return this;
   }

   @Override
   public JButton wrappedComponent() {
      return this;
   }
}
