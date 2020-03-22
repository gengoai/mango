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

import com.gengoai.collection.Lists;
import com.gengoai.collection.tree.IntervalTree;
import com.gengoai.collection.tree.Span;
import com.gengoai.conversion.Cast;
import com.gengoai.swing.fluent.FluentComponent;
import com.gengoai.swing.fluent.FluentJTextPane;
import com.gengoai.swing.fluent.FluentStyle;
import com.gengoai.swing.fluent.SelectionChangeEvent;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.event.CaretListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.FocusListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.gengoai.swing.Layouts.borderLayout;
import static com.gengoai.swing.components.Components.textPane;
import static com.gengoai.swing.listeners.MouseListeners.mouseWheelMoved;

public abstract class BaseHighlightedRangeViewer<F extends BaseHighlightedRangeViewer<F>> extends JComponent implements FluentComponent<JComponent, F> {
   public static final String DEFAULT_HIGHLIGHT_STYLE_NAME = "**DEFAULT_HIGHLIGHT**";
   protected final FluentStyle defaultHighlightStyle;
   private final IntervalTree<StyledSpan> range2Style = new IntervalTree<>();
   private final FluentJTextPane editor;
   private final JScrollPane scrollPane;
   private final AtomicBoolean allowZoom = new AtomicBoolean(true);
   private final AtomicBoolean viewTooltips = new AtomicBoolean(true);

