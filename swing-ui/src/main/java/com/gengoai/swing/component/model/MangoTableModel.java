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

package com.gengoai.swing.component.model;

import com.gengoai.tuple.Tuple2;
import com.gengoai.tuple.Tuple3;
import lombok.NonNull;

import javax.swing.table.DefaultTableModel;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.stream.Stream;

public class MangoTableModel extends DefaultTableModel {
   private Map<Integer, Class<?>> columnClasses = new HashMap<>();
   private Map<Integer, Boolean> columnEditable = new HashMap<>();

   public MangoTableModel() {
      super();
   }

   public MangoTableModel(@NonNull String... columnNames) {
      super(columnNames, 0);
   }

   @SafeVarargs
   public MangoTableModel(@NonNull Tuple2<String, Class<?>>... columns) {
      super(Stream.of(columns).map(Tuple2::getKey).toArray(), 0);
      for(int i = 0; i < columns.length; i++) {
         setColumnClass(i, columns[i].v2);
      }
   }

   @SafeVarargs
   public MangoTableModel(@NonNull Tuple3<String, Class<?>, Boolean>... columns) {
      super(Stream.of(columns).map(Tuple3::getV1).toArray(), 0);
      for(int i = 0; i < columns.length; i++) {
         setColumnClass(i, columns[i].v2);
         setColumnEditable(i, columns[i].v3);
      }
   }

   public void addAllRows(Object[][] rows) {
      for(Object[] row : rows) {
         addRow(row);
      }
   }

   public void addAllRows(List<List<?>> rows) {
      rows.forEach(this::addRow);
   }

   public void addRow(List<?> row) {
      super.addRow(new Vector<>(row));
   }

   public void addRow(Object... row) {
      super.addRow(row);
   }

   @Override
   public Class<?> getColumnClass(int columnIndex) {
      return columnClasses.getOrDefault(columnIndex, Object.class);
   }

   @Override
   public boolean isCellEditable(int row, int column) {
      return columnEditable.getOrDefault(column, false);
   }

   public void setColumnClass(int column, Class<?> clazz) {
      columnClasses.put(column, clazz);
   }

   public void setColumnEditable(int column, boolean isEditable) {
      this.columnEditable.put(column, isEditable);
   }

}//END OF MangoTableModel
