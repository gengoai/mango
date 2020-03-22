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

import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class FluentJTextField extends JTextField implements FluentTextComponent<FluentJTextField> {

   public FluentJTextField(String text) {
      super(text);
   }

   public FluentJTextField() {
      super();
   }

   public FluentJTextField alignTextHorizontalCenter() {
      this.setHorizontalAlignment(SwingConstants.CENTER);
      return this;
   }

   public FluentJTextField alignTextHorizontalLeading() {
      this.setHorizontalAlignment(SwingConstants.LEADING);
      return this;
   }

   public FluentJTextField alignTextHorizontalLeft() {
      this.setHorizontalAlignment(SwingConstants.LEFT);
      return this;
   }

   public FluentJTextField alignTextHorizontalRight() {
      this.setHorizontalAlignment(SwingConstants.RIGHT);
      return this;
   }

   public FluentJTextField alignTextHorizontalTrailing() {
      this.setHorizontalAlignment(SwingConstants.TRAILING);
      return this;
   }

   public FluentJTextField columns(int columns) {
      this.setColumns(columns);
      return this;
   }

   public int getTextWidthInPixels() {
      return getFontMetrics().stringWidth(getText());
   }

   @Override
   public JTextField wrappedComponent() {
      return this;
   }
}
