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

import javax.swing.*;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * @author David B. Bracewell
 */
public class LongTextField extends JTextField {
   public LongTextField(int size) {
      super(size);
   }

   @Override
   protected javax.swing.text.Document createDefaultModel() {
      return new LongTextDocument();
   }

   public void decrement() {
      setValue(getValue() - 1);
   }

   public long getValue() {
      return Long.parseLong(getText());
   }

   public void setValue(long index) {
      setText(Long.toString(index));
   }

   public void increment() {
      setValue(getValue() + 1);
   }

   class LongTextDocument extends PlainDocument {

      @Override
      public void insertString(int i, String s, AttributeSet attributeSet) throws BadLocationException {
         if (s == null) {
            return;
         }
         String oldString = getText(0, getLength());
         String newString = oldString.substring(0, i) + s + oldString.substring(i);
         try {
            Long.parseLong(newString);
            super.insertString(i, s, attributeSet);
         } catch (NumberFormatException e) {
            //ignore
         }
      }
   }

}//END OF LongTextField
