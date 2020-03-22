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

import javax.swing.event.CaretEvent;
import javax.swing.text.JTextComponent;
import java.awt.Color;
import java.awt.Insets;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public interface FluentTextComponent<T extends FluentTextComponent<T>> extends FluentComponent<JTextComponent, T> {

   default T caretListener(@NonNull BiConsumer<T, CaretEvent> listener) {
      wrappedComponent().addCaretListener(e -> listener.accept(Cast.as(this), e));
      return Cast.as(this);
   }

   default T editable() {
      wrappedComponent().setEditable(true);
      return Cast.as(this);
   }

   default int getTextAtPosition(Point2D point2D) {
      return wrappedComponent().viewToModel2D(point2D);
   }

   default T margin(int top, int left, int bottom, int right) {
      wrappedComponent().setMargin(new Insets(top, left, bottom, right));
      return Cast.as(this);
   }

   default T nonEditable() {
      wrappedComponent().setEditable(false);
      return Cast.as(this);
   }

   default T onEnterPressed(@NonNull Consumer<T> consumer) {
      onKeyPressed(($, e) -> {
         if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            consumer.accept($);
         }
      });
      return Cast.as(this);
   }

   default T selectedTextColor(Color color) {
      wrappedComponent().setSelectedTextColor(color);
      return Cast.as(this);
   }

   default T selectionColor(Color color) {
      wrappedComponent().setSelectionColor(color);
      return Cast.as(this);
   }

   default T text(String text) {
      wrappedComponent().setText(text);
      return Cast.as(this);
   }

}//END OF FluentTextComponent
