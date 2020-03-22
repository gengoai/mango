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
import com.gengoai.swing.ColorUtils;
import lombok.Getter;
import lombok.NonNull;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.*;
import java.awt.Color;
import java.awt.Component;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

public class FluentJTable extends JTable implements FluentComponent<JTable, FluentJTable> {
   @Getter
   private Color alternateRowColor;
   @Getter
   private Color rowColor;

   public FluentJTable() {
      super(new DefaultMutableTableModel());
   }

   public FluentJTable(@NonNull TableModel dm) {
      super(dm);
   }

   public FluentJTable addAllRows(Object[][] rows) {
      for(Object[] row : rows) {
         addRow(row);
      }
      return this;
   }

   public FluentJTable addAllRows(List<List<?>> rows) {
      rows.forEach(this::addRow);
      return this;
   }

   public FluentJTable addRow(Object... row) {
      TableModel model = getModel();
      if(model instanceof MutableTableModel) {
         Cast.<MutableTableModel>as(model).addRow(row);
      } else if(model instanceof DefaultTableModel) {
         Cast.<DefaultTableModel>as(model).addRow(row);
      } else {
         throw new UnsupportedOperationException();
      }
      return this;
   }

   public FluentJTable addRow(List<?> row) {
      TableModel model = getModel();
      if(model instanceof MutableTableModel) {
         Cast.<MutableTableModel>as(model).addRow(row);
      } else if(model instanceof DefaultTableModel) {
         Cast.<DefaultTableModel>as(model).addRow(row.toArray());
      } else {
         throw new UnsupportedOperationException();
      }
      return this;
   }

   public FluentJTable alternateRowColor(Color color) {
      this.alternateRowColor = color;
      return this;
   }

   public FluentJTable alternateRowColor(@NonNull Function<Color, Color> colorConverter) {
      this.alternateRowColor = colorConverter.apply(getBackground());
      return this;
   }

   public FluentJTable autoCreateRowSorter(boolean value) {
      this.setAutoCreateRowSorter(value);
      return this;
   }

   public FluentJTable disableColumnReordering() {
      if(getTableHeader() != null) {
         this.getTableHeader().setReorderingAllowed(false);
      }
      return this;
   }

   public FluentJTable enableColumnReordering() {
      if(getTableHeader() != null) {
         this.getTableHeader().setReorderingAllowed(false);
      }
      return this;
   }

   public FluentJTable fillViewPortHeight() {
      this.setFillsViewportHeight(true);
      return this;
   }

   public Object getValueAt(int row, int column) {
      return getModel().getValueAt(convertRowIndexToModel(row),
                                   convertColumnIndexToModel(column));
   }

   public FluentJTable hideGrid() {
      this.setShowGrid(false);
      return this;
   }

   public FluentJTable hideHeader() {
      setTableHeader(null);
      return this;
   }

   public <T extends TableModel> T model() {
      return Cast.as(getModel());
   }

   public FluentJTable multipleIntervalSelectionMode() {
      this.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
      return this;
   }

   public FluentJTable nonEditable() {
      setDefaultEditor(Object.class, null);
      return this;
   }

   public FluentJTable onSelectionChanged(@NonNull BiConsumer<FluentJTable, ListSelectionEvent> consumer) {
      this.getSelectionModel()
          .addListSelectionListener(x -> {
             consumer.accept(this, x);
          });
      return this;
   }

   @Override
   public Component prepareRenderer(TableCellRenderer renderer, int row, int column) {
      Component comp = super.prepareRenderer(renderer, row, column);
      if(!comp.getBackground().equals(getSelectionBackground())) {
         Color background = comp.getBackground();
         if(row % 2 == 0 && alternateRowColor != null) {
            background = alternateRowColor;
         } else if(row % 2 != 0) {
            background = rowColor == null
                         ? getBackground()
                         : rowColor;
         }
         comp.setBackground(background);
         comp.setForeground(ColorUtils.calculateBestFontColor(background));
      }
      return comp;
   }

   public void resizeColumnWidth(int minWidth) {
      final TableColumnModel columnModel = getColumnModel();
      for(int column = 0; column < getColumnCount(); column++) {
         int width = minWidth;
         for(int row = 0; row < getRowCount(); row++) {
            TableCellRenderer renderer = getCellRenderer(row, column);
            Component comp = prepareRenderer(renderer, row, column);
            width = Math.max(comp.getPreferredSize().width + 1, width);
         }
         columnModel.getColumn(column).setPreferredWidth(width);
      }
   }

   public FluentJTable rowColor(Color color) {
      this.rowColor = color;
      return this;
   }

   public FluentJTable rowCount(int rowCount) {
      TableModel model = getModel();
      if(model instanceof MutableTableModel) {
         Cast.<MutableTableModel>as(model).setRowCount(rowCount);
      } else if(model instanceof DefaultTableModel) {
         Cast.<DefaultTableModel>as(model).setRowCount(rowCount);
      } else {
         throw new UnsupportedOperationException();
      }
      return this;
   }

   public FluentJTable rowHeightPadding(int amount) {
      this.setRowHeight(getFontMetrics().getHeight() + amount);
      return this;
   }

   public FluentJTable showGrid() {
      this.setShowGrid(true);
      return this;
   }

   public FluentJTable singleIntervalSelectionMode() {
      this.setSelectionMode(ListSelectionModel.SINGLE_INTERVAL_SELECTION);
      return this;
   }

   public FluentJTable singleSelectionModel() {
      this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
      return this;
   }

   public FluentJTable withColumn(int index, @NonNull Consumer<TableColumn> consumer) {
      consumer.accept(getColumnModel().getColumn(index));
      return this;
   }

   @Override
   public JTable wrappedComponent() {
      return this;
   }

}//END OF FluentJTable
