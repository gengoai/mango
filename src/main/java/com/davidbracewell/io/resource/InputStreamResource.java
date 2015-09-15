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

import com.google.common.base.Preconditions;

import java.io.IOException;
import java.io.InputStream;

/**
 * @author David B. Bracewell
 */
public class InputStreamResource extends Resource {

  private static final long serialVersionUID = -4341744444291053097L;
  private final InputStream inputStream;

  /**
   * Default Constructor
   *
   * @param stream The input stream
   */
  public InputStreamResource(InputStream stream) {
    this.inputStream = Preconditions.checkNotNull(stream);
  }

  @Override
  public String resourceDescriptor() {
    return "";
  }

  @Override
  public InputStream createInputStream() throws IOException {
    return inputStream;
  }

  @Override
  public boolean exists() {
    return true;
  }


  @Override
  public boolean canRead() {
    return true;
  }


}//END OF InputStreamResource
