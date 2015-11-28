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
import com.davidbracewell.collection.Counter;
import com.davidbracewell.collection.Index;
import com.davidbracewell.collection.Indexes;
import com.davidbracewell.conversion.Cast;
import com.davidbracewell.conversion.Convert;
import com.davidbracewell.io.CSV;
import com.davidbracewell.io.structured.StructuredIOException;
import com.davidbracewell.io.structured.StructuredWriter;
import com.davidbracewell.string.CSVFormatter;
import com.davidbracewell.string.StringUtils;
import com.google.common.base.Preconditions;
import lombok.NonNull;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p> Wraps writing collections and maps in DSV format to resources. </p>
 *
 * @author David B. Bracewell
 */
public class CSVWriter extends StructuredWriter implements AutoCloseable {

  private final CSVFormatter formatter;
  private final BufferedWriter writer;
  private boolean documentEnd = false;
  private boolean inArray = false;
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

  public void writeMapAsOneRow(Map<?, ?> row, char keyValueDelimiter) throws IOException {
    if (row != null) {
      String keyValueDelimStr = Character.toString(keyValueDelimiter);
      Preconditions.checkArgument(!keyValueDelimStr.equals(formatter.getDelimiter()), "Key-value delimiter cannot be the same as the file delimiter.");
      List<String> rowList = row.entrySet().stream().map(m ->
        Convert.convert(m.getKey(), String.class).replace(keyValueDelimStr, formatter.getEscape() + keyValueDelimStr)
        + keyValueDelimiter +
        Convert.convert(m.getValue(), String.class).replace(keyValueDelimStr, formatter.getEscape() + keyValueDelimStr)).collect(Collectors.toCollection(LinkedList::new));
      write(rowList);
    }
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
        writer.write(formatter.format(
          header.asList().stream()
            .map(h -> row.containsKey(h) ? Convert.convert(row.get(h), String.class) : StringUtils.EMPTY)
            .collect(Collectors.toList())
        ));
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
        writer.write(formatter.format(
          header.asList().stream()
            .map(h -> row.containsKey(h) ? Convert.convert(row.get(h), String.class) : StringUtils.EMPTY)
            .collect(Collectors.toList())
        ));
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
  public CSVWriter beginDocument() throws StructuredIOException {
    if (documentEnd) {
      throw new StructuredIOException("Already ended document");
    }
    return this;
  }

  @Override
  public CSVWriter endDocument() throws StructuredIOException {
    if (inArray) {
      throw new StructuredIOException("Cannot end document with an open array.");
    }
    documentEnd = true;
    return this;
  }


  @Override
  @SuppressWarnings("unchecked")
  public CSVWriter writeObject(@NonNull Object value) throws StructuredIOException {
    checkState();
    if (inArray) {
      throw new StructuredIOException("Cannot write object while in an array");
    }

    try {
      if (value instanceof Map) {
        write(Cast.<Map>as(value));
      } else if (value instanceof Counter) {
        write(Cast.<Counter>as(value).asMap());
      } else if (value instanceof Iterable) {
        write(Cast.<Iterable>as(value));
      } else if (value.getClass().isArray()) {
        write(Convert.convert(value, Iterable.class));
      } else {
        beginArray();
        writeValue(value);
        endArray();
      }
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }

    return this;
  }

  private void checkState() throws StructuredIOException {
    if (documentEnd) {
      throw new StructuredIOException("Already ended document");
    }
  }

  @Override
  public CSVWriter writeObject(String name, Object object) throws StructuredIOException {
    throw new UnsupportedOperationException("CSV does not support named objects");
  }

  @Override
  public CSVWriter writeKeyValue(String key, Object value) throws StructuredIOException {
    checkState();
    checkInArray();
    row.put(key, value);
    return this;
  }

  private void checkInArray() throws StructuredIOException {
    if (!inArray) {
      throw new StructuredIOException("Must be in an array to write a key value");
    }
  }

  @Override
  public CSVWriter writeValue(Object value) throws StructuredIOException {
    checkState();
    checkInArray();
    row.put("--NON-HEADER_" + row.size(), value);
    return this;
  }

  @Override
  public CSVWriter beginObject() throws StructuredIOException {
    return beginArray();
  }

  @Override
  public CSVWriter beginObject(String objectName) throws StructuredIOException {
    throw new UnsupportedOperationException("CSV does not support named objects");
  }

  @Override
  public CSVWriter endObject() throws StructuredIOException {
    return endArray();
  }

  @Override
  public CSVWriter beginArray() throws StructuredIOException {
    if (inArray) {
      throw new StructuredIOException("CSV does not support nested arrays.");
    }
    inArray = true;
    row.clear();
    return this;
  }

  @Override
  public CSVWriter beginArray(String arrayName) throws StructuredIOException {
    throw new UnsupportedOperationException("CSV does not support named arrays");
  }

  @Override
  public CSVWriter endArray() throws StructuredIOException {
    try {
      if (header.isEmpty()) {
        write(row.entrySet().stream()
          .map(e -> e.getKey().startsWith("--NON_HEADER") ? e.getValue() : e)
          .collect(Collectors.toList())
        );
      } else {
        write(header.asList().stream()
          .map(h -> row.containsKey(h) ? Convert.convert(row.get(h), String.class) : StringUtils.EMPTY)
          .collect(Collectors.toList()));
      }
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
    inArray = false;
    return this;
  }

  @Override
  public void flush() throws StructuredIOException {
    try {
      writer.flush();
    } catch (IOException e) {
      throw new StructuredIOException(e);
    }
  }

}//END OF CSVWriter
