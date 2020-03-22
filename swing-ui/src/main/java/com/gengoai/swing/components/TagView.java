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

import com.gengoai.HierarchicalEnumValue;
import com.gengoai.Tag;
import com.gengoai.collection.Iterables;
import com.gengoai.conversion.Cast;
import com.gengoai.string.Strings;
import com.gengoai.swing.ColorUtils;
import com.gengoai.swing.fluent.FluentJTree;
import com.gengoai.swing.fluent.VBox;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.tree.*;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class TagView extends VBox {
   private final Map<Tag, DefaultMutableTreeNode> tag2View = new HashMap<>();
   private final FluentJTree treeView;
   private TagModel tagModel = null;
   private String[] nonRootTags;
   private JComboBox<String> comboBox = new JComboBox<>();
   @NonNull
   private Vector<Consumer<TagInfo>> selectActions = new Vector<>();
   @NonNull
   private Supplier<Boolean> canPerformShortcut = () -> true;

   public TagView() {
      treeView = new FluentJTree() {
         @Override
         public TreeCellRenderer getCellRenderer() {
            return new CustomCellRenderer();
         }
      }.rowPadding(3)
       .singleSelectionModel()
       .removeAllKeyListeners()
       .onMouseClicked(($, e) -> {
          TreePath tp = $.getSelectionPath();
          if(tp != null) {
             DefaultMutableTreeNode node = Cast.as(tp.getLastPathComponent());
             performSelectionTag(Cast.as(node.getUserObject()));
          }
       });
      comboBox.addActionListener(a -> {
         if(a.getActionCommand().equalsIgnoreCase("comboBoxEdited") ||
               (a.getModifiers() & ActionEvent.MOUSE_EVENT_MASK) == ActionEvent.MOUSE_EVENT_MASK) {
            TagInfo tag = tagModel.getTagInfo(comboBox.getSelectedItem().toString());
            treeView.selectionPath(tag2View.get(tag.getTag()).getPath());
            performSelectionTag(tag);
         }
      });
      setMinimumSize(new Dimension(150, 100));

      add(comboBox);
      add(new JScrollPane(treeView));
      setResizeWithComponent(1);
   }

   private DefaultMutableTreeNode createTreeItem(TagInfo tagInfo) {
      var treeItem = new DefaultMutableTreeNode(tagInfo);
      if(tagInfo.getShortcut() != null) {
         KeyboardFocusManager.getCurrentKeyboardFocusManager()
                             .addKeyEventDispatcher(e -> {
                                if(canPerformShortcut.get()) {
                                   KeyStroke ks = KeyStroke.getKeyStrokeForEvent(e);
                                   if(tagInfo.getShortcut().equals(ks)) {
                                      final DefaultMutableTreeNode node = tag2View.get(tagInfo.getTag());
                                      treeView.setSelectionPath(new TreePath(node.getPath()));
                                      performSelectionTag(tagInfo);
                                   }
                                }
                                return false;
                             });
      }
      return treeItem;
   }

   public DefaultMutableTreeNode getNodeFor(TagInfo tagInfo) {
      return tag2View.get(tagInfo.getTag());
   }

   public DefaultMutableTreeNode getNodeFor(Tag tag) {
      return tag2View.get(tag);
   }

   public TagModel getTagModel() {
      return tagModel;
   }

   public String[] getTags() {
      return nonRootTags;
   }

   public TagView onTagSelect(@NonNull Consumer<TagInfo> selectFunction) {
      selectActions.add(selectFunction);
      return this;
   }

   private void performSelectionTag(TagInfo tagInfo) {
      for(Consumer<TagInfo> selectAction : selectActions) {
         selectAction.accept(tagInfo);
      }
   }

   public void setCanPerformShortcut(Supplier<Boolean> canPerformShortcut) {
      this.canPerformShortcut = canPerformShortcut;
   }


   public void setTagModel(TagModel newTagModel) {
      this.tagModel = newTagModel;
      updateView();
   }

   private void updateView() {
      tag2View.clear();
      final DefaultTreeModel model = Cast.as(treeView.getModel());
      final DefaultMutableTreeNode ROOT;
      if(tagModel.getRoots().size() == 1) {
         TagInfo ti = Iterables.getFirst(tagModel.getRoots(), null);
         ROOT = createTreeItem(ti);
         tag2View.put(ti.getTag(), ROOT);
      } else {
         ROOT = new DefaultMutableTreeNode();
         for(TagInfo root : tagModel.getRoots()) {
            DefaultMutableTreeNode node = createTreeItem(root);
            tag2View.put(root.getTag(), node);
            ROOT.add(node);
         }
      }
      model.setRoot(ROOT);
      treeView.setRootVisible(false);
      final List<String> tags = new ArrayList<>();
      for(TagInfo n : tagModel) {
         if(tag2View.containsKey(n.getTag())) {
            continue;
         }
         Tag p = n.parent();
         while(!tag2View.containsKey(p)) {
            p = Cast.<HierarchicalEnumValue<?>>as(p).parent();
         }
         DefaultMutableTreeNode ti = tag2View.get(p);
         DefaultMutableTreeNode node = createTreeItem(n);
         tag2View.put(n.getTag(), node);
         ti.add(node);
         if(node != ROOT) {
            tags.add(n.toString());
         }
      }
      model.nodeStructureChanged(ROOT);
      for(int i = 0; i < treeView.getRowCount(); i++) {
         treeView.expandRow(i);
      }
      nonRootTags = tags.toArray(new String[0]);
      Arrays.sort(nonRootTags);
      for(String t : nonRootTags) {
         comboBox.addItem(t);
      }
   }

   static class CustomCellRenderer extends DefaultTreeCellRenderer {
      private Icon createIcon(TagInfo ti) {
         int w = 15;
         int h = 15;
         Font font = new Font(Font.DIALOG, Font.BOLD, 12);
         String shortcut = null;
         if(ti.getShortcut() != null) {
            shortcut = ti.getShortcut().toString().replaceAll("pressed", "");
            w = getFontMetrics(font).stringWidth(shortcut) + 6;
            h = h + 5;
         }

         BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
         var g = image.getGraphics();
         g.setColor(ti.getColor());
         g.fillRect(0, 0, w, h);

         if(Strings.isNotNullOrBlank(shortcut)) {
            g.setFont(font);
            g.setColor(ColorUtils.calculateBestFontColor(ti.getColor()));
            int y = h - font.getSize() / 2 + 1;
            g.drawString(shortcut, 2, y);
         }

         return new ImageIcon(image);
      }

      public Component getTreeCellRendererComponent(JTree tree,
                                                    Object value, boolean sel, boolean expanded, boolean leaf,
                                                    int row, boolean hasFocus) {
         super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
         DefaultMutableTreeNode node = Cast.as(value);
         if(node.getUserObject() instanceof TagInfo) {
            TagInfo nodeObj = Cast.as(node.getUserObject());
            if(nodeObj == null) {
               return this;
            }
            setFont(new Font(
                  getFont().getName(),
                  Font.BOLD,
                  getFont().getSize()
            ));
            setLeafIcon(null);
            setIcon(createIcon(nodeObj));
            setHorizontalAlignment(SwingConstants.CENTER);
         }
         return this;
      }

   }


}//END OF TagView
