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

import java.awt.BorderLayout;
import java.awt.Component;

public class FluentBorderLayoutPanel extends FluentJPanelBase<FluentBorderLayoutPanel> {

   public FluentBorderLayoutPanel(int hgap, int vgap) {
      setLayout(new BorderLayout(hgap, vgap));
   }

   public FluentBorderLayoutPanel() {
      setLayout(new BorderLayout());
   }

   public FluentBorderLayoutPanel center(Component component) {
      super.add(component, BorderLayout.CENTER);
      return this;
   }

   public FluentBorderLayoutPanel east(Component component) {
      super.add(component, BorderLayout.EAST);
      return this;
   }

   public FluentBorderLayoutPanel north(Component component) {
      super.add(component, BorderLayout.NORTH);
      return this;
   }

   public FluentBorderLayoutPanel south(Component component) {
      super.add(component, BorderLayout.SOUTH);
      return this;
   }

   public FluentBorderLayoutPanel west(Component component) {
      super.add(component, BorderLayout.WEST);
      return this;
   }


}//END OF FluentBorderLayoutPanel
