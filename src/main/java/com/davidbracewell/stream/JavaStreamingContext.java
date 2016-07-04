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

import com.davidbracewell.stream.accumulator.Accumulatable;
import com.davidbracewell.stream.accumulator.JavaAccumulator;
import com.davidbracewell.stream.accumulator.IntAccumulatable;
import com.davidbracewell.stream.accumulator.MAccumulator;
import com.google.common.base.Throwables;
import lombok.NonNull;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public enum JavaStreamingContext implements StreamingContext, Serializable {
  INSTANCE;
  private static final long serialVersionUID = 1L;

  @Override
  public MAccumulator<Double> accumulator(double initialValue, String name) {
    return null;
  }

  @Override
  public MAccumulator<Integer> accumulator(int initialValue, String name) {
    return new JavaAccumulator<>(new IntAccumulatable(), initialValue, name);
  }

  @Override
  public <T> MAccumulator<T> accumulator(T initialValue, Accumulatable<T> accumulatable, String name) {
    return new JavaAccumulator<>(accumulatable, initialValue, name);
  }

  @Override
  public <T> MStream<T> stream(@NonNull T... items) {
    return new JavaMStream<>(items);
  }

  @Override
  public <T> MStream<T> stream(@NonNull Stream<T> stream) {
    return new JavaMStream<>(stream);
  }

  @Override
  public MStream<String> textFile(String location) {
    try {
      return new JavaMStream<>(Files.lines(Paths.get(location)));
    } catch (IOException e) {
      throw Throwables.propagate(e);
    }
  }
}//END OF JavaStreamingContext
