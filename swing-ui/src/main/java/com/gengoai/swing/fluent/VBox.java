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
import com.gengoai.swing.layout.VerticalLayout;

import javax.swing.JComponent;
import java.awt.LayoutManager;

public class VBox extends FluentJPanelBase<VBox> {
   private final VerticalLayout layout;

   public VBox() {
      this(0);
   }

   public VBox(int gap) {
      this.layout = new VerticalLayout(gap);
      super.setLayout(layout);
   }


   @Override
   public void setLayout(LayoutManager mgr) {

   }

   public VBox setResizeWithComponent(int index) {
      layout.setResizeWith(Cast.as(getComponent(index)));
      invalidate();
      repaint();
      return this;
   }

   public VBox setResizeWithComponent(JComponent component) {
      layout.setResizeWith(component);
      invalidate();
      repaint();
      return this;
   }

}//END OF VBox
