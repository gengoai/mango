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
import com.davidbracewell.stream.accumulator.MAccumulator;
import lombok.NonNull;
import org.apache.spark.api.java.JavaSparkContext;

import java.util.stream.Stream;

/**
 * @author David B. Bracewell
 */
public enum SparkStreamingContext implements StreamingContext {
  INSTANCE;

  private transient volatile JavaSparkContext sparkContext;


  @Override
  public MAccumulator<Double> accumulator(double initialValue, String name) {
    return null;
  }

  @Override
  public MAccumulator<Integer> accumulator(int initialValue, String name) {
    return null;
  }

  @Override
  public <T> MAccumulator<T> accumulator(T initialValue, Accumulatable<T> accumulatable, String name) {
    return null;
  }

  @Override
  public <T> MStream<T> stream(@NonNull T... items) {
    return null;
  }

  @Override
  public <T> MStream<T> stream(@NonNull Stream<T> stream) {
    return null;
  }

  @Override
  public MStream<String> textFile(String location) {
    return null;
  }
}//END OF SparkStreamingContext
