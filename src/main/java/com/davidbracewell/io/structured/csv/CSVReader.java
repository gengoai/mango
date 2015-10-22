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
 */

package com.davidbracewell.io.structured.csv;

import com.davidbracewell.collection.Collect;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Val;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.ElementType;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredReader;
import com.davidbracewell.reflection.BeanMap;
import com.davidbracewell.reflection.Reflect;
import com.davidbracewell.reflection.ReflectionException;
import com.davidbracewell.string.StringUtils;
import com.davidbracewell.tuple.Tuple2;
import com.google.common.base.CharMatcher;
import com.google.common.base.Throwables;
import com.google.common.collect.Lists;
import lombok.NonNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.*;
import java.util.stream.Collectors;

/**
 * The type CSV reader.
 *
 * @author David B. Bracewell
 */
public class CSVReader extends StructuredReader implements AutoCloseable, Iterable<List<String>> {

  static final int END_OF_ROW = 2;
  static final int IN_FIELD = 3;
  static final int IN_QUOTE = 1;
  static final int OUT_QUOTE = 4;
  //----- States that the reader can be in
  static final int START = 0;
  final Queue<Integer> buffer = Lists.newLinkedList();
  final int comment;
  final int delimiter;
  final int escape;
  final boolean keepEmptyCells;
  final int quote;
  final Reader reader;
  int STATE = START;
  StringBuilder cell = new StringBuilder();
  List<String> row;
  List<String> header;
  boolean hasHeader;

  private boolean documentEnd = false;
  private int rowId = 0;
  private int valueIdx = -1;


  /**
   * Instantiates a new DSV reader.
   *
   * @param builder the builder
   * @param reader  the reader
   */
  public CSVReader(@NonNull CSV builder, @NonNull Reader reader) throws IOException {
    this.delimiter = builder.getDelimiter();
    this.escape = builder.getEscape();
    this.quote = builder.getQuote();
    this.comment = builder.getComment();
    this.keepEmptyCells = builder.isKeepEmptyCells();
    this.reader = new BufferedReader(reader);
    this.hasHeader = builder.getHasHeader();
    this.header = builder.getHeader() == null ? Collections.emptyList() : builder.getHeader();
    if (hasHeader && header.isEmpty()) {
      header = nextRow();
      consume();
      rowId = 1;
    }
  }

  private void addCell() {
    String cellString = cell.toString();
    if (STATE == IN_FIELD) {
      cellString = StringUtils.rightTrim(cellString);
    }
    if (keepEmptyCells || !StringUtils.isNullOrBlank(cellString)) {
      if (cellString.length() > 0 && cellString.charAt(cellString.length() - 1) == escape) {
        cellString += " ";
      }
      row.add(cellString.replaceAll("\\\\(.)", "$1"));
    }
    cell.setLength(0);
  }

  /**
   * @return The header of the CSV file
   */
  public List<String> getHeader() {
    if (header == null) {
      return Collections.emptyList();
    }
    return Collections.unmodifiableList(header);
  }

  @Override
  public String beginArray() throws StructuredIOException {
    valueIdx = 0;
    rowId++;
    return StringUtils.EMPTY;
  }

  @Override
  public StructuredReader beginDocument() throws StructuredIOException {
    if (header != null) {
      rowId++;
      return this;
    }

    if (rowId != 0) {
      throw new StructuredIOException("Illegal begin of document");
    }
    consume();
    rowId++;
    return this;
  }

  @Override
  public String beginObject() throws StructuredIOException {
    return beginArray();
  }

  private int beginOfLine(int c) throws IOException {
    if (c == comment) {
      readToEndOfLine();
      return START;
    } else if (c == quote) {
      return IN_QUOTE;
    } else if (c == delimiter) {
      addCell();
      return IN_FIELD;
    } else if (c == escape) {
      cell.append((char) escape).append(escape());
      return IN_FIELD;
    } else if (c == '\n') {
      return END_OF_ROW;
    } else if (!Character.isWhitespace(c)) {
      cell.append((char) c);
      return IN_FIELD;
    }
    gobbleWhiteSpace();
    return START;
  }

  private int bufferPeek() throws IOException {
    if (buffer.isEmpty()) {
      int next = reader.read();
      buffer.add(next);
    }
    return buffer.peek();
  }

  @Override
  public void close() throws IOException {
    reader.close();
  }

