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

package com.gengoai.swing.components;

import com.gengoai.swing.fluent.*;
import lombok.NonNull;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JScrollPane;
import javax.swing.table.TableModel;
import java.awt.Component;
import java.util.Arrays;
import java.util.Vector;
import java.util.function.Consumer;

public final class Components {

   private Components() {
      throw new IllegalAccessError();
   }

   public static FluentBorderLayoutPanel borderLayoutPanel(@NonNull Consumer<FluentBorderLayoutPanel> consumer) {
      FluentBorderLayoutPanel panel = new FluentBorderLayoutPanel();
      consumer.accept(panel);
      return panel;
   }

   public static FluentJButton button(String text) {
      return new FluentJButton(text);
   }

   public static FluentJButton button(Icon icon) {
      return new FluentJButton(icon);
   }

   public static FluentJButton button(String text, Icon icon) {
      return new FluentJButton(text, icon);
   }

   public static HBox hbox() {
      return new HBox();
   }

   public static HBox hbox(@NonNull Consumer<HBox> consumer) {
      return hbox(0, consumer);
   }

   public static HBox hbox(int gap, @NonNull Consumer<HBox> consumer) {
      HBox hbox = new HBox(gap);
      consumer.accept(hbox);
      return hbox;
   }

   public static HBox hbox(int gap) {
      return new HBox(gap);
   }

   public static HBox hbox(Component... components) {
      return hbox(0, components);
   }

   public static HBox hbox(int gap, Component... components) {
      HBox hBox = new HBox(gap);
      for(Component component : components) {
         hBox.add(component);
      }
      return hBox;
   }

   public static FluentJLabel label(String text) {
      return new FluentJLabel(text);
   }

   public static FluentJLabel label(Icon icon) {
      return new FluentJLabel(icon);
   }

   public static <E> FluentJList<E> list() {
      return new FluentJList<>();
   }

   public static FluentJPanel panel() {
      return new FluentJPanel();
   }

   public static FluentJPanel panel(@NonNull Consumer<FluentJPanel> consumer) {
      FluentJPanel panel = new FluentJPanel();
      consumer.accept(panel);
      return panel;
   }

   public static JScrollPane scrollPane(Component component) {
      JScrollPane scrollPane = new JScrollPane(component);
      scrollPane.setBorder(BorderFactory.createEmptyBorder());
      return scrollPane;
   }

   public static FluentJTable table() {
      return new FluentJTable();
   }

   public static FluentJTable table(boolean isEditable, String... columns) {
      DefaultMutableTableModel model;
      if(isEditable) {
         model = new DefaultMutableTableModel(new Vector<>(Arrays.asList(columns)), 0);
      } else {
         model = new DefaultMutableTableModel(new Vector<>(Arrays.asList(columns)), 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
               return false;
            }
         };
      }
      return new FluentJTable(model);
   }

   public static FluentJTable table(TableModel tableModel) {
      return new FluentJTable(tableModel);
   }

   public static FluentJTextField textField(String text) {
      return new FluentJTextField(text);
   }

   public static FluentJTextField textField() {
      return new FluentJTextField();
   }

   public static FluentJTextPane textPane() {
      return new FluentJTextPane();
   }

   public static FluentJTextPane textPane(@NonNull Consumer<FluentJTextPane> consumer) {
      var textPane = new FluentJTextPane();
      consumer.accept(textPane);
      return textPane;
   }

   public static TitlePane titlePane(String title, boolean showCloseButton, JComponent component) {
      return new TitlePane(title, showCloseButton, component);
   }

   public static VBox vbox() {
      return new VBox();
   }

   public static VBox vbox(@NonNull Consumer<VBox> consumer) {
      return vbox(0, consumer);
   }

   public static VBox vbox(int gap, @NonNull Consumer<VBox> consumer) {
      VBox vBox = new VBox(gap);
      consumer.accept(vBox);
      return vBox;
   }

   public static VBox vbox(int gap) {
      return new VBox(gap);
   }

   public static VBox vbox(Component... components) {
      return vbox(0, components);
   }

   public static VBox vbox(int gap, Component... components) {
      VBox vbox = new VBox(gap);
      for(Component component : components) {
         vbox.add(component);
      }
      return vbox;
   }

}//END OF Components
