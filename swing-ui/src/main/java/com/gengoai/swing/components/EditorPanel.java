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
import com.gengoai.string.Strings;
import com.gengoai.swing.KeyListeners;
import com.gengoai.swing.MouseListeners;
import com.gengoai.tuple.IntPair;
import lombok.NonNull;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.List;
import java.util.function.Consumer;


/**
 * @author David B. Bracewell
 */
public class EditorPanel extends JScrollPane {

   public static final AutoExpandAction NO_AUTO_EXPANSION = (s, e, t, o) -> IntPair.of(s, e);

   public static final AutoExpandAction DEFAULT_AUTO_EXPANSION = (s, e, t, span) -> {
      if(span == null) {
         return Strings.expand(t, s, e);
      }
      return IntPair.of(span.start(), span.end());
   };


   private final JTextPane editorPane;
   private final IntervalTree<StyledSpan> range2Style = new IntervalTree<>();
   private Style DEFAULT;
   @NonNull
   private AutoExpandAction autoExpand = NO_AUTO_EXPANSION;

   public EditorPanel() {
      super();
      editorPane = new JTextPane(new DefaultStyledDocument());
      getViewport().add(editorPane);
      editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      DEFAULT = editorPane.addStyle("DEFAULT", null);
      editorPane.setCharacterAttributes(DEFAULT, true);
      editorPane.setSelectionColor(Color.YELLOW);
      editorPane.setSelectedTextColor(Color.BLACK);
      editorPane.addKeyListener(KeyListeners.onKeyReleased(this::zoom));
      SimpleAttributeSet attributeSet = new SimpleAttributeSet();
      StyleConstants.setLineSpacing(attributeSet, 0.5f);
      editorPane.getStyledDocument()
                .setParagraphAttributes(0, editorPane.getStyledDocument().getLength(), attributeSet, false);
      editorPane.addMouseListener(MouseListeners.mouseReleased(e -> {
         if(!e.isAltDown()) {
            autoExpandSelection();
         }
      }));

      LineNumbers lineNumbers = new LineNumbers();
      setRowHeaderView(lineNumbers);
      editorPane.setCaret(new DefaultCaret() {
         @Override
         public void setSelectionVisible(boolean visible) {
            super.setSelectionVisible(true);
         }
      });
   }

   public void addCaretListener(CaretListener listener) {
      editorPane.addCaretListener(listener);
   }

   @Override
   public synchronized void addMouseListener(MouseListener mouseListener) {
      editorPane.addMouseListener(mouseListener);
   }

   public void addMouseMotionListener(MouseMotionListener listener) {
      editorPane.addMouseMotionListener(listener);
   }

   public void addStyle(String name, Consumer<Style> styleInitializer) {
      styleInitializer.accept(editorPane.addStyle(name, null));
   }

   private void autoExpandSelection() {
      IntPair span = autoExpand.expand(editorPane.getSelectionStart(),
                                       editorPane.getSelectionEnd(),
                                       editorPane.getText(),
                                       getOverlappingStyledSpan(editorPane.getSelectionStart(),
                                                                editorPane.getSelectionEnd()));
      editorPane.setSelectionEnd(span.v2);
      editorPane.setSelectionStart(span.v1);
   }

   public void clearStyle(int start, int end) {
      for(StyledSpan styledSpan : getStyleName(start, end)) {
         range2Style.remove(styledSpan);
         editorPane.getStyledDocument()
                   .setCharacterAttributes(styledSpan.start(), styledSpan.length(), DEFAULT, true);
      }
   }

   @Override
   public JToolTip createToolTip() {
      return editorPane.createToolTip();
   }

   public boolean editorHasFocus() {
      return editorPane.hasFocus();
   }

   public Highlighter getHighlighter() {
      return editorPane.getHighlighter();
   }

   public int getLineCount() {
      return editorPane.getDocument()
                       .getDefaultRootElement()
                       .getElementCount();
   }

   public StyledSpan getOverlappingStyledSpan(final int start, final int end) {
      List<StyledSpan> spans = Lists.asArrayList(range2Style.overlapping(Span.of(start, end)));
      return spans.stream()
                  .max((s1, s2) -> {
                     int cmp = Integer.compare(Math.abs(s1.start() - start),
                                               Math.abs(s2.start() - start));
                     if(cmp == 0) {
                        cmp = Integer.compare(s1.length(), s2.length());
                     } else {
                        cmp = -cmp;
                     }
                     return cmp;
                  }).orElse(null);
   }

   public String getSelectedText() {
      return editorPane.getSelectedText();
   }

   public int getSelectionEnd() {
      return editorPane.getSelectionEnd();
   }

   public int getSelectionStart() {
      return editorPane.getSelectionStart();
   }

   public List<StyledSpan> getStyleName(int start, int end) {
      return Lists.asArrayList(range2Style.overlapping(Span.of(start, end)));
   }

   public String getText(int start, int length) throws BadLocationException {
      return editorPane.getText(start, length);
   }

   public String getText() {
      return editorPane.getText();
   }

   public void setText(String text) {
      range2Style.clear();
      editorPane.setText(text);
   }

