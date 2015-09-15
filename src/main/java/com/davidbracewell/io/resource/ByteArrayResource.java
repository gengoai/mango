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

package com.davidbracewell.io.resource;

import com.davidbracewell.io.resource.spi.ByteArrayResourceProvider;
import com.google.common.base.Preconditions;

import java.io.*;

/**
 * A Resource that wraps a byte array
 *
 * @author David B. Bracewell
 */
public class ByteArrayResource extends Resource {

  private static final long serialVersionUID = 9152033221857665242L;
  private final ByteArrayOutputStream buffer;


  /**
   * Instantiates a new Byte array resource.
   */
  public ByteArrayResource() {
    this.buffer = new ByteArrayOutputStream();
  }

  /**
   * Instantiates a new Byte array resource.
   *
   * @param b an initial byte array
   */
  public ByteArrayResource(byte[] b) {
    this(Preconditions.checkNotNull(b), 0, b.length);
  }

  /**
   * Instantiates a new Byte array resource.
   *
   * @param b      an initial byte array
   * @param offset the offset into the byte array
   * @param len    the number of bytes to copy
   */
  public ByteArrayResource(byte[] b, int offset, int len) {
    Preconditions.checkNotNull(b);
    this.buffer = new ByteArrayOutputStream();
    this.buffer.write(b, offset, len);
  }


  @Override
  protected InputStream createInputStream() throws IOException {
    return new ByteArrayInputStream(buffer.toByteArray());
  }

  @Override
  protected OutputStream createOutputStream() throws IOException {
    return buffer;
  }

  @Override
  public boolean canRead() {
    return true;
  }

  @Override
  public String resourceDescriptor() {
    return ByteArrayResourceProvider.PROTOCOL + ":";
  }

  @Override
  public boolean canWrite() {
    return true;
  }

  @Override
  public boolean exists() {
    return true;
  }


}//END OF ByteArrayResource
