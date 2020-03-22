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
import lombok.NonNull;

import javax.swing.JComponent;
import javax.swing.JPanel;
import java.awt.Component;
import java.util.function.Predicate;
import java.util.function.Supplier;

public class FluentJPanelBase<T extends FluentJPanelBase<T>> extends JPanel implements FluentComponent<JPanel, T> {

   public T add(Component component) {
      super.add(component);
      return Cast.as(this);
   }

   public T addIf(Component component, @NonNull Predicate<T> condition) {
      if(condition.test(Cast.as(this))) {
         super.add(component);
      }
      return Cast.as(this);
   }

   public T addIf(@NonNull Predicate<T> condition, @NonNull Supplier<JComponent> supplier) {
      if(condition.test(Cast.as(this))) {
         super.add(supplier.get());
      }
      return Cast.as(this);
   }


   @Override
   public JPanel wrappedComponent() {
      return this;
   }

}//END OF FluentJPanel
