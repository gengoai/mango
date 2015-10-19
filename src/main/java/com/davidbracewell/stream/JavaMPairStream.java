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

package com.davidbracewell.stream;

import lombok.NonNull;

import java.util.Map;
import java.util.function.BiConsumer;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public class JavaMPairStream<T, U> implements MPairStream<T, U> {

  private final Stream<? extends Map.Entry<T, U>> stream;

  public JavaMPairStream(Stream<Map.Entry<T, U>> stream) {
    this.stream = stream;
  }

  @Override
  public void close() throws Exception {
    stream.close();
  }

  @Override
  public void forEach(@NonNull BiConsumer<? super T, ? super U> consumer) {
    stream.forEach(e -> consumer.accept(e.getKey(), e.getValue()));
  }

}//END OF JavaMPairStream
