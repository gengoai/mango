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

package com.davidbracewell.logging;

import java.util.logging.Handler;
import java.util.logging.LogRecord;

public class StringBuilderHandler extends Handler {

  private static final Object lock = new Object();
  private StringBuilder stringBuffer = new StringBuilder();

  @Override
  public void publish(LogRecord record) {
    synchronized (lock) {
      stringBuffer.setLength(0);
      stringBuffer.append(getFormatter().format(record));
    }
  }

  @Override
  public void flush() {
  }

  @Override
  public void close() throws SecurityException {
  }

  public String getContent() {
    synchronized (lock) {
      return stringBuffer.toString();
    }
  }

  public void clear() {
    synchronized (lock) {
      stringBuffer = new StringBuilder();
    }
  }

}
