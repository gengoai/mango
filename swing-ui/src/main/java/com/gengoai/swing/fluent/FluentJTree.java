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

import javax.swing.JTree;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

public class FluentJTree extends JTree implements FluentComponent<JTree, FluentJTree> {

   public FluentJTree() {
   }

   public FluentJTree(TreeModel newModel) {
      super(newModel);
   }


   public FluentJTree contiguousSelectionMode() {
      getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
      return this;
   }

   public FluentJTree discontiguousSelectionMode() {
      getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
      return this;
   }


   public TreePath getSelectionPath() {
      return getSelectionModel().getSelectionPath();
   }

   public FluentJTree rowPadding(int padAmount) {
      setRowHeight(getFontMetrics(getFont()).getHeight() + 3);
      return this;
   }

   public FluentJTree selectionPath(TreePath path) {
      getSelectionModel().setSelectionPath(path);
      return this;
   }

   public FluentJTree selectionPath(TreeNode... path) {
      getSelectionModel().setSelectionPath(new TreePath(path));
      return this;
   }

   public FluentJTree singleSelectionModel() {
      getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
      return this;
   }

   @Override
   public JTree wrappedComponent() {
      return this;
   }
}//END OF FluentJTree
