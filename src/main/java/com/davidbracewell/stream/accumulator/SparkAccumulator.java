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

package com.davidbracewell.stream.accumulator;

import lombok.NonNull;
import org.apache.spark.Accumulator;
import scala.runtime.AbstractFunction0;

/**
 * The type Spark accumulator.
 *
 * @param <T> the type parameter
 * @author David B. Bracewell
 */
public class SparkAccumulator<T> implements MAccumulator<T> {
  private static final long serialVersionUID = 1L;

  /**
   * The Spark accumulator.
   */
  final Accumulator<T> sparkAccumulator;

  /**
   * Instantiates a new Spark accumulator.
   *
   * @param sparkAccumulator the spark accumulator
   */
  public SparkAccumulator(@NonNull Accumulator<T> sparkAccumulator) {
    this.sparkAccumulator = sparkAccumulator;
  }


  @Override
  public void add(T value) {
    sparkAccumulator.add(value);
  }

  @Override
  public T value() {
    return sparkAccumulator.value();
  }

  @Override
  public void setValue(T value) {
    sparkAccumulator.setValue(value);
  }

  @Override
  public String name() {
    return sparkAccumulator.name().getOrElse(new AbstractFunction0<String>() {
      @Override
      public String apply() {
        return null;
      }
    });
  }
}//END OF SparkAccumulator
