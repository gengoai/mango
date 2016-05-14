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

package com.davidbracewell.string;

import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.google.common.base.Preconditions;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * <p> Formats a series of items in Delimited Separated Value format. </p>
 *
 * @author David B. Bracewell
 */
public class CSVFormatter implements Serializable {
  private static final long serialVersionUID = 1L;

  final String comment;
  final String delimiter;
  final String doubleEscape;
  final String doubleQuote;
  final String escape;
  final boolean keepEmptyCells;
  final String quote;

  /**
   * Instantiates a new CSV formatter.
   *
   * @param csvFormat the csv format
   */
  public CSVFormatter(CSV csvFormat) {
    Preconditions.checkNotNull(csvFormat);
    this.delimiter = Character.toString(csvFormat.getDelimiter());
    this.escape = Character.toString(csvFormat.getEscape());
    this.quote = Character.toString(csvFormat.getQuote());
    this.comment = Character.toString(csvFormat.getComment());
    this.keepEmptyCells = csvFormat.isKeepEmptyCells();
    this.doubleEscape = escape + escape;
    this.doubleQuote = quote + quote;
  }

  /**
   * Gets comment.
   *
   * @return the comment
   */
  public String getComment() {
    return comment;
  }

  /**
   * Gets delimiter.
   *
   * @return the delimiter
   */
  public String getDelimiter() {
    return delimiter;
  }

  /**
   * Gets escape.
   *
   * @return the escape
   */
  public String getEscape() {
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
  public String getQuote() {
    return quote;
  }

  public String format(Stream<?> stream) {
    if (stream == null) {
      return StringUtils.EMPTY;
    }
    return format(stream.iterator());
  }

  /**
   * Formats the items in an <code>Iterator</code>.
   *
   * @param itr The iterator to format
   * @return A String representing the single DSV formatted row
   */
  public String format(Iterator<?> itr) {
    StringBuilder rowString = new StringBuilder();

    for (; itr.hasNext(); ) {
      Object n = itr.next();
      String s = n == null ? StringUtils.EMPTY : n.toString();
      if (escape.equals("\\")) {
        s = s.replaceAll(Pattern.quote(escape), doubleEscape + doubleEscape);
      } else {
        s = s.replaceAll(Pattern.quote(escape), doubleEscape);
      }
      s = s.replaceAll(Pattern.quote(comment), doubleEscape + comment);
      if (s.contains(delimiter) || s.contains("\r") || s.contains("\n") || s.contains(quote)) {
        rowString.append(quote);
        s = s.replaceAll(Pattern.quote(quote), doubleQuote);
        rowString.append(s);
        rowString.append(quote);
      } else {
        rowString.append(s);
      }

      if (itr.hasNext()) {
        rowString.append(delimiter);
      }
    }
    return rowString.toString();
  }

  /**
   * Formats the items in a <code>Map</code>. Keys and values are separated using <code>:</code>.
   *
   * @param map The Map to format
   * @return A String representing the single DSV formatted row of Map entries
   */
  public String format(Map<?, ?> map) {
    return format(map, ':');
  }

  /**
   * Formats the items in a <code>Map</code>.
   *
   * @param map               The Map to format
   * @param keyValueSeparator the character to use to separate keys and values
   * @return A String representing the single DSV formatted row of Map entries
   */
  public String format(Map<?, ?> map, char keyValueSeparator) {
    Preconditions.checkNotNull(map);
    Preconditions.checkArgument(keyValueSeparator != ' ');

    StringBuilder rowString = new StringBuilder();
    for (Iterator<?> itr = map.entrySet().iterator(); itr.hasNext(); ) {
      Map.Entry<?, ?> entry = Cast.as(itr.next());
      String key = entry.getKey() == null ? "null" : entry.getKey().toString();
      String value = entry.getValue() == null ? "null" : entry.getValue().toString();

      key = key.replaceAll(Pattern.quote(escape), doubleEscape + doubleEscape);
      value = value.replaceAll(Pattern.quote(escape), doubleEscape + doubleEscape);

      rowString.append(quote);

      if (key.indexOf(keyValueSeparator) >= 0) {
        rowString.append(doubleQuote).append(key.replaceAll(quote, doubleQuote))
            .append(doubleQuote);
      } else {
        rowString.append(key.replaceAll(quote, doubleQuote));
      }

      rowString.append(keyValueSeparator);

      if (value.indexOf(keyValueSeparator) >= 0) {
        rowString.append(doubleQuote).append(value.replaceAll(quote, doubleQuote))
            .append(doubleQuote);
      } else {
        rowString.append(value.replaceAll(quote, doubleQuote));
      }

      rowString.append(quote);

      if (itr.hasNext()) {
        rowString.append(delimiter);
      }

    }

    return rowString.toString();
  }

  /**
   * Format string.
   *
   * @param iterable the iterable
   * @return the string
   */
  public String format(Iterable<?> iterable) {
    Preconditions.checkNotNull(iterable);
    return format(iterable.iterator());
  }


  /**
   * Formats the items in an Array.
   *
   * @param array The array to format
   * @return A String representing the single DSV formatted row
   */
  public String format(Object... array) {
    Preconditions.checkNotNull(array);
    if (array.length == 1 && array[0].getClass().isArray()) {
      return format(Convert.convert(array[0], Iterable.class));
    }
    return format(Arrays.asList(array).iterator());
  }


}//END OF CSVFormatter