  private void consume() throws StructuredIOException {
    if (documentEnd) {
      throw new StructuredIOException("Document has ended.");
    }
    try {
      row = nextRow();
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
  }

  @Override
  public void endArray() throws StructuredIOException {
    valueIdx = -1;
    consume();
  }

  @Override
  public StructuredReader endDocument() throws StructuredIOException {
    documentEnd = true;
    return this;
  }

  @Override
  public void endObject() throws StructuredIOException {
    endArray();
  }

  private char escape() throws IOException {
    int c = reader.read();
    if (c == -1) {
      throw new IOException("Premature EOF");
    }
    return (char) c;
  }

  private void gobbleWhiteSpace() throws IOException {
    while (bufferPeek() != -1 && Character.isWhitespace(bufferPeek()) && !CharMatcher.BREAKING_WHITESPACE.matches((char) bufferPeek())) {
      read();
    }
  }

  @Override
  public boolean hasNext() throws StructuredIOException {
    return row != null;
  }

  private int inField(int c, boolean isQuoted) throws IOException {
    if (c == quote && isQuoted) {
      if (bufferPeek() == quote) {
        read();
      } else {
        return OUT_QUOTE;
      }
    } else if (c == quote && StringUtils.isNullOrBlank(cell.toString())) {
      return IN_QUOTE;
    } else if (c == delimiter && !isQuoted) {
      addCell();
      gobbleWhiteSpace();
      return START;
    } else if (c == escape) {
      cell.append((char) escape).append(escape());
      return isQuoted ? IN_QUOTE : IN_FIELD;
    } else if (c == '\r' && !isQuoted) {
      if (bufferPeek() == '\n') {
        read();
        return END_OF_ROW;
      }
    } else if (c == '\n' && !isQuoted) {
      return END_OF_ROW;
    }
    cell.append((char) c);
    return isQuoted ? IN_QUOTE : IN_FIELD;
  }

  @Override
  public Iterator<List<String>> iterator() {
    return new Iterator<List<String>>() {

      List<String> row = null;
      boolean complete = false;

      private boolean advance() {
        if (row == null) {
          try {
            row = nextRow();
          } catch (IOException e) {
            throw Throwables.propagate(e);
          }
        }
        complete = row == null;
        return complete;
      }

      @Override
      public boolean hasNext() {
        advance();
        return !complete;
      }

      @Override
      public List<String> next() {
        advance();
        if (complete) {
          throw new NoSuchElementException();
        }
        List<String> c = row;
        row = null;
        return c;
      }

      @Override
      public void remove() {
        throw new UnsupportedOperationException();
      }
    };
  }

  @Override
  public Map<String, Val> nextMap() throws StructuredIOException {
    beginArray();
    Map<String, Val> map = new LinkedHashMap<>();
    while (peek() != ElementType.END_ARRAY) {
      Map.Entry<String, Val> next = nextKeyValue();
      map.put(next.getKey(), next.getValue());
    }
    endArray();
    return map;
  }

  @Override
  public Tuple2<String, Val> nextKeyValue() throws StructuredIOException {
    String key;
    if (valueIdx >= header.size()) {
      key = Integer.toString(valueIdx);
    } else {
      key = header.get(valueIdx);
    }
    return Tuple2.of(key, nextValue());
  }

  @Override
  public <T> T nextObject(Class<T> clazz) throws StructuredIOException {
    Map<String, Val> map = nextMap();
    BeanMap beanMap;
    try {
      beanMap = new BeanMap(Reflect.onClass(clazz).create().get());
    } catch (ReflectionException e) {
      throw new StructuredIOException(e);
    }
    beanMap.putAll(map);
    return Cast.as(beanMap.getBean());
  }

  /**
   * Read row.
   *
   * @return the list
   * @throws IOException the iO exception
   */
  public List<String> nextRow() throws IOException {
    row = new ArrayList<>();
    STATE = START;
    int c;
    int readCount = 0;
    gobbleWhiteSpace();
    while ((c = read()) != -1) {
      if (c == '\r') {
        if (bufferPeek() == '\n') {
          continue;
        } else {
          c = '\n';
        }
      }
      readCount++;
      switch (STATE) {
        case START:
          STATE = beginOfLine(c);
          break;
        case IN_QUOTE:
          STATE = inField(c, true);
          break;
        case IN_FIELD:
          STATE = inField(c, false);
          break;
        case OUT_QUOTE:
          STATE = outQuote(c);
          break;
        default:
          throw new IOException("State [" + STATE + "]");
      }
      if (STATE == END_OF_ROW) {
        break;
      }
    }
    if (readCount > 0) {
      addCell();
    }
    if (row.isEmpty()) {
      return null;
    }
    rowId++;
    return new ArrayList<>(row);
  }

  @Override
  public Val nextValue() throws StructuredIOException {
    if (row == null) {
      throw new StructuredIOException("Expecting a value, but found null");
    }
    Val val = Val.of(row.get(valueIdx));
    valueIdx++;
    return val;
  }

  private int outQuote(int c) throws IOException {
    if (c == '\n') {
      return END_OF_ROW;
    } else if (c == delimiter) {
      addCell();
      gobbleWhiteSpace();
      return IN_FIELD;
    } else if (Character.isWhitespace(c)) {
      gobbleWhiteSpace();
      return OUT_QUOTE;
    }
    throw new IOException("Illegal character [" + (char) c + "] outside of the end quote of a cell.");
  }

  @Override
  public ElementType peek() throws StructuredIOException {
    if (rowId == 0) {
      return ElementType.BEGIN_DOCUMENT;
    } else if (row == null) {
      return ElementType.END_DOCUMENT;
    } else if (valueIdx == -1) {
      return ElementType.BEGIN_ARRAY;
    } else if (valueIdx < row.size()) {
      return ElementType.VALUE;
    } else {
      return ElementType.END_ARRAY;
    }
  }

  private int read() throws IOException {
    if (buffer.isEmpty()) {
      return reader.read();
    }
    return buffer.remove();
  }

  /**
   * Runs the iterator over the entire resource returning all the rows it processed.
   *
   * @return the list of rows (lists)
   * @throws IOException the iO exception
   */
  public List<List<String>> readAll() throws IOException {
    return Collect.from(this).collect(Collectors.toList());
  }

  private void readToEndOfLine() throws IOException {
    do {
      int c = reader.read();
      if (c == -1 || c == '\n') {
        return;
      }
    } while (true);
  }

  @Override
  public ElementType skip() throws StructuredIOException {
    consume();
    return ElementType.BEGIN_ARRAY;
  }

}//END OF CSVReader
