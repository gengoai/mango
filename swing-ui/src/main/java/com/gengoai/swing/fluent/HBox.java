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
import com.gengoai.swing.layout.HorizontalLayout;

import javax.swing.JComponent;
import java.awt.Component;

public class HBox extends FluentJPanel {
   private final HorizontalLayout layout;


   public HBox() {
      this(0);
   }

   public HBox(int gap) {
      this(gap, null);
   }

   public HBox(int gap, JComponent component) {
      this.layout = new HorizontalLayout(gap);
      this.layout.setResizeWith(component);
      setLayout(layout);
   }

   @Override
   public HBox add(Component component) {
      super.add(component);
      return this;
   }

   public HBox setResizeWithComponent(int index) {
      layout.setResizeWith(Cast.as(getComponent(index)));
      return this;
   }

   public HBox setResizeWithComponent(JComponent component) {
      layout.setResizeWith(component);
      return this;
   }

}//END OF VBox
