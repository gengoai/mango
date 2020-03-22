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

package com.gengoai.swing;

import lombok.NonNull;

import java.awt.Component;
import java.awt.Font;
import java.awt.FontMetrics;
import java.util.stream.IntStream;

public class Fonts {

   public static int getAverageFontWidth(@NonNull Component component) {
      return (int) IntStream.of(component.getFontMetrics(component.getFont()).getWidths()).average().orElse(1d);
   }

   public static int getFontHeight(@NonNull Component component) {
      return component.getFontMetrics(component.getFont()).getHeight();
   }

   public static FontMetrics getFontMetrics(@NonNull Component component) {
      return component.getFontMetrics(component.getFont());
   }

   public static int getFontWidth(@NonNull Component component, String string) {
      return component.getFontMetrics(component.getFont()).stringWidth(string);
   }

   public static int getMaxFontWidth(@NonNull Component component) {
      return IntStream.of(component.getFontMetrics(component.getFont()).getWidths()).max().orElse(1);
   }

   public static int getMinFontWidth(@NonNull Component component) {
      return IntStream.of(component.getFontMetrics(component.getFont()).getWidths()).min().orElse(1);
   }

   public static <T extends Component> T setFont(@NonNull T component, String fontName) {
      component.setFont(new Font(fontName, component.getFont().getStyle(), component.getFont().getSize()));
      return component;
   }

   public static <T extends Component> T setFont(@NonNull T component, int style, float size) {
      component.setFont(component.getFont().deriveFont(style, size));
      return component;
   }

   public static <T extends Component> T setFont(@NonNull T component, int style) {
      component.setFont(component.getFont().deriveFont(style));
      return component;
   }

   public static <T extends Component> T setFont(@NonNull T component, float size) {
      component.setFont(component.getFont().deriveFont(size));
      return component;
   }

   public static <T extends Component> T setMaxFontSize(@NonNull T component, float maxSize) {
      component.setFont(component.getFont().deriveFont(Math.max(maxSize, component.getFont().getSize())));
      return component;
   }

   public static <T extends Component> T setMaxFontSize(@NonNull T component, int style, float maxSize) {
      component.setFont(component.getFont().deriveFont(style, Math.max(maxSize, component.getFont().getSize())));
      return component;
   }

   public static <T extends Component> T setMinFontSize(@NonNull T component, float minSize) {
      component.setFont(component.getFont().deriveFont(Math.max(minSize, component.getFont().getSize())));
      return component;
   }

   public static <T extends Component> T setMinFontSize(@NonNull T component, int style, float minSize) {
      component.setFont(component.getFont().deriveFont(style, Math.max(minSize, component.getFont().getSize())));
      return component;
   }

}//END OF Fonts
