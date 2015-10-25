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

package com.davidbracewell.io;

import com.davidbracewell.io.resource.Resource;
import com.davidbracewell.io.structured.csv.CSVReader;
import com.davidbracewell.io.structured.csv.CSVWriter;
import com.davidbracewell.reflection.Specification;
import com.davidbracewell.string.CSVFormatter;

import java.io.IOException;
import java.io.Reader;
import java.io.Serializable;
import java.io.Writer;
import java.util.Arrays;
import java.util.List;

/**
 * The type Csv.
 *
 * @author David B. Bracewell
 */
public class CSV implements Specification, Serializable {

  private static final long serialVersionUID = 1L;

  char comment = '#';
  char delimiter = ',';
  char escape = '\\';
  boolean keepEmptyCells = true;
  char quote = '\"';
  boolean hasHeader = false;
  List<String> header = null;

  /**
   * Builder csv.
   *
   * @return the csv
   */
  public static CSV builder() {
    return new CSV();
  }

  public CSV hasHeader() {
    hasHeader = true;
    return this;
  }

  public CSV noHeader() {
    hasHeader = false;
    return this;
  }

  public CSV header(String... items) {
    this.header = (items == null ? null : Arrays.asList(items));
    return this;
  }

  public CSV header(List<String> items) {
    this.header = items;
    return this;
  }

  public boolean getHasHeader() {
    return hasHeader;
  }

  public List<String> getHeader() {
    return header;
  }

  /**
   * Comment csv.
   *
   * @param commentChar the comment char
   * @return the csv
   */
  public CSV comment(char commentChar) {
    this.comment = commentChar;
    return this;
  }

  /**
   * Delimiter csv.
   *
   * @param delimiter the delimiter
   * @return the csv
   */
  public CSV delimiter(char delimiter) {
    this.delimiter = delimiter;
    return this;
  }

  /**
   * Escape csv.
   *
   * @param escape the escape
   * @return the csv
   */
  public CSV escape(char escape) {
    this.escape = escape;
    return this;
  }

  /**
   * Quote csv.
   *
   * @param quote the quote
   * @return the csv
   */
  public CSV quote(char quote) {
    this.quote = quote;
    return this;
  }

  /**
   * Keep empty cells.
   *
   * @return the csv
   */
  public CSV keepEmptyCells() {
    this.keepEmptyCells = true;
    return this;
  }

  /**
   * Remove empty cells.
   *
   * @return the csv
   */
  public CSV removeEmptyCells() {
    this.keepEmptyCells = false;
    return this;
  }

  /**
   * Build cSV reader.
   *
   * @param reader the reader
   * @return the cSV reader
   */
  public CSVReader reader(Reader reader) throws IOException {
    return new CSVReader(this, reader);
  }

  /**
   * Reader cSV reader.
   *
   * @param resource the resource
   * @return the cSV reader
   * @throws IOException the iO exception
   */
  public CSVReader reader(Resource resource) throws IOException {
    return reader(resource.reader());
  }

  /**
   * Writer cSV writer.
   *
   * @param writer the writer
   * @return the cSV writer
   */
  public CSVWriter writer(Writer writer) throws IOException {
    return new CSVWriter(this, writer);
  }

  /**
   * Writer cSV writer.
   *
   * @param resource the resource
   * @return the cSV writer
   * @throws IOException the iO exception
   */
  public CSVWriter writer(Resource resource) throws IOException {
    return writer(resource.writer());
  }

  /**
   * Formatter cSV formatter.
   *
   * @return the cSV formatter
   */
  public CSVFormatter formatter() {
    return new CSVFormatter(this);
  }

  /**
   * Gets comment.
   *
   * @return the comment
   */
  public char getComment() {
    return comment;
  }

  /**
   * Gets delimiter.
   *
   * @return the delimiter
   */
  public char getDelimiter() {
    return delimiter;
  }

  /**
   * Gets escape.
   *
   * @return the escape
   */
  public char getEscape() {
    return escape;
  }

  /**
   * Is keep empty cells.
   *
   * @return the boolean
   */
  public boolean isKeepEmptyCells() {
    return keepEmptyCells;
  }

  /**
   * Gets quote.
   *
   * @return the quote
   */
  public char getQuote() {
    return quote;
  }
}//END OF CSV
