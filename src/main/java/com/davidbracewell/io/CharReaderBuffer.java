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

import java.io.IOException;
import java.io.Reader;

/**
 * The type Char reader buffer.
 * @author David B. Bracewell
 */
public class CharReaderBuffer implements AutoCloseable {

  private final Reader reader;
  private char[] buffer = new char[1024];
  private int bufferIndex = 0;
  private int validBufferLength = 0;
  private boolean endOfBuffer = false;
  private int currentChar = -1;

  /**
   * Instantiates a new Char reader buffer.
   *
   * @param reader the reader
   */
  public CharReaderBuffer(Reader reader) {
    this.reader = reader;
  }

  /**
   * Instantiates a new Char reader buffer.
   *
   * @param resource the resource
   * @throws IOException the iO exception
   */
  public CharReaderBuffer(Resource resource) throws IOException {
    this.reader = resource.openReader();
  }

  /**
   * Gets current char.
   *
   * @return the current char
   */
  public int getCurrentChar() {
    return currentChar;
  }

  /**
   * Read int.
   *
   * @return the int
   * @throws IOException the iO exception
   */
  public int read() throws IOException {
    if (endOfBuffer) {
      return -1;
    }
    if (bufferIndex >= validBufferLength) {
      validBufferLength = reader.read(buffer);
      bufferIndex = 0;
      if (validBufferLength == -1) {
        endOfBuffer = true;
        return -1;
      }
    }
    currentChar = buffer[bufferIndex++];
    return currentChar;
  }

  /**
   * Peek int.
   *
   * @return the int
   * @throws IOException the iO exception
   */
  public int peek() throws IOException {
    if (endOfBuffer) {
      return -1;
    }
    if (bufferIndex >= validBufferLength) {
      read();
    }
    return buffer[bufferIndex];
  }


  @Override
  public void close() throws Exception {
    QuietIO.closeQuietly(reader);
  }

}//END OF CharReaderBuffer
