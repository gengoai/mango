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

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class DefaultMutableTableModel extends DefaultTableModel implements MutableTableModel {
   public DefaultMutableTableModel() {
   }

   public DefaultMutableTableModel(int rowCount, int columnCount) {
      super(rowCount, columnCount);
   }

   public DefaultMutableTableModel(Vector<?> columnNames, int rowCount) {
      super(columnNames, rowCount);
   }

   public DefaultMutableTableModel(Object[] columnNames, int rowCount) {
      super(columnNames, rowCount);
   }

   public DefaultMutableTableModel(Vector<? extends Vector> data, Vector<?> columnNames) {
      super(data, columnNames);
   }

   public DefaultMutableTableModel(Object[][] data, Object[] columnNames) {
      super(data, columnNames);
   }

   @Override
   public void addRow(List<?> row) {
      addRow(new Vector<>(row));
   }
}//END OF DefaultMutableTableModel
