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

import javax.swing.*;
import java.util.Arrays;
import java.util.Collection;

public class FluentJList<E> extends JList<E> implements FluentComponent<JList<E>, FluentJList<E>> {

   public FluentJList() {
      super(new DefaultListModel<>());
   }

   public FluentJList<E> add(E item) {
      this.<DefaultListModel<E>>model().addElement(item);
      return this;
   }

   @SafeVarargs
   public final FluentJList<E> addAll(E... items) {
      this.<DefaultListModel<E>>model().addAll(Arrays.asList(items));
      return this;
   }

   public FluentJList<E> addAll(Collection<E> items) {
      this.<DefaultListModel<E>>model().addAll(items);
      return this;
   }

   public FluentJList<E> alignTextHorizontalLeading() {
      DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.LEADING);
      return this;
   }

   public FluentJList<E> alignTextHorizontalLeft() {
      DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.LEFT);
      return this;
   }

   public FluentJList<E> alignTextHorizontalRight() {
      DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.HORIZONTAL);
      return this;
   }

   public FluentJList<E> alignTextHorizontalTrailing() {
      DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.TRAILING);
      return this;
   }


   public FluentJList<E> alignTextHorizontalCenter() {
      DefaultListCellRenderer renderer = (DefaultListCellRenderer) getCellRenderer();
      renderer.setHorizontalAlignment(SwingConstants.CENTER);
      return this;
   }

   public FluentJList<E> clear() {
      this.<DefaultListModel>model().clear();
      return this;
   }

   public <T extends ListModel<E>> T model() {
      return Cast.as(getModel());
   }

   public FluentJList<E> multipleIntervalSelectionMode() {
      this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      return this;
   }

   public FluentJList<E> singleIntervalSelectionMode() {
      this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      return this;
   }

   public FluentJList<E> singleSelectionModel() {
      this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      return this;
   }

   @Override
   public JList<E> wrappedComponent() {
      return this;
   }
}//END OF FluentJList
