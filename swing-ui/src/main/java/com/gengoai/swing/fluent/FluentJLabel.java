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

import javax.swing.Icon;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

public class FluentJLabel extends JLabel implements FluentComponent<JLabel, FluentJLabel> {

   public FluentJLabel(String text) {
      super(text);
   }

   public FluentJLabel() {
   }

   public FluentJLabel(Icon icon) {
      super(icon);
   }

   public static FluentJLabel label(String text) {
      return new FluentJLabel(text);
   }

   public static FluentJLabel label() {
      return new FluentJLabel();
   }

   public static FluentJLabel label(Icon icon) {
      return new FluentJLabel(icon);
   }

   public FluentJLabel alignTextHorizontalCenter() {
      this.setHorizontalAlignment(SwingConstants.CENTER);
      return this;
   }

   public int getTextWidthInPixels(){
      return getFontMetrics().stringWidth(getText());
   }

   public FluentJLabel alignTextHorizontalLeading() {
      this.setHorizontalAlignment(SwingConstants.LEADING);
      return this;
   }

   public FluentJLabel alignTextHorizontalLeft() {
      this.setHorizontalAlignment(SwingConstants.LEFT);
      return this;
   }

   public FluentJLabel alignTextHorizontalRight() {
      this.setHorizontalAlignment(SwingConstants.RIGHT);
      return this;
   }

   public FluentJLabel alignTextHorizontalTrailing() {
      this.setHorizontalAlignment(SwingConstants.TRAILING);
      return this;
   }

   public FluentJLabel alignTextVerticalBottom() {
      this.setVerticalTextPosition(SwingConstants.BOTTOM);
      return this;
   }

   public FluentJLabel alignTextVerticalCenter() {
      this.setVerticalTextPosition(SwingConstants.CENTER);
      return this;
   }

   public FluentJLabel alignTextVerticalTop() {
      this.setVerticalTextPosition(SwingConstants.TOP);
      return this;
   }

   public FluentJLabel icon(Icon icon) {
      this.setIcon(icon);
      return this;
   }

   public FluentJLabel text(String text) {
      this.setText(text);
      return this;
   }

   @Override
   public JLabel wrappedComponent() {
      return this;
   }
}
