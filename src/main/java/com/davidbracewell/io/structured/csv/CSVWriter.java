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

import com.davidbracewell.SystemInfo;
import com.davidbracewell.collection.Index;
import com.davidbracewell.collection.Indexes;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.io.structured.Writeable;
import com.davidbracewell.string.CSVFormatter;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <p> Wraps writing collections and maps in DSV format to resources. </p>
 *
 * @author David B. Bracewell
 */
public class CSVWriter extends StructuredWriter {

  private final CSVFormatter formatter;
  private final BufferedWriter writer;
  private boolean endOfDocument = false;
  private Index<String> header = Indexes.newIndex();
  private Map<String, Object> row = new LinkedHashMap<>();

  public CSVWriter(@NonNull CSV csv, @NonNull Writer writer) throws IOException {
    this.formatter = csv.formatter();
    this.writer = new BufferedWriter(writer);
    if (csv.getHeader() != null && !csv.getHeader().isEmpty()) {
      writer.write(formatter.format(csv.getHeader()));
      writer.write(SystemInfo.LINE_SEPARATOR);
      header.addAll(csv.getHeader());
    }
  }

  /**
   * Writes the items in the row to the resource in DSV format.
   *
   * @param row the row
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void write(Iterable<?> row) throws IOException {
    if (row != null) {
      writer.write(formatter.format(row));
    }
    writer.write(SystemInfo.LINE_SEPARATOR);
  }

  /**
   * Writes the items in the row to the resource in DSV format.
   *
   * @param row the row
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void write(Iterator<?> row) throws IOException {
    if (row != null) {
      writer.write(formatter.format(row));
    }
    writer.write(SystemInfo.LINE_SEPARATOR);
  }

  /**
   * Writes the items in the row to the resource in DSV format.
   *
   * @param row the row
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void write(Map<?, ?> row) throws IOException {
    if (row != null) {
      if (header.isEmpty()) {
        for (Map.Entry<?, ?> m : row.entrySet()) {
          writer.write(formatter.format(Convert.convert(m.getKey(), String.class), Convert.convert(m.getValue(), String.class)));
          writer.write(SystemInfo.LINE_SEPARATOR);
        }
      } else {
        writer.write(
          formatter.format(
            Stream.concat(
              header.asList().stream()
                .map(h -> row.containsKey(h) ? Convert.convert(row.get(h), String.class) : StringUtils.EMPTY),
              row.keySet().stream()
                .map(k -> Convert.convert(k, String.class))
                .filter(h -> !header.contains(h))
                .map(h -> Convert.convert(row.get(h), String.class))
            )
              .collect(Collectors.toList())
          )
        );
        writer.write(SystemInfo.LINE_SEPARATOR);
      }

    }

  }

  /**
   * Writes the items in the row to the resource in DSV format.
   *
   * @param row               the row
   * @param keyValueSeparator the character to use to separate keys and values
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void write(Map<?, ?> row, char keyValueSeparator) throws IOException {
    if (row != null) {
      if (header.isEmpty()) {
        writer.write(formatter.format(row, keyValueSeparator));
      } else {
        writer.write(
          formatter.format(
            Stream.concat(
              header.asList().stream()
                .map(h -> row.containsKey(h) ? Convert.convert(row.get(h), String.class) : StringUtils.EMPTY),
              row.keySet().stream()
                .map(k -> Convert.convert(k, String.class))
                .filter(h -> !header.contains(h))
                .map(h -> Convert.convert(row.get(h), String.class))
            )
              .collect(Collectors.toList())
          )
        );
      }
    }
    writer.write(SystemInfo.LINE_SEPARATOR);
  }

  /**
   * Writes the items in the row to the resource in DSV format.
   *
   * @param row the row
   * @throws IOException Signals that an I/O exception has occurred.
   */
  public void write(Object... row) throws IOException {
    if (row != null) {
      writer.write(formatter.format(row));
    }
    writer.write(SystemInfo.LINE_SEPARATOR);
  }

  @Override
  public void close() throws IOException {
    writer.close();
  }

  @Override
  public void flush() throws IOException {
    writer.flush();
  }

  @Override
  public StructuredWriter beginDocument(boolean isArray) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    return this;
  }

  @Override
  public void endDocument() throws IOException {
    if (row.size() > 0) {
      write(row);
    }
    endOfDocument = true;
  }

  @Override
  public StructuredWriter beginObject(String name) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    return this;
  }

  @Override
  public StructuredWriter beginObject() throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    return this;
  }

  @Override
  public StructuredWriter endObject() throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    write(row);
    row.clear();
    return this;
  }

  @Override
  public StructuredWriter beginArray(String name) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    return this;
  }

  @Override
  public StructuredWriter beginArray() throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    return this;
  }

  @Override
  public StructuredWriter endArray() throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    write(row);
    row.clear();
    return this;
  }

  @Override
  public boolean inArray() {
    return true;
  }

  @Override
  public boolean inObject() {
    return true;
  }

  @Override
  public StructuredWriter writeValue(Object value) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    if (value instanceof Writeable) {
      Cast.<Writeable>as(value).write(this);
    } else {
      row.put("___UNNAMED___[" + row.size() + "]", value);
    }
    return this;
  }

  @Override
  protected StructuredWriter writeObject(@NonNull Object value) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    if (value instanceof Writeable) {
      Cast.<Writeable>as(value).write(this);
    } else {
      row.put("___UNNAMED___[" + row.size() + "]", value);
    }
    return this;
  }

  @Override
  protected StructuredWriter writeMap(@NonNull Map<?, ?> map) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    map.entrySet().forEach(entry ->
      row.put(Convert.convert(entry.getKey(), String.class), Convert.convert(entry.getValue(), String.class))
    );
    return this;
  }

  @Override
  protected StructuredWriter writeCollection(@NonNull Collection<?> collection) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    collection.forEach(value ->
      row.put("___UNNAMED___[" + row.size() + "]", value)
    );
    return this;
  }

  @Override
  protected StructuredWriter writeArray(@NonNull Object[] array) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    for (Object value : array) {
      row.put("___UNNAMED___[" + row.size() + "]", value);
    }
    return this;
  }

  @Override
  public StructuredWriter writeKeyValue(String key, Object value) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    if (value instanceof Writeable) {
      Cast.<Writeable>as(value).write(this);
    } else {
      row.put(key, value);
    }
    return this;
  }

  @Override
  protected StructuredWriter writeNull() throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    row.put("___UNNAMED___[" + row.size() + "]", null);
    return this;
  }

  @Override
  protected StructuredWriter writeNumber(Number number) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    row.put("___UNNAMED___[" + row.size() + "]", number.toString());
    return this;
  }

  @Override
  protected StructuredWriter writeString(String string) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    row.put("___UNNAMED___[" + row.size() + "]", string);
    return this;
  }

  @Override
  protected StructuredWriter writeBoolean(boolean value) throws IOException {
    Preconditions.checkState(!endOfDocument, "endDocument() has been called");
    row.put("___UNNAMED___[" + row.size() + "]", Boolean.toString(value));
    return this;
  }

}//END OF CSVWriter
