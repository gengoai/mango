/*
 * (c) 2005 David B. Bracewell
 *
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
 *
 */

package com.gengoai.ui;

import com.gengoai.string.CharMatcher;

import javax.swing.*;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

import static com.gengoai.application.SwingApplication.mouseReleased;

/**
 * @author David B. Bracewell
 */
public class EditorPanel extends JScrollPane {
   private final JTextPane editorPane;
   private Style DEFAULT;
   private boolean autoExpand = false;


   public EditorPanel() {
      super();
      editorPane = new JTextPane(new DefaultStyledDocument());
      getViewport().add(editorPane);

      editorPane.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, Boolean.TRUE);
      editorPane.getCaret().setVisible(true);
      DEFAULT = editorPane.addStyle("DEFAULT", null);
      editorPane.setCharacterAttributes(DEFAULT, true);
      editorPane.addKeyListener(new KeyAdapter() {
         @Override
         public void keyReleased(KeyEvent e) {
            if (e.isControlDown() && e.getKeyChar() == '+') {
               Font font = editorPane.getFont();
               if (font.getSize() < 24) {
                  Font f2 = new Font(font.getName(), font.getStyle(), font.getSize() + 2);
                  editorPane.setFont(f2);
                  StyleConstants.setFontSize(editorPane.getStyle(StyleContext.DEFAULT_STYLE), f2.getSize());
               }
            } else if (e.isControlDown() && e.getKeyChar() == '-') {
               Font font = editorPane.getFont();
               if (font.getSize() > 12) {
                  Font f2 = new Font(font.getName(), font.getStyle(), font.getSize() - 2);
                  editorPane.setFont(f2);
               }
            }
//            lines.setFont(new Font(Font.MONOSPACED, Font.PLAIN, editorPane.getFont().getSize()));
            super.keyPressed(e);
         }
      });

      editorPane.addMouseListener(mouseReleased(e -> {
         if (!e.isAltDown() && autoExpand) {
            autoExpandSelection();
         }
      }));

      editorPane.getCaret().setVisible(true);
      editorPane.setFont(new Font(Font.MONOSPACED, Font.PLAIN, editorPane.getFont().getSize()));
//      JTextArea lines = new JTextArea("1");
//      lines.setPreferredSize(new Dimension(getFontMetrics(getFont()).charWidth('1') * 4, 0));
//      lines.setMargin(editorPane.getMargin());
//      lines.setEnabled(false);
//      lines.setDisabledTextColor(SystemColor.textInactiveText);
//      lines.setFont(new Font(Font.MONOSPACED, Font.PLAIN, editorPane.getFont().getSize()));
//      lines.setBorder(null);
//      editorPane.getStyledDocument().addDocumentListener(new DocumentListener() {
//         @Override
//         public void changedUpdate(DocumentEvent de) {
//            lines.setText(getText());
//         }
//
//         public String getText() {
//            int caretPosition = editorPane.getStyledDocument().getLength();
//            Element root = editorPane.getStyledDocument().getDefaultRootElement();
//            int width = String.valueOf(root.getElementIndex(caretPosition) + 2).length();
//            StringBuilder text = new StringBuilder("1")
//                                    .append(System.getProperty("line.separator"));
//            for (int i = 2; i < root.getElementIndex(caretPosition) + 2; i++) {
//               text.append(i)
//                   .append(System.getProperty("line.separator"));
//            }
//            return text.toString();
//         }
//
//         @Override
//         public void insertUpdate(DocumentEvent de) {
//            lines.setText(getText());
//         }
//
//         @Override
//         public void removeUpdate(DocumentEvent de) {
//            lines.setText(getText());
//         }
//      });
//      setRowHeaderView(lines);

      LineNumbers lineNumbers = new LineNumbers();
      setRowHeaderView(lineNumbers);
   }

   public void addCaretListener(CaretListener listener) {
      editorPane.addCaretListener(listener);
   }

   @Override
   public synchronized void addMouseListener(MouseListener mouseListener) {
      editorPane.addMouseListener(mouseListener);
   }

   public void addStyle(String name, Consumer<Style> styleInitializer) {
      styleInitializer.accept(editorPane.addStyle(name, null));
   }

   private void autoExpandSelection() {
      int start = editorPane.getSelectionStart();
      int end = editorPane.getSelectionEnd();
      String txt = editorPane.getText();
      while (start > 0 && !Character.isWhitespace(txt.charAt(start - 1)) && !CharMatcher.Punctuation.test(
         txt.charAt(start - 1))) {
         start--;
      }

      while (start < end && Character.isWhitespace(txt.charAt(start))) {
         start++;
      }

      while (end < txt.length() && !Character.isWhitespace(txt.charAt(end)) && !CharMatcher.Punctuation.test(
         txt.charAt(end))) {
         end++;
      }

      while (end > start && Character.isWhitespace(txt.charAt(end - 1))) {
         end--;
      }
      if (start == end) {
         return;
      }
      editorPane.setSelectionEnd(end);
      editorPane.setSelectionStart(start);
   }

   public JTextPane getEditorPane() {
      return editorPane;
   }

   public String getText(int start, int end) throws BadLocationException {
      return editorPane.getText(start, end);
   }

   public String getText() {
      return editorPane.getText();
   }

   public void setText(String text) {
      editorPane.setText(text);
   }

   public boolean isAutoExpand() {
      return autoExpand;
   }

   public void setAutoExpand(boolean autoExpand) {
      this.autoExpand = autoExpand;
   }

   public boolean isEditorEnabled() {
      return editorPane.isEnabled();
   }

   public void setEditorEnabled(boolean isEnabled) {
      editorPane.setEnabled(isEnabled);
   }

   @Override
   public void setComponentPopupMenu(JPopupMenu jPopupMenu) {
      editorPane.setComponentPopupMenu(jPopupMenu);
   }

   public void setStyle(int start, int end, String styleName) {
      editorPane.getStyledDocument()
                .setCharacterAttributes(start, end - start, editorPane.getStyle(styleName), true);
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
         while (startOffset <= endOffset) {
            try {
               final int lineNumber = root.getElementIndex(startOffset);
               final Element line = root.getElement(lineNumber);
               if (line.getStartOffset() == startOffset) {
                  int x = getInsets().left + 2;
                  Rectangle r = editorPane.modelToView(startOffset);
                  int y = r.y + r.height - editorPane.getFontMetrics(editorPane.getFont()).getDescent();
                  g.setFont(editorPane.getFont());
                  g.drawString(String.format("%3d", lineNumber + 1), x, y);
               }
               startOffset = Utilities.getRowEnd(editorPane, startOffset) + 1;
            } catch (BadLocationException e) {
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

   public int getLineCount() {
      return editorPane.getDocument()
                       .getDefaultRootElement()
                       .getElementCount();
   }

}//END OF EditorPanel