   public BaseHighlightedRangeViewer() {
      editor = textPane($_ -> {
         $_.clientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE)
           .nonEditable()
           .characterAttributes($_.getDefaultStyle(), true)
           .font(Font.MONOSPACED)
           .fontSize(16f)
           .margin(8, 8, 8, 8)
           .lineSpacing(0.5f)
           .onMouseReleased((txt, e) -> {
              if(!e.isAltDown()) {
                 autoExpandSelection();
              }
           })
           .onMouseMoved((txt, e) -> {
              if(viewTooltips.get()) {
                 int index = txt.getTextAtPosition(e.getPoint());
                 List<StyledSpan> styledSpans = Lists.asArrayList(range2Style.overlapping(Span.of(index, index + 1)));
                 onTextHover(index, styledSpans);
              }
           })
           .onKeyReleased((txt, e) -> {
              if(allowZoom.get()) {
                 if(e.isControlDown() && e.getKeyChar() == '+') {
                    increaseFontSize();
                 } else if(e.isControlDown() && e.getKeyChar() == '-') {
                    decreaseFontSize();
                 }
              }
           });
      });
      defaultHighlightStyle = new FluentStyle(editor.addStyle(DEFAULT_HIGHLIGHT_STYLE_NAME, null));
      defaultHighlightStyle.bold(true).background(Color.YELLOW).foreground(Color.BLACK);
      scrollPane = new JScrollPane(editor);
      scrollPane.addMouseWheelListener(mouseWheelMoved(e -> {
         if(allowZoom.get()) {
            if(e.isControlDown() && e.getWheelRotation() < 0) {
               increaseFontSize();
            } else if(e.isControlDown() && e.getWheelRotation() > 0) {
               decreaseFontSize();
            }
         }
      }));
      borderLayout(this).center(scrollPane);
   }



   public synchronized void addCaretListener(CaretListener listener) {
      editor.addCaretListener(listener);
   }

   @Override
   public synchronized void addFocusListener(FocusListener l) {
      super.addFocusListener(l);
      editor.addFocusListener(l);
   }

   @Override
   public synchronized void addMouseListener(MouseListener mouseListener) {
      editor.addMouseListener(mouseListener);
   }

   @Override
   public synchronized void addMouseMotionListener(MouseMotionListener listener) {
      editor.addMouseMotionListener(listener);
   }

   public F addStyle(String name, @NonNull Consumer<FluentStyle> styleInitializer) {
      editor.addAnSetStyle(name, styleInitializer);
      return Cast.as(this);
   }

   public boolean allowZoom() {
      return allowZoom.get();
   }

   public F keepHighlightWhenFocusLost() {
      editor.setAlwaysHighlight(true);
      return Cast.as(this);
   }

   protected void autoExpandSelection() {
      return;
   }

   public int calculateMinimumHeight() {
      return editor.calculateMinimumHeight();
   }

   public void clearStyle(int start, int end) {
      for(StyledSpan styledSpan : getStyleSpans(start, end)) {
         range2Style.remove(styledSpan);
         editor.getStyledDocument()
               .setCharacterAttributes(styledSpan.start(), styledSpan.length(), editor.getDefaultStyle(), true);
      }
   }

   public boolean containsStyle(String style) {
      return editor.getStyle(style) != null;
   }

   @Override
   public JToolTip createToolTip() {
      return editor.createToolTip();
   }

   private void decreaseFontSize() {
      Font font = editor.getFont();
      if(font.getSize() > 12) {
         editor.fontSize(font.getSize() - 2);
         StyleConstants.setFontSize(editor.getStyle(StyleContext.DEFAULT_STYLE), editor.getFont().getSize());
      }
   }

   public F defaultStyleBackground(Color color) {
      defaultHighlightStyle.background(color);
      return Cast.as(this);
   }

   public F defaultStyleForeground(Color color) {
      defaultHighlightStyle.foreground(color);
      return Cast.as(this);
   }

   public boolean editorHasFocus() {
      return editor.hasFocus();
   }

   @Override
   public Color getBackground() {
      return editor.getBackground();
   }

   public StyledSpan getBestMatchingSelectedStyledSpan() {
      return getBestMatchingStyledSpan(getSelectionStart(), getSelectionEnd());
   }

   public StyledSpan getBestMatchingStyledSpan(final int start, final int end) {
      List<StyledSpan> spans = Lists.asArrayList(range2Style.overlapping(Span.of(start, end)));
      return spans.stream().max((s1, s2) -> {
         int cmp = Integer.compare(Math.abs(s1.start() - start), Math.abs(s2.start() - start));
         if(cmp == 0) {
            cmp = Integer.compare(s1.length(), s2.length());
         } else {
            cmp = -cmp;
         }
         return cmp;
      }).orElse(null);
   }

   @Override
   public Font getFont() {
      return editor.getFont();
   }

   @Override
   public Color getForeground() {
      return editor.getForeground();
   }

   public List<StyledSpan> getSelectedStyleSpans() {
      return getStyleSpans(getSelectionStart(), getSelectionEnd());
   }

   public String getSelectedText() {
      return editor.getSelectedText();
   }

   public int getSelectionEnd() {
      return editor.getSelectionEnd();
   }

   public int getSelectionStart() {
      return editor.getSelectionStart();
   }

   public List<StyledSpan> getStyleSpans(int start, int end) {
      return Lists.asArrayList(range2Style.overlapping(Span.of(start, end)));
   }

   public String getText() {
      return editor.getText();
   }

   public String getText(int start, int length) throws BadLocationException {
      return editor.getText(start, length);
   }

   public int getTextAtPosition(Point2D point2D) {
      return editor.getTextAtPosition(point2D);
   }

   public boolean hasSelection() {
      return editor.getSelectionRange() != null;
   }

   public F hideToolTips() {
      viewTooltips.set(false);
      return Cast.as(this);
   }

   public void highlight(int start, int end, String style) {
      highlight(start, end, style, style);
   }

   public void highlight(int start, int end, String style, String label) {
      range2Style.add(new StyledSpan(start, end, style, label));
      editor.getStyledDocument().setCharacterAttributes(start, end - start, editor.getStyle(style), true);
   }

   private void increaseFontSize() {
      Font font = editor.getFont();
      if(font.getSize() < 24) {
         editor.fontSize(font.getSize() + 2);
         StyleConstants.setFontSize(editor.getStyle(StyleContext.DEFAULT_STYLE), editor.getFont().getSize());
      }
   }

   public F lineSpacing(float factor) {
      editor.lineSpacing(factor);
      return Cast.as(this);
   }

   public F onSelectionChange(@NonNull BiConsumer<F, SelectionChangeEvent> consumer) {
      editor.onSelectionChange(($, change) -> consumer.accept(Cast.as(this), change));
      return Cast.as(this);
   }

   protected void onTextHover(int cursorPosition, List<StyledSpan> styledSpans) {
      if(styledSpans.isEmpty()) {
         setToolTipText(null);
      } else {
         setToolTipText(styledSpans.stream().map(s -> s.label).collect(Collectors.joining("\n")));
      }
   }

   public F popupMenu(JPopupMenu component) {
      wrappedComponent().setComponentPopupMenu(component);
      return Cast.as(this);
   }

   public F removeHighlightWhenFocusLost() {
      editor.setAlwaysHighlight(false);
      return Cast.as(this);
   }

   public F selectedTextColor(Color color) {
      editor.selectedTextColor(color);
      return Cast.as(this);
   }

   public F selectionColor(Color color) {
      editor.selectionColor(color);
      return Cast.as(this);
   }

   public F setAllowZoom(boolean value) {
      allowZoom.set(value);
      return Cast.as(this);
   }

   @Override
   public void setBorder(Border border) {
      if(scrollPane != null) {
         scrollPane.setBorder(border);
      }
   }

   @Override
   public void setComponentPopupMenu(JPopupMenu jPopupMenu) {
      editor.setComponentPopupMenu(jPopupMenu);
   }

   @Override
   public void setFont(@NonNull Font font) {
      if(editor != null) {
         editor.setFont(font);
      }
   }

   public void setSelectionRange(int start, int end) {
      editor.requestFocus();
      editor.select(start, end);
   }

   public void setStyle(int start, int end, String styleName) {
      editor.getStyledDocument().setCharacterAttributes(start, end - start, editor.getStyle(styleName), true);
   }

   public void setText(String text) {
      editor.setText(text);
      range2Style.clear();
   }

   @Override
   public void setToolTipText(String text) {
      editor.setToolTipText(text);
   }

   public F showToolTips() {
      viewTooltips.set(true);
      return Cast.as(this);
   }

   public void updateHighlight(int start, int end, String oldStyle, String newStyle) {
      range2Style.remove(new StyledSpan(start, end, oldStyle, oldStyle));
      highlight(start, end, newStyle);
   }

   @Override
   public JComponent wrappedComponent() {
      return editor;
   }

}//END OF HighlightedRangeViewer
