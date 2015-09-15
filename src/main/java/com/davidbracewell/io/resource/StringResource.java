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

import com.davidbracewell.io.resource.spi.StringResourceProvider;
import com.google.common.base.Charsets;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

import java.io.*;

/**
 * <p> A resource that wraps a String. </p>
 *
 * @author David B. Bracewell
 */
public class StringResource extends Resource {

  private static final long serialVersionUID = 8750046186020559958L;
  private final StringBuilder resource;

  /**
   * Instantiates a new string resource.
   */
  public StringResource() {
    this(null);
  }


  /**
   * <p> Creates a Resource that is a String. </p>
   *
   * @param resource The string contents.
   */
  public StringResource(String resource) {
    if (resource == null) {
      this.resource = new StringBuilder();
    } else {
      this.resource = new StringBuilder(resource);
    }
  }

  @Override
  public void append(String content) throws IOException {
    Preconditions.checkNotNull(content);
    this.resource.append(content);
  }

  @Override
  public String resourceDescriptor() {
    return StringResourceProvider.PROTOCOL + ":";
  }

  @Override
  public boolean canWrite() {
    return true;
  }

  @Override
  public boolean canRead() {
    return true;
  }

  @Override
  public boolean equals(Object o) {
    if (o == null) {
      return false;
    }
    if (o == this) {
      return true;
    }
    return o instanceof StringResource && Objects.equal(resource, ((StringResource) o).resource);
  }

  @Override
  public boolean exists() {
    return true;
  }

  @Override
  public int hashCode() {
    return resource.hashCode();
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return new ByteArrayInputStream(resource.toString().getBytes(Charsets.UTF_8));
  }

  @Override
  public OutputStream createOutputStream() throws IOException {
    this.resource.setLength(0);
    return new OutputStream() {

      ByteArrayOutputStream baos = new ByteArrayOutputStream();


      @Override
      public void write(int b) throws IOException {
        baos.write(b);
      }

      @Override
      public void close() throws IOException {
        super.close();
        resource.setLength(0);
        resource.append(baos.toString("UTF-8"));
        baos.close();
      }
    };
  }

  @Override
  public String toString() {
    return resource.toString();
  }

}// END OF StringResource
