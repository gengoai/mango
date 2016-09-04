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

import com.davidbracewell.concurrent.Threads;
import com.davidbracewell.logging.Logger;
import com.google.common.base.Preconditions;
import lombok.NonNull;
import lombok.SneakyThrows;

import java.io.IOException;
import java.io.Writer;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * <p>Wraps an underlying writer allowing multiple threads to write through buffering calls to a blocking queue.</p>
 *
 * @author David B. Bracewell
 */
public class AsyncWriter extends Writer implements Runnable {

  private static final Logger log = Logger.getLogger(AsyncWriter.class);
  private final AtomicBoolean isStopped = new AtomicBoolean(false);
  private final BlockingQueue<String> queue = new LinkedBlockingQueue<>();
  private final AtomicBoolean isTerminated = new AtomicBoolean(false);
  private final Writer wrap;
  private Thread thread;

  /**
   * Creates an instance of an Asynchronous writer wrapping a given writer.
   *
   * @param wrap The writer being wrapped
   */
  public AsyncWriter(@NonNull Writer wrap) {
    this.wrap = wrap;
    thread = new Thread(this);
    thread.start();
  }

  @Override
  public void write(char[] cbuf, int off, int len) throws IOException {
    Preconditions.checkArgument(!isStopped.get(), "Cannot write to a closed writer.");
    try {
      queue.put(new String(cbuf, off, len));
    } catch (InterruptedException e) {
      throw new IOException(e);
    }
  }

  @Override
  public void flush() throws IOException {
    wrap.flush();
  }

  @Override
  public void close() throws IOException {
    isStopped.set(true);
    while (thread.isAlive()) {
      Threads.sleep(100);
    }
  }


  /**
   * Is terminated.
   *
   * @return the boolean
   */
  public boolean isTerminated() {
    return isTerminated.get();
  }

  @Override
  @SneakyThrows
  public void run() {
    while (!Thread.currentThread().isInterrupted()) {
      try {
        String out = queue.poll(100, TimeUnit.MILLISECONDS);
        if (out != null) {
          wrap.write(out);
        }
        if (queue.isEmpty() && isStopped.get()) {
          break;
        }
      } catch (InterruptedException e) {
        break;
      } catch (IOException e) {
        log.severe(e);
        break;
      }
    }
    wrap.flush();
    QuietIO.closeQuietly(wrap);
    isTerminated.set(true);
  }

}//END OF AsyncWriter