   public int getTextAtPosition(Point2D point2D) {
      return editorPane.viewToModel2D(point2D);
   }

   public boolean hasSelection() {
      return editorPane.getSelectionStart() < editorPane.getSelectionEnd();
   }

   public void highlight(int start, int end, String style) {
      range2Style.add(new StyledSpan(start, end, style));
      editorPane.getStyledDocument()
                .setCharacterAttributes(start, end - start, editorPane.getStyle(style), true);
   }


   public void setAutoExpandAction(@NonNull AutoExpandAction action) {
      this.autoExpand = action;
   }

   @Override
   public void setComponentPopupMenu(JPopupMenu jPopupMenu) {
      editorPane.setComponentPopupMenu(jPopupMenu);
   }

   public void setEditable(boolean isEnabled) {
      editorPane.setEditable(isEnabled);
   }

   public void setFontSize(int fontSize) {
      editorPane.setFont(new Font(editorPane.getFont().getName(),
                                  editorPane.getFont().getStyle(),
                                  fontSize));
   }

   public void setSelectionRange(int start, int end) {
      editorPane.requestFocus();
      editorPane.select(start, end);
   }

   public void setShowLineNumbers(boolean showLineNumbers) {
      if(showLineNumbers) {
         setRowHeaderView(new LineNumbers());
      } else {
         setRowHeaderView(null);
      }
      invalidate();
   }

   public void setStyle(int start, int end, String styleName) {
      editorPane.getStyledDocument()
                .setCharacterAttributes(start, end - start, editorPane.getStyle(styleName), true);
   }

   @Override
   public void setToolTipText(String text) {
      editorPane.setToolTipText(text);
   }

   public void updateHighlight(int start, int end, String oldStyle, String newStyle) {
      range2Style.remove(new StyledSpan(start, end, oldStyle));
      highlight(start, end, newStyle);
   }

   private void zoom(KeyEvent e) {
      if(e.isControlDown() && e.getKeyChar() == '+') {
         Font font = editorPane.getFont();
         if(font.getSize() < 24) {
            Font f2 = new Font(font.getName(), font.getStyle(), font.getSize() + 2);
            editorPane.setFont(f2);
            StyleConstants.setFontSize(editorPane.getStyle(StyleContext.DEFAULT_STYLE), f2.getSize());
         }
      } else if(e.isControlDown() && e.getKeyChar() == '-') {
         Font font = editorPane.getFont();
         if(font.getSize() > 12) {
            Font f2 = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
            editorPane.setFont(f2);
         }
      }
   }

   class LineNumbers extends JComponent implements DocumentListener, CaretListener, ComponentListener {

      public LineNumbers() {
         editorPane.getDocument().addDocumentListener(this);
         editorPane.addCaretListener(this);
         editorPane.addComponentListener(this);
      }

      @Override
      public void caretUpdate(CaretEvent e) {
         documentChanged();
      }

      @Override
      public void changedUpdate(DocumentEvent e) {
         documentChanged();
      }

      @Override
      public void componentHidden(ComponentEvent e) {
      }

      @Override
      public void componentMoved(ComponentEvent e) {
      }

      @Override
      public void componentResized(ComponentEvent e) {
         updateSize();
         documentChanged();
      }

      @Override
      public void componentShown(ComponentEvent e) {
         updateSize();
         documentChanged();
      }

      private void documentChanged() {
         SwingUtilities.invokeLater(this::repaint);
      }

      @Override
      public void insertUpdate(DocumentEvent e) {
         documentChanged();
      }

      @Override
      public void paintComponent(Graphics g) {
         super.paintComponent(g);
         Rectangle clip = g.getClipBounds();
         int startOffset = editorPane.viewToModel(new Point(0, clip.y));
         int endOffset = editorPane.viewToModel(new Point(0, clip.y + clip.height));
         final Element root = editorPane.getDocument().getDefaultRootElement();
         while(startOffset <= endOffset) {
            try {
               final int lineNumber = root.getElementIndex(startOffset);
               final Element line = root.getElement(lineNumber);
               if(line.getStartOffset() == startOffset) {
                  int x = getInsets().left + 2;
                  Rectangle r = editorPane.modelToView(startOffset);
                  int y = r.y + r.height - editorPane.getFontMetrics(editorPane.getFont()).getDescent();
                  g.setFont(editorPane.getFont());
                  g.drawString(String.format("%3d", lineNumber + 1), x, y);
               }
               startOffset = Utilities.getRowEnd(editorPane, startOffset) + 1;
            } catch(BadLocationException e) {
               throw new RuntimeException(e);
            }
         }
      }

      @Override
      public void removeUpdate(DocumentEvent e) {
         documentChanged();
      }

      private void updateSize() {
         Dimension size = new Dimension(editorPane.getGraphics()
                                                  .getFontMetrics()
                                                  .stringWidth(String.format("%3d", getLineCount() + 2)
                                                              ) + 5,
                                        editorPane.getHeight());
         setPreferredSize(size);
         setSize(size);
      }
   }

}//END OF EditorPanel
